---
layout: post
title:  "spring-session分布式集群共享"
date:   2016-12-21 13:24:00
categories: 分布式高可用
excerpt: spring-session分布式集群共享
---

* content
{:toc}


### 场景出现的问题

HttpSession 是通过 Servlet 容器创建和管理的，像 Tomcat/Jetty 

都是保存在内存中的。而如果我们把 web 服务器搭建成分布式的集群，然后利用  

Nginx 做负载均衡，那么来自同一用户的 Http 请求将有可能被分发到两个不同的 web 

站点中去。那么问题就来了，如何保证不同的 web 站点能够共享同一份 session 

数据呢？


* 第一种是使用容器扩展来实现，大家比较容易接受的是通过容器插件来实现，比如基于

  Tomcat 的 tomcat-redis-session-manager ，基于 Jetty 的 jetty-session-redis 

 等等。好处是对项目来说是透明的，无需改动代码。不过前者目前还不支持 Tomcat 8 ，

 或者说不太完善。但是由于过于依赖容器，一旦容器升级或者更换意味着又得从新来过。

 并且代码不在项目中，对开发者来说维护也是个问题。


* 第二种是自己写一套会话管理的工具类，包括 Session 管理和 Cookie 

管理，在需要使用会话的时候都从自己的工具类中获取，而工具类后端存储可以放到 

Redis中。很显然这个方案灵活性最大，但开发需要一些额外的时间。并且系统中存在两套

Session 方案，很容易弄错而导致取不到数据。


* 第三种是使用框架的会话管理工具，也就是如下介绍的 spring-session 

，可以理解是替换了 Servlet 那一套会话管理，接管创建和管理 Session 

数据的工作。既不依赖容器，又不需要改动代码，并且是用了 spring-data-redis 

那一套连接池，可以说是最完美的解决方案。



### cookie 管理 sessionid


在 maven 中添加如下依赖


      <dependency>
      <groupId>org.springframework.session</groupId>
      <artifactId>spring-session-data-redis</artifactId>
      <version>1.0.1.RELEASE</version>
       </dependency>
       <dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>1.6.1.RELEASE</version>
       </dependency>
      <dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
       </dependency> 
       <dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session</artifactId>
    <version>1.1.0.RELEASE</version>
       </dependency>
      <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.2</version>
      </dependency>



在 applicationContext.xml 配置文件中添加如下配置


      <?xml version="1.0" encoding="UTF-8"?>
      <beans xmlns="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:context="http://www.springframework.org/schema/context"
             xsi:schemaLocation="http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans-2.5.xsd  
          http://www.springframework.org/schema/context  
          http://www.springframework.org/schema/context/spring-context-2.5.xsd"
             default-lazy-init="true">
    <!--
    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.maxIdle}" />
        <property name="maxTotal" value="${redis.maxActive}" />
        <property name="maxWaitMillis" value="${redis.maxWait}" />
        <property name="testOnBorrow" value="${redis.testOnBorrow}" />
    </bean>
     -->
    <bean id="redisHttpSessionConfiguration"
          class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
        <property name="maxInactiveIntervalInSeconds" value="1800"/>
    </bean>
    <bean id="jedisConnectionFactory"
          class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="${redis.host}"/>
        <property name="port" value="${redis.port}"/>
        <property name="usePool" value="true"/>
    </bean>
    <bean id="redisTemplate" class="org.springframework.data.redis.core.StringRedisTemplate">
        <property name="connectionFactory" ref="jedisConnectionFactory"/>
        <property name="keySerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
        <property name="valueSerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
        <property name="hashKeySerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
        <property name="hashValueSerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
    </bean></beans>


在 web.xml 中配置过滤器


接下来在 web.xml 中添加一个 session 代理 filter ，通过这个 filter 来包装 

Servlet 的 getSession() 。需要注意的是这个 filter 需要放在所有 filter 

链最前面，从而保证完全替换掉 tomcat 的 session。这个是约定。。

      <!-- delegatingFilterProxy -->
      <filter>
          <filter-name>springSessionRepositoryFilter</filter-name>
          <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
      </filter>
      <filter-mapping>
          <filter-name>springSessionRepositoryFilter</filter-name>
          <url-pattern>/*</url-pattern>
      </filter-mapping>


此实现方式弊端：如果浏览器禁用掉了 cookie 或者是非 web 请求时根本没有 cookie 

的时候，那么如上通过cookie 管理 sessionid 的实现方式将不能够实现 session 共享。



### httpheader 管理 sessionid



在 applicationContext.xml 配置文件中添加如下配置


      <?xml version="1.0" encoding="UTF-8"?>
      <beans xmlns="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:context="http://www.springframework.org/schema/context"
             xsi:schemaLocation="http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans-2.5.xsd  
          http://www.springframework.org/schema/context  
          http://www.springframework.org/schema/context/spring-context-2.5.xsd"
             default-lazy-init="true">
    <!--
    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.maxIdle}" />
        <property name="maxTotal" value="${redis.maxActive}" />
        <property name="maxWaitMillis" value="${redis.maxWait}" />
        <property name="testOnBorrow" value="${redis.testOnBorrow}" />
    </bean>
     -->
      <!-- 替代默认使用 cookie ,这里使用的是 httpheader -->
      <bean id="httpSessonStrategy"  class="org.springframework.session.web.http.HeaderHttpSessionStrategy"/>
      <!-- 将 session 放入 redis -->
     <bean id="redisHttpSessionConfiguration"
          class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
        <property name="maxInactiveIntervalInSeconds" value="1800"/>
        <property name="httpSessionStrategy" ref="httpSessonStrategy"/>
    </bean>
    <bean id="jedisConnectionFactory"
          class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="${redis.host}"/>
        <property name="port" value="${redis.port}"/>
        <property name="usePool" value="true"/>
    </bean>
    <bean id="redisTemplate" class="org.springframework.data.redis.core.StringRedisTemplate">
        <property name="connectionFactory" ref="jedisConnectionFactory"/>
        <property name="keySerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
        <property name="valueSerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
        <property name="hashKeySerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
        <property name="hashValueSerializer">
            <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>
        </property>
    </bean></beans>



### 注意事项



    如上实现方式都是基于 xml 方式来配置的，官方也有通过注解方式来配置的
    spring-session 要求 Redis 版本在2.8及以上
    Spring Session 的核心项目并不依赖于Spring框架，所以，我们甚至能够将其应用于不使用 Spring 框架的项目中，只是需要引入 spring 常用的包，包括 spring-beans, spring-core, spring-tx 等，版本需在 3.2.9 及以上。但是当我们项目使用了 spring 的时候，版本需在 3.2.9 及以上。
    默认情况下，session 存储在 redis 的 key 是“spring:session::”，但如果有多个系统同时使用一个 redis，则会冲突，此时应该配置 redisNamespace 值，配置后，其 key 为 spring:session:devlops:keyName
        配置 redisNamesapce 的方式，在之前配置文件的 bean 中添加一个属性即可
        <!-- 将session放入redis -->
            <bean id="redisHttpSessionConfiguration" class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
                <property name="maxInactiveIntervalInSeconds" value="1800" />
                <property name="redisNamespace" value="${redisNamespace}"/>
            </bean>
        注意：spring-session 的版本在 1.1.0 及以上才支持命名空间
    如果想在 session 中保存一个对象，必须实现了 Serializable接口,这样 Spring-session 才能对保存的对象进行序列化,从而存储在 redis 里
    session 的域不同会生成新的 session 的。所以在项目中做了负载均衡的话，域就是一样的，所以可以实现session 共享
    如果选用 redis 云服务，使用过程中会出现异常，异常原因是：很多 Redis 云服务提供商考虑到安全因素，会禁用掉 Redis 的 config 命令，因此需要我们手动在云服务后台管理系统手动配置，或者找云服务售后帮忙配置。然后我们在配置文件 RedisHttpSessionConfiguration 的 bean 中添加如下配置，解决使用 redis 云服务异常问题
    <!-- 让Spring Session不再执行config命令 -->
    <util:constant static-field="org.springframework.session.data.redis.config.ConfigureRedisAction.NO_OP">
    </util:constant>

注意：判断 config 命令是否被禁用，可以在 redis 的命令行去使用 config 命令，如果报没有找到该命令，说明 config 命令被禁用了。