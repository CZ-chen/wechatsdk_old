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

import com.tagsin.tutils.json.JsonUtils;
import com.tagsin.wechat_sdk.pay.protocol.MoneyTransferReq;
import com.tagsin.wechat_sdk.pay.protocol.MoneyTransferResp;
import com.tagsin.wechat_sdk.pay.vo.MoneyTransferVo;
import com.tagsin.wechat_sdk.util.RandomStringGenerator;
import com.tagsin.wechat_sdk.util.SignUtils;
import com.tagsin.wechat_sdk.util.SignUtils.SignType;
import com.tagsin.wechat_sdk.util.XMLUtils;

public class MoneyTransfer {
	private static final Logger logger = Logger.getLogger(MoneyTransfer.class);
	
	private PayConfig payConfig;
	private KeyStore keyStore;
	
	public MoneyTransfer(PayConfig payConfig){
		this.payConfig = payConfig;
		try {
			this.keyStore = loadKeyStore();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public MoneyTransferResp send(MoneyTransferVo transfer) throws Exception{
		MoneyTransferReq req = new MoneyTransferReq();
		Morph.copy(req, transfer);
		req.setSpbill_create_ip(payConfig.getRedpack_client_ip());
		req.setNonce_str(RandomStringGenerator.getRandomStringByLength(10));
		req.setMch_appid(payConfig.getAppid());
		req.setMchid(payConfig.getMch_id());
		req.setSpbill_create_ip(payConfig.getRedpack_client_ip());
		req.setSign(SignUtils.sign(req, payConfig.getApi_key(), SignType.MD5).toUpperCase());
		
//		Requester req = Requester.builder()
//			.setUdfHttpClent(sslHttpClient)
//			.setUrl("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack")
//			.setMethod(Method.POST)
//			.setBody(XMLUtils.toXml(rpReq,"xml",true));
//		HttpResult httpResult = req.execute();
		
		String result = sslPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers",XMLUtils.toXml(req,"xml",true));
		logger.info("");
		logger.info(result);
		MoneyTransferResp resp = new MoneyTransferResp();
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
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PayConfig payConfig = new PayConfig();
		payConfig.setApi_key("locat12io3npr4ot1oRcoDlXSA11098L");
		payConfig.setAppid("wx892a52b121212967");
		payConfig.setMch_id("1310628001");
		payConfig.setNotify_url("http://weixin.qunxiaodian.com/wx/pay_notify");
		payConfig.setP12_apiclient_cert_file(System.getProperty("user.home")+"/cert/wallet.p12");
		payConfig.setRedpack_client_ip("139.129.19.229");
		
		MoneyTransfer transfer = new MoneyTransfer(payConfig);
		MoneyTransferVo vo = new MoneyTransferVo();
		vo.setPartner_trade_no("TEST_00001");
		vo.setOpenid("oVfMvwb97IivMp_aSX3nvfKV8aOw");
		vo.setCheck_name("NO_CHECK");
		vo.setAmount(200L);
		vo.setDesc("测试转账");
		
		System.out.println(JsonUtils.toJson(transfer.send(vo)));
	}
}
