---
layout: post
title:  "Spring Boot 属性注入"
date:   2017-02-07 17:27:00
categories: springboot
excerpt:  Spring Boot 属性注入
---

* content
{:toc}




### 配置文件

    foo.enabled=false
    foo.remote-address=172.16.1.65
    foo.security.username=admin
    foo.security.password=123456
    foo.security.roles=admin,student


读取配置文件映射POJO

    package com.wt.config;
    import java.net.InetAddress;
    import java.util.ArrayList;
    import java.util.List;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;
        @ConfigurationProperties(prefix = "foo")
        public class FooProperties {
            private boolean enabled;
            private InetAddress remoteAddress;
            private final Security security = new Security();
            
        
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public InetAddress getRemoteAddress() {
            return remoteAddress;
        }
        public void setRemoteAddress(InetAddress remoteAddress) {
            this.remoteAddress = remoteAddress;
        }
        public Security getSecurity() {
            return security;
        }
        public static class Security {
            
            private String username;
            private String password;
            private List<String> roles = new ArrayList<>();
            public String getUsername() {
                return username;
            }
            public void setUsername(String username) {
                this.username = username;
            }
            public String getPassword() {
                return password;
            }
            public void setPassword(String password) {
                this.password = password;
            }
            public List<String> getRoles() {
                return roles;
            }
            public void setRoles(List<String> roles) {
                this.roles = roles;
            }
            
            
        }
    }


注入类

    package com.wt.config;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.stereotype.Component;
    @Configuration
    @EnableConfigurationProperties(FooProperties.class) 
    public class MyConfiguration {
        
        @Autowired
        private FooProperties fooProperties;
        
        public String getName(){
            return fooProperties.getSecurity().getUsername();
            
            
            
        }
    }

//启用@EnableConfigurationProperties这个注解 FooProperties.class类上不用加@Component,不启用不加的话会报错