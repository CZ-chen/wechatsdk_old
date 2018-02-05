package com.tagsin.wechat_sdk.token;

import java.util.HashMap;
import java.util.Map;

public class MemTokenStore implements TokenStore {
	private static final Map<String,Token> tokens = new HashMap<String,Token>();

	public String getToken(TokenType tokenType, String appid) {
		Token token = tokens.get(getTokenKey(tokenType, appid));
		if(token!=null && !token.isExpired()){
			return token.getToken();
		}
		return null;
	}

	public void saveToken(Token token, String appid) {
		if(token!=null){
			tokens.put(getTokenKey(token.getType(),appid), token);
		}
	}
	
	private String getTokenKey(TokenType tokenType,String appid){
		return tokenType.name()+"."+appid;
	}

}
