package com.tagsin.wechat_sdk;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import com.tagsin.tutils.http.HttpResult;
import com.tagsin.tutils.http.Requester;
import com.tagsin.tutils.http.Requester.Method;
import com.tagsin.tutils.json.JsonUtils;
import com.tagsin.tutils.okhttp.OkHttpUtil;
import com.tagsin.wechat_sdk.token.Token;
import com.tagsin.wechat_sdk.token.TokenType;
import com.tagsin.wechat_sdk.user.UserInfo;
import com.tagsin.wechat_sdk.v2.BaseWxSdk;
import com.tagsin.wechat_sdk.v2.WxComponentSdk;
import com.tagsin.wechat_sdk.v2.WxSdk;
import com.tagsin.wechat_sdk.vo.MaterialType;
import com.tagsin.wechat_sdk.vo.MaterialVo;
import com.tagsin.wechat_sdk.vo.NewsVo;

public class WXServerApi {
	
	private static final Logger LOGGER = Logger.getLogger(WXServerApi.class);
	
	private static HttpClient client = HttpClientBuilder.create().build();
	
	
	private static final BaseWxSdk baseWxSdk = new WxSdk();
	
	public static BaseWxSdk getBaseWxSdk(){
		return baseWxSdk;
	}
	
	/**
	 * 获取AccessToken
	 * @param app
	 * @return
	 */
	public static Token accessToken(App app){
		try {
			HttpUrl httpUrl = HttpUrl.parse("https://api.weixin.qq.com/cgi-bin/token").newBuilder()
			.addQueryParameter("grant_type", "client_credential")
			.addQueryParameter("appid", app.getId())
			.addQueryParameter("secret", app.getSecret()).build();
			Response response = OkHttpUtil.requestByPost(new Request.Builder().url(httpUrl).build());
			
			if(response.code()==200){
				String json = response.body().string();
				LOGGER.info("result:"+json);
				ObjectNode jsonObj = JsonUtils.readJsonObject(json);
				String access_token = jsonObj.get("access_token").getTextValue();
				long expires_in = jsonObj.get("expires_in").getLongValue();
				return new Token(TokenType.ACCESS_TOKEN, access_token, System.currentTimeMillis() + (expires_in-60) * 1000);
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code());
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	public static UserInfo getUserInfo(App app,String openid){
		try {
			Requester req = Requester.builder()
					.setUrl("https://api.weixin.qq.com/cgi-bin/user/info")
					.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN))
					.addUrlParm("lang", "zh_CN")
					.addUrlParm("openid", openid);
					
			HttpResult result = req.execute();
			if(result.getCode()==200){
				return JsonUtils.fromJson(result.getRespBody(), UserInfo.class);
			}
			throw new WXServerApiException("faild get user info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	public static String getQrCode(App app,long scene_id,long expireSeconds){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create")
					.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN));
					
			Map<String,Object> reqData = convertPathValueToMap(scene_id, "action_info","scene","scene_id");
			if(expireSeconds>0){
				reqData.put("action_name", "QR_SCENE");
				reqData.put("expire_seconds",expireSeconds);
			}else{
				reqData.put("action_name", "QR_LIMIT_SCENE");
			}
			
			req.setBody(JsonUtils.toJson(reqData));
			
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String ticket = objNode.get("ticket").getTextValue();
				
				req = Requester.builder()
						.setUrl("https://mp.weixin.qq.com/cgi-bin/showqrcode")
						.addUrlParm("ticket", ticket);
				
				result = req.execute();
				if(result.getCode()==200){
					String fileType = result.getContentType().toLowerCase().replace("image/", "");
					StringBuilder sb = new StringBuilder();
					sb.append("data:image/").append(fileType).append(";base64, ").append(Base64.encodeBase64String(result.getRespBody()));
					return sb.toString();
				}
			}
			throw new WXServerApiException("faild get qr code: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	public static String getQrCodeUrl(App app,long scene_id,long expireSeconds){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create")
					.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN));
					
			Map<String,Object> reqData = convertPathValueToMap(scene_id, "action_info","scene","scene_id");
			if(expireSeconds>0){
				reqData.put("action_name", "QR_SCENE");
				reqData.put("expire_seconds",expireSeconds);
			}else{
				reqData.put("action_name", "QR_LIMIT_SCENE");
			}
			
			req.setBody(JsonUtils.toJson(reqData));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String ticket = objNode.get("ticket").getTextValue();
				
				req = Requester.builder()
						.setUrl("https://mp.weixin.qq.com/cgi-bin/showqrcode")
						.addUrlParm("ticket", ticket);
				
				return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
			}
			throw new WXServerApiException("faild get qr code: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	public static String createTmpResource(App app,String type,byte[] mediaContent,String mediaType,String fileName){
		try {
			HttpPost post = new HttpPost("https://api.weixin.qq.com/cgi-bin/media/upload?access_token="+app.tokenManager.getToken(TokenType.ACCESS_TOKEN)+"&type="+type);
			
			ByteArrayBody img = new ByteArrayBody(mediaContent, fileName);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("media", img);
			HttpEntity entity = builder.build();
			post.setEntity(entity);
			
			HttpResponse response = client.execute(post);
			
			if(response.getStatusLine().getStatusCode()==200){
				String respBody = null;
				try {
					respBody = EntityUtils.toString(response.getEntity());
					ObjectNode objNode = JsonUtils.readJsonObject(respBody);
					String media_id = objNode.get("media_id").getTextValue();
					return media_id;
				} catch (Exception e) {
					throw new WXServerApiException("unexpect response:" + respBody); 
				}
			}
			throw new WXServerApiException("faild upload media: " + response.getStatusLine());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	public static Map<String,Object> convertPathValueToMap(Object pathValue , String ... path){
		Map<String,Object> root = new HashMap<String,Object>();
		Map<String,Object> current = root;
		for(int i=0;i<path.length;i++){
			String key = path[i];
			if(i==path.length-1){
				current.put(key, pathValue);
			}else{
				Map<String,Object> sub = new HashMap<String,Object>();
				current.put(key,sub);
				current = sub;
			}
		}
		return root;
	}
	
	/**
	 * 获取JSAPI ticket
	 * @param app
	 * @return
	 */
	public static Token jsApiTicket(App app){
		try {
			HttpUrl httpUrl = HttpUrl.parse("https://api.weixin.qq.com/cgi-bin/ticket/getticket").newBuilder()
					.addQueryParameter("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN))
					.addQueryParameter("type", "jsapi").build();
			Response response = OkHttpUtil.requestByGet(new Request.Builder().url(httpUrl).build());
			
			if(response.code()==200){
				String json = response.body().string();
				ObjectNode jsonObj = JsonUtils.readJsonObject(json);
				if(jsonObj.has("ticket")){
					String ticket = jsonObj.get("ticket").getTextValue();
					long expires_in = jsonObj.get("expires_in").getLongValue();
					return new Token(TokenType.JSAPI_TICKET, ticket, System.currentTimeMillis() + (expires_in-60) * 1000);
				}
				throw new WXServerApiException("Invalid rsponse : " + json);
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code());
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	/**
	 * 向客户端发送消息
	 * @param app
	 * @param msg
	 */
	public static boolean send(App app,String msg){
		HttpResult result = Requester.builder().setMethod(Method.POST)
			.setUrl("https://api.weixin.qq.com/cgi-bin/message/custom/send")
			.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN))
			.setBody(msg)
			.execute();
		if(result.getCode() == 200){
			ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
			int code = objNode.get("errcode").asInt();
			System.out.println("result:"+objNode.toString());
			LOGGER.info("token:"+objNode.toString());
			return code == 0;
			
		}
		return false;
	}
	
	
	
	public static Token getCardTicket(App app){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/ticket/getticket")
					.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN))
					.addUrlParm("type", "wx_card");
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				LOGGER.debug("token:"+app.tokenManager.getToken(TokenType.ACCESS_TOKEN));
				if(objNode.has("ticket")){
					String ticket = objNode.get("ticket").getTextValue();
					long expires_in = objNode.get("expires_in").getLongValue();
					return new Token(TokenType.API_TICKET, ticket, System.currentTimeMillis() + (expires_in-60) * 1000);
				}
			}
			throw new WXServerApiException("faild get qr code: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * OAuth code 验证，获取openid
	 * @param app
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static OAuthResult oauth2(App app,String code) throws Exception{
        if(code==null){
            return null;
        }
        HttpResult result = Requester.builder().setUrl("https://api.weixin.qq.com/sns/oauth2/access_token")
        	.addUrlParm("appId",app.getId())
            .addUrlParm("secret", app.getSecret())
            .addUrlParm("code", code)
            .addUrlParm("grant_type", "authorization_code").execute();
        ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
        String json = objNode.toString();
        OAuthResult oAuthResult = JsonUtils.fromJson(json, OAuthResult.class);
        if(StringUtils.isNotBlank(oAuthResult.getErrcode())){
        	throw new WXServerApiException("oauth2 error,code:"+code+",json:"+json);
        }
        return oAuthResult;
        
    }
	
	/**
	 * 获取自定义菜单配置接口
	 * @param app
	 * @return
	 */
	public static String getCurrentSelfmenuInfo(App app,String authAppId,String authRefreshToken){
		try {
			Requester req = Requester.builder().setMethod(Method.GET)
					.setUrl("https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				LOGGER.info("json:"+json);
				return json;
			}
			throw new WXServerApiException("faild get_current_selfmenu_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 自定义菜单查询接口
	 * @param app
	 * @return
	 */
	public static String getMenuInfo(App app){
		try {
			Requester req = Requester.builder().setMethod(Method.GET)
					.setUrl("https://api.weixin.qq.com/cgi-bin/menu/get")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				LOGGER.info("json:"+json);
				return json;
			}
			throw new WXServerApiException("faild get_current_selfmenu_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 创建菜单
	 * @param app
	 * @return
	 */
	public static boolean createMenu(App app,String data){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/menu/create")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			req.setBody(data);
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				System.out.println("createMenu result:"+json);
				int code = JsonUtils.readJsonObject(json).get("errcode").asInt();
				return code == 0;
			}
			throw new WXServerApiException("faild get_current_selfmenu_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 创建菜单
	 * @param app
	 * @return
	 */
	public static boolean createSelfMenu(App app,String data){
		return createMenu(app, data, app.tokenManager.getToken(TokenType.ACCESS_TOKEN));
	}
	
	/**
	 * 创建菜单
	 * @param app
	 * @return
	 */
	public static boolean createMenu(App app,String data,String accessToken){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/menu/create")
					.addUrlParm("access_token", accessToken);
			
			req.setBody(data);
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				System.out.println("createMenu result:"+json);
				int code = JsonUtils.readJsonObject(json).get("errcode").asInt();
				return code == 0;
			}
			throw new WXServerApiException("faild get_current_selfmenu_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	
	/**
	 * 获取素材总数
	 * @param app
	 * @return
	 */
	public static String getMaterialCount(App app,String authAppId,String authRefreshToken){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/material/get_materialcount")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				LOGGER.info("json:"+json);
				return json;
			}
			throw new WXServerApiException("faild get_materialcount: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取图文消息素材
	 * @param app
	 * @return
	 */
	public static List<NewsVo> getNews(App app,int offset,int count){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/material/batchget_material")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", MaterialType.news.name());
			map.put("offset", offset);
			map.put("count", count);
			req.setBody(JsonUtils.toJson(map));
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				List<NewsVo> resutList = jsonToMaterialVoList(json);
				return resutList;
			}
			throw new WXServerApiException("faild get_materialcount: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取图文消息素材
	 * @param app
	 * @return
	 */
	public static List<NewsVo> getNewsByMediaId(App app,String mediaId){
		try {
			byte [] data = getMaterial(app, mediaId);
			ObjectNode objNode = JsonUtils.readJsonObject(data);
			String json = objNode.get("news_item").toString();
			return JsonUtils.fromJson(json, new TypeReference<List<NewsVo>>() {});
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取素材
	 * @param app
	 * @return
	 */
	public static byte[] getMaterial(App app,String mediaId){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/material/get_material")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("media_id", mediaId);
			req.setBody(JsonUtils.toJson(map));
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				return result.getRespBody();
			}
			throw new WXServerApiException("faild get_materialcount: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取素材列表
	 * @param app
	 * @return
	 */
	public static List<MaterialVo> getMaterialList(App app,String type,int offset,int count){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/material/batchget_material")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", type);
			map.put("offset", offset);
			map.put("count", count);
			req.setBody(JsonUtils.toJson(map));
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				List<MaterialVo> resutList = null;
				if(JsonUtils.readJsonObject(json).get("item") != null){
					resutList = JsonUtils.fromJson(JsonUtils.readJsonObject(json).get("item").toString(), new TypeReference<List<MaterialVo>>(){});
				}else{
					LOGGER.info("getMaterialList json:"+json);
				}
				
				return resutList;
			}
			throw new WXServerApiException("faild get_materialcount: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取图文消息
	 * @param app
	 * @return
	 */
	public static List<NewsVo> getNewsList(App app,String media_id){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/material/get_material")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("media_id", media_id);
			req.setBody(JsonUtils.toJson(map));
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				JsonNode objNode = JsonUtils.readJsonObject(result.getRespBody()).get("news_item");
				String json = objNode.toString();
				LOGGER.info("json:"+json);
				
				return JsonUtils.fromJson(json, new TypeReference<List<NewsVo>>() {});
			}
			throw new WXServerApiException("faild get_materialcount: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	private static List<NewsVo> jsonToMaterialVoList(String json){
		List<NewsVo> list = new ArrayList<NewsVo>();
		try {
//			System.out.println("materialJson:"+json);
			ArrayNode arrayNode = (ArrayNode)JsonUtils.readJsonObject(json).get("item");
			if(arrayNode != null){
				for(int i=0;i<arrayNode.size();i++){
					JsonNode jsonNode = arrayNode.get(i);
					String mediaId = jsonNode.get("media_id").asText();
					long updateTime = jsonNode.get("update_time").asLong()*1000;
					JsonNode item = jsonNode.get("content").get("news_item");
					NewsVo newsVo = new NewsVo();
					newsVo.setMedia_id(mediaId);
					newsVo.setUpdate_time(updateTime);
					List<MaterialVo> materialVos = JsonUtils.fromJson(item.toString(), new TypeReference<List<MaterialVo>>() {});
//					materialVo.setTitle(item.get("title").asText());
//					materialVo.setName(item.get("title").asText());
//					materialVo.setUrl(item.get("url").asText());
//					materialVo.setName(materialVo.getTitle());
					newsVo.setNewsList(materialVos);
					list.add(newsVo);
				}
			}else{
				LOGGER.info("jsonToMaterialVoList json:"+json);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 获取用户列表
	 * @param app
	 * @return
	 */
	public static String getUserList(App app,String authAppId,String authRefreshToken){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/cgi-bin/user/get")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				LOGGER.info("json:"+json);
				return json;
			}
			throw new WXServerApiException("faild getUserList: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取用户信息
	 * @param app
	 * @param openId
	 * @return
	 */
	public static UserInfo getSnsUserInfo(App app,String code){
		try {
			
			OAuthResult oAuthResult = oauth2(app, code);
			if(oAuthResult != null){
				Requester req = Requester.builder().setMethod(Method.GET)
						.setUrl("https://api.weixin.qq.com/sns/userinfo")
						.addUrlParm("access_token", oAuthResult.getAccess_token())
						.addUrlParm("openid", oAuthResult.getOpenid())
						.addUrlParm("lang", "zh_CN");
				
				HttpResult result = req.execute();
				if(result.getCode()==200){
					return JsonUtils.fromJson(result.getRespBody(), UserInfo.class);
				}
				throw new WXServerApiException("faild getUserList: "+result.getCode());
			}
			throw new WXServerApiException("faild getUserList: oAuthResult is null");
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取公众号的自动回复规则
	 * @param app
	 * @return
	 */
	public static String getCurrentAutoreplyInfo(App app,String authAppId,String authRefreshToken){
		try {
			Requester req = Requester.builder().setMethod(Method.GET)
					.setUrl("https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				String json = objNode.toString();
				LOGGER.info("json:"+json);
				return json;
			}
			throw new WXServerApiException("faild get_current_autoreply_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 下载多媒体文件
	 * @param app
	 * @return
	 */
	public static String downloadMedia(App app,String mediaId){
		try {
			Requester req = Requester.builder().setMethod(Method.GET)
					.setUrl("http://file.api.weixin.qq.com/cgi-bin/media/get")
					.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN))
					.addUrlParm("media_id", mediaId);
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				/*String fileType = result.getContentType().toLowerCase().replace("image/", "");
				StringBuilder sb = new StringBuilder();
				sb.append("data:image/").append(fileType).append(";base64, ").append(Base64.encodeBase64String(result.getRespBody()));
				//ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				//String json = objNode.toString();
				String data = sb.toString();
//				LOGGER.info("downloadMedia data:"+data);
				return data;*/
				String data = Base64.encodeBase64String(result.getRespBody());
				return data;
			}
			throw new WXServerApiException("faild get_current_autoreply_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	
}
