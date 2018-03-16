---
layout: post
title:  "Spring Boot 启动加载数据 CommandLineRunner"
date:   2017-02-05 13:39:00
categories: springboot
excerpt: Spring Boot 启动加载数据 CommandLineRunner 
---

* content
{:toc}


### CommandLineRunner 

创建实现接口 CommandLineRunner 的类

    import org.springframework.boot.CommandLineRunner;
    import org.springframework.stereotype.Component;
    /**
     * 服务启动执行
     *
     */
    @Component
    @Order(value=1)
    public class MyStartupRunner implements CommandLineRunner {
        @Override
        public void run(String... args) throws Exception {
            System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
        }
    }

@Order 注解的执行优先级是按value值从小到大顺序。 


### 参考文献

* [小单的博客专栏](http://blog.csdn.net/catoop/article/details/50501710)

