package com.tagsin.wechat_sdk.pay;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import net.sf.morph.Morph;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.tagsin.tutils.lang.TimeUtils;
import com.tagsin.wechat_sdk.pay.protocol.RedpackReq;
import com.tagsin.wechat_sdk.pay.protocol.RedpackResp;
import com.tagsin.wechat_sdk.pay.vo.RedpackVo;
import com.tagsin.wechat_sdk.util.RandomStringGenerator;
import com.tagsin.wechat_sdk.util.SignUtils;
import com.tagsin.wechat_sdk.util.SignUtils.SignType;
import com.tagsin.wechat_sdk.util.XMLUtils;

public class RedpackSender {
	private static final Logger logger = Logger.getLogger(RedpackSender.class);
	
	private PayConfig payConfig;
	private KeyStore keyStore;
	
	public RedpackSender(PayConfig payConfig){
		this.payConfig = payConfig;
		try {
			loadKeyStore();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public RedpackResp send(RedpackVo redpack) throws Exception{
		RedpackReq rpReq = new RedpackReq();
		Morph.copy(rpReq, redpack);
		rpReq.setClient_ip(payConfig.getRedpack_client_ip());
		rpReq.setNonce_str(RandomStringGenerator.getRandomStringByLength(10));
		rpReq.setMch_id(payConfig.getMch_id());
		rpReq.setWxappid(payConfig.getAppid());
		rpReq.setSign(SignUtils.sign(rpReq, payConfig.getApi_key(), SignType.MD5).toUpperCase());
		
		
//		Requester req = Requester.builder()
//			.setUdfHttpClent(sslHttpClient)
//			.setUrl("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack")
//			.setMethod(Method.POST)
//			.setBody(XMLUtils.toXml(rpReq,"xml",true));
//		HttpResult httpResult = req.execute();
		
		String result = sslPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack",XMLUtils.toXml(rpReq,"xml",true));
		logger.info("");
		logger.info(result);
		RedpackResp resp = new RedpackResp();
		Morph.copy(resp, XMLUtils.parse(result));
		return  resp;
	}
	
	private KeyStore loadKeyStore() throws Exception{
		KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(payConfig.getP12_apiclient_cert_file()));
		keyStore.load(instream, payConfig.getMch_id().toCharArray());
		instream.close();
		return keyStore;
	}
	
	public String sslPost(String url,String data) throws Exception{
		KeyStore keyStore  = loadKeyStore();
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore,payConfig.getMch_id().toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,
		 SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom() .setSSLSocketFactory(sslsf) .build();
        HttpPost httpost = new HttpPost(url); 
        httpost.addHeader("Connection", "keep-alive");
        httpost.addHeader("Accept", "*/*");
        httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpost.addHeader("Host", "api.mch.weixin.qq.com");
        httpost.addHeader("X-Requested-With", "XMLHttpRequest");
        httpost.addHeader("Cache-Control", "max-age=0");
        httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        httpost.setEntity(new StringEntity(data, "UTF-8"));
        CloseableHttpResponse response = httpclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils .toString(response.getEntity(), "UTF-8");
        EntityUtils.consume(entity);
        return jsonStr;
	}
	
	public static void main(String[] args) throws Exception {
		PayConfig payConfig = new PayConfig();
		payConfig.setApi_key("Team2015isoneTechwuliangchenzhao");
		payConfig.setAppid("wx62039c9c025717bb");
		payConfig.setMch_id("1266278601");
		payConfig.setNotify_url(null);
		payConfig.setP12_apiclient_cert_file("C:\\opt\\证书\\壹是壹\\VIP007\\apiclient_cert.p12");
		payConfig.setRedpack_client_ip("139.129.19.229");
		
		RedpackSender rpSender = new RedpackSender(payConfig);
		System.out.println(rpSender.loadKeyStore().aliases().nextElement());
		
		
		RedpackVo rpVo = new RedpackVo();
		rpVo.setAct_name("act_name");
		
		String str = System.currentTimeMillis() + "";
		str = str.substring(str.length()-9);
		str = payConfig.getMch_id()+TimeUtils.getShortDateStr()+str;
		System.out.println(str);
		rpVo.setMch_billno(str);
		rpVo.setRe_openid("o8fsmsx26KCJ0X8BfZzgLoX_vcns");
		rpVo.setRemark("remark");
		rpVo.setSend_name("send_name");
		rpVo.setTotal_amount(100);
		rpVo.setWishing("wishing");
		
		rpSender.send(rpVo);
	}
}
