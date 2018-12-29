package com.snail.framework.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.snail.framework.utils.BeanUtils;
import com.snail.framework.utils.ResourceUtils;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ContextLoaderListener implements ServletContextListener {
	
	private BeanFactory beanFactory=new BeanFactory();
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		beanFactory.init();
        BeanUtils.setBeanFactory(beanFactory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
