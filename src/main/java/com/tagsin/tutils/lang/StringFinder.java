package com.tagsin.tutils.lang;

public class StringFinder {
	public static final FindResult NOT_FIND = new FindResult(false,-1,null);
	
	public static class FindResult {
		public boolean find;
		public int offsetFind;
		public String result;
		public FindResult(boolean find, int offsetFind, String result) {
			super();
			this.find = find;
			this.offsetFind = offsetFind;
			this.result = result;
		}
	}
	public static FindResult find(String str,int from,String before,String after){
		if(str==null){
			return NOT_FIND;
		}
		int start = str.indexOf(before, from)+before.length();
		if(start>0){
			int end = str.indexOf(after,start+1);
			if(end>0){
				return new FindResult(true,end+after.length(),str.substring(start, end));
			}
		}
		
		return NOT_FIND;
	}
}
