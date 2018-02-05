package com.tagsin.wechat_sdk.pay.protocol;

/**
 * Created by chenzhao on 15-9-10.
 */
public class QueryOrderReq {
    String appid; //公众账号ID 	appid 	是 	String(32) 	wx8888888888888888 	微信分配的公众账号ID（企业号corpid即为此appId）
    String mch_id; //商户号 	mch_id 	是 	String(32) 	1900000109 	微信支付分配的商户号
    String transaction_id; //微信订单号 	transaction_id 	二选一 	String(32) 	013467007045764 	微信的订单号，优先使用
    String out_trade_no; //商户订单号 	out_trade_no 	String(32) 	1217752501201407033233368018 	商户系统内部的订单号，当没提供transaction_id时需要传这个。
    String nonce_str; //随机字符串 	nonce_str 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	随机字符串，不长于32位。推荐随机数生成算法
    String sign; //签名 	sign 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	签名，详见签名生成算法

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
