package com.tagsin.wechat_sdk.msg.out;

import java.util.HashMap;
import java.util.Map;

public class TemplateNews {

	private String key;
	
	private String value;
	
	private String color = "#000000";
	
	public TemplateNews(){
		
	}
	
	public TemplateNews(String key,String value){
		this.key = key;
		this.value = value;
	}
	
	public TemplateNews(String key,String value,String color){
		this.key = key;
		this.value = value;
		this.color = color;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
