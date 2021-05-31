package com.lagou.sql.session;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.pojo.SqlCommandType;
import com.mchange.v2.lang.ObjectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementid, Object... params) throws Exception {
//        将要完成对simpleExcutor方法的调用
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        Executor executor = new SimpleExecutor();
        List<Object> list = executor.query(configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <E> E selectOne(String statementid, Object... params) throws Exception {
        List<Object> objects = selectList(statementid, params);
        if (CollectionUtils.isNotEmpty(objects) && objects.size() == 1) {
            return (E) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或者查询异常");
        }
    }

    @Override
    public int insert(String statementid, Object... params) throws Exception {
        return update(statementid, params);
    }

    @Override
    public int update(String statementid, Object... params) throws Exception {
//        将要完成对simpleExcutor方法的调用
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        Executor executor = new SimpleExecutor();
        return   executor.update(configuration, mappedStatement, params);
    }

    @Override
    public int delete(String statementid, Object... params) throws Exception {
        return update(statementid, params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClasss) {
//        使用jdk动态代理来为Dao接口生成代理对象，并返回
        Object newProxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClasss}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //底层都还是执行jdbc方法，根据不同的情况，来调用selectList或者selectOne
                //准备参数：1,statementId: sql语句的唯一标识，namespace.id=接口名.方法名  2，接口入参,args
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                //根据返回值类型判断调用的方法
                Object result;
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                switch (mappedStatement.getSqlCommandType()) {
                    case SELECT:
                        Type genericReturnType = method.getGenericReturnType();
                        result = genericReturnType instanceof ParameterizedType ? selectList(statementId, args) : selectOne(statementId, args);
                        break;
                    case INSERT:
                        result = insert(statementId, args);
                        break;
                    case UPDATE:
                        result = update(statementId, args);
                        break;
                    case DELETE:
                        result = delete(statementId, args);
                        break;
                    default:
                        throw new RuntimeException("异常sql类型");

                }
                return result;
            }
        });
        return (T) newProxyInstance;
    }
}
