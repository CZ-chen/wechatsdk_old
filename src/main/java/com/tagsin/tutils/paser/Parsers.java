package com.tagsin.tutils.paser;

public class Parsers {
	public static final Parser<byte[],String> BYTE2STRING_PARSER = new Parser<byte[],String>() {
		public String pase(byte[] data) {
			if(data==null){
				return null;
			}
			if(data.length==0){
				return "";
			}
			return new String(data,Parser.UTF8);
		}
	};
	
	public static final Parser<String,byte[]> STRING2BYTE_PARSER = new Parser<String,byte[]>() {
		public byte[] pase(String data) {
			if(data==null){
				return null;
			}
			return data.getBytes(Parser.UTF8);
		}
	};
}
