package com.tagsin.wechat_sdk;

public class WXServerApiException extends RuntimeException {
	private static final long serialVersionUID = 1610130578011905592L;

	public WXServerApiException() {
		super();
	}

	public WXServerApiException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public WXServerApiException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public WXServerApiException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public WXServerApiException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}
