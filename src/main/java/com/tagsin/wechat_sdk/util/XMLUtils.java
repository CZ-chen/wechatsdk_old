package com.tagsin.wechat_sdk.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;

import net.sf.morph.Morph;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLUtils {
	private static final Logger logger = Logger.getLogger(XMLUtils.class);
	
	static XMLInputFactory inFactory = XMLInputFactory.newInstance();
	public static Map<String,String> parse(String xml){
		try {
            Map<String,String> map = new HashMap<String,String>();
            Document document = DocumentHelper.parseText(xml);
            Element nodeElement = document.getRootElement();
            List node = nodeElement.elements();
            for (Iterator it = node.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();
                String tag = elm.getName();
                if(elm.isTextOnly()){
                	map.put(tag, elm.getText());
                }else{
                	map.put(tag, elm.asXML());
                }
                elm = null;
            }
            node = null;
            nodeElement = null;
            document = null;
            return map;
        } catch (Exception e) {
            logger.warn(e,e);
        }
        return null;
	}
	
	public static String toXml(Map<String,?> map,String root,boolean withCData){
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement(root);
		if(map!=null){
			for(Entry<String, ?> entry:map.entrySet()){
				Object value = entry.getValue();
				String textValue = 
						value == null ? "" : value.toString();
				if(withCData){
					rootElement.addElement(entry.getKey()).addCDATA(textValue);
				}else{
					rootElement.addElement(entry.getKey()).setText(textValue);
				}
				
			}
		}
		return rootElement.asXML();
	}
	
	public static String toXml(Object obj){
		 Map<String,Object> map = new HashMap<String,Object>();
		 Morph.copy(map, obj);
		 return toXml(map);
	}
	
	public static String toXml(Object obj,String root,boolean withCData){
		 Map<String,Object> map = new HashMap<String,Object>();
		 Morph.copy(map, obj);
		 return toXml(map,root,withCData);
	}
	
	public static String toXml(Map<String,?> map){
		return toXml(map, "xml", false);
	}
	
	public static void main(String[] args) {
		String xml =  "<xml>"+
		 "<ToUserName><x>xxx</x></ToUserName>\n"+
		 "<FromUserName><![CDATA[fromUser]]></FromUserName>\n "+
		 "<CreateTime>1348831860</CreateTime>\n"+
		 "<MsgType><![CDATA[text]]></MsgType>\n"+
		 "<Content><![CDATA[this is a test]]></Content>\n"+
		 "<MsgId>1234567890123456</MsgId>\n"+
		 "</xml>\n";
		System.out.println(toXml(parse(xml),"xml",true));
	}
}
