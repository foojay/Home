---
layout: post
title:  " spring-cloud-注册中心  "
date:   2017-02-05 13:39:00
categories: springboot
excerpt:  spring-cloud-注册中心
---

* content
{:toc}


### Eureka介绍

Eureka是Netflix开源的一款提供服务注册和发现的产品，它提供了完整的Service 

Registry和Service Discovery实现。也是springcloud体系中最重要最核心的组件之一。

管理各种服务功能包括服务的注册、发现、熔断、负载、降级等

用一张图来认识以下：


![](http://i63.tinypic.com/miz29x.jpg)



上图简要描述了Eureka的基本架构，由3个角色组成：

1、Eureka Server

    提供服务注册和发现

2、Service Provider

    服务提供方

    将自身服务注册到Eureka，从而使服务消费方能够找到

3、Service Consumer

    服务消费方

    从Eureka获取注册服务列表，从而能够消费服务


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
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
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

    server.port=1111
    #eureka.instance.hostname=localhost
    eureka.client.register-with-eureka=false
    eureka.client.fetch-registry=false
    eureka.client.serviceUrl.defaultZone=http://localhost:${server.port}/eureka/


程序启动



    @EnableEurekaServer
    @SpringBootApplication
    public class DemoApplication {
      public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
      }
    }

