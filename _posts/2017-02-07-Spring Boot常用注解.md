---
layout: post
title:  "Spring Boot常用注解"
date:   2017-02-07 15:27:00
categories: springboot
excerpt:  Spring Boot常用注解
---

* content
{:toc}




### @RestController和@RequestMapping注解

*  @RequestMapping 注解提供路由信息。它告诉Spring任何来自"/"路径的HTTP请求都应该被映射到的方法。 
1.  @RequestMapping(value="/hello",method = RequestMethod.POST, consumes="application/json") 
描述：方法仅处理request Content-Type为“application/json”类型的请求。
2.  @RequestMapping("/hello")
3.  @RequestMapping(value = "/pets/{petId}", method = RequestMethod.GET, produces="application/json")
描述：方法仅处理request请求中Accept头中包含了"application/json"的请求，同时暗示了返回的内容类型为application/json;
4.  @RequestMapping(value = "/pets/{petId}", method = RequestMethod.GET, params="myParam=myValue")
描述：仅处理请求中包含了名为“myParam”，值为“myValue”的请求；
*  @RestController 
注解告诉Spring以字符串的形式渲染结果，并直接返回给调用者。


### @EnableAutoConfiguration注解

*  @EnableAutoConfiguration
 这个注解告诉Spring Boot根据添加的jar依赖猜测你想如何配置Spring。由于 spring-boot-starter-web 添加了Tomcat和Spring MVC，所以auto-configuration将假定你正在开发一个web应用并相应地对Spring进行设置。Starter POMs和Auto-Configuration：设计auto-configuration的目的是更好的使用"Starter POMs"，但这两个概念没有直接的联系。你可以自由地挑选starter POMs以外的jar依赖，并且Spring Boot将仍旧尽最大努力去自动配置你的应用。

你可以通过将 @EnableAutoConfiguration 或 @SpringBootApplication 注解添加到一个 @Configuration 类上来选择自动配置。
注：你只需要添加一个 @EnableAutoConfiguration 注解。我们建议你将它添加到主 @Configuration 类上。

如果发现应用了你不想要的特定自动配置类，你可以使用 @EnableAutoConfiguration 注解的排除属性来禁用它们。

 

### @Configuration注解

*  @Configuration
 如果你绝对需要使用基于XML的配置，我们建议你仍旧从一个 @Configuration 类开始。你可以使用附加的 @ImportResource 注解加载XML配置文件。

@Configuration注解该类，等价 与XML中配置beans；用@Bean标注方法等价于XML中配置bean

### @SpringBootApplication注解

*  @SpringBootApplication
很多Spring Boot开发者总是使用 @Configuration ， @EnableAutoConfiguration 和 @ComponentScan 注解他们的main类。由于这些注解被如此频繁地一块使用（特别是你遵循以上最佳实践时），Spring Boot提供一个方便的 @SpringBootApplication 选择。
该 @SpringBootApplication 注解等价于以默认属性使用 @Configuration ， @EnableAutoConfiguration 和 @ComponentScan 。


### @ConfigurationProperties注解

Spring Boot将尝试校验外部的配置，默认使用JSR-303（如果在classpath路径中）。你可以轻松的为你的@ConfigurationProperties类添加JSR-303 javax.validation约束注解：

    @Component  
    @ConfigurationProperties(prefix="connection")  
    public class ConnectionSettings {  
    @NotNull  
    private InetAddress remoteAddress;  
    // ... getters and setters  
    }  


### @ResponseBody注解

*  @ResponseBody
一般在异步获取数据时使用，在使用@RequestMapping后，返回值通常解析为跳转路径，加上
@responsebody后返回结果不会被解析为跳转路径，而是直接写入HTTP response body中。比如
异步获取json数据，加上@responsebody后，会直接返回json数据。

### @Component注解

*  @Component
泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。一般公共的方法我会用上这个注解
不加这个注解的话, 使用@Autowired 就不能注入进去了

### @AutoWired注解

*  @AutoWired
byType方式。把配置好的Bean拿来用，完成属性、方法的组装，它可以对类成员变量、方法及构
造函数进行标注，完成自动装配的工作。
当加上（required=false）时，就算找不到bean也不报错。

### @RequestParam

*  @RequestParam
用在方法的参数前面

如下：

        public Map<String, Object> addComment(@RequestParam("applyId") Integer applyId) {  
                ....  
                return result;  
            } 

### @PathVariable注解

*  @PathVariable
路径变量

如下：

    RequestMapping("user/get/mac/{macAddress}")  
    public String getByMacAddress(@PathVariable String macAddress){  
    //do something;  
    } 
参数与大括号里的名字一样要相同。



###JPA注解

* @Entity：
* @Table(name="")：
表明这是一个实体类。一般用于jpa
这两个注解一般一块使用，但是如果表名和实体类名相同的话，@Table可以省略


* @MappedSuperClass:

用在确定是父类的entity上。父类的属性子类可以继承。


* @NoRepositoryBean:

一般用作父类的repository，有这个注解，spring不会去实例化该repository。

* @Column：
如果字段名与列名相同，则可以省略。


* @Id：
表示该属性为主键。


* @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "repair_seq")：
表示主键生成策略是sequence（可以为Auto、IDENTITY、native等，Auto表示可在多个数据库间
切换），指定sequence的名字是repair_seq。


* @SequenceGeneretor(name = "repair_seq", sequenceName = "seq_repair", allocationSize = 1)
name为sequence的名称，以便使用，sequenceName为数据库的sequence名称，两个名称可以一致。


* @Transient：
表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性.
如果一个属性并非数据库表的字段映射,就务必将其标示为@Transient,否则,ORM框架默认其注解为@Basic

* @Basic(fetch=FetchType.LAZY)：
标记可以指定实体属性的加载方式


* @JsonIgnore：
作用是json序列化时将java bean中的一些属性忽略掉,序列化和反序列化都受影响。


* @JoinColumn（name="loginId"）:
一对一：本表中指向另一个表的外键。
一对多：另一个表指向本表的外键。


* @OneToOne
* @OneToMany
* @ManyToOne
对应Hibernate配置文件中的一对一，一对多，多对一。
