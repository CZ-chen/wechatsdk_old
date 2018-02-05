package com.tagsin.wechat_sdk.v2;

import com.tagsin.wechat_sdk.App;
import com.tagsin.wechat_sdk.token.TokenType;

public class WxSdk extends BaseWxSdk{

	@Override
	protected String getAccessToken(App app) {
		return app.tokenManager.getToken(TokenType.ACCESS_TOKEN);
	}

}
