package com.tagsin.wechat_sdk.pay.vo;

import java.util.Date;

public class TradeInfo {
	public static final String STAT_PAING = "PAING";
	public static final String STAT_PAIED = "PAIED";
	public static final String STAT_FAILD = "FAILD";
	
	public static final String JSAPI = "JSAPI";
	public static final String NATIVE = "NATIVE";
	
	private String id;
	private String state;
	private String summary;
	private Long total_fee;
	private String trade_type;
	private String user_id;
	private String openid;
	private Date create_time;
	private String code_url;
	private String prepay_id;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public void setTotal_fee(Long total_fee) {
		this.total_fee = total_fee;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public long getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(long total_fee) {
		this.total_fee = total_fee;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getCode_url() {
		return code_url;
	}
	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}
	public String getPrepay_id() {
		return prepay_id;
	}
	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
}
