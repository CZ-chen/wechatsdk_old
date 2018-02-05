package com.tagsin.wechat_sdk.msg.out;

import java.io.CharArrayWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tagsin.tutils.json.JsonUtils;
import com.tagsin.wechat_sdk.App;
import com.tagsin.wechat_sdk.WXServerApi;

import freemarker.template.Configuration;

public class OutMsgHelper {
	static Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
	static{
		/*try {
			cfg.setDirectoryForTemplateLoading(
					new File(OutMsgHelper.class.getResource("").getFile()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		cfg.setClassForTemplateLoading(OutMsgHelper.class, "ftl");
	}
	
	public static String createNewsReply(String to,String from,List<News> articles){
		CharArrayWriter out = new CharArrayWriter();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("to", to);
		data.put("from", from);
		data.put("size", articles.size());
		data.put("articles", articles);
		data.put("createTime",getCurrentTime());
		try {
			cfg.getTemplate("news.xml").process(data, out);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return out.toString();
	}
	
	public static String createTextReply(String to,String from,String text){
		CharArrayWriter out = new CharArrayWriter();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("to", to);
		data.put("from", from);
		data.put("content", text);
		data.put("createTime",getCurrentTime());
		try {
			cfg.getTemplate("text.xml").process(data, out);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return out.toString();
	}
	
	public static String createImgReply(String to,String from,String media_id){
		CharArrayWriter out = new CharArrayWriter();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("to", to);
		data.put("from", from);
		data.put("media_id", media_id);
		data.put("createTime",getCurrentTime());
		try {
			cfg.getTemplate("img.xml").process(data, out);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return out.toString();
	}
	
	private static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		return sdf.format(new Date());
	}
	
	public static void sendNews(App app,String to,String title, String description, String picurl, String url){
		List<News> articles = new ArrayList<News>();
		articles.add(new News(title, description, picurl, url));
		sendNews(app,to,articles);
	}

	public static void sendNews(App app,String to,List<News> articles){
		Map<String,Object> news = new HashMap<String,Object>();
		news.put("articles", articles);
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("touser", to);
		data.put("msgtype", "news");
		data.put("news", news);
		String json = JsonUtils.toJson(data);
		
		System.out.println(json);
		WXServerApi.send(app, json);
	}
	
	public static boolean sendText(App app,String to,String text){
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("touser", to);
		data.put("msgtype", "text");
		Map<String,Object> txt = new HashMap<String,Object>();
		txt.put("content", text);
		data.put("text", txt);
		String json = JsonUtils.toJson(data);
		
//		System.out.println(json);
		return WXServerApi.send(app, json);
	}
	
	public static String createSendText(String to,String text){
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("touser", to);
		data.put("msgtype", "text");
		Map<String,Object> txt = new HashMap<String,Object>();
		txt.put("content", text);
		data.put("text", txt);
		String json = JsonUtils.toJson(data);
		return json;
	}
	
	public static String createPreviewNews(String towxname,String mediaId){
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("towxname", towxname);
		data.put("msgtype", "mpnews");
		Map<String,Object> mpnews = new HashMap<String,Object>();
		mpnews.put("media_id", mediaId);
		data.put("mpnews", mpnews);
		String json = JsonUtils.toJson(data);
		return json;
	}
	
	public static String createSendImg(String to,String media_id){
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("touser", to);
		data.put("msgtype", "image");
		Map<String,Object> img = new HashMap<String,Object>();
		img.put("media_id", media_id);
		data.put("image", img);
		return JsonUtils.toJson(data);
		
	}
	
	public static void sendImg(App app,String to,String media_id){
		String json = createSendImg(to, media_id);
		System.out.println(json);
		WXServerApi.send(app, json);
	}
	
	public static String createSendNews(String to,String title, String description, String picurl, String url){
		List<News> articles = new ArrayList<News>();
		articles.add(new News(title, description, picurl, url));
		Map<String,Object> news = new HashMap<String,Object>();
		news.put("articles", articles);
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("touser", to);
		data.put("msgtype", "news");
		data.put("news", news);
		return JsonUtils.toJson(data);
	}
	
	public static String createSendNews(String to,String mediaId){
		Map<String,Object> news = new HashMap<String,Object>();
		news.put("media_id", mediaId);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("touser", to);
		data.put("msgtype", "mpnews");
		data.put("mpnews", news);
		return JsonUtils.toJson(data);
	}
	
	public static String createSendallText(boolean isToAll,long tagId,String msgType,String text){
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("filter", getFilter(isToAll, tagId));
		Map<String,Object> content = new HashMap<String,Object>();
		content.put("content", text);
		data.put(msgType, content);
		
		data.put("msgtype", msgType);
		String json = JsonUtils.toJson(data);
		return json;
	}
	
	public static String createSendallMaterial(boolean isToAll,long tagId,String msgType,String mediaId){
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("filter", getFilter(isToAll, tagId));
		
		Map<String,Object> content = new HashMap<String,Object>();
		content.put("media_id", mediaId);
		data.put(msgType, content);
		data.put("msgtype", msgType);
		String json = JsonUtils.toJson(data);
		return json;
	}
	
	private static Map<String, Object> getFilter(boolean isToAll,long tagId){
		Map<String,Object> filter = new HashMap<String,Object>();
		filter.put("is_to_all", isToAll);
		if(!isToAll){
			filter.put("tag_id", tagId);
		}
		return filter;
	}
	
	public static void main(String[] args) {
		System.out.println(createTextReply("a","b","c"));
		
		List<News> news = new ArrayList<News>();
		news.add(new News("ttt","dddddd","picpic","urlurlurl"));
		
		System.out.println(createNewsReply("a","b", news));
		
		App app = new App("wx6ed677567e2f3e34","2be167c48c46c4fc12f1b623735bbf58");
		sendNews(app,"tttt",news);
	}
}
