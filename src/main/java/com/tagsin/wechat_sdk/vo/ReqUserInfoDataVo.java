package com.tagsin.wechat_sdk.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tagsin.tutils.json.JsonUtils;

public class ReqUserInfoDataVo {
	
	private static final String zh_CN = "zh-CN";

	private String openid;
	
	private String lang;
	
	public ReqUserInfoDataVo() {}

	public ReqUserInfoDataVo(String openid, String lang) {
		this.openid = openid;
		this.lang = lang;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public static String toZhCNJson(List<String> openids){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("user_list", getZhCNData(openids));
		return JsonUtils.toJson(data);
	}
	
	private static List<ReqUserInfoDataVo> getZhCNData(List<String> openids){
		List<ReqUserInfoDataVo> list = new ArrayList<ReqUserInfoDataVo>();
		for(String s : openids){
			list.add(new ReqUserInfoDataVo(s, zh_CN));
		}
		return list;
	}

}
