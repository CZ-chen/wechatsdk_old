package com.tagsin.tutils.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.morph.Morph;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.tagsin.tutils.paser.Parser;

public class Requester {
	private static final Logger logger = Logger.getLogger(Requester.class);

	private static HttpClient client = new HttpClient();
	
	private HttpClient udfHttpClent;
	
	static{
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.setMaxConnectionsPerAddress(50); // max 200 concurrent connections to every address
		client.setThreadPool(new QueuedThreadPool(50)); // max 250 threads
		client.setTimeout(30000); // 30 seconds timeout; if no server reply, the request expires
		try {
			client.start();
		} catch (Exception e) {
			logger.warn(e,e);
		}
	}
	
    public enum Method{
        POST,GET,PUT,DELETE
    }
    
    public static Requester builder(){
    	return new Requester();
    }

    private String url;
    private Map<String,Object> urlParams;
    private Method method = Method.GET;
    private BasicExchange exchange;
    private byte[] body;
    private Map<String,Object> formData;
    private Map<String,String> headers;
    
    public HttpResult execute(){
    	HttpResult result = new HttpResult();
    	result.setCode(-1);
    	try{
    		initAndSendExchange();
    		exchange.waitForDone();
    		result.setCode(exchange.getResponseStatus());
    		result.setRespBody(exchange.getResponseContentBytes());
    		result.setContentType(exchange.getContentType());
    	}catch(Exception ex){
    		result.setError(ex);
    	}
    	
    	return result;
    }

	private void initAndSendExchange() throws IOException, InterruptedException {
		if(exchange==null){
			exchange = new BasicExchange();
		}
		exchange.setURL(UrlUtils.setParms(url, urlParams));
		exchange.setMethod(method.name());
		if(headers!=null){
			for(Entry<String, String> header:headers.entrySet()){
				exchange.addRequestHeader(header.getKey(), header.getValue());
			}
		}
		if(body!=null){
			exchange.setRequestContent(new ByteArrayBuffer(body));
		}else if(formData!=null){
			exchange.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			exchange.setRequestContent(
					new ByteArrayBuffer(UrlUtils.toQueryString(formData).getBytes(Parser.UTF8)));
		}
		
		if(udfHttpClent!=null){
			udfHttpClent.send(exchange);
		}else{
			client.send(exchange);
		}
	}
	
    public ContentExchange asyncExecute(){
    	try {
			initAndSendExchange();
		} catch (Exception e) {
			logger.warn(e,e);
		} 
    	return this.exchange;
    }
    
    public Requester addUrlParm(String key,Object value){
    	if(key!=null && value!=null){
    		if(urlParams==null){
        		urlParams = new HashMap<String,Object>();
        	}
    		urlParams.put(key, value);
    	}
    	return this;
    }
    
    public Requester addFieldsAsUrlParm(Object obj){
    	if(obj!=null){
    		if(urlParams==null){
        		urlParams = new HashMap<String,Object>();
        	}
    		Morph.copy(urlParams, obj);
    	}
    	return this;
    }
    
    public Requester addFieldsAsUrlParm(Object obj,String[] fields){
    	if(obj!=null && fields!=null && fields.length>0){
    		for(String field:fields){
    			addUrlParm(field, Morph.get(obj, field));
    		}
    		Morph.copy(urlParams, obj);
    	}
    	return this;
    }
    
    public Requester addUrlParm(Map<String,Object> map){
    	if(map!=null && map.size()>0){
    		if(urlParams==null){
        		urlParams = new HashMap<String,Object>();
        	}
    		urlParams.putAll(map);
    	}
    	return this;
    }
    
    public Requester addFieldsAsUrlParm(Map<String,Object> map,String[] keys){
    	if(map!=null && map.size()>0){
    		for(String key:keys){
    			addUrlParm(key, map.get(key));
    		}
    	}
    	return this;
    }
    
	public String getUrl() {
		return url;
	}
	public Requester setUrl(String url) {
		this.url = url;
		return this;
	}
	public Map<String, Object> getUrlParams() {
		return urlParams;
	}
	public Requester setUrlParams(Map<String, Object> urlParams) {
		this.urlParams = urlParams;
		return this;
	}
	public Method getMethod() {
		return method;
	}
	public Requester setMethod(Method method) {
		this.method = method;
		return this;
	}
	public Requester setMethod(String method){
		this.method = Method.valueOf(method.toUpperCase());
		return this;
	}
	public static Logger getLogger() {
		return logger;
	}

	public byte[] getBody() {
		return body;
	}

	public Requester setBody(byte[] body) {
		this.body = body;
		return this;
	}
	
	public Requester setBody(String body) {
		this.body = body.getBytes(Parser.UTF8);
		return this;
	}

	public BasicExchange getExchange() {
		return exchange;
	}

	public void setExchange(BasicExchange exchange) {
		this.exchange = exchange;
	}

	public Requester addFormData(Map<String, Object> map) {
		if(map!=null && map.size()>0){
    		if(this.formData==null){
        		formData = new HashMap<String,Object>();
        	}
    		formData.putAll(map);
    	}
    	return this;
	}
	
	public Requester setHeader(String name,String value){
		if(headers==null){
			headers = new HashMap<String,String>();
		}
		headers.put(name, value);
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Requester setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public HttpClient getUdfHttpClent() {
		return udfHttpClent;
	}

	public Requester setUdfHttpClent(HttpClient udfHttpClent) {
		this.udfHttpClent = udfHttpClent;
		return this;
	}
}
