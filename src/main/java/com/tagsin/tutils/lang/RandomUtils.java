package com.tagsin.tutils.lang;

import java.util.Random;

public class RandomUtils {
	private static final String CHAR_BASE = "abcdefghijklmnopqrstuvwxyz0123456789";
	private static final String NUM_BASE = "0123456789";

    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
    	return getRandomStringByLength(length,CHAR_BASE);
    }
    
    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomNumberByLength(int length) {
    	return getRandomStringByLength(length,NUM_BASE);
    }
    
    public static String getRandomStringByLength(int length,String base){
    	 Random random = new Random();
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < length; i++) {
             int number = random.nextInt(base.length());
             sb.append(base.charAt(number));
         }
         return sb.toString();
    }

}
