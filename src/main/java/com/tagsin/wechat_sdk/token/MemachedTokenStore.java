package com.tagsin.wechat_sdk.token;

import java.io.IOException;
import java.util.Date;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.log4j.Logger;

public class MemachedTokenStore implements TokenStore {
	private static final Logger logger = Logger.getLogger(MemachedTokenStore.class);
	
	private MemcachedClient memcachedClient;
	
	public MemachedTokenStore(String connStr) {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(connStr));
		MemcachedClient memcachedClient;
		try {
			memcachedClient = builder.build();
			this.memcachedClient = memcachedClient;
		} catch (IOException e) {
			logger.warn("Connect to memcached faild!",e);
		}
	}
	
	public MemachedTokenStore(String connStr,String userName,String password) {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(connStr));
		builder.addAuthInfo(AddrUtil.getOneAddress(connStr), AuthInfo.plain(userName, password));
		builder.setCommandFactory(new BinaryCommandFactory());
		MemcachedClient memcachedClient;
		try {
			memcachedClient = builder.build();
			this.memcachedClient = memcachedClient;
		} catch (IOException e) {
			logger.warn("Connect to memcached faild!",e);
		}
	}

	@Override
	public String getToken(TokenType tokenType, String appid) {
		String key = getTokenKey(tokenType,appid);
		try {
			return memcachedClient.get(key);
		} catch (Exception e) {
			logger.warn(e,e);
		} 
		return null;
	}

	@Override
	public void saveToken(Token token, String appid) {
		String key = getTokenKey(token.getType(),appid);
		int ttl = (int)((token.getExpireAt() - System.currentTimeMillis()) / 1000);
		try {
			logger.info("saveToken key:"+key+",time:"+new Date()+",ttl:"+ttl);
			memcachedClient.set(key, ttl , token.getToken());
		} catch (Exception e) {
			logger.warn(e,e);
		} 
	}
	
	private String getTokenKey(TokenType tokenType,String appid){
		return tokenType.name()+"."+appid;
	}

}
