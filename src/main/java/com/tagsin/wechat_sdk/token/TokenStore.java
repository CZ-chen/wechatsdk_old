package com.tagsin.wechat_sdk.token;

public interface TokenStore {
	public String getToken(TokenType tokenType, String appid);
	public void saveToken(Token token, String appid);
}
