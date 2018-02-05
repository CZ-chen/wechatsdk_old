package com.tagsin.wechat_sdk.token;

public class Token {
	private TokenType type;
	private String token;
	private long expireAt;
	
	public Token(TokenType type, String token, long expireAt) {
		this.type = type;
		this.token = token;
		this.expireAt = expireAt;
	}
	
	public boolean isExpired(){
		return System.currentTimeMillis() > expireAt; 
	}
	
	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(long expireAt) {
		this.expireAt = expireAt;
	}
}
