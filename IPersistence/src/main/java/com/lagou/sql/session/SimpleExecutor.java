package com.lagou.sql.session;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;
import com.mysql.jdbc.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
//        1注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();


        //2处理SQL语句
        //获取SQL语句：select * from user where id = #{id} and username = #{username}
        String sql = mappedStatement.getSql();
//        转换SQL语句为select * from user where id = ? and username = ?,并为每个参数赋值
        BoundSql boundSql = getBoundSql(sql);

        //3获取预处理对象，preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //4设置参数
//获取入参类
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object paramValue = declaredField.get(params[0]);
            preparedStatement.setObject(i + 1, paramValue);
        }
        //5执行SQL
        ResultSet resultSet = preparedStatement.executeQuery();

        //6封装返回结果集
        String resultType = mappedStatement.getResultType();
        Class resultTypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();

        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Object resultClassObject = resultTypeClass.newInstance();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //字段名
                String columnName = metaData.getColumnName(i);
                //字段值
                Object columnValue = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(resultClassObject, columnValue);
            }
            objects.add(resultClassObject);
        }
        return (List<E>) objects;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //        1注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();
        //2处理SQL语句
        //获取SQL语句：select * from user where id = #{id} and username = #{username}
        String sql = mappedStatement.getSql();
//        转换SQL语句为select * from user where id = ? and username = ?,并为每个参数赋值
        BoundSql boundSql = getBoundSql(sql);
        //3获取预处理对象，preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        //4设置参数
        //获取入参类
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            //判断是否基础类型及其包装类
            if (parameterTypeClass.isPrimitive() || ((Class) parameterTypeClass.getField("TYPE").get(null)).isPrimitive()) {
                preparedStatement.setObject(i + 1, params[0]);
            } else {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();
                Field declaredField = parameterTypeClass.getDeclaredField(content);
                declaredField.setAccessible(true);
                Object paramValue = declaredField.get(params[0]);
                preparedStatement.setObject(i + 1, paramValue);
            }
        }
        //5执行SQL
        return preparedStatement.executeUpdate();
    }

    private Class getClassType(String parameterType) throws ClassNotFoundException {
        if (!StringUtils.isNullOrEmpty(parameterType)) {
            return Class.forName(parameterType);
        }
        return null;
    }

    /**
     * 完成对{}的解析工作，1将#{} 替换成 ？2 解析出#{} 内的值并进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parse = genericTokenParser.parse(sql);
        //#{}中解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        return new BoundSql(parse, parameterMappings);
    }
}
