package com.tagsin.tutils.paser;

import java.nio.charset.Charset;

public interface Parser<SourceType,TargetType> {
	public static final Charset UTF8 = Charset.forName("UTF-8");
	public TargetType pase(SourceType data);
}