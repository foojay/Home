---
layout: post
title:  "Spring Boot下Druid配置与监控"
date:   2017-02-07 13:15:00
categories: springboot
excerpt:  Spring Boot下Druid配置与监控
---

* content
{:toc}




### Druid介绍

Druid是一个JDBC组件，它包括三部分：

*  DruidDriver 代理Driver，能够提供基于Filter－Chain模式的插件体系。
*  DruidDataSource 高效可管理的数据库连接池。 
*  SQLParser 

Druid可以做什么？  

*  可以监控数据库访问性能，Druid内置提供了一个功能强大的StatFilter插件，能够详细统计SQL的执行性能，这对于线上分析数据库访问性能有帮助。
*  替换DBCP和C3P0。Druid提供了一个高效、功能强大、可扩展性好的数据库连接池。
*  数据库密码加密。直接把数据库密码写在配置文件中，这是不好的行为，容易导致安全问题。DruidDruiver和DruidDataSource都支持PasswordCallback。  
*  SQL执行日志，Druid提供了不同的LogFilter，能够支持Common-Logging、Log4j和JdkLog，你可以按需要选择相应的LogFilter，监控你应用的数据库访问情况。
*  扩展JDBC，如果你要对JDBC层有编程的需求，可以通过Druid提供的Filter-Chain机制，很方便编写JDBC层的扩展插件。 


### Spring Boot与Druid的集成

包含：  
*  MySQL Driver驱动包
*  Spring Boot的JPA依赖包
*  阿里系的Druid依赖包

    <dependency>  
                <groupId>mysql</groupId>  
                <artifactId>mysql-connector-java</artifactId>  
                <scope>runtime</scope>  
     </dependency>
      <dependency>  
                <groupId>org.springframework.boot</groupId>  
                <artifactId>spring-boot-starter-data-jpa</artifactId>  
            </dependency> 
        <dependency>  
                    <groupId>com.alibaba</groupId>  
                    <artifactId>druid</artifactId>  
                    <version>1.0.25</version>  
        </dependency>  


Spring Boot中的application.properties配置信息：

    # 驱动配置信息  
    spring.datasource.type=com.alibaba.druid.pool.DruidDataSource  
    spring.datasource.url = jdbc:mysql://127.0.0.1:3306/mealsystem?useUnicode=true&characterEncoding=utf-8  
    spring.datasource.username = root  
    spring.datasource.password = 123456  
    spring.datasource.driverClassName = com.mysql.jdbc.Driver  
       
    #连接池的配置信息  
    spring.datasource.initialSize=5  
    spring.datasource.minIdle=5  
    spring.datasource.maxActive=20  
    spring.datasource.maxWait=60000  
    spring.datasource.timeBetweenEvictionRunsMillis=60000  
    spring.datasource.minEvictableIdleTimeMillis=300000  
    spring.datasource.validationQuery=SELECT 1 FROM DUAL  
    spring.datasource.testWhileIdle=true  
    spring.datasource.testOnBorrow=false  
    spring.datasource.testOnReturn=false  
    spring.datasource.poolPreparedStatements=true  
    spring.datasource.maxPoolPreparedStatementPerConnectionSize=20  
    spring.datasource.filters=stat,wall,log4j  
    spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000  


新建Druid数据源配置类

    package com.wt.config;
    import groovy.util.logging.Commons;
    import java.beans.ConstructorProperties;
    import javax.sql.DataSource;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Primary;
    import org.springframework.stereotype.Component;
    import com.alibaba.druid.pool.DruidDataSource;
    /**
     * Druid数据源配置
     * @author foojay
     *
     */
    @Configuration
    @Component
    public class DruidDBConfig {  
          
        //实例化数据源
        @Bean
        @Primary
        @ConfigurationProperties(prefix="spring.datasource")
        public DataSource druidDataSource() {  
            DruidDataSource druidDataSource = new DruidDataSource();  
            return druidDataSource;  
        }  
    }  

DruidDBConfig类被@Configuration标注，用作配置信息； DataSource对象被@Bean声明，为Spring容器所管理， @Primary表示这里定义的DataSource将覆盖其他来源的DataSource。

#### 下面为连接池的补充设置，应用到上面所有数据源中

    # 初始化大小，最小，最大
    spring.datasource.initialSize=5
    spring.datasource.minIdle=5
    spring.datasource.maxActive=20
    # 配置获取连接等待超时的时间
    spring.datasource.maxWait=60000
    # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    spring.datasource.timeBetweenEvictionRunsMillis=60000
    # 配置一个连接在池中最小生存的时间，单位是毫秒
    spring.datasource.minEvictableIdleTimeMillis=300000
    spring.datasource.validationQuery=SELECT 1 FROM DUAL
    spring.datasource.testWhileIdle=true
    spring.datasource.testOnBorrow=false
    spring.datasource.testOnReturn=false
    # 打开PSCache，并且指定每个连接上PSCache的大小
    spring.datasource.poolPreparedStatements=true
    spring.datasource.maxPoolPreparedStatementPerConnectionSize=20
    # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    spring.datasource.filters=stat,wall,log4j
    # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
    spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
    # 合并多个DruidDataSource的监控数据
    #spring.datasource.useGlobalDataSourceStat=true
    需要注意的是：spring.datasource.type旧的spring boot版本是不能识别的。

### 配置StatView的Servlet

 Filter的实现类：
    package com.wt.filter;
    import javax.servlet.annotation.WebFilter;  
    import javax.servlet.annotation.WebInitParam;  
      
    import com.alibaba.druid.support.http.WebStatFilter;  
      
    @WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",  
        initParams={  
            @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//忽略资源  
       }  
    )  
    public class DruidStatFilter extends WebStatFilter {  
      
    }  


StatViewServlet:

    package com.wt.servlet;
    import javax.servlet.annotation.WebInitParam;  
    import javax.servlet.annotation.WebServlet; 
    import com.alibaba.druid.support.http.StatViewServlet;  
      
    @WebServlet(urlPatterns="/druid/*",  
        initParams={  
             @WebInitParam(name="allow",value="127.0.0.1,172.16.1.182"),// IP白名单(没有配置或者为空，则允许所有访问)  
             @WebInitParam(name="deny",value="192.168.1.73"),// IP黑名单 (存在共同时，deny优先于allow)  
             @WebInitParam(name="loginUsername",value="admin"),// 用户名  
             @WebInitParam(name="loginPassword",value="123456"),// 密码  
             @WebInitParam(name="resetEnable",value="false")// 禁用HTML页面上的“Reset All”功能  
    })  
    public class DruidStatViewServlet extends StatViewServlet {  
        private static final long serialVersionUID = -2688872071445249539L;  
      
    }  

 访问地址： http://xxx.xxx.x.xxx:xxxx/druid/index.html