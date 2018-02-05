package com.tagsin.wechat_sdk.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;


public class MaterialVo {
	
	

	private String media_id;
	
	private String title;
	
	private String content;
	
	private String author;
	
	private String digest;
	
	private String content_source_url;
	
	private String thumb_media_id;
	
	private Integer show_cover_pic;
	
	private String thumb_url;
	
	private String name;
	
	private String url;
	
	private Integer idx;
	
	private Date update_time;

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getContent_source_url() {
		return content_source_url;
	}

	public void setContent_source_url(String content_source_url) {
		this.content_source_url = content_source_url;
	}

	public String getThumb_media_id() {
		return thumb_media_id;
	}

	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}

	public Integer getShow_cover_pic() {
		return show_cover_pic;
	}

	public void setShow_cover_pic(Integer show_cover_pic) {
		this.show_cover_pic = show_cover_pic;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	@JsonIgnore
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	
}
