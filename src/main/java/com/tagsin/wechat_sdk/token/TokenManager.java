package com.tagsin.wechat_sdk.token;

import com.tagsin.wechat_sdk.App;
import com.tagsin.wechat_sdk.WXServerApi;
import com.tagsin.wechat_sdk.WxComponentServerApi;

public class TokenManager {
	private TokenStore tokenStore = new MemTokenStore();
	
	private App app;
	public TokenManager(App app){
		this.app = app;
	}
	
	public String getToken(TokenType tokenType){
		String token = tokenStore.getToken(tokenType, app.getId());
		if(token==null){
			token = updateToken(tokenType);
		}
		return token;
	}
	
	public String getComponentToken(TokenType tokenType){
		String key = new StringBuffer().append(app.getId()).append("_").append(app.getAuthAppId()).toString();
		String token = tokenStore.getToken(tokenType, key);
		if(token==null){
			token = updateComponentToken(tokenType,key);
		}
		return token;
	}
	
	public String getAuthToken(TokenType tokenType){
		String key = app.getId() + "_" + app.getAuthAppId();
		String token = tokenStore.getToken(tokenType, key);
		if(token==null){
			Token accessToken = WxComponentServerApi.getAuthAccessToken(app, app.getAuthAppId(), app.getAuthRefreshToken());
			tokenStore.saveToken(accessToken, key);
			return accessToken.getToken();
		}
		return token;
	}
	
	public String flushAuthToken(TokenType tokenType){
		String key = app.getId() + "_" + app.getAuthAppId();
		Token accessToken = WxComponentServerApi.getAuthAccessToken(app, app.getAuthAppId(), app.getAuthRefreshToken());
		tokenStore.saveToken(accessToken, key);
		return accessToken.getToken();
	}
	
	public String updateToken(TokenType tokenType) {
		Token token =null; 
		switch (tokenType) {
			case ACCESS_TOKEN:
				token = WXServerApi.accessToken(app);
				break;
			case JSAPI_TICKET:
				token = WXServerApi.jsApiTicket(app);
				break;
			case API_TICKET:
				token = WXServerApi.getCardTicket(app);
				break;
			case COMPONENT_ACCESS_TOKEN:
				token = WxComponentServerApi.accessToken(app);
				break;
			default:
				token = null;
		}
		if(token!=null){
			tokenStore.saveToken(token, app.getId());
			return token.getToken();
		}
		return null;
	}
	
	public String updateComponentToken(TokenType tokenType,String key) {
		Token token =null; 
		switch (tokenType) {
			case JSAPI_TICKET:
				token = WxComponentServerApi.getBaseWxSdk().jsApiTicket(app);
				break;
			default:
				token = null;
		}
		if(token!=null){
			tokenStore.saveToken(token, key);
			return token.getToken();
		}
		return null;
	}

	public TokenStore getTokenStore() {
		return tokenStore;
	}

	public void setTokenStore(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}
}
