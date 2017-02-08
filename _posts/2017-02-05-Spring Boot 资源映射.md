---
layout: post
title:  "Spring Boot 资源映射"
date:   2017-02-05 10:39:00
categories: springboot
excerpt: 资源映射
---

* content
{:toc}


### 默认资源映射

我们在启动应用的时候，可以在控制台中看到如下信息：

    2016-01-08 09:29:30.362  INFO 24932 --- [main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/webjars/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
    2016-01-08 09:29:30.362  INFO 24932 --- [main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
    2016-01-08 09:29:30.437  INFO 24932 --- [main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**/favicon.ico] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]

其中默认配置的 /** 映射到 /static （或/public、/resources、/META-INF/resources）
其中默认配置的 /webjars/** 映射到 classpath:/META-INF/resources/webjars/
PS：上面的 static、public、resources 等目录都在 classpath: 下面（如 src/main/resources/static）。

如果我按如下结构存放相同名称的图片，那么Spring Boot 读取图片的优先级是怎样的呢？
如下图： 

![资源映射](http://img.blog.csdn.net/20160112092328665)

当我们访问地址 http://localhost:8080/fengjing.jpg 的时候，显示哪张图片？优先级顺序为：META/resources > resources > static > public
如果我们想访问pic2.jpg，请求地址 http://localhost:8080/img/pic2.jpg

### 自定义资源映射

上面我们介绍了Spring Boot 的默认资源映射，一般够用了，那我们如何自定义目录？
这些资源都是打包在jar包中的，然后实际应用中，我们还有很多资源是在管理系统中动态维护的，并不可能在程序包中，对于这种随意指定目录的资源，如何访问？

#### 自定义目录

以增加 /myres/* 映射到 classpath:/myres/* 为例的代码处理为：
实现类继承 WebMvcConfigurerAdapter 并重写方法 addResourceHandlers

        import org.springboot.sample.interceptor.MyInterceptor1;
        import org.springboot.sample.interceptor.MyInterceptor2;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
        import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
        import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
        @Configuration
        public class MyWebAppConfigurer 
                extends WebMvcConfigurerAdapter {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/myres/**").addResourceLocations("classpath:/myres/");
                super.addResourceHandlers(registry);
            }
        }

        // 访问myres根目录下的fengjing.jpg 的URL为 http://localhost:8080/fengjing.jpg （/** 会覆盖系统默认的配置）
        // registry.addResourceHandler("/**").addResourceLocations("classpath:/myres/").addResourceLocations("classpath:/static/");

其中 addResourceLocations 的参数是动参，可以这样写 addResourceLocations(“classpath:/img1/”, “classpath:/img2/”, “classpath:/img3/”);

#### 使用外部目录

如果我们要指定一个绝对路径的文件夹（如 H:/myimgs/ ），则只需要使用 addResourceLocations 指定即可。

        // 可以直接使用addResourceLocations 指定磁盘绝对路径，同样可以配置多个位置，注意路径写法需要加上file:
        registry.addResourceHandler("/myimgs/**").addResourceLocations("file:H:/myimgs/");

### 通过配置文件配置

上面是使用代码来定义静态资源的映射，其实Spring Boot也为我们提供了可以直接在 application.properties（或.yml）中配置的方法。
配置方法如下：

    # 默认值为 /**
    spring.mvc.static-path-pattern=
    # 默认值为 classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/ 
    spring.resources.static-locations=这里设置要指向的路径，多个使用英文逗号隔开，

使用 spring.mvc.static-path-pattern 可以重新定义pattern，如修改为 /myimgs/** ，则访问static 等目录下的fengjing.jpg文件应该为 http://localhost:8080/myimgs/fengjing.jpg ，修改之前为 http://localhost:8080/fengjing.jpg
使用 spring.resources.static-locations 可以重新定义 pattern 所指向的路径，支持 classpath: 和 file: （上面已经做过说明）
注意 spring.mvc.static-path-pattern 只可以定义一个，目前不支持多个逗号分割的方式。


### 参考文献

* [小单的博客专栏](http://blog.csdn.net/catoop/article/details/50501688)

