package com.tagsin.wechat_sdk.pay;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.morph.Morph;
import okhttp3.Response;

import org.apache.log4j.Logger;

import com.tagsin.tutils.http.HttpResult;
import com.tagsin.tutils.http.Requester;
import com.tagsin.tutils.http.Requester.Method;
import com.tagsin.tutils.json.JsonUtils;
import com.tagsin.tutils.lang.TimeUtils;
import com.tagsin.tutils.okhttp.OkHttpUtil;
import com.tagsin.tutils.okhttp.OkHttpUtil.RequestMediaType;
import com.tagsin.tutils.paser.Parsers;
import com.tagsin.wechat_sdk.pay.protocol.PayResultNotify;
import com.tagsin.wechat_sdk.pay.protocol.QueryOrderReq;
import com.tagsin.wechat_sdk.pay.protocol.QueryOrderResp;
import com.tagsin.wechat_sdk.pay.protocol.WXUnifiedOrderReq;
import com.tagsin.wechat_sdk.pay.vo.TradeInfo;
import com.tagsin.wechat_sdk.util.RandomStringGenerator;
import com.tagsin.wechat_sdk.util.SignUtils;
import com.tagsin.wechat_sdk.util.SignUtils.SignType;
import com.tagsin.wechat_sdk.util.XMLUtils;

/**
 * Created by chenzhao on 15-9-10.
 */
public class Pay {
    private static final Logger logger = Logger.getLogger(Pay.class);
    private PayConfig payConf;
    
    public Pay(PayConfig payConfig){
    	this.payConf = payConfig;
    }

    /**
     * 创建用于JSAPI发起支付试用的数据
     * @param prepay_id
     * @return
     */
    public Map<String,Object> createJsApiReq(String prepay_id) {
    	logger.info("creating WXPayReq ... ");
        /*
        {
           "appId" ： "wx2421b1c4370ec43b",     //公众号名称，由商户传入
           "timeStamp"：" 1395712654",         //时间戳，自1970年以来的秒数
           "nonceStr" ： "e61463f8efa94090b1f366cccfbbb444", //随机串
           "package" ： "prepay_id=u802345jgfjsdfgsdg888",
           "signType" ： "MD5",         //微信签名方式：
           "paySign" ： "70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名
       }
         */
        Map<String,Object> wxPayReq = new HashMap<String,Object>();
        wxPayReq.put("appId",payConf.getAppid());
        wxPayReq.put("timeStamp", Long.toString(System.currentTimeMillis() / 1000));
        wxPayReq.put("nonceStr", RandomStringGenerator.getRandomStringByLength(16));
        wxPayReq.put("package", "prepay_id=" + prepay_id);
        wxPayReq.put("signType", "MD5");
        String paySing = SignUtils.sign(wxPayReq, payConf.getApi_key(),SignType.MD5);
        logger.debug("paySing :" + paySing);
        wxPayReq.put("paySign", paySing);
        return wxPayReq;
    }

    /**
     * 初始化统一下单
     * @param tradeVo
     * @throws Exception
     */
    public void initUnifiedOrder(TradeInfo trade) throws Exception {
    	logger.info("init WX UnifiedOrder ...");
        WXUnifiedOrderReq req = new WXUnifiedOrderReq();

        req.setAppid(payConf.getAppid());
        req.setMch_id(payConf.getMch_id());
        req.setDevice_info("WEB");
        req.setNonce_str(RandomStringGenerator.getRandomStringByLength(16));
        req.setBody(trade.getSummary());
        req.setDetail(trade.getSummary());
        req.setOut_trade_no(trade.getId());
        req.setFee_type("CNY");
        req.setTotal_fee(trade.getTotal_fee());
        req.setSpbill_create_ip(payConf.getRedpack_client_ip());
        req.setTime_start(TimeUtils.dateToString(new Date(),"yyyyMMddHHmmss"));
        req.setTime_expire(TimeUtils.dateToString(new Date(System.currentTimeMillis()+10*60*1000), "yyyyMMddHHmmss"));
        req.setNotify_url(payConf.getNotify_url());
        req.setTrade_type(trade.getTrade_type());
        req.setOpenid(trade.getOpenid());
        req.setSign(SignUtils.sign(req, payConf.getApi_key(), SignType.MD5));

        logger.info("before send unifiedorder request.");
        
        String respBody = doUnifiedOrderReq(req);
        
        logger.info("response of unifiedorder request : " + respBody);

        Map<String,String> resultMap = XMLUtils.parse(respBody);
        
        if("SUCCESS".equals(resultMap.get("return_code")) && 
				"SUCCESS".equals(resultMap.get("result_code"))){
			
			trade.setPrepay_id((String)resultMap.get("prepay_id"));
			trade.setCode_url((String)resultMap.get("code_url"));
		}else{
			logger.warn("Weixin create unified order faild :" + respBody);
			throw new IllegalStateException("");
		}
    }
    
    /**
     * 初始化统一下单
     * @param tradeVo
     * @throws Exception
     */
    public String initUnifiedOrder(WXUnifiedOrderReq req) throws Exception {
    	logger.info("init WX UnifiedOrder ...");

        req.setAppid(payConf.getAppid());
        req.setMch_id(payConf.getMch_id());
        req.setDevice_info("WEB");
        req.setNonce_str(RandomStringGenerator.getRandomStringByLength(16));
        req.setFee_type("CNY");
        req.setSpbill_create_ip(payConf.getRedpack_client_ip());
        req.setTime_start(TimeUtils.dateToString(new Date(),"yyyyMMddHHmmss"));
        req.setTime_expire(TimeUtils.dateToString(new Date(System.currentTimeMillis()+10*60*1000), "yyyyMMddHHmmss"));
        req.setNotify_url(payConf.getNotify_url());
        
        req.setSign(SignUtils.sign(req, payConf.getApi_key(), SignType.MD5));

        logger.info("before send unifiedorder request.");
        
        String respBody = doUnifiedOrderReq(req);
        
        logger.info("response of unifiedorder request : " + respBody);

        Map<String,String> resultMap = XMLUtils.parse(respBody);
        
        if("SUCCESS".equals(resultMap.get("return_code")) && 
				"SUCCESS".equals(resultMap.get("result_code"))){
			return (String)resultMap.get("prepay_id");
//			trade.setPrepay_id((String)resultMap.get("prepay_id"));
//			trade.setCode_url((String)resultMap.get("code_url"));
		}else{
			logger.warn("Weixin create unified order faild :" + respBody);
			throw new IllegalStateException("");
		}
    }
    
    /**
     * 初始化扫一扫支付统一下单
     * @param req
     * @return
     * @throws Exception
     */
    public String[] initNativeUnifiedOrder(WXUnifiedOrderReq req) throws Exception {

        Map<String,String> resultMap = initWxUnifiedOrder(req);
        
        if("SUCCESS".equals(resultMap.get("return_code")) && 
				"SUCCESS".equals(resultMap.get("result_code"))){
        	
			String prepayId = (String)resultMap.get("prepay_id");
			String qrcodeUrl = (String)resultMap.get("code_url");
			return new String[]{prepayId,qrcodeUrl};
//			trade.setPrepay_id((String)resultMap.get("prepay_id"));
//			trade.setCode_url((String)resultMap.get("code_url"));
		}else{
			throw new IllegalStateException("");
		}
    }
    
    /**
     * 初始化统一下单
     * @param tradeVo
     * @throws Exception
     */
    private Map<String,String> initWxUnifiedOrder(WXUnifiedOrderReq req) throws Exception {
    	logger.info("init WX UnifiedOrder ...");

        req.setAppid(payConf.getAppid());
        req.setMch_id(payConf.getMch_id());
        req.setDevice_info("WEB");
        req.setNonce_str(RandomStringGenerator.getRandomStringByLength(16));
        req.setFee_type("CNY");
        req.setSpbill_create_ip(payConf.getRedpack_client_ip());
        req.setTime_start(TimeUtils.dateToString(new Date(),"yyyyMMddHHmmss"));
        req.setTime_expire(TimeUtils.dateToString(new Date(System.currentTimeMillis()+10*60*1000), "yyyyMMddHHmmss"));
        req.setNotify_url(payConf.getNotify_url());
        
        req.setSign(SignUtils.sign(req, payConf.getApi_key(), SignType.MD5));

        logger.info("before send unifiedorder request.");
        
        String respBody = doUnifiedOrderReq(req);
        return XMLUtils.parse(respBody);
        
    }
    
    private String doUnifiedOrderReq(WXUnifiedOrderReq req) throws Exception{
    	Response response = OkHttpUtil.requestByPost("https://api.mch.weixin.qq.com/pay/unifiedorder", RequestMediaType.TEXT, XMLUtils.toXml(req));
        
        int code = response.code();
        if(code>300){
        	logger.warn("create unifiedorder reponse error : "+ code);
            throw new IOException("Illegal http response code : " + code);
        }
        String respBody = response.body().string();
        logger.info("response of unifiedorder request : " + respBody);
        return respBody;
    }
    

    public QueryOrderResp queryOrder(String traid_id) throws Exception {
        QueryOrderReq req = new QueryOrderReq();
        req.setAppid(payConf.getAppid());
        req.setMch_id(payConf.getMch_id());
        req.setNonce_str(RandomStringGenerator.getRandomStringByLength(8));
        req.setOut_trade_no(traid_id);
        req.setSign(SignUtils.sign(req,payConf.getApi_key(),SignType.MD5));
        
        HttpResult result = Requester.builder()
        		.setUrl("https://api.mch.weixin.qq.com/pay/orderquery")
        		.setMethod(Method.POST)
        		.setBody(XMLUtils.toXml(req)).execute();
        if(result.getCode()!=200){
            throw new IOException("Illegal http response code : " + result.getCode());
        }
        String respBody = result.parse(Parsers.BYTE2STRING_PARSER);
        logger.info("query order info from wx , result :" + respBody);
        Map<String,String> resultMap = XMLUtils.parse(respBody);
        QueryOrderResp resp = new QueryOrderResp();
        Morph.copy(resp, resultMap);
        return resp;
    }
    
    public PayResultNotify pasePayResultNotify(String notifyXml) throws Exception{
    	logger.info("receive pay result notify:" + notifyXml);
    	Map<String, String> reqMap = XMLUtils.parse(notifyXml);
		PayResultNotify payResultNotify = new PayResultNotify();
		Morph.copy(payResultNotify, reqMap);
		return payResultNotify;
    }
}
