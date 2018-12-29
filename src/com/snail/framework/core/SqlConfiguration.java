package com.snail.framework.core;

import com.snail.framework.utils.CommonUtils;
import com.snail.framework.utils.FileUtils;
import com.snail.framework.utils.JdbcUtils;
import freemarker.cache.StringTemplateLoader;

import java.io.*;
import java.util.*;

public class SqlConfiguration {

    private Map<String,SqlEntity> sqlEntityMap=new HashMap<String,SqlEntity>();

    private  SqlTemplateParser parser=new SqlTemplateParser();

    public Map<String, SqlEntity> getSqlEntityMap() {
        return sqlEntityMap;
    }

    public void setSqlEntityMap(Map<String, SqlEntity> sqlEntityMap) {
        this.sqlEntityMap = sqlEntityMap;
    }

    public SqlTemplateParser getParser() {
        return parser;
    }

    public void setParser(SqlTemplateParser parser) {
        this.parser = parser;
    }

    public void init(){
        initTemplateMap();
        initStringTemplateLoader();
    }

    public void initStringTemplateLoader(){
        StringTemplateLoader templateLoader = parser.getStringTemplateLoader();
        for (String name : sqlEntityMap.keySet()) {
            templateLoader.putTemplate(name,sqlEntityMap.get(name).getContent());
            parser.getTemplate(name);
        }
    }

    public void initTemplateMap(){
        LinkedList<String> sqlFiles = new LinkedList<>();
        FileUtils.findSqlFile(new File(SqlConfiguration.class.getResource("/").getFile()),sqlFiles);
        File sqlFile=null;
        InputStream inputStream=null;
        StringBuilder builder = null;
        for (String path:sqlFiles) {
            sqlFile=new File(path);
            builder=new StringBuilder();
            try {
                inputStream = new FileInputStream(sqlFile);
                List<String> readLines = FileUtils.readLines(inputStream, "UTF-8");
                for (String readLine : readLines) {
                    builder.append(readLine);
                }
                SqlEntity sqlEntity = new SqlEntity();
                sqlEntity.setName(this.getTemplateName(sqlFile.getName()));
                sqlEntity.setContent(builder.toString().trim());
                sqlEntity.setType(JdbcUtils.getType(sqlEntity.getContent()));
                sqlEntityMap.put(sqlEntity.getName(),sqlEntity);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getTemplateName(String originalName){
        String name = originalName.replaceAll(".sql", "").replaceAll("_", ".");
        return name;
    }

}
