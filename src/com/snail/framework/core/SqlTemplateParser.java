package com.snail.framework.core;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SqlTemplateParser {

    private  Configuration configuration;

    private StringTemplateLoader stringTemplateLoader;

    private ConcurrentHashMap<String,Template> templateMap=new ConcurrentHashMap<String,Template>();

    private final String ENCODING="UTF-8";

    public StringTemplateLoader getStringTemplateLoader() {
        return stringTemplateLoader;
    }

    public void setStringTemplateLoader(StringTemplateLoader stringTemplateLoader) {
        this.stringTemplateLoader = stringTemplateLoader;
    }

    public SqlTemplateParser(){
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        stringTemplateLoader = new StringTemplateLoader();
        configuration.setTemplateLoader(stringTemplateLoader);

    }
    public Template getTemplate(String name){
        try {
            Template template=templateMap.get(name);
            if(template==null){
                template = configuration.getTemplate(name, ENCODING);
                templateMap.put(name,template);
            }
            return template;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSql(String name, Object dataModel){
        StringWriter stringWriter = new StringWriter();
        Template template = this.getTemplate(name);
        try {
            template.process(dataModel,stringWriter);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }


}
