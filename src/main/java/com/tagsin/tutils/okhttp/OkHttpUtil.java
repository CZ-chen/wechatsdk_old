package com.tagsin.tutils.okhttp;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.lang3.StringUtils;


public class OkHttpUtil {
	
	public enum RequestMediaType{
		
		JSON(MediaType.parse("application/json; charset=utf-8")),
		TEXT(MediaType.parse("text/x-markdown; charset=utf-8"));
		
		private MediaType mediaType;
		
		private RequestMediaType(MediaType mediaType){
			this.mediaType = mediaType;
		}

		public MediaType getMediaType() {
			return mediaType;
		}

		public void setMediaType(MediaType mediaType) {
			this.mediaType = mediaType;
		}
		
		
	}
	
	//文本
	public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private static ConnectionPool CONNECTIONPOOL = new ConnectionPool(10, 60, TimeUnit.SECONDS);
	private static final OkHttpClient OKHTTPCLIENT;
	
	static{
		OKHTTPCLIENT = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectionPool(CONNECTIONPOOL)
        .build();
	}
	
	/**
	 * get 请求
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static Response requestByGet(Request request) throws Exception{
		return OKHTTPCLIENT.newCall(request).execute();
	}
	
	/**
	 * get 请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Response requestByGet(String url) throws Exception{
		Request request = new Request.Builder().url(url).build();
		return OKHTTPCLIENT.newCall(request).execute();
	}
	
	/**
	 * get 请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Response requestByGet(String url,RequestBody body) throws Exception{
		Request request = new Request.Builder().url(url).post(body).build();
		return OKHTTPCLIENT.newCall(request).execute();
	}
	
	/**
	 * post 请求
	 * @param url
	 * @param requestMediaType
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response requestByPost(String url,RequestMediaType requestMediaType,String params) throws Exception{
		Request request = new Request.Builder().url(url).post(RequestBody.create(requestMediaType.mediaType, params)).build();
		return OKHTTPCLIENT.newCall(request).execute();
	}
		
	/**
	 * post 请求
	 * @param url
	 * @param requestMediaType
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response requestByPost(Request request) throws Exception{
		return OKHTTPCLIENT.newCall(request).execute();
	}
	
	public static Response requestUploadFileWithForm(String url,Map<String, File> fileMap) throws Exception{
		Response response = null;
		try {
			
			MultipartBody.Builder builderBody = new MultipartBody.Builder()
		        .setType(MultipartBody.FORM);
		       
			if(fileMap != null ){
				for(Entry<String, File> entry: fileMap.entrySet()){
					String name = entry.getKey();
					File file = entry.getValue();
					String suffix = StringUtils.substringAfter(file.getName(), ".");
					String fileName = getUUID() + "." + suffix;
					RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
					builderBody.addFormDataPart(name, fileName,fileBody);
				}
			}
			RequestBody requestBody = builderBody.build();
			Request request = new Request.Builder().url(url).post(requestBody).build();
			response = OKHTTPCLIENT.newCall(request).execute();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * ssl 认证
	 * @param url
	 * @param data
	 * @param keyStoreFile
	 * @param keyPwd
	 * @return
	 * @throws Exception
	 */
	public static Response requestWithSSL(String url, String data,File keyStoreFile,String keyPwd)
			throws Exception {

		KeyStore clientStore = loadKeyStore(keyStoreFile,keyPwd);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(clientStore,keyPwd.toCharArray());
		KeyManager[] kms = kmf.getKeyManagers();
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kms, null, new SecureRandom());
		
		OkHttpClient okHttpClient = OKHTTPCLIENT.newBuilder()
				.sslSocketFactory(sslContext.getSocketFactory()).build();

		Request request = new Request.Builder().url(url)
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, data)).build();
		return okHttpClient.newCall(request).execute();

	}
	
	private static KeyStore loadKeyStore(File file,String pwd) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(file);
		keyStore.load(instream, pwd.toCharArray());
		instream.close();
		return keyStore;
	}
	
	private static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
}
