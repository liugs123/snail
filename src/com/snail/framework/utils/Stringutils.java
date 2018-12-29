package com.snail.framework.utils;


public class Stringutils {
	public static String addSeparator(String str){
		str = str.replace("/", "");
		str="/"+str+"/";
		return str;
	}
}
