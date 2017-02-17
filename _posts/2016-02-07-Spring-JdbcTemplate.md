---
layout: post
title:  "Spring JdbcTemplate"
date:   2016-01-06 08:27:00
categories: java
excerpt: Spring JdbcTemplate
---

* content
{:toc}




### JdbcTemplate简介


Spring对数据库的操作在jdbc上面做了深层次的封装，使用spring的注入功能，可以把Dat

aSource注册到JdbcTemplate之中。

　JdbcTemplate位于
![asd](http://images2015.cnblogs.com/blog/659572/201606/659572-20160630165703077-1456883788.png)中。

其全限定命名为org.springframework.jdbc.core.

JdbcTemplate。要使用JdbcTemlate还需一个

![ads](http://images2015.cnblogs.com/blog/659572/201606/659572-20160630165907734-2109476245.png)

这个包包含了一下事务和异常控制

JdbcTemplate主要提供以下五类方法：

* execute方法：可以用于执行任何SQL语句，一般用于执行DDL语句；

* update方法用于执行新增、修改、删除等语句；batchUpd

ate方法用于执行批处理相关语句；

* query方法及queryForXXX方法：用于执行查询相关语句；

call方法：用于执行存储过程、函数相关语句。


### XML配置


配置Spring配置文件applicationContext.xml


      <context:property-placeholder location="classpath:db.properties"/>
      <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
          <property name="user" value="${jdbc.user}"></property>
          <property name="password" value="${jdbc.password}"></property>
          <property name="driverClass" value="${jdbc.driverClass}"></property>
          <property name="jdbcUrl" value="${jdbc.jdbcUrl}"></property>
      </bean>
      
      <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
         <property name="dataSource" ref="dataSource"></property>
     </bean>


### 通过update插入数据


    String sql="insert into user (name,deptid) values (?,?)";
     int count= jdbcTemplate.update(sql, new Object[]{"caoyc",3});
     System.out.println(count);

这里update方法，第二参可以为可变参数。在数据库中可以看到，数据以被正确插入


### 通过update修改数据

    String sql="update user set name=?,deptid=? where id=?";
     jdbcTemplate.update(sql,new Object[]{"zhh",5,51});


### batchUpdate()批量插入、更新和删除方法

    String sql="insert into user (name,deptid) values (?,?)";
     
     List<Object[]> batchArgs=new ArrayList<Object[]>();
     batchArgs.add(new Object[]{"caoyc",6});
     batchArgs.add(new Object[]{"zhh",8});
     batchArgs.add(new Object[]{"cjx",8});
     
     jdbcTemplate.batchUpdate(sql, batchArgs);

 batchUpdate方法第二参数是一个元素为Object[]数组类型的List集合


### 读取单个对象

    String sql="select id,name,deptid from user where id=?";
     
     RowMapper<User> rowMapper=new BeanPropertyRowMapper<User>(User.class);
     User user= jdbcTemplate.queryForObject(sql, rowMapper,52);
     System.out.println(user);


    【注意】：1、使用BeanProperytRowMapper要求sql数据查询出来的列和实体属性需要一一对应。如果数据中列明和属性名不一致，在sql语句中需要用as重新取一个别名
    2、使用JdbcTemplate对象不能获取关联对象

### 读取多个对象


    String sql="select id,name,deptid from user";
     
     RowMapper<User> rowMapper=new BeanPropertyRowMapper<User>(User.class);
     List<User> users= jdbcTemplate.query(sql, rowMapper);
     for (User user : users) {
         System.out.println(user);
    }


### 获取某个记录某列或者count、avg、sum等函数返回唯一值

     String sql="select count(*) from user";
     int count= jdbcTemplate.queryForObject(sql, Integer.class);
     System.out.println(count);