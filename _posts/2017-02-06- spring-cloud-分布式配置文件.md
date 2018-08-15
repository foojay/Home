---
layout: post
title:  " spring-cloud-分布式配置文  "
date:   2017-02-05 13:39:00
categories: springboot
excerpt:  spring-cloud-分布式配置文
---

* content
{:toc}


### Spring Cloud Config

是用来为分布式系统中的基础设施和微服务应用提供集中化的外部配置支持，它分为服务

端与客户端两个部分。其中服务端也称为分布式配置中心，它是一个独立的微服务应用，

用来连接配置仓库并为客户端提供获取配置信息、加密 / 解密信息等访问接口；而客户端

则是微服务架构中的各个微服务应用或基础设施，它们通过指定的配置中心来管理应用资

源与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。Spring 

Cloud Config 

实现了对服务端和客户端中环境变量和属性配置的抽象映射，所以它除了适用于 Spring 

构建的应用程序之外，也可以在任何其他语言运行的应用程序中使用。由于 Spring 

Cloud Config 实现的配置中心默认采用 Git 来存储配置信息，所以使用 Spring Cloud 

Config 

构建的配置服务器，天然就支持对微服务应用配置信息的版本管理，并且可以通过 Git 客


户端工具来方便的管理和访问配置内容。当然它也提供了对其他存储方式的支持，比如：S

VN 仓库、本地化文件系统。



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
            <artifactId>spring-cloud-config-server</artifactId>
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

spring.profiles.active=native来配置config 

server从本地读取配置读配置的路径为classpath下的config目录。


    server.port=8769
    spring.application.name=config-server
    spring.profiles.active=native
    cloud:config.server.native.search-locations=classpath:/config



在resources目录下新建config文件夹，存放配置文件，新建文件config-client-dev.

properties，用作eureka-client工程的dev（开发环境）的配置文件。在config-client-

dev.properties配置文件中，指定端口号8762，定义一个变量foo，值为foo version 1。

    server.port=8762
    foo=foo version 1



程序启动



    @SpringBootApplication
    @EnableConfigServer
    public class DemoApplication {
      public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
      }
    }




### config-client

新建工程config-client，该工程作为Config Client从Config Server读取配置文件

    <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>


在配置文件bootstrap.yml中做相关配置。注意，这些与 Spring Cloud Config 

相关的属性必须配置在 bootstrap.yml 中，config 部分内容才能被正确加载，因为 

config 的相关配置会先于 application.yml，而 bootstrap.yml 的加载也是先于 

application.yml。指定程序名config-

client，向Url地址为http://localhost:8769的Config 

Server读取配置文件。如果没有读取成功则执行快速失败（fail-

fast），读取的是dev文件。配置文件中的变量{spring.application.name}和{spring.

profiles.active}，两者以 “-” 相连，构成了向Config 

Server读取的配置文件名，config-client-dev.properties。


    spring:
    application:
        name: config-client
      profiles:
        active: dev
      cloud:
        config:
          uri: http://localhost:8769
          fail-fast: true

###  Config Server从远程Git仓库读取配置文件


        server:
          port: 8769
        spring:
          application:
            name: config-server
          cloud:
            config:
              server:
                git:
                  uri: https://github.com/cralor7/springcloud
                  search-paths: config-repo
                #  username:
                #  password:
                  default-label: master