---
layout: post
title:  "Spring  Boot Redis"
date:   2017-02-06 08:27:00
categories: springboot
excerpt: Spring  Boot Redis
---

* content
{:toc}




### Redis简介


Redis是一个开源（BSD许可），内存存储的数据结构服务器，可用作数据库，高速缓存和

消息队列代理。它支持字符串、哈希表、列表、集合、有序集合，位图，hyperloglogs等

数据类型。内置复制、Lua脚本、LRU收回、事务以及不同级别磁盘持久化功能，同时通过R

edis Sentinel提供高可用，通过Redis Cluster提供自动分区

Redis有三个主要特点，使它优越于其它键值数据存储系统 -

* Redis将其数据库完全保存在内存中，仅使用磁盘进行持久化。


* 与其它键值数据存储相比，Redis有一组相对丰富的数据类型。

* Redis可以将数据复制到任意数量的从机中。


### 添加jar包依赖


    
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-redis</artifactId>
    </dependency>


### 创建类

配置文件：

        spring.redis.database=0
        spring.redis.hostName=主机IP
        spring.redis.port=6382
        spring.redis.password=
        spring.redis.pool.max-active=8
        spring.redis.pool.max-wait=-1
        spring.redis.pool.max-idle=8
        spring.redis.pool.min-idle=0
        spring.redis.timeout=0






RedisConfig类：

    import com.wt.utils.RedisObjectSerializer;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.redis.connection.RedisConnectionFactory;
    import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.serializer.*;
    @Configuration
    public class RedisConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String,  Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }


重写RedisObjectSerializer 支持Object：


    import org.springframework.core.convert.converter.Converter;
    import org.springframework.core.serializer.support.DeserializingConverter;
    import org.springframework.core.serializer.support.SerializingConverter;
    import org.springframework.data.redis.serializer.RedisSerializer;
    import org.springframework.data.redis.serializer.SerializationException;
    public class RedisObjectSerializer implements RedisSerializer<Object> {
      private Converter<Object, byte[]> serializer = new SerializingConverter();
      private Converter<byte[], Object> deserializer = new DeserializingConverter();
      static final byte[] EMPTY_ARRAY = new byte[0];
      public Object deserialize(byte[] bytes) {
        if (isEmpty(bytes)) {
          return null;
        }
        try {
          return deserializer.convert(bytes);
        } catch (Exception ex) {
          throw new SerializationException("Cannot deserialize", ex);
        }
      }
      public byte[] serialize(Object object) {
        if (object == null) {
          return EMPTY_ARRAY;
        }
        try {
          return serializer.convert(object);
        } catch (Exception ex) {
          return EMPTY_ARRAY;
        }
      }
      private boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
      }
    }


### 测试


    public class HelloController {
      
      @Autowired
      private  RedisTemplate<String,Object> redisTemplate;
       @GetMapping("/redsTemplate")
        @ResponseBody
        public String getRedsDate(){
          this.redisTemplate.opsForValue().set("name","helloword");
          String n=(String) this.redisTemplate.opsForValue().get("name");
          return n;
        }
    }



