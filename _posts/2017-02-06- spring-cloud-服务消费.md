---
layout: post
title:  " spring-cloud-服务消费  "
date:   2017-02-05 13:39:00
categories: springboot
excerpt:  spring-cloud-服务消费
---

* content
{:toc}


### 服务消费者

服务注册：

通过@EnableDiscoveryClient注解启动，将自身注册为服务消费客户端。



获取服务：

发送REST请求给服务注册中心，服务注册中心会返回给服务消费者一个只读的服务清单。

该缓存清单默认是30秒更新一次。

eureka.client.registry-fetch-interval-seconds= 30 

这个参数用来修改缓存清单更新的时间间隔，时间单位为秒。



服务调用：

服务消费者在获取服务清单后，通过服务名可以获得具体提供服务的实例名和该实例的元

数据信息，服务消费者可以根据需要调用哪个实例。在Ribbon中默认采用轮询的方式进行

调用，从而实现客户端的负载均衡。




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
    <!-- feign 声明式服务调用框架 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    <!-- feign httpclient，提高http性能 -->
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-httpclient</artifactId>
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

    server.port=3333
    hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 
    spring.application.name=eureka-consumer
    feign.httpclient.enabled=true
    feign.hystrix.enabled=true //启动熔断器
    eureka.client.serviceUrl.defaultZone=http://localhost:1111/eureka/




service:

    @Service
    @FeignClient(value = "eureka-service",configuration = FeignConfig.class,fallback = HiHystrix.class)
    public interface UserService {
        @RequestMapping(value = "/getUser",method = RequestMethod.GET)
        User getUser();
    }


HiHystrix类

    @Component
    public class HiHystrix implements EurekaClientFeign {
        @Override
        public String sayHiFromClientEureka(String name) {
            return "hi,"+name+",sorry.error!";
        }
    }



FeignConfig类

    @Configuration
    public class FeignConfig {
        @Bean
        public Retryer feignRetryer(){
            return new Retryer.Default(100,TimeUnit.SECONDS.toMillis(1),5);
        }
    }






controller :


    @RestController
    public class HelloController {
        @Autowired
        private UserService userService;
        @RequestMapping(value = "/hello",method =RequestMethod.GET)
        public User getHello() {
            return this.userService.getUser();
        }
    }







程序启动



    @SpringBootApplication
    @EnableFeignClients
    public class DemoApplication {
      public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
      }
    }

