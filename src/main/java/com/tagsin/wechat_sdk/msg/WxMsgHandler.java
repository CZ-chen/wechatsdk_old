package com.tagsin.wechat_sdk.msg;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.tagsin.tutils.codec.AES;
import com.tagsin.tutils.paser.Parser;
import com.tagsin.tutils.paser.Parsers;
import com.tagsin.wechat_sdk.msg.in.EventMsg;
import com.tagsin.wechat_sdk.msg.in.WxMsg;
import com.tagsin.wechat_sdk.msg.in.MediaMsgs.ImageMsg;
import com.tagsin.wechat_sdk.msg.in.MediaMsgs.ShortVideoMsg;
import com.tagsin.wechat_sdk.msg.in.MediaMsgs.VideoMsg;
import com.tagsin.wechat_sdk.msg.in.MediaMsgs.VoiceMsg;
import com.tagsin.wechat_sdk.msg.in.SimpleMsgs.LinkMsg;
import com.tagsin.wechat_sdk.msg.in.SimpleMsgs.LocationMsg;
import com.tagsin.wechat_sdk.msg.in.SimpleMsgs.TextMsg;
import com.tagsin.wechat_sdk.msg.out.News;
import com.tagsin.wechat_sdk.util.XMLUtils;

public class WxMsgHandler {
	private static final String QRSCENE_PREFIX = "qrscene_";

	private static final Logger logger = Logger.getLogger(XMLUtils.class);
	
	private String aesKey;
	
	public void enableMsgEncopt(String aesKey){
		this.aesKey = aesKey;
	}
	
	public void disableMsgEncopt(){
		this.aesKey = null;
	}
	
	public String process(byte[] requestBody){
		String xml = null;
		String result = null;
		if(aesKey==null){
			xml = new String(requestBody,Parser.UTF8);
		}else{
			xml = Parsers.BYTE2STRING_PARSER
					.pase(AES.decrypt(requestBody, aesKey));
		}
		logger.info("receive message :" + xml);
		try{
			Map<String,String> msg = XMLUtils.parse(xml);
			String msgType = msg.get("MsgType");
			
			if(TextMsg.MsgType.equals(msgType)){
				result = processMsg(convert(TextMsg.class, msg));
			}else if(LinkMsg.MsgType.equals(msgType)){
				result = processMsg(convert(LinkMsg.class, msg));
			}else if(LocationMsg.MsgType.equals(msgType)){
				result = processMsg(convert(LocationMsg.class, msg));
			}else if(ImageMsg.MsgType.equals(msgType)){
				result = processMsg(convert(ImageMsg.class, msg));
			}else if(VideoMsg.MsgType.equals(msgType)){
				result = processMsg(convert(VideoMsg.class, msg));
			}else if(ShortVideoMsg.MsgType.equals(msgType)){
				result = processMsg(convert(ShortVideoMsg.class, msg));
			}else if(VoiceMsg.MsgType.equals(msgType)){
				result = processMsg(convert(VoiceMsg.class, msg));
			}else if(EventMsg.MsgType.equals(msgType)){
				result = processEvent(convert(EventMsg.class, msg));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.warn(ex);
		}
		if(result!=null && aesKey!=null){
			return Parsers.BYTE2STRING_PARSER.pase(AES.encrypt(result, aesKey));
		}
		return result;
	}
	
	public static void main(String[] args) {
		String xml =  "<xml>"+
				 "<ToUserName>abc</ToUserName>\n"+
				 "<FromUserName><![CDATA[fromUser]]></FromUserName>\n "+
				 "<CreateTime>1348831860</CreateTime>\n"+
				 "<MsgType><![CDATA[text]]></MsgType>\n"+
				 "<Content><![CDATA[this is a test]]></Content>\n"+
				 "<MsgId>1234567890123456</MsgId>\n"+
				 "</xml>\n";
		WxMsgHandler handler = new WxMsgHandler();
		String key = "OOD91aKBCnVOZR0q8RGdH78OVlaDiLnIyiJIu3zecBo";
		String content = "MDGihiWTccPokbEraMTplNmhVZDVBputq8KvHvJkZjBRFfIuGw5Yl3zvowDWSxzweV0jz1N1EywqHjos/mCBQ4DCTiFLqDTofxJTBhzRZnY0VbcHN3pOX3lV59eUNgcdIzelF47cu5z6SVazGtbJYwF+512xv0JALl1xnFSzjyxFcc8goUliaEQaVN70JXjg5ARO9lDGR0YaA4TO3Fturyex68lOOITeOz4LQrcRZTd4w9tdDIQ0mu5FIIb0w84i5FJvvmaMPrSDJGmpE6HyUYQ5KmcncL4CLcwU5NdmrhwPWLk4KUJsTY2FFkWCQwoUYErmw7gL03fj/QAA6FtXadH4jdM/Y5WLgIzQmhBepEm6sAefWx3fbrs6Q/LHQNuDd21aWpzbMcJJhvT/45dRvLToLKH1FebqRoQXO1KHJYA69wJOg3VzRpK40sGeMmwkCmHhI+vsEhdv86kKB5qedg==";
		String result = new String(AES.decrypt(content.getBytes(), key));
		System.out.println(result);
		//handler.process(xml.getBytes());
	}
	
	private String processEvent(EventMsg eventMsg) throws Exception{
		//用户关注		
		if(EventMsg.EVENT_TYPE_SUBSCRIBE.equals(eventMsg.Event)){
			//扫码关注
			if(eventMsg.EventKey!=null && eventMsg.EventKey.toLowerCase().startsWith(QRSCENE_PREFIX)){
				long qrscene = 0;
				if(eventMsg.EventKey.length()>QRSCENE_PREFIX.length()){
					qrscene = Long.parseLong(eventMsg.EventKey.substring(QRSCENE_PREFIX.length()));
				}
				return onScanSubscribe(eventMsg, qrscene, eventMsg.Ticket);
			}
			return onSubscribe(eventMsg);
		}else if(EventMsg.EVENT_TYPE_UNSUBSCRIBE.equals(eventMsg.Event)){
			return onUnsubscribe(eventMsg);
		}else if(EventMsg.EVENT_TYPE_CLICK.equals(eventMsg.Event)){
			return onMenuClick(eventMsg, eventMsg.EventKey);
		}else if(EventMsg.EVENT_TYPE_SCAN.equals(eventMsg.Event)){
			return onFollowerScan(eventMsg, eventMsg.EventKey,eventMsg.Ticket);
		}
		return null;
	}
	
	private <T>T convert(Class<T> type,Map<String,String> map)throws Exception{
		T msg = type.newInstance();
		for(Entry<String,String> entry:map.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			
			try {
				Field field = type.getField(key);
				if(field!=null){
					int modifiers = field.getModifiers();
					if(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)){
						continue;
					}
					field.set(msg, value);
				}
			} catch (NoSuchFieldException e) {
				logger.warn(e.getMessage() + " in type :" + type.getName());
			}			
		}
		return msg;
	}
	
	protected String createReply(TextMsg msg){
		return null;
	}
	
	protected String createReply(ImageMsg msg){
		return null;
	}
	
	protected String createReply(VoiceMsg msg){
		return null;
	}
	
	protected String createNewsReply(WxMsg msg,List<News> newsItems){
		return null;
	}
	
	
	//================以下为模版方法:=========================
	protected String processMsg(TextMsg msg)throws Exception{return null;}
	protected String processMsg(VideoMsg msg)throws Exception{return null;}
	protected String processMsg(LinkMsg msg)throws Exception{return null;}
	protected String processMsg(LocationMsg msg)throws Exception{return null;}
	protected String processMsg(ImageMsg msg)throws Exception{return null;}
	protected String processMsg(ShortVideoMsg msg)throws Exception{return null;}
	protected String processMsg(VoiceMsg msg)throws Exception{return null;}
	
	protected String onSubscribe(EventMsg event)throws Exception{return null;}
	protected String onUnsubscribe(EventMsg event)throws Exception{return null;}
	protected String onScanSubscribe(EventMsg event,long qrscene,String ticket)throws Exception{return null;}
	protected String onFollowerScan(EventMsg event,String qrScene,String ticket)throws Exception{return null;}
	protected String onLocationReport(EventMsg event,String latitude,String longitude,String precision)throws Exception{return null;}
	protected String onMenuClick(EventMsg event,String menuKey)throws Exception{return null;}
	protected String onMenuLinkClick(EventMsg event,String url)throws Exception{return null;}
}
