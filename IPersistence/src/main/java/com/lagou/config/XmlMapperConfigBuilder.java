package com.lagou.config;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.pojo.SqlCommandType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * 解析mapper文件，并封装进Configuration中
 */
public class XmlMapperConfigBuilder {
    private Configuration configuration;

    public XmlMapperConfigBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        String nameSpace = rootElement.attributeValue("namespace");

        List<Element> list = rootElement.selectNodes("//select|insert|update|delete");
        for (Element element : list) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sqlText = element.getTextTrim();
            String name = element.getName();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            SqlCommandType sqlCommandType = SqlCommandType.valueOf(name.toUpperCase(Locale.ENGLISH));
            mappedStatement.setSqlCommandType(sqlCommandType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sqlText);
            String statementId = nameSpace + "." + id;
            configuration.getMappedStatementMap().put(statementId, mappedStatement);
        }

//        List<Element> list = rootElement.selectNodes("//select");

    }
}
