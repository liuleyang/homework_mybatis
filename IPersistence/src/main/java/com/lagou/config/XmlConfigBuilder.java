package com.lagou.config;

import com.lagou.io.Resources;
import com.lagou.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 使用dom4j进行解析，并封装成Configuration
 */
public class XmlConfigBuilder {
    //    其他地方还要用configuration所以创建该成员变量
    private Configuration configuration;

    public XmlConfigBuilder() {
        configuration = new Configuration();
    }

    public Configuration parseConfig(InputStream inputStream) throws DocumentException, PropertyVetoException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (Element element : list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        configuration.setDataSource(comboPooledDataSource);

        //解析mapper.xml

        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for (int i = 0; i < mapperList.size(); i++) {
            Element element = mapperList.get(i);
            String resource = element.attributeValue("resource");
            InputStream resourcesAsStream = Resources.getResourcesAsStream(resource);
            XmlMapperConfigBuilder xmlMapperConfigBuilder = new XmlMapperConfigBuilder(configuration);
            xmlMapperConfigBuilder.parse(resourcesAsStream);
        }
        return configuration;
    }

}
