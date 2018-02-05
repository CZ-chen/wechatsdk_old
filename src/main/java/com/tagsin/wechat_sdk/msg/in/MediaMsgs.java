package com.tagsin.wechat_sdk.msg.in;


/**
 * @author cz
 * 媒体消息
 */
public abstract class MediaMsgs {
	public static abstract class MediaMsg extends WxMsg {
		public String MediaId;
	}
	
	public static class ImageMsg extends MediaMsg{
		public static final String MsgType = "image";
		public String PicUrl;
	}
	
	public static class VideoMsg extends MediaMsg {
		public static final String MsgType = "video";
		public String ThumbMediaId;
	}
	
	public static class ShortVideoMsg extends VideoMsg {
		public static final String MsgType = "shortvideo";
	}
	
	public class VoiceMsg extends MediaMsg{
		public static final String MsgType = "voice";
		
		/**
		 * 语音格式，如amr，speex等
		 */
		public String Format;
		
		/**
		 * 语音识别结果
		 */
		public String Recognition;
	}
}
