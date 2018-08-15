---
layout: post
title:  " spring-cloud-服务发布  "
date:   2017-02-05 13:39:00
categories: springboot
excerpt:  spring-cloud-服务发布
---

* content
{:toc}


### 服务提供者

服务注册：

服务提供者在启动的时候通过发送Rest请求的方式将自己注册到Eureka 

Server上，同时带上了自身服务的一些元数据信息。Eureka Server在收到这个请求后，将

元数据信息存储在一个双层结构Map中，第一层的key是服务名，第二层的key是具体服务的

实例名。


服务同步：

两个服务提供者分别注册到两个不同的服务注册中心上，因为服务中心之间是相互注册为

服务的，所以当服务提供者发送注册请求到一个服务注册中心时，它会将该请求转发给集

群中的其他注册中心，从而实现服务注册中心之间的服务同步，通过服务同步，两个服务

提供者的服务信息就可以通过这两台服务注册中心的任意一台获取到。


服务续约：

服务提供者在注册完后，会定期向注册中心发送一个“心跳"来告诉Eureka 

Server自己还存活着，以防被Eureka Servertichu踢出服务列表。

eureka.instance.lease-renewal-interval-in-seconds= 30 

这个参数是定义服务续约的调用间隔时间，默认为30秒。

eureka.instance.lease-expiration-duration-in-seconds= 90 

这个参数是定义服务失效时间，默认为90秒。





### 搭建

使用框架

    SpringBoot 2.0.3
    SpringCloud Finchley.RELEASE


pom文件


    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.wt</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>
    <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.3.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <spring-cloud.version>Finchley.RELEASE</spring-cloud.version>
    </properties>
    <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    </dependencies>
     <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>${spring-cloud.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
     </dependencyManagement>
    <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
    </build>
    </project>


application.properties

    server.port=2222
	spring.application.name=eureka-service
    eureka.client.serviceUrl.defaultZone=http://localhost:1111/eureka/



controller 


    @RestController
    public class HelloController {
        @Autowired
        private UserRepository userRepository;
        @RequestMapping(value = "/hello",method =RequestMethod.GET)
        public User getHello() {
            return this.userRepository.getItem("foojay");
        }
    }







程序启动



    @SpringBootApplication
    public class DemoApplication {
      public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
      }
    }

