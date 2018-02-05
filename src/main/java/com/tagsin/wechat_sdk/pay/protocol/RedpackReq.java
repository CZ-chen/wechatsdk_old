package com.tagsin.wechat_sdk.pay.protocol;

/**
 * @author cz
 * DOC: https://pay.weixin.qq.com/wiki/doc/api/cash_coupon.php?chapter=13_5
 */
public class RedpackReq {
	String nonce_str; //	是	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	String(32)	随机字符串，不长于32位
	String sign; //	是	C380BEC2BFD727A4B6845133519F3AD6	String(32)	详见签名生成算法
	String mch_billno; //	是	10000098201411111234567890	String(28)	商户订单号（每个订单号必须唯一）组成：mch_id+yyyymmdd+10位一天内不能重复的数字。接口根据商户订单号支持重入，如出现超时可再调用。
	String mch_id; //	是	10000098	String(32)	微信支付分配的商户号
	String wxappid; //	是	wx8888888888888888	String(32)	微信分配的公众账号ID（企业号corpid即为此appId）。接口传入的所有appid应该为公众号的appid（在mp.weixin.qq.com申请的），不能为APP的appid（在open.weixin.qq.com申请的）。
	String send_name; //	是	天虹百货	String(32)	红包发送者名称
	String re_openid; //	是	oxTWIuGaIt6gTKsQRLau2M0yL16E	String(32)	接受红包的用户 用户在wxappid下的openid
	String total_amount; //	是	1000	int	付款金额，单位分
	String total_num; //	是	1	int	红包发放总人数 total_num=1
	String wishing; //	是	感谢您参加猜灯谜活动，祝您元宵节快乐！	String(128)	红包祝福语
	String client_ip; //	是	192.168.0.1	String(15)	调用接口的机器Ip地址
	String act_name; //	是	猜灯谜抢红包活动	String(32)	活动名称
	String remark; //	是	猜越多得越多，快来抢！	String(256)	备注信息
	
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
	public String getMch_billno() {
		return mch_billno;
	}
	public void setMch_billno(String mch_billno) {
		this.mch_billno = mch_billno;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getWxappid() {
		return wxappid;
	}
	public void setWxappid(String wxappid) {
		this.wxappid = wxappid;
	}
	public String getSend_name() {
		return send_name;
	}
	public void setSend_name(String send_name) {
		this.send_name = send_name;
	}
	public String getRe_openid() {
		return re_openid;
	}
	public void setRe_openid(String re_openid) {
		this.re_openid = re_openid;
	}
	public String getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}
	public String getTotal_num() {
		return total_num;
	}
	public void setTotal_num(String total_num) {
		this.total_num = total_num;
	}
	public String getWishing() {
		return wishing;
	}
	public void setWishing(String wishing) {
		this.wishing = wishing;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
	public String getAct_name() {
		return act_name;
	}
	public void setAct_name(String act_name) {
		this.act_name = act_name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
