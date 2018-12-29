package com.snail.framework.utils;

import com.alibaba.fastjson.util.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommonUtils {

	public static Object convertParamTypeByClass(String param,Class<?> clazz){
		if(clazz==String.class){
			return param;
		}else if(clazz==int.class||clazz==Integer.class){
			 return Integer.parseInt(param);
		}else if(clazz==double.class||clazz==Double.class){
			 return Double.parseDouble(param);
		}else if(clazz==boolean.class||clazz==Boolean.class){
			return Boolean.parseBoolean(param);
		}
		return null;
	}

	public static boolean isBasisClass(Class<?> clazz){
		List<Class<?>> classes = Arrays.asList(new Class<?>[]{
				String.class,int.class,Integer.class,double.class,
				Double.class,boolean.class,Boolean.class
		});
		if(classes.contains(clazz)){
			return true;
		}
		return false;
	}

	public static Object getObjectByClassAndParam(Class<?> clazz,String paramValue,String paramName, Map<Class<?>, Object> paramObj){
		Field[] declaredFields = clazz.getDeclaredFields();
		Object obj=paramObj.get(clazz);
		for (Field field : declaredFields) {
			field.setAccessible(true);
			if(field.getName().equals(paramName)){
				if(obj==null){
					obj = ReflectUtils.newInstance(clazz);
				}
				ReflectUtils.setField(field, CommonUtils.convertParamTypeByClass(paramValue, field.getType()), obj);
			}
		}
		return obj;
	}

	public static String readRequestBody(HttpServletRequest req){
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder("");
		try {
			br = req.getReader();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();

	}

	public static String formatName(String dbName){
		StringBuilder result=new StringBuilder();
		String a[]=dbName.split("_");
		for(String s:a){
			if(result.length()==0){
				result.append(s.toLowerCase());
			}else{
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

}
