package com.tagsin.wechat_sdk.server;

import java.io.File;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.tagsin.wechat_sdk.server.api.AccessToken;
import com.tagsin.wechat_sdk.server.api.JsapiTicket;

public class ServerConfig extends JFinalConfig{

	@Override
	public void configConstant(Constants me) {
		// TODO Auto-generated method stub
		me.setDevMode(true);
	}

	@Override
	public void configRoute(Routes me) {
		// TODO Auto-generated method stub
		me.add("/access_token", AccessToken.class);
		me.add("/jsapi_ticket", JsapiTicket.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		// TODO Auto-generated method stub
		PropKit.use(new File(PathKit.getWebRootPath() +"/token_conf.properties"));
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub
		me.add(new ContextPathHandler("ctx"));
	}

}
