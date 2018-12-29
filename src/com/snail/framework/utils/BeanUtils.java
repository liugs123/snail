package com.snail.framework.utils;

import com.snail.framework.core.BeanFactory;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BeanUtils {

	private static BeanFactory beanFactory;

	public static void setBeanFactory(BeanFactory beanFactory) {
		BeanUtils.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz){
		return (T) beanFactory.getBean(clazz);
	}

}
