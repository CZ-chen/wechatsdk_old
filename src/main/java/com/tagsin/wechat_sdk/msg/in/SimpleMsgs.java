package com.tagsin.wechat_sdk.msg.in;

public class SimpleMsgs {
	public static class TextMsg extends WxMsg{
		public static final String MsgType = "text";
		public String Content;
	}
	
	public static class LinkMsg extends WxMsg{
		public static final String MsgType = "link";
		public String Title;
		public String Description;
		public String Url;
	}
	
	public static class LocationMsg extends WxMsg {
		public static final String MsgType = "location";
		
		/**
		 * 纬度
		 */
		public static String Location_X;
		
		/**
		 * 经度
		 */
		public static String Location_Y;
		
		/**
		 * 地图缩放大小
		 */
		public static Integer Scale;
		
		/**
		 * 地理位置信息
		 */
		public static Integer Label;
	}
}
