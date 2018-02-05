package com.tagsin.wechat_sdk.vo;

import java.util.List;
import java.util.Map;

public class AuthInfo {

	private String authorizer_appid;
	private String authorizer_access_token;
	private String expires_in;
	private String authorizer_refresh_token;
	private List<Map<String, Object>> func_info;
	public String getAuthorizer_appid() {
		return authorizer_appid;
	}
	public void setAuthorizer_appid(String authorizer_appid) {
		this.authorizer_appid = authorizer_appid;
	}
	public String getAuthorizer_access_token() {
		return authorizer_access_token;
	}
	public void setAuthorizer_access_token(String authorizer_access_token) {
		this.authorizer_access_token = authorizer_access_token;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getAuthorizer_refresh_token() {
		return authorizer_refresh_token;
	}
	public void setAuthorizer_refresh_token(String authorizer_refresh_token) {
		this.authorizer_refresh_token = authorizer_refresh_token;
	}
	public List<Map<String, Object>> getFunc_info() {
		return func_info;
	}
	public void setFunc_info(List<Map<String, Object>> func_info) {
		this.func_info = func_info;
	}
	

}
