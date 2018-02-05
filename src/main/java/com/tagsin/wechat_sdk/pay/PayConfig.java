package com.tagsin.wechat_sdk.pay;

public class PayConfig {
    private String appid;
    private String mch_id;
    private String api_key;
    private String notify_url;
    
    /**
     * 发送红包的ip 
     */
    private String redpack_client_ip;
    /**
     * 这个密钥文件需要从微信支付平台上下载
     */
    private String p12_apiclient_cert_file;
    
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getApi_key() {
		return api_key;
	}
	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getP12_apiclient_cert_file() {
		return p12_apiclient_cert_file;
	}
	public void setP12_apiclient_cert_file(String p12_apiclient_cert_file) {
		this.p12_apiclient_cert_file = p12_apiclient_cert_file;
	}
	public String getRedpack_client_ip() {
		return redpack_client_ip;
	}
	public void setRedpack_client_ip(String redpack_client_ip) {
		this.redpack_client_ip = redpack_client_ip;
	}
}
