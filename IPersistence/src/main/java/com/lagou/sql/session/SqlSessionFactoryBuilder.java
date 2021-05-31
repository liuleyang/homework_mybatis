package com.lagou.sql.session;

import com.lagou.config.XmlConfigBuilder;
import com.lagou.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(InputStream inputStream) throws PropertyVetoException, DocumentException {
        //1解析配置文件并封装到Configuration中
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(inputStream);


//        创建sqlSessionFactory对象,并通过sqlSessionFactory创建sqlSession
        return new DefaultSqlSessionFactory(configuration);
    }
}
