package com.snail.framework.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {
	public static Object newInstance(Class<?> clazz){
		Object instance=null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	public static Object invokeMethod(Method method,Object obj,Object... args) throws Exception{
		return method.invoke(obj, args);
	}
	
	public static void setField(Field field,Object fieldObj,Object obj){
		field.setAccessible(true);
		try {
			field.set(obj, fieldObj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
