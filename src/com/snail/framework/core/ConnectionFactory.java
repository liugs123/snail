package com.snail.framework.core;


import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static Logger logger = Logger.getLogger(ConnectionFactory.class);

    private  String url;
    private  String username;
    private  String password;
    private  String driver;
    BasicDataSource dataSource;
    
    public void init(Properties config) {
        url = config.getProperty("jdbc.url");
        driver = config.getProperty("jdbc.driver");
        username = config.getProperty("jdbc.username");
        password = config.getProperty("jdbc.password");
        dataSource= new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(20);
        dataSource.setMinIdle(3);
        dataSource.setTestWhileIdle(Boolean.parseBoolean(config.getProperty("jdbc.testWhileIdle")));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(config.getProperty("jdbc.testOnBorrow")));
        dataSource.setTestOnReturn(Boolean.parseBoolean(config.getProperty("jdbc.testOnReturn")));
        dataSource.setValidationQuery(config.getProperty("jdbc.validationQuery"));
        dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(config.getProperty("jdbc.minEvictableIdleTimeMillis")));
        dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(config.getProperty("jdbc.timeBetweenEvictionRunsMillis")));
    }

    public  Connection getConnection() {
        try {
            logger.debug("get connection from dataSource");
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
