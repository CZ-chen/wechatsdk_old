package com.tagsin.wechat_sdk.msg.in;

public class EventMsg extends WxMsg {
	public static final String MsgType = "event";
	
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	public static final String EVENT_TYPE_SCAN = "SCAN";
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	public static final String EVENT_TYPE_CLICK = "CLICK";
	public static final String EVENT_TYPE_VIEW = "VIEW";
	
	public static final String KEY_PREFIX_QRSCENE = "qrscene_";
	
	public String Event;
	/**
	 * 事件KEY值
	 * 		未关注扫码时(subscribe)： qrscene_为前缀，后面为二维码的参数值
	 * 		已关注扫码时(SCAN)： 是一个32位无符号整数，即创建二维码时的二维码scene_id
	 * 		CLICK时： 事件KEY值，与自定义菜单接口中KEY值对应
	 * 		点击菜单跳转链接(VIEW)时： 事件KEY值，与自定义菜单接口中KEY值对应
	 */
	public String EventKey;
	
	/**
	 * 二维码的ticket，可用来换取二维码图片
	 */
	public String Ticket;
	
	/**
	 * 地理位置纬度
	 */
	public String Latitude;
	
	/**
	 * 地理位置经度 
	 */
	public String Longitude;
	
	/**
	 * 地理位置精度 
	 */
	public String Precision;

	/**
	 * 菜单ID
	 */
	public String MenuId;
}
