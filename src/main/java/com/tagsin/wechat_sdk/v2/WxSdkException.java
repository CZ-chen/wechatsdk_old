package com.tagsin.wechat_sdk.v2;

public class WxSdkException extends RuntimeException {
	
	private static final long serialVersionUID = -8642025769705149292L;

	public WxSdkException() {
		super();
	}

	public WxSdkException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public WxSdkException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public WxSdkException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public WxSdkException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}
