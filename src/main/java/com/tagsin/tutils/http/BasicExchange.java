package com.tagsin.tutils.http;

import java.io.IOException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.io.Buffer;

public class BasicExchange extends ContentExchange {
	
	private String contentType;

	@Override
	protected synchronized void onResponseHeader(Buffer name, Buffer value)
			throws IOException {
		if(name.toString().toLowerCase().equals("content-type")){
			String ct = value.toString();
			int pos = ct.indexOf(';');
			if(pos>0){
				ct = ct.substring(0, pos);
			}
			this.contentType = ct;			
		}
	}

	public String getContentType() {
		return contentType;
	}
	
}
