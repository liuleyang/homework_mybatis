package com.lagou.dao;

import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sql.session.SqlSession;
import com.lagou.sql.session.SqlSessionFactory;
import com.lagou.sql.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class UserDaoImpl implements IUserDao {
    @Override
    public List<User> findAll() throws Exception {
        InputStream inputStream = Resources.getResourcesAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.selectList("user.selectList");
    }

    @Override
    public User findByCondition(User user) throws Exception {
        InputStream inputStream = Resources.getResourcesAsStream("sqlMapConfig.xml");

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user1 = null;
        return sqlSession.selectOne("user.selectOne", user);
    }
}
