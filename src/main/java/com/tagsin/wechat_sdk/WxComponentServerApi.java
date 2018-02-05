package com.tagsin.wechat_sdk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

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
import com.tagsin.tutils.okhttp.OkHttpUtil.RequestMediaType;
import com.tagsin.wechat_sdk.token.Token;
import com.tagsin.wechat_sdk.token.TokenType;
import com.tagsin.wechat_sdk.user.UserInfo;
import com.tagsin.wechat_sdk.v2.BaseWxSdk;
import com.tagsin.wechat_sdk.v2.WxComponentSdk;
import com.tagsin.wechat_sdk.vo.ArticleStatDetailVo;
import com.tagsin.wechat_sdk.vo.ArticleStatVo;
import com.tagsin.wechat_sdk.vo.AuthInfo;

public class WxComponentServerApi {
	
	private static final Logger LOGGER = Logger.getLogger(WxComponentServerApi.class);
	
	private static BaseWxSdk baseWxSdk = new WxComponentSdk();
	
	public static BaseWxSdk getBaseWxSdk(){
		return baseWxSdk;
	}

	/**
	 * 获取ComponentAccessToken
	 * @param app
	 * @return
	 */
	public static synchronized Token accessToken(App app){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("component_appid", app.getId());
			params.put("component_appsecret", app.getSecret());
			params.put("component_verify_ticket", app.getComponentTicket());
			Response response = OkHttpUtil.requestByPost(url, RequestMediaType.JSON, JsonUtils.toJson(params));
			if(response.code() == 200){
				
				ObjectNode jsonObj = JsonUtils.readJsonObject(response.body().string());
				LOGGER.info("component_access_token:"+jsonObj.toString());
				String access_token = jsonObj.get("component_access_token").getTextValue();
				long expires_in = jsonObj.get("expires_in").getLongValue();
				return new Token(TokenType.COMPONENT_ACCESS_TOKEN, access_token, System.currentTimeMillis() + (expires_in-60) * 1000);
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	/**
	 * OAuth code 验证，获取openid
	 * @param app
	 * @param code
	 * @return
	 * @throws Exception
	 */
	/*public static OAuthResult oauth2(String appId,String code,App componentApp) throws  Exception{
        try {
			if(code==null){
			    return null;
			}
			String url = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid="+appId+
					"&code="+code+
					"&grant_type=authorization_code"+
					"&component_appid="+componentApp.getId()+
					"&component_access_token="+componentApp.tokenManager.getToken(TokenType.COMPONENT_ACCESS_TOKEN);;
			Response response = OkHttpUtil.requestByGet(url);
			System.out.println("oauth2 response:"+response.body().string());
			if(response.code() == 200){
				String result = response.body().string();
				return JsonUtils.fromJson(result, OAuthResult.class);
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
			
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
        
    }*/
	
	public static OAuthResult oauth2(String appId,String code,App componentApp) throws  Exception{
		if(code==null){
	       return null;
		}
        HttpResult result = Requester.builder().setUrl("https://api.weixin.qq.com/sns/oauth2/component/access_token")
        	.addUrlParm("appid",appId)
            .addUrlParm("code", code)
            .addUrlParm("grant_type", "authorization_code")
            .addUrlParm("component_appid", componentApp.getId())
            .addUrlParm("component_access_token", componentApp.tokenManager.getToken(TokenType.COMPONENT_ACCESS_TOKEN)).execute();
        ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
        System.out.println("oauth2 response:"+objNode.toString());
        return JsonUtils.fromJson(result.getRespBody(), OAuthResult.class);
    }
	
	/**
	 * 获取preAuthCode
	 * @param app
	 * @return
	 */
	public static String getPreAuthCode(App app){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token="+app.tokenManager.getToken(TokenType.COMPONENT_ACCESS_TOKEN);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("component_appid", app.getId());
			Response response = OkHttpUtil.requestByPost(url, RequestMediaType.JSON, JsonUtils.toJson(params));
			if(response.code() == 200){
				ObjectNode jsonObj = JsonUtils.readJsonObject(response.body().string());
				String pre_auth_code = jsonObj.get("pre_auth_code").getTextValue();
				long expires_in = jsonObj.get("expires_in").getLongValue();
				return pre_auth_code;
				//return new Token(TokenType.PRE_AUTH_CODE, pre_auth_code, System.currentTimeMillis() + (expires_in-5) * 1000);
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	/**
	 * 获取授权信息
	 * @param app
	 * @return
	 */
	public static AuthInfo queryAuthByAuthCode(App app,String authCode){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+app.tokenManager.getToken(TokenType.COMPONENT_ACCESS_TOKEN);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("component_appid", app.getId());
			params.put("authorization_code", authCode);
			Response response = OkHttpUtil.requestByPost(url, RequestMediaType.JSON, JsonUtils.toJson(params));
			if(response.code() == 200){
				ObjectNode jsonObj = JsonUtils.readJsonObject(response.body().string());
				LOGGER.info("queryAuthByAuthCode authorization_info:"+jsonObj.toString());
				String json = jsonObj.get("authorization_info").toString();
				
				AuthInfo authInfo = JsonUtils.fromJson(json, AuthInfo.class);
				return authInfo;
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	/**
	 * 获取授权方的公众号帐号基本信息
	 * @param app
	 * @return
	 */
	public static String getAuthInfo(App app,String authAppId){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="+app.tokenManager.getToken(TokenType.COMPONENT_ACCESS_TOKEN);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("component_appid", app.getId());
			params.put("authorizer_appid", authAppId);
			Response response = OkHttpUtil.requestByPost(url, RequestMediaType.JSON, JsonUtils.toJson(params));
			if(response.code() == 200){
				return response.body().string();
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	/**
	 * 获取（刷新）授权公众号的接口调用凭据（令牌）
	 * @param app
	 * @return
	 */
	public static synchronized Token getAuthAccessToken(App app,String authAppId,String authRefreshToken){
		try {
			//更新component token
			Token componentAccessToken = accessToken(app);
			app.tokenManager.getTokenStore().saveToken(componentAccessToken, app.getId());
			String url = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="+componentAccessToken.getToken();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("component_appid", app.getId());
			params.put("authorizer_appid", authAppId);
			params.put("authorizer_refresh_token", authRefreshToken);
			Response response = OkHttpUtil.requestByPost(url, RequestMediaType.JSON, JsonUtils.toJson(params));
			if(response.code() == 200){
				String result = response.body().string();
				LOGGER.info("authAppId:" + authAppId + ", getAuthAccessToken:"+result);
				ObjectNode jsonObj = JsonUtils.readJsonObject(result);
				String authorizer_access_token = jsonObj.get("authorizer_access_token").getTextValue();
				long expires_in = jsonObj.get("expires_in").getLongValue();
				return new Token(TokenType.AUTHORIZER_ACCESS_TOKEN, authorizer_access_token, System.currentTimeMillis() + (expires_in-60) * 1000);
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	public static UserInfo getUserInfo(App app,String openid){
		try {
			Requester req = Requester.builder()
					.setUrl("https://api.weixin.qq.com/cgi-bin/user/info")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN))
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
	
	/**
	 * 获取用户总数
	 * @param app
	 * @return
	 */
	public static long getTotalUser(App app){
		String data = null;
		try {
			Response response = OkHttpUtil.requestByGet("https://api.weixin.qq.com/cgi-bin/user/get?access_token="+app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			data = response.body().string();
			ObjectNode objNode = JsonUtils.readJsonObject(data);
			return objNode.get("total").asLong();
		} catch (Exception e) {
			LOGGER.info("json:"+data);
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 * @param app
	 * @param queryAuthCode
	 * @param msg
	 */
	public static boolean send(App app,String queryAuthCode,String msg){
		
		try {
			AuthInfo authInfo = queryAuthByAuthCode(app, queryAuthCode);
			String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+authInfo.getAuthorizer_access_token();
			Response response = OkHttpUtil.requestByPost(url, RequestMediaType.JSON, msg);
			int code = response.code();
			if(code==200){
				ObjectNode objNode = JsonUtils.readJsonObject(response.body().string());
				String json = objNode.toString();
				LOGGER.info("send return json:"+json);
				return objNode.get("errcode").asInt() == 0;
			}
			throw new WXServerApiException("faild send: " + code);
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
		
	}
	
	/**
	 * 向客户端发送消息
	 * @param app
	 * @param msg
	 */
	public static String getSendResult(App app,String msg){
		HttpResult result = Requester.builder().setMethod(Method.POST)
			.setUrl("https://api.weixin.qq.com/cgi-bin/message/custom/send")
			.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN))
			.setBody(msg)
			.execute();
		try {
			return new String(result.getRespBody(),"utf-8");
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage());
		}
	}
	
	/**
	 * 添加图片素材
	 * @param app
	 * @return
	 */
	public static String addMaterial(App app,File imgFile){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN)+
					"&type=image";
			Map<String, File> fileMap = new HashMap<String, File>();
			fileMap.put("media", imgFile);
			Response response = OkHttpUtil.requestUploadFileWithForm(url, fileMap);
			if(response.code() == 200){
				ObjectNode objectNode = JsonUtils.readJsonObject(response.body().string());
				return objectNode.get("media_id").asText();
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
		} catch (Exception e) {
			throw new WXServerApiException(e);
		}
	}
	
	/**
	 * 预览接口【订阅号与服务号认证后均可用】
	 * @param app content
	 * @return
	 */
	public static boolean preview(App app,String content){
		try {
			Requester req = Requester.builder().setMethod(Method.GET)
					.setUrl("https://api.weixin.qq.com/cgi-bin/message/mass/preview")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objNode = JsonUtils.readJsonObject(result.getRespBody());
				int code = objNode.get("errcode").asInt();
				if(code == 0){
					return true;
				}else{
					System.out.println("json:"+objNode.toString());
					LOGGER.info("json:"+objNode.toString());
				}
				return false;
			}
			throw new WXServerApiException("faild get_current_autoreply_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取图文群发每日数据
	 * @param app
	 * @return
	 */
	public static String getArticleSummary(App app,String beginDate,String endDate){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/datacube/getarticlesummary")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("begin_date", beginDate);
			map.put("end_date", endDate);
			req.setBody(JsonUtils.toJson(map));
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				return new String(result.getRespBody(),"utf-8");
			}
			throw new WXServerApiException("faild get_current_autoreply_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取图文群发总数据
	 * @param app
	 * @return
	 */
	public static List<ArticleStatVo> getArticleTotal(App app,String beginDate,String endDate){
		try {
			Requester req = Requester.builder().setMethod(Method.POST)
					.setUrl("https://api.weixin.qq.com/datacube/getarticletotal")
					.addUrlParm("access_token", app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN));
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("begin_date", beginDate);
			map.put("end_date", endDate);
			req.setBody(JsonUtils.toJson(map));
			HttpResult result = req.execute();
			
			if(result.getCode()==200){
				ObjectNode objectNode = JsonUtils.readJsonObject(result.getRespBody());
				if(objectNode.get("errcode") != null){
					throw new WXServerApiException(objectNode.get("errcode").asText());
				}
				List<ArticleStatVo> articleStatVoList = new ArrayList<ArticleStatVo>();
				ArrayNode arrayNode = (ArrayNode)objectNode.get("list");
				if(arrayNode != null){
					for(int i=0;i<arrayNode.size();i++){
						JsonNode jsonNode = arrayNode.get(i);
						String title = jsonNode.get("title").asText();
						String details = jsonNode.get("details").toString();
						List<ArticleStatDetailVo> list = JsonUtils.fromJson(details, new TypeReference<List<ArticleStatDetailVo>>() {});
						articleStatVoList.add(new ArticleStatVo(title,list));
					}
				}
				
				return articleStatVoList;
			}
			throw new WXServerApiException("faild get_current_autoreply_info: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}

}
