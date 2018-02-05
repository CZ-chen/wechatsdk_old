package com.tagsin.wechat_sdk.v2;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
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
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import com.tagsin.tutils.http.HttpResult;
import com.tagsin.tutils.http.Requester;
import com.tagsin.tutils.http.Requester.Method;
import com.tagsin.tutils.json.JsonUtils;
import com.tagsin.tutils.okhttp.OkHttpUtil;
import com.tagsin.tutils.okhttp.OkHttpUtil.RequestMediaType;
import com.tagsin.wechat_sdk.App;
import com.tagsin.wechat_sdk.OAuthResult;
import com.tagsin.wechat_sdk.WXServerApiException;
import com.tagsin.wechat_sdk.WxComponentServerApi;
import com.tagsin.wechat_sdk.msg.out.OutMsgHelper;
import com.tagsin.wechat_sdk.msg.out.TemplateNews;
import com.tagsin.wechat_sdk.token.Token;
import com.tagsin.wechat_sdk.token.TokenType;
import com.tagsin.wechat_sdk.user.UserInfo;
import com.tagsin.wechat_sdk.vo.ReqUserInfoDataVo;

public abstract class BaseWxSdk {

	private static final Logger LOGGER = Logger.getLogger(BaseWxSdk.class);
	
	private static HttpClient client = HttpClientBuilder.create().build();
	
	protected abstract String getAccessToken(App app);
	
	/**
	 * 获取公众号已创建的标签
	 * @param app
	 * @return
	 */
	public String getTags(App app) throws WxSdkException{
		return doOkHttpReq(new Request.Builder().url("https://api.weixin.qq.com/cgi-bin/tags/get?access_token="+getAccessToken(app)).build());
	}
	
	/**
	 * OAuth code 验证，获取openid
	 * @param app
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public OAuthResult oauth2(App app,String code) throws Exception{
        if(code==null){
            return null;
        }
        FormBody body = new FormBody.Builder()
        .add("appId",app.getId())
        .add("secret", app.getSecret())
        .add("code", code)
        .add("grant_type", "authorization_code").build();
        String result = doOkHttpPostReq("https://api.weixin.qq.com/sns/oauth2/access_token", body);
        LOGGER.info("oauth2 response:"+result);
        return JsonUtils.fromJson(result, OAuthResult.class);
        
    }
	
	/**
	 * 
	 * @param appId
	 * @param code
	 * @param componentApp
	 * @return
	 * @throws Exception
	 */
	public OAuthResult oauth2(String appId,String code,App componentApp) throws  Exception{
		if(code==null){
	       return null;
		}
		FormBody body = new FormBody.Builder()
				.add("appid",appId)
	            .add("code", code)
	            .add("grant_type", "authorization_code")
	            .add("component_appid", componentApp.getId())
	            .add("component_access_token", componentApp.tokenManager.getToken(TokenType.COMPONENT_ACCESS_TOKEN)).build();
        String result = doOkHttpPostReq("https://api.weixin.qq.com/sns/oauth2/component/access_token", body);
        LOGGER.info("oauth2 response:"+result);
        return JsonUtils.fromJson(result, OAuthResult.class);
    }
	
	/**
	 * 获取标签下粉丝列表
	 * @param app
	 * @param tagId
	 * @param nextOpenid
	 * @return
	 * @throws WxSdkException
	 */
	public String getUsersByTag(App app,long tagId,String nextOpenid) throws WxSdkException{
		if(StringUtils.isBlank(nextOpenid)){
			nextOpenid = "";
		}
		Map<String,Object> reqData = new HashMap<String, Object>();
		reqData.put("tagid", tagId);
		reqData.put("next_openid", nextOpenid);
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), JsonUtils.toJson(reqData)));
	}
	
	/**
	 * 获取用户身上的标签列表
	 * @param app
	 * @param openid
	 * @return
	 * @throws WxSdkException
	 */
	public String getUserTagList(App app,String openid) throws WxSdkException{
		Map<String,Object> reqData = new HashMap<String, Object>();
		reqData.put("openid", openid);
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), JsonUtils.toJson(reqData)));
	}
	
	/**
	 * 获取用户列表
	 * @param app
	 * @param openid
	 * @param nextOpenId
	 * @return
	 * @throws WxSdkException
	 */
	public String getUserList(App app,String nextOpenId) throws WxSdkException{
		if(StringUtils.isBlank(nextOpenId)){
			nextOpenId = "";
		}
		Request request = new Request.Builder().url("https://api.weixin.qq.com/cgi-bin/user/get?access_token="+getAccessToken(app)+"&next_openid="+nextOpenId).build();
		return doOkHttpReq(request);
	}
	
	/**
	 * 获取用户基本信息
	 * @param app
	 * @param openid
	 * @return
	 * @throws WxSdkException
	 */
	public String getUserInfo(App app,String openid) throws WxSdkException{
		FormBody body = new FormBody.Builder()
		.add("access_token", getAccessToken(app))
		.add("openid", openid).build();
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/user/info", body);
	}
	
	/**
	 * 批量获取用户基本信息
	 * @param app
	 * @param openid
	 * @return
	 * @throws WxSdkException
	 */
	public String getBatchGetUserInfoResult(App app,List<String> openids) throws WxSdkException{
		String data = ReqUserInfoDataVo.toZhCNJson(openids);
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), data));
	}
	
	/**
	 * 批量获取用户基本信息
	 * @param app
	 * @param openid
	 * @return
	 * @throws WxSdkException
	 */
	public List<UserInfo> batchGetUserInfo(App app,List<String> openids) throws WxSdkException{
		String result = getBatchGetUserInfoResult(app, openids);
		String json = JsonUtils.readJsonObject(result).get("user_info_list").toString();
		return JsonUtils.fromJson(json,new TypeReference<List<UserInfo>>() {});
	}
	
	/**
	 * 获取素材列表
	 * @param app
	 * @param openid
	 * @return
	 * @throws WxSdkException
	 */
	public String getMaterialList(App app,String type,long offset,long count) throws WxSdkException{
		Map<String,Object> reqData = new HashMap<String, Object>();
		reqData.put("type", type);
		reqData.put("offset", offset);
		reqData.put("count", count);
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), JsonUtils.toJson(reqData)));
	}
	
	/**
	 * 获取素材总数
	 * @param app
	 * @return
	 * @throws WxSdkException
	 */
	public String getMaterialCount(App app) throws WxSdkException{
		return doOkHttpReq(new Request.Builder().url("https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token="+getAccessToken(app)).build());
	}
	
	/**
	 * 获取永久素材
	 * @param app
	 * @param mediaId
	 * @return
	 * @throws WxSdkException
	 */
	public byte[] getMaterial(App app,String mediaId) throws WxSdkException{
		Map<String,Object> reqData = new HashMap<String, Object>();
		reqData.put("media_id", mediaId);
		return doByteOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), JsonUtils.toJson(reqData)));
	}
	
	/**
	 * 高清语音素材获取接口
	 * @param app
	 * @param mediaId
	 * @return
	 * @throws WxSdkException
	 */
	public byte[] getJssdkVoice(App app,String mediaId) throws WxSdkException{
		FormBody formBody = new FormBody.Builder()
		.add("access_token", getAccessToken(app))
		.add("media_id", mediaId).build();
		return doByteOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/media/get/jssdk", formBody);
	}
	
	/**
	 * 获取临时素材
	 * @param app
	 * @param mediaId
	 * @return
	 * @throws WxSdkException
	 */
	public byte[] getTmpMaterial(App app,String mediaId) throws WxSdkException{
		FormBody formBody = new FormBody.Builder()
		.add("access_token", getAccessToken(app))
		.add("media_id", mediaId).build();
		return doByteOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/media/get", formBody);
	}
	
	/**
	 * 
	 * @param app
	 * @param msg
	 * @return
	 */
	public String getSendResult(App app,String msg){
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), msg));
	}
	
	/**
	 * 向客户端发送模板消息
	 * @param app
	 * @param openId
	 * @param templateId
	 * @param url
	 * @param lst
	 * @return
	 */
	public boolean sendTemplate(App app,String openId,String templateId,String url,List<TemplateNews> lst){
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("touser", openId);
		content.put("template_id", templateId);
		content.put("url", url);
		Map<String, Object> data = new HashMap<String, Object>();
		
		for(TemplateNews t : lst){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("color", t.getColor());
			map.put("value", t.getValue());
			data.put(t.getKey(), map);
		}
		content.put("data", data);
		return sendTemplate(app, JsonUtils.toJson(content));
	}
	
	/**
	 * 向客户端发送模板消息
	 * @param app
	 * @param msg
	 */
	public boolean sendTemplate(App app,String msg){
		try {
			Response response = OkHttpUtil.requestByPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+getAccessToken(app), RequestMediaType.JSON, msg);
			if(response.code() == 200){
				ObjectNode objNode = JsonUtils.readJsonObject(response.body().string());
				LOGGER.info("send Msg result:"+objNode.toString());
				int code = objNode.get("errcode").asInt();
				return code == 0;
			}
		} catch (Exception e) {
			LOGGER.warn(e, e);
		}
		return false;
	}
	
	/**
	 * 向客户端发送消息
	 * @param app
	 * @param msg
	 */
	public boolean send(App app,String msg){
		try {
			Response response = OkHttpUtil.requestByPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+getAccessToken(app), RequestMediaType.JSON, msg);
			if(response.code() == 200){
				ObjectNode objNode = JsonUtils.readJsonObject(response.body().string());
				LOGGER.info("send Msg result:"+objNode.toString());
				int code = objNode.get("errcode").asInt();
				return code == 0;
			}
		} catch (Exception e) {
			LOGGER.warn(e, e);
		}
		return false;
	}
	
	/**
	 * 群发消息
	 * @param app
	 * @param isToAll
	 * @param tagId
	 * @param msgType
	 * @param mediaId
	 * @return
	 * @throws WxSdkException
	 */
	public String sendAll(App app,boolean isToAll,long tagId,String msgType,String content) throws WxSdkException{
		String msg = null;
		if(msgType == "text"){
			msg = OutMsgHelper.createSendallText(isToAll, tagId,msgType, content);
		}else{
			msg = OutMsgHelper.createSendallMaterial(isToAll, tagId,msgType, content);
		}
		if(msg == null){
			throw new WxSdkException("msg is null");
		}
		LOGGER.info("sendAll msg:"+msg);
		return sendAll(app, msg);
	}
	
	/***
	 * 群发消息
	 * @param app
	 * @param msg
	 * @return
	 * @throws WxSdkException
	 */
	public String sendAll(App app,String msg) throws WxSdkException{
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), msg));
	}
	
	public String getQrCode(App app,String scene_str){
		Map<String,Object> reqData = convertPathValueToMap(scene_str, "action_info","scene","scene_str");
		reqData.put("action_name", "QR_LIMIT_STR_SCENE");
		return getQrCodeResult(app, JsonUtils.toJson(reqData));
	}
	
	public String getQrCodeUrl(App app,String scene_str){
		Map<String,Object> reqData = convertPathValueToMap(scene_str, "action_info","scene","scene_str");
		reqData.put("action_name", "QR_LIMIT_STR_SCENE");
		String ticket = getQrCodeTicket(app, JsonUtils.toJson(reqData));
		return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
	}
	
	public String getQrCode(App app,long scene_id,long expireSeconds){
					
		Map<String,Object> reqData = convertPathValueToMap(scene_id, "action_info","scene","scene_id");
		if(expireSeconds>0){
			reqData.put("action_name", "QR_SCENE");
			reqData.put("expire_seconds",expireSeconds);
		}else{
			reqData.put("action_name", "QR_LIMIT_SCENE");
		}
		
		return getQrCodeResult(app, JsonUtils.toJson(reqData));
	}
	
	public byte[] getWxNativeQrCode(App app,long scene_id,long expireSeconds){
		
		Map<String,Object> reqData = convertPathValueToMap(scene_id, "action_info","scene","scene_id");
		if(expireSeconds>0){
			reqData.put("action_name", "QR_SCENE");
			reqData.put("expire_seconds",expireSeconds);
		}else{
			reqData.put("action_name", "QR_LIMIT_SCENE");
		}
		
		return getQrCodeResult(app, JsonUtils.toJson(reqData),false);
	}
	
	public String createQrCode(App app,long scene_id,long expireSeconds){
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), buildQrcodeReqBody(scene_id, expireSeconds)));
	}
	
	public String createQrCode(App app,String scene_str){
		return doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), buildQrcodeStrReqBody(scene_str)));
	}
	
	private String buildQrcodeReqBody(long scene_id,long expireSeconds){
		Map<String,Object> reqData = convertPathValueToMap(scene_id, "action_info","scene","scene_id");
		if(expireSeconds>0){
			reqData.put("action_name", "QR_SCENE");
			reqData.put("expire_seconds",expireSeconds);
		}else{
			reqData.put("action_name", "QR_LIMIT_SCENE");
		}
		return JsonUtils.toJson(reqData);
	}
	
	private String buildQrcodeStrReqBody(String scene_str){
		Map<String,Object> reqData = convertPathValueToMap(scene_str, "action_info","scene","scene_str");
		reqData.put("action_name", "QR_LIMIT_STR_SCENE");
		return JsonUtils.toJson(reqData);
	}
	
	public String getQrCodeTicket(App app,String reqJson){
		try {
			String data = doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), reqJson));
			ObjectNode objNode = JsonUtils.readJsonObject(data);
			if(objNode.get("ticket") == null){
				throw new WxSdkException("getQrCodeResult data:"+data);
			}
			return objNode.get("ticket").getTextValue();
		} catch (Exception e) {
			throw new WxSdkException(e.getMessage(),e);
		}
	}
	
	public String getQrCodeResult(App app,String reqJson){
		try {
			return new String(getQrCodeResult(app, reqJson, true));
		} catch (Exception e) {
			throw new WxSdkException(e.getMessage(),e);
		}
	}
	
	public byte[] getQrCodeResult(App app,String reqJson,boolean encodeFlag){
		try {
			String ticket = getQrCodeTicket(app, reqJson);
			Request request = new Request.Builder().url("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket).build();
			Response response = OkHttpUtil.requestByPost(request);
			if(response.code() == 200){
				if(encodeFlag){
					String fileType = response.body().contentType().subtype();
					LOGGER.info("fileType:"+fileType);
					byte [] resultData = response.body().bytes();
					StringBuilder sb = new StringBuilder();
					sb.append("data:image/").append(fileType).append(";base64, ").append(Base64.encodeBase64String(resultData));
					return sb.toString().getBytes();
				}
			}
			throw new WxSdkException("getQrCode result code :"+response.code());
		} catch (Exception e) {
			throw new WxSdkException(e.getMessage(),e);
		}
	}
	
	public String getQrCodeUrl(App app,long scene_id,long expireSeconds) throws WxSdkException{
		try {
			Map<String,Object> reqData = convertPathValueToMap(scene_id, "action_info","scene","scene_id");
			if(expireSeconds>0){
				reqData.put("action_name", "QR_SCENE");
				reqData.put("expire_seconds",expireSeconds);
			}else{
				reqData.put("action_name", "QR_LIMIT_SCENE");
			}
			String reqJson = JsonUtils.toJson(reqData);
			String result = doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+getAccessToken(app), RequestBody.create(RequestMediaType.JSON.getMediaType(), reqJson));
			ObjectNode objNode = JsonUtils.readJsonObject(result);
			if(objNode.has("ticket")){
				String ticket = objNode.get("ticket").getTextValue();
				
				/*req = Requester.builder()
						.setUrl("https://mp.weixin.qq.com/cgi-bin/showqrcode")
						.addUrlParm("ticket", ticket);*/
				
				return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
			}else{
				throw new WxSdkException("getQrCodeUrl error reqJson :"+reqJson+" result:"+objNode.toString());
			}
			
		} catch (Exception e) {
			throw new WxSdkException(e.getMessage(),e);
		}
	}
	
	/**
	 * 添加图片素材
	 * @param app
	 * @return
	 */
	public String addMaterial(App app,File imgFile){
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
	 * 添加图片素材
	 * @param app
	 * @param imgData
	 * @param fileName
	 * @return
	 */
	public String addMaterial(App app,byte [] imgData,String fileName){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN)+
					"&type=image";
			
			MultipartBody.Builder builderBody = new MultipartBody.Builder()
	        .setType(MultipartBody.FORM);
	       
			RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), imgData);
			builderBody.addFormDataPart("media", fileName,fileBody);
			Response response = OkHttpUtil.requestByPost(new Request.Builder().url(url).post(builderBody.build()).build());
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
	
	public String addTmpMaterial(App app,String mediaType,byte[] mediaContent,String fileName){
		try {
			String url ="https://api.weixin.qq.com/cgi-bin/media/upload?access_token="+getAccessToken(app)+"&type="+mediaType;
			MultipartBody.Builder builderBody = new MultipartBody.Builder()
	        .setType(MultipartBody.FORM);
	       
			RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), mediaContent);
			builderBody.addFormDataPart("media", fileName,fileBody);
			Response response = OkHttpUtil.requestByPost(new Request.Builder().url(url).post(builderBody.build()).build());
			if(response.code() == 200){
				ObjectNode objectNode = JsonUtils.readJsonObject(response.body().string());
				return objectNode.get("media_id").asText();
			}else{
				throw new WXServerApiException("Invalid statu code : " + response.code() + " , url : " + url);
			}
			
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 创建菜单
	 * @param app
	 * @return
	 */
	public boolean createMenu(App app,String data){
		try {
			
			String json = doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN), RequestBody.create(RequestMediaType.JSON.getMediaType(), data));
			LOGGER.info("createMenu result:"+json);
			int code = JsonUtils.readJsonObject(json).get("errcode").asInt();
			return code == 0;
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 自定义菜单查询接口
	 * @param app
	 * @return
	 */
	public String getMenuInfo(App app){
		try {
			String json = doOkHttpReq(new Request.Builder().url("https://api.weixin.qq.com/cgi-bin/menu/get?access_token="+app.tokenManager.getAuthToken(TokenType.AUTHORIZER_ACCESS_TOKEN)).build());
			LOGGER.info("json:"+json);
			return json;
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取JSAPI ticket
	 * @param app
	 * @return
	 */
	public Token jsApiTicket(App app){
		FormBody formBody = new FormBody.Builder()
				.add("access_token", getAccessToken(app))
				.add("type", "jsapi").build();
		String json = doOkHttpPostReq("https://api.weixin.qq.com/cgi-bin/ticket/getticket", formBody);
		
		ObjectNode jsonObj = JsonUtils.readJsonObject(json);
		if(jsonObj.has("ticket")){
			String ticket = jsonObj.get("ticket").getTextValue();
			long expires_in = jsonObj.get("expires_in").getLongValue();
			return new Token(TokenType.JSAPI_TICKET, ticket, System.currentTimeMillis() + (expires_in-60) * 1000);
		}
		throw new WXServerApiException("Invalid rsponse : " + json);
	}
	
	/**
	 * 下载多媒体文件
	 * @param app
	 * @return
	 */
	public byte[] downloadMedia(App app,String mediaId){
		try {
			FormBody formBody = new FormBody.Builder()
					.add("access_token", getAccessToken(app))
					.add("media_id", mediaId).build();
			return doByteOkHttpPostReq("http://file.api.weixin.qq.com/cgi-bin/media/get", formBody);
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	public String doReq(Requester req) throws WxSdkException{
		try {
			HttpResult result = req.execute();
			if(result.getCode()==200){
				return new String(result.getRespBody(),"utf-8");
			}
			throw new WxSdkException("code:"+result.getCode()+", url:"+req.getUrl());
		} catch (Exception e) {
			throw new WxSdkException(e.getMessage(),e);
		}
	}
	
	public String doOkHttpPostReq(String url,RequestBody body) throws WxSdkException{
		return doOkHttpReq(new Request.Builder().url(url).post(body).build());
	}
	
	public String doOkHttpReq(Request request) throws WxSdkException{
		String result = new String(doByteOkHttpReq(request));
		String out = result;
		if(result.length() > 100){
			out = out.substring(0, 100);
		}
		LOGGER.info("response:"+out);
		return result;
	}
	
	public byte[] doByteOkHttpPostReq(String url,RequestBody body) throws WxSdkException{
		return doByteOkHttpReq(new Request.Builder().url(url).post(body).build());
	}
	
	public byte[] doByteOkHttpReq(Request request) throws WxSdkException{
		try {
			String url = request.url().encodedPath();
			Response response = OkHttpUtil.requestByPost(request);
			if(response.code() == 200){
				return response.body().bytes();
			}
			throw new WxSdkException("url:"+url);
		} catch (Exception e) {
			throw new WxSdkException(e.getMessage(),e);
		}
	}
	
	private Map<String,Object> convertPathValueToMap(Object pathValue , String ... path){
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
}
