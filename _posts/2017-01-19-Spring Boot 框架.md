---
layout: post
title:  "springboot框架"
date:   2017-01-04 14:39:00
categories: springboot
excerpt: springboot框架
---

* content
{:toc}

### 项目预览

 ![项目预览](http://i68.tinypic.com/14mvuvr.jpg)

### 创建Maven

 * [创建Maven](http://jingyan.baidu.com/article/9f63fb91a7d2a5c8400f0e20.html)

### Maven配置

完整的【pom.xml】配置如下：

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>cn.7player.framework</groupId>
      <artifactId>springboot-mybatis</artifactId>
      <version>1.0-SNAPSHOT</version>
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>1.2.5.RELEASE</version>
      </parent>
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <java.version>1.7</java.version>
      </properties>
      <dependencies>
          <!--Spring Boot-->
              <!--支持 Web 应用开发，包含 Tomcat 和 spring-mvc。 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <!--模板引擎-->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-thymeleaf</artifactId>
          </dependency>
          <!--支持使用 JDBC 访问数据库-->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-jdbc</artifactId>
          </dependency>
          <!--添加适用于生产环境的功能，如性能指标和监测等功能。 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
          </dependency>
          <!--Mybatis-->
          <dependency>
              <groupId>org.mybatis</groupId>
              <artifactId>mybatis-spring</artifactId>
              <version>1.2.2</version>
          </dependency>
          <dependency>
              <groupId>org.mybatis</groupId>
              <artifactId>mybatis</artifactId>
              <version>3.2.8</version>
          </dependency>
          <!--Mysql / DataSource-->
          <dependency>
              <groupId>org.apache.tomcat</groupId>
              <artifactId>tomcat-jdbc</artifactId>
          </dependency>
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
          </dependency>
          <!--Json Support-->
          <dependency>
              <groupId>com.alibaba</groupId>
              <artifactId>fastjson</artifactId>
              <version>1.1.43</version>
          </dependency>
          <!--Swagger support-->
          <dependency>
              <groupId>com.mangofactory</groupId>
              <artifactId>swagger-springmvc</artifactId>
              <version>0.9.5</version>
          </dependency>
      </dependencies>
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
      <repositories>
          <repository>
              <id>spring-milestone</id>
              <url>https://repo.spring.io/libs-release</url>
          </repository>
      </repositories>
      <pluginRepositories>
          <pluginRepository>
              <id>spring-milestone</id>
              <url>https://repo.spring.io/libs-release</url>
          </pluginRepository>
      </pluginRepositories>
        </project>

### 主函数

 【Application.java】包含main函数，像普通java程序启动即可。

此外，该类中还包含和数据库相关的DataSource，SqlSeesion配置内容。

注：@MapperScan(“cn.no7player.mapper”) 表示Mybatis的映射路径（package路径）

    import org.apache.ibatis.session.SqlSessionFactory;
    import org.apache.log4j.Logger;
    import org.mybatis.spring.SqlSessionFactoryBean;
    import org.mybatis.spring.annotation.MapperScan;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
    import org.springframework.jdbc.datasource.DataSourceTransactionManager;
    import org.springframework.transaction.PlatformTransactionManager;
     
    import javax.sql.DataSource;
     
    @EnableAutoConfiguration
    @SpringBootApplication
    @ComponentScan
    @MapperScan("com.wt.mapper")
    public class Application {
        private static Logger logger = Logger.getLogger(Application.class);
     
        //DataSource配置
        @Bean
        @ConfigurationProperties(prefix="spring.datasource")
        public DataSource dataSource() {
            return new org.apache.tomcat.jdbc.pool.DataSource();
        }
     
        //提供SqlSeesion
        @Bean
        public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
     
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource());
     
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
     
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
     
            return sqlSessionFactoryBean.getObject();
        }
     
        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }
     
        /**
         * Main Start
         */
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
            logger.info("============= SpringBoot Start Success =============");
        }
     
    }

### Controller


 请求入口Controller部分提供三种接口样例：视图模板，Json，restful风格

（1）视图模板

返回结果为视图文件路径。视图相关文件默认放置在路径 resource/templates下：


    import org.apache.log4j.Logger;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
     
    @Controller
    public class HelloController {
     
        private Logger logger = Logger.getLogger(HelloController.class);
     
        /*
        *   http://localhost:8080/hello?name=cn.7player
         */
     
        @RequestMapping("/hello")
        public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
            logger.info("hello");
            model.addAttribute("name", name);
            return "hello";
        }
        
    }

  （2）Json

  返回Json格式数据，多用于Ajax请求。

    package cn.no7player.controller;
     
    import cn.no7player.model.User;
    import cn.no7player.service.UserService;
    import org.apache.log4j.Logger;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.ResponseBody;
     
    @Controller
    public class UserController {
     
        private Logger logger = Logger.getLogger(UserController.class);
     
        @Autowired
        private UserService userService;
     
        /*
         *  http://localhost:8080/getUserInfo
         */
     
        @RequestMapping("/getUserInfo")
        @ResponseBody
        public User getUserInfo() {
            User user = userService.getUserInfo();
            if(user!=null){
                System.out.println("user.getName():"+user.getName());
                logger.info("user.getAge():"+user.getAge());
            }
            return user;
        }
    }

（3）restful

REST 指的是一组架构约束条件和原则。满足这些约束条件和原则的应用程序或设计就是 RESTful。

此外，有一款RESTFUL接口的文档在线自动生成+功能测试功能软件——Swagger UI，具体配置过程可移步《Spring Boot 利用 Swagger 实现restful测试》

    package cn.no7player.controller;
     
    import cn.no7player.model.User;
    import com.wordnik.swagger.annotations.ApiOperation;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;
     
    import java.util.ArrayList;
    import java.util.List;
     
    @RestController
    @RequestMapping(value="/users")
    public class SwaggerController {
        
        /*
         *  http://localhost:8080/swagger/index.html
         */
     
        @ApiOperation(value="Get all users",notes="requires noting")
        @RequestMapping(method=RequestMethod.GET)
        public List<User> getUsers(){
            List<User> list=new ArrayList<User>();
     
            User user=new User();
            user.setName("hello");
            list.add(user);
     
            User user2=new User();
            user.setName("world");
            list.add(user2);
            return list;
        }
     
        @ApiOperation(value="Get user with id",notes="requires the id of user")
        @RequestMapping(value="/{name}",method=RequestMethod.GET)
        public User getUserById(@PathVariable String name){
            User user=new User();
            user.setName("hello world");
            return user;
        }
    }

5.Mybatis

配置相关代码在Application.java中体现。

（1）【application.properties】

    spring.datasource.url=jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=gbk&zeroDateTimeBehavior=convertToNull
    spring.datasource.username=root
    spring.datasource.password=123456
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver

注意，在Application.java代码中，配置DataSource时的注解

@ConfigurationProperties(prefix=“spring.datasource”) 

表示将根据前缀“spring.datasource”从application.properties中匹配相关属性值。

（2）【UserMapper.xml】

Mybatis的sql映射文件。Mybatis同样支持注解方式，在此不予举例了。

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.wt.mapper.UserMapper">
     
      <select id="findUserInfo" resultType="com.wt.model.User">
        select name, age,password from user;
      </select>
     
    </mapper>

（3）接口UserMapper

    package com.wt.mapper;
     
    import cn.no7player.model.User;
     
    public interface UserMapper {
        public User findUserInfo();
    }


### 项目部署到tomcat


要部署在自己的Tomcat中的时候需要添加Java EE,或者是J2EE依赖包。否则在Application类中继承SpringBootServletInitializer的时候会报错。

      　　<!--如果要部署到自己的tomcat中，这一项配置必不可少，否则生成的war文件将无法执行。 如果不用部署到自己的Tomcat中，这一个依赖可以去掉-->
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-tomcat</artifactId>
                  <scope>provided</scope>
              </dependency>
      　　<!--在用maven 编译，打包过程中会出现javax.servlet找不到的情况，所以需要在这里配置-->
              <dependency>
                  <groupId>javax.servlet</groupId>
                  <artifactId>javax.servlet-api</artifactId>
              </dependency>


      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
      import org.springframework.boot.builder.SpringApplicationBuilder;
      import org.springframework.boot.context.web.SpringBootServletInitializer;
      import org.springframework.context.annotation.ComponentScan;
      import org.springframework.context.annotation.Configuration;
      @Configuration
      @ComponentScan
      @EnableAutoConfiguration
      public class Application extends SpringBootServletInitializer {
          public static void main(String[] args) {
              SpringApplication.run(Application.class, args);
          }
      　　/**
           * 如果要发布到自己的Tomcat中的时候，需要继承SpringBootServletInitializer类，并且增加如下的configure方法。
           * 如果不发布到自己的Tomcat中的时候，就无需上述的步骤
           */
          protected SpringApplicationBuilder configure(
                  SpringApplicationBuilder application) {
              return application.sources(Application.class);
          }
      }



这里还要多说一句， SpringBoot 默认有内嵌的 tomcat 模块，因此，我们要把这一部分排除掉。
即：我们在 spring-boot-starter-web 里面排除了 spring-boot-starter-tomcat ，但是我们为了在本机测试方便，我们还要引入它，所以我们这样写：

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!--provided 发布到tomcat不打包>
    <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-tomcat</artifactId>
                  <scope>provided</scope>
    </dependency>


另外，我们还可以使用 war 插件来定义打包以后的 war 包名称，以免 maven 为我们默认地起了一个带版本号的 war 包名称。例如：

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-war-plugin</artifactId>
        <configuration>
            <warName>springboot</warName>
        </configuration>
    </plugin>


### 参考文献

*[7player](http://7player.cn/2015/08/30/%E3%80%90%E5%8E%9F%E5%88%9B%E3%80%91%E5%9F%BA%E4%BA%8Espringboot-mybatis%E5%AE%9E%E7%8E%B0springmvc-web%E9%A1%B9%E7%9B%AE/)
*[李威威的专栏](http://blog.csdn.net/lw_power/article/details/46843489)

