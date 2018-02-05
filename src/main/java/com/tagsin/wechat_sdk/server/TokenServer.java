package com.tagsin.wechat_sdk.server;

import com.jfinal.core.JFinal;

public class TokenServer {
	public static void main(String[] args) {
		JFinal.start("/Users/wuliang/git/wechat_sdk/wechat_sdk/src/conf", 8083, "/", 10);
	}
}
