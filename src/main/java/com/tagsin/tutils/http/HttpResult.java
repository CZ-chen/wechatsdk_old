package com.tagsin.tutils.http;

import com.tagsin.tutils.paser.Parser;
import com.tagsin.tutils.paser.Parsers;

public class HttpResult {
	private int code;
	private Exception error;
	private byte[] respBody;
	private String contentType;
	
	public <T>T parse(Parser<byte[],T> parser){
		return parser.pase(respBody);
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Exception getError() {
		return error;
	}
	public void setError(Exception error) {
		this.error = error;
	}
	public byte[] getRespBody() {
		return respBody;
	}
	public void setRespBody(byte[] respBody) {
		this.respBody = respBody;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String toString(){
		return Parsers.BYTE2STRING_PARSER.pase(respBody);
	}
}
