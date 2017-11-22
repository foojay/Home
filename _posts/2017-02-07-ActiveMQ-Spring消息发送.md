---
layout: post
title:  "ActiveMQ-Spring消息发送"
date:   2017-02-07 11:27:00
categories: java
excerpt:  ActiveMQ-Spring消息发送
---

* content
{:toc}




### 基本使用方法

1.POM依赖


    <dependency>  
        <groupId>org.springframework</groupId>  
        <artifactId>spring-jms</artifactId>  
        <version>${spring-version}</version>  
    </dependency>  
    <dependency>  
        <groupId>org.apache.activemq</groupId>  
        <artifactId>activemq-core</artifactId>  
        <version>5.7.0</version>  
    </dependency>


 
 2.现在假设生产者和消费者是两个不同的系统，那么就有两套连接，两套不同的配置。其

 实大多数配置都是基本一致，例如创建连接，创建jmsTemplate，创建消息目的地destina

 tion，消费者比生产者多了一个消息监听的配置项，生产者比消费者多了一个消息创建的

 自定义bean的配置。


 相同部分：

    <!-- ActiveMQ的连接实现类 -->
    <bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">  
        <property name="brokerURL" value="tcp://localhost:61616"/>  
    </bean>    
    <!-- Spring用于管理真正的ConnectionFactory的ConnectionFactory -->  
    <bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">  
        <property name="targetConnectionFactory" ref="targetConnectionFactory"/>  
    </bean> 
    <!-- Spring提供的JMS工具类，它可以进行消息发送、接收等 -->  
    <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">  
        <property name="connectionFactory" ref="connectionFactory"/>  
    </bean>   
    <!--这个是队列目的地-->  
    <bean id="destination" class="org.apache.activemq.command.ActiveMQTopic">  
        <constructor-arg>  
            <value>topic_js2</value>  
        </constructor-arg>  
    </bean>
    或者：
    <bean id="queueDestination" class="org.apache.activemq.command.ActiveMQQueue">  
        <constructor-arg>  
            <value>queue</value>  
        </constructor-arg>  
    </bean>


    生产者的消息创建bean：
    <!-- 自定义发送消息bean -->
    <bean id="producerService" class="com.ds.spring.activemq.consumer.ProducerServiceImpl"></bean>  



消费者消息监听bean：

    <!-- 自定义消息监听器 -->  
    <bean id="consumerMessageListener" class="com.ds.spring.activemq.producer.ConsumerMessageListener"/>  
    <!-- 消息监听容器 -->  
    <bean id="jmsContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">  
        <property name="connectionFactory" ref="connectionFactory" />  
        <property name="destination" ref="destination" />  
        <property name="messageListener" ref="consumerMessageListener" /> 
    </bean>


3.自定义生产者 


由于在配置文件中已经定义了连接，jmsTemplate和destination。jmsTemplate里边内容比较丰富，他帮你完成了session和producer的创建，只需要调用send()方法就一步到位了。本例中使用的spring的注解，所以要在spring的配置中开启注解扫描。

    <context:component-scan base-package="com.ds" /> 

    package com.ds.spring.activemq.consumer;
    import javax.jms.Destination;
    import javax.jms.JMSException;
    import javax.jms.Message;
    import javax.jms.Session;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.jms.core.JmsTemplate;
    import org.springframework.jms.core.MessageCreator;
    import org.springframework.stereotype.Component;
    @Component
    public class ProducerServiceImpl implements ProducerService {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination destination;
    @Override
    public void sendMessage(final String message) {  
        System.out.println("---------------生产者发了一个消息：" + message);  
        jmsTemplate.send(destination, new MessageCreator() {  
            @Override
            public Message createMessage(Session session) throws JMSException {  
                return session.createTextMessage(message);  
            }  
        });  
    }
      }


4.自定义消费者 

消费者就比较简单了，只需要继承MessageListener，实现监听事件的方法就行了。

    package com.ds.spring.activemq.producer;
    import javax.jms.JMSException;
    import javax.jms.Message;
    import javax.jms.MessageListener;
    import javax.jms.TextMessage;
    public class ConsumerMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        //根据具体需要转换消息内容
        TextMessage textMsg = (TextMessage) message;  
        try {  
            System.out.println("消息内容是：" + textMsg.getText());  
        } catch (JMSException e) {  
            e.printStackTrace();  
        } 
    }}
