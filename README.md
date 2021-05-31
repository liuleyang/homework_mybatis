# homework_mybatis

# 分析：
1使用代理模式增加新增删除修改，首先需要在调用方法时，和原来的查询方法区分开，
即重写创建代理类时InvocationHandler接口的的invoke方法，增加判断条件，根据判断条件调用不通的底层方法。  
2.在区分调用的方法种类后，就需要在DefaultSqlSession  
3.源码已下载，参照源码可知，上述第一个问题是在解析xml文件时，根据标签得到调用的方法类型，  
上述第二个问题中，新增删除修改最终调用的是DefaultSqlSession的update的方法，故只需要实现更新方法，新增删除时调用即可  

# 测试类：
HomeworkTest