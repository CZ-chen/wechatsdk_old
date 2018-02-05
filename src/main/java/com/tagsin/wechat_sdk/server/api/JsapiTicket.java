package com.tagsin.wechat_sdk.server.api;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.tagsin.wechat_sdk.App;
import com.tagsin.wechat_sdk.token.TokenType;

public class JsapiTicket extends Controller{
	private static final String SUCCESS = "success";
	private static final String CONFIG_ERROR = "property config error.";
	private static final String NO_CONFIG = "no property config.";
	private static final String REQUEST_PARAM_ERROR = "request param error.";
	public void index(){
		String key = getPara("key");
		String refresh = getPara("refresh");
		
		TokenJson token = new TokenJson();
		if(!StringUtils.isEmpty(key)){
			String value = PropKit.get(key);
			if(!StringUtils.isEmpty(value)){
				String[] temp = value.split(",");
				if(temp.length == 2){
					String appid = temp[0].trim();
					String secret = temp[1].trim();
					
					App app = new App(appid, secret);
					if("true".equals(refresh)){
						app.tokenManager.updateToken(TokenType.JSAPI_TICKET);
					}
					token.setError_msg(SUCCESS);
					token.setToken(app.tokenManager.getToken(TokenType.JSAPI_TICKET));
				}else{
					token.setError_msg(CONFIG_ERROR);
				}
			}else{
				token.setError_msg(NO_CONFIG);
			}
		}else{
			token.setError_msg(REQUEST_PARAM_ERROR);
		}
		
		renderJson(token);
	}
}
