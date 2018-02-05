package com.tagsin.wechat_sdk.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.morph.Morph;

import org.apache.commons.lang3.StringUtils;

import com.tagsin.tutils.codec.MD5;
import com.tagsin.tutils.codec.SHA1;

public class SignUtils {
	public enum SignType{
		MD5,SHA1
	}
	
	public String signUrl(String url,String key,SignType signType){
		
		return null;
	}
	
	public static String sign(Object obj,String key,SignType signType){
		Map<String,Object> map = new HashMap<String,Object>();
		Morph.copy(map, obj);
		return sign(map, key, signType);
	}
	
	public static String sign(Map<String,Object> map,String key,SignType signType){
		ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=null && StringUtils.isNotEmpty(entry.getValue().toString())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort,String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String strA = sb.toString();
        return sigStrA(strA,key,signType);
	}
	
	public static String sigStrA(String strA,String key,SignType signType){
		String total = strA;
		if(!total.endsWith("&")){
			total+="&";
		}
		total = total+"key="+key;
		System.out.println(total);
		switch (signType) {
		case MD5:
			return MD5.encode(total);
		case SHA1:
			return SHA1.encode(total);
		default:
			throw new IllegalStateException("Not subbport signType :" +signType);
		}
	}
	
	public static void main(String[] args) {
		Map<String,String> testMap = new HashMap<String,String>();
		testMap.put("appid", "123");
		System.out.println(sign(testMap, "JhkDXhytt3EOsbOek7nycTOOjTgx70NkpLidnsgck29", SignType.MD5).toUpperCase());
	}
}
