package com.snail.framework.utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResourceUtils {
	private static final Logger logger=LoggerFactory.getLogger(ResourceUtils.class);

	public static Properties loadProps(String fileName){
		InputStream in=null;
		Properties props=null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			
			if(in==null){
				new FileNotFoundException(fileName+"file is not found");
			}
			
			props = new Properties();
		
			props.load(in);
		} catch (IOException e) {
			logger.error("load properties file failure",e);
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close input stream failure",e);
				}
			}
		}
		return props;
		
	}
	

	public static String getString(Properties props ,String key){
		String value="";
		if(props.containsKey(key)){
			value = props.getProperty(key);
		}
		return value;
	}
	
	
}
