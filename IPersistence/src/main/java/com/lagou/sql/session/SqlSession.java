package com.lagou.sql.session;

import java.util.List;

public interface SqlSession {

     <E> List<E> selectList(String statementid, Object... params) throws Exception;

     <E> E selectOne(String statementid, Object... params) throws Exception;

     int insert(String statementid, Object... params)throws Exception;

    int update(String statementid, Object... params) throws Exception;


    int delete(String statementid, Object... params) throws Exception;
    /**
     * 为Dao接口生成代理实现类
     *
     * @param mapperClasss
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<?> mapperClasss);
}
