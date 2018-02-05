package com.tagsin.wechat_sdk.vo;

import java.util.List;

public class ArticleStatVo {

	private String errcode;
	
	private String errmsg;
	
	private String title;
	
	private List<ArticleStatDetailVo> list;
	
	public ArticleStatVo(){}
	
	public ArticleStatVo(String title,List<ArticleStatDetailVo> list){
		this.title = title;
		this.list = list;
	}

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ArticleStatDetailVo> getList() {
		return list;
	}

	public void setList(List<ArticleStatDetailVo> list) {
		this.list = list;
	}

}
