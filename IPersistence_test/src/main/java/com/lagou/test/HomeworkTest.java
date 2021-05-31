package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.io.Resources;
import com.lagou.mapper.IUserMapper;
import com.lagou.pojo.User;
import com.lagou.sql.session.SqlSession;
import com.lagou.sql.session.SqlSessionFactory;
import com.lagou.sql.session.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class HomeworkTest {

    private IUserMapper userMapper;

    private User user;

    @Before
    public void before() throws PropertyVetoException, DocumentException {
        InputStream inputStream = Resources.getResourcesAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //使用代理模式生成Dao层的代理实现类
        userMapper = sqlSession.getMapper(IUserMapper.class);

        user = new User();
    }

    @Test
    public void testInsert() throws PropertyVetoException, DocumentException {
        user.setId(6);
        user.setUsername("test");
        userMapper.saveUser(user);
    }

    @Test
    public void testUpdate() throws PropertyVetoException, DocumentException {
        user.setId(6);
        user.setUsername("test1111");
        userMapper.updateUser(user);
    }

    @Test
    public void testDelete() throws PropertyVetoException, DocumentException {
        userMapper.deleteUser(6);
    }
}
