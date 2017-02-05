---
layout: post
title:  " Spring Boot 日志记录  "
date:   2017-02-05 13:39:00
categories: springboot
excerpt:  Spring Boot 日志记录 
---

* content
{:toc}

默认情况下，spring boot使用Logback作为日志实现的框架
下面有2中方式配置

### logback配置

Spring Boot 提供了一套日志系统，logback是最优先的选择。配置了logback.xml可以利用Spring Boot提供的默认日志配置：

使用步骤： 
将logbak.xml拷贝至resource目录下的根目录，然后在logbak.xml中，配置相关的log生成规则，log级别，以及日志路径，log的字符编码集，这个非常重要，因为刚开始用这个log记录程序运行的信息时，发现它不支持中文log，后来经查名，需要配置相关的log编码才可以正确记录对应的信息。一个通用的配置如下： 

Xml代码   

    <!-- Logback configuration. See http://logback.qos.ch/manual/index.html -->  
    <configuration scan="true" scanPeriod="10 seconds">  
        
      <!-- Simple file output -->  
      <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">  
        <!-- encoder defaults to ch.qos.logback.classic.encoder.PatternLayoutEncoder -->  
        <encoder>  
            <pattern>  
                [ %-5level] [%date{yyyy-MM-dd HH:mm:ss}] %logger{96} [%line] - %msg%n  
            </pattern>  
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->  
        </encoder>  
      
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">  
          <!-- rollover daily 配置日志所生成的目录以及生成文件名的规则 -->  
          <fileNamePattern>logs/mylog-%d{yyyy-MM-dd}.%i.log</fileNamePattern>  
          <timeBasedFileNamingAndTriggeringPolicy  
              class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">  
            <!-- or whenever the file size reaches 64 MB -->  
            <maxFileSize>64 MB</maxFileSize>  
          </timeBasedFileNamingAndTriggeringPolicy>  
        </rollingPolicy>  
      
      
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">  
          <level>DEBUG</level>  
        </filter>  
        <!-- Safely log to the same file from multiple JVMs. Degrades performance! -->  
        <prudent>true</prudent>  
      </appender>  
      
      
      <!-- Console output -->  
      <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">  
        <!-- encoder defaults to ch.qos.logback.classic.encoder.PatternLayoutEncoder -->  
          <encoder>  
              <pattern>  
                  [ %-5level] [%date{yyyy-MM-dd HH:mm:ss}] %logger{96} [%line] - %msg%n  
              </pattern>  
              <charset>GBK</charset> <!-- 此处设置字符集 -->  
          </encoder>  
        <!-- Only log level WARN and above -->  
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">  
          <level>WARN</level>  
        </filter>  
      </appender>  
      
      
      <!-- Enable FILE and STDOUT appenders for all log messages.  
           By default, only log at level INFO and above. -->  
      <root level="INFO">  
        <appender-ref ref="FILE" />  
        <appender-ref ref="STDOUT" />  
      </root>  
      
      <!-- For loggers in the these namespaces, log at all levels. -->  
      <logger name="pedestal" level="ALL" />  
      <logger name="hammock-cafe" level="ALL" />  
      <logger name="user" level="ALL" />  
    </configuration>  


在Spring Boot 中记录日志只需两步：
1、在 src/main/resources 下面创建logback.xml 文件，
或者使用最简单的方法在 application 配置文件中配置。

logback.xml配置

    <?xml version="1.0" encoding="UTF-8"?>
    <!-- Logback configuration. See http://logback.qos.ch/manual/index.html -->
    <configuration scan="true" scanPeriod="10 seconds">
        <include resource="org/springframework/boot/logging/logback/base.xml" />
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${LOG_PATH}/info.log</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/info-%d{yyyyMMdd}.log.%i</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>500MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>2</maxHistory>
        </rollingPolicy>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%msg%n
            </Pattern>
        </layout>
    </appender>
    <appender name="consoleAppender" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
    <Pattern>.%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg %n</Pattern>
     </encoder>
       <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>TRACE</level>
      </filter>
    </appender>
        <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
                <level>ERROR</level>
            </filter>
            <File>${LOG_PATH}/error.log</File>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>${LOG_PATH}/error-%d{yyyyMMdd}.log.%i
                </fileNamePattern>
                <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                    <maxFileSize>500MB</maxFileSize>
                </timeBasedFileNamingAndTriggeringPolicy>
                <maxHistory>2</maxHistory>
            </rollingPolicy>
            <layout class="ch.qos.logback.classic.PatternLayout">
                <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%msg%n
                </Pattern>
            </layout>
        </appender>
        
    <!-- hibernate日志输入 -->
    <!-- <logger name="org.hibernate.type.descriptor.sql.BasicBinder"
        level="TRACE" />
    <logger name="org.hibernate.type.descriptor.sql.BasicExtractor"
        level="TRACE" />
    <logger name="org.hibernate.SQL" level="INFO" />
    <logger name="org.hibernate.engine.QueryParameters" level="INFO" />
    <logger name="org.hibernate.engine.query.HQLQueryPlan" level="INFO" /> -->
    <root level="INFO">
        <appender-ref ref="INFO_FILE" />
        <appender-ref ref="ERROR_FILE" />
    </root>
    </configuration>

application.properties配置文件

    #使用自定义配置文件,注意：不要使用logback这个来命名，否则spring boot将不能完全实例化
    logging.config=classpath:logback-roncoo.xml
    
    debug=true
    logging.level.root=INFO
    logging.level.org.springframework.web=DEBUG
    #日志路径
    logging.path=/log
    #日志文件名称
    logging.file=/log/spring-boot.log


2、在Java代码中创建实例，并在需要输出日志的地方使用。

    // 在Java类中创建 logger 实例
    private static final Logger logger = LoggerFactory.getLogger(SpringBootSampleApplication.class);
    // 在方法中使用日志输出，如
    public void logTest() {
        logger.debug("日志输出测试 Debug");
        logger.trace("日志输出测试 Trace");
        logger.info("日志输出测试 Info");
    }

### log4j2配置
默认的properties配置对log4j2不够友好，我们应用外部配置文件，在资源文件夹src/main/resources下添加log4j2.xml,
删除pom.xml文件中所有对日志jar包的引用
例如下面两个，如果不删除，和后面将要加入的依赖jar包会有冲突，导致日志不能正常输出到文件

      <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>log4j-over-slf4j</artifactId>
        </dependency>

排除依赖

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
                <exclusion> 
                <groupId>org.slf4j</groupId>
                <artifactId>log4j-over-slf4j</artifactId>
                </exclusion>
            </exclusions>
         </dependency>
           
     <dependency> 
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
     </dependency>

创建log4j2.xml配置

         <?xml version="1.0" encoding="UTF-8"?>  
    <Configuration status="WARN">  
        <Appenders>  
            <Console name="Console" target="SYSTEM_OUT" follow="true">  
                <PatternLayout>  
                    <pattern>%d %p %C{1.} [%t] %m%n</pattern>  
                </PatternLayout>  
            </Console>  
            <File name="File" fileName="/log/spring-boot-log4j2.log">  
                <PatternLayout>  
                    <pattern>%d %p %C{1.} [%t] %m%n</pattern>  
                </PatternLayout>  
            </File>  
        </Appenders>  
        <Loggers>  
            <Logger name="org.apache.catalina.util.LifecycleBase" level="error" />  
            <Logger name="org.apache.coyote.http11.Http11NioProtocol" level="warn" />  
            <Logger name="org.apache.tomcat.util.net.NioSelectorPool" level="warn" />  
            <Logger name="org.hibernate.validator.internal.util.Version" level="warn" />  
            <Logger name="org.springframework" level="warn" />  
            <Logger name="com.github" level="debug" />  
            <Root level="info">  
                <AppenderRef ref="Console" />
                <AppenderRef ref="File" /> 
            </Root>  
        </Loggers>  
    </Configuration>

这样，我们便能在程序中使用log了，目前我们的日志是在控制台进行输出，如果需要输出到文件中的话，添加AppenderRef ref="File"即可

最后在application.properties文件中，指定加载的log文件位置：

    logging.config=classpath:log4j2.xml  



