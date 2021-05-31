package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.dao.UserDaoImpl;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sql.session.SqlSession;
import com.lagou.sql.session.SqlSessionFactory;
import com.lagou.sql.session.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {
    @Test
    public void test() {
        try {
            InputStream inputStream = Resources.getResourcesAsStream("sqlMapConfig.xml");

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession();
            User user = new User();
            user.setId(1);
            user.setUsername("lucy");

            User user1 = null;
            user1 = sqlSession.selectOne("user.selectOne", user);
            System.out.println(user1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDao() {
        try {
            IUserDao userDao = new UserDaoImpl();
            List<User> all = userDao.findAll();
            System.out.println(all);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMapper() {
        try {
            InputStream inputStream = Resources.getResourcesAsStream("sqlMapConfig.xml");

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession();
            User user = new User();
            user.setId(1);
            user.setUsername("lucy");

            //使用代理模式生成Dao层的代理实现类
            IUserDao userDao = sqlSession.getMapper(IUserDao.class);
            User user1 = null;
            //代理对象调用接口中任意方法，都会执行invoke方法
            user1 = userDao.findByCondition(user);
            System.out.println(user1);

//            System.out.println(userDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
