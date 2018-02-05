package com.tagsin.wechat_sdk.msg.out;

public class News {
	
	public News(String title, String description, String picurl, String url) {
		this.title = title;
		this.description = description;
		this.picurl = picurl;
		this.url = url;
	}
	
	public News(){}

	/**
	 * 标题
	 */
	public String title;
	
	/**
	 * 图文消息描述
	 */
	public String description;
	
	/**
	 *	图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200 
	 */
	public String picurl;
	
	/**
	 * 点击图文消息跳转链接
	 */
	public String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
