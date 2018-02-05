package com.tagsin.wechat_sdk;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tagsin.tutils.codec.SHA1;
import com.tagsin.tutils.http.UrlUtils;
import com.tagsin.wechat_sdk.token.TokenManager;
import com.tagsin.wechat_sdk.token.TokenType;
import com.tagsin.wechat_sdk.util.RandomStringGenerator;


public class App {
	private static Logger logger = Logger.getLogger(App.class);
	private String id;
	private String wxId;
	private String name;
	private String secret;
	private String msgAesKey;
	private String componentTicket;
	private String authAppId;
	private String authRefreshToken;
	
	public final TokenManager tokenManager = new TokenManager(this);
	
	public App(String appId,String appSecret){
		this.id = appId;
		this.secret = appSecret;
	}
	
	public String getOauthRedirectUrl(String callBackUrl,String state){
		StringBuilder oauthUrl = new StringBuilder("https://open.weixin.qq.com/connect/oauth2/authorize?");
		oauthUrl.append("appid=").append(id);
		oauthUrl.append("&redirect_uri=").append(UrlUtils.encode(callBackUrl));
		oauthUrl.append("&response_type=code&scope=snsapi_base&state=").append(state).append("#wechat_redirect");
		return oauthUrl.toString();
	}
	
	public Map<String,String> getJSAPIConf(String url){
    	Map<String,String> config = new HashMap<String,String>();
    	config.put("appId",id);
    	config.put("nonceStr", RandomStringGenerator.getRandomStringByLength(12));
    	config.put("timestamp", Long.toString(System.currentTimeMillis() / 1000));
    	setJSAPISignature(config,url);
        return config;
    }
	
	public Map<String,String> getComponentJSAPIConf(String url){
    	Map<String, String> config = getJSAPIConf(url, authAppId);
    	config.put("signature", getComponentJSAPISignature(tokenManager.getComponentToken(TokenType.JSAPI_TICKET), config.get("nonceStr"), config.get("timestamp"), url));
    	return config;
	}
	
	public Map<String,String> getJSAPIConf(String url,String appId){
    	Map<String,String> config = new HashMap<String,String>();
    	config.put("appId",appId);
    	config.put("nonceStr", RandomStringGenerator.getRandomStringByLength(12));
    	config.put("timestamp", Long.toString(System.currentTimeMillis() / 1000));
    	
        return config;
    }
	
	 private void setJSAPISignature(Map<String,String> config, String url) {
        try
        {
        	//注意这里参数名必须全部小写，且必须有序
            String string1 = "jsapi_ticket=" + tokenManager.getToken(TokenType.JSAPI_TICKET) +
                    "&noncestr=" + config.get("nonceStr") +
                    "&timestamp=" + config.get("timestamp") +
                    "&url=" + url;
            logger.info("JSPAI sign str1 : " + string1);
            config.put("signature", SHA1.encode(string1));
        } catch (Exception e) {
            logger.warn(e,e);
        }
    }
	 
	 private String getComponentJSAPISignature(String jsapiTicket,String nonceStr,String timestamp, String url) {
        try
        {
        	//注意这里参数名必须全部小写，且必须有序
            String string1 = "jsapi_ticket=" + jsapiTicket +
                    "&noncestr=" + nonceStr +
                    "&timestamp=" +timestamp +
                    "&url=" + url;
            logger.info("JSPAI sign str1 : " + string1);
            return SHA1.encode(string1);
//	            config.put("signature", SHA1.encode(string1));
        } catch (Exception e) {
            logger.warn(e,e);
        }
        return null;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWxId() {
		return wxId;
	}
	public void setWxId(String wxId) {
		this.wxId = wxId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getMsgAesKey() {
		return msgAesKey;
	}
	public void setMsgAesKey(String msgAesKey) {
		this.msgAesKey = msgAesKey;
	}

	public String getComponentTicket() {
		return componentTicket;
	}

	public void setComponentTicket(String componentTicket) {
		this.componentTicket = componentTicket;
	}

	public String getAuthAppId() {
		return authAppId;
	}

	public void setAuthAppId(String authAppId) {
		this.authAppId = authAppId;
	}

	public String getAuthRefreshToken() {
		return authRefreshToken;
	}

	public void setAuthRefreshToken(String authRefreshToken) {
		this.authRefreshToken = authRefreshToken;
	}
	
}
