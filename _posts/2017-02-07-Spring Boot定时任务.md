---
layout: post
title:  "Spring Boot定时任务"
date:   2017-02-07 11:27:00
categories: springboot
excerpt:  Spring Boot定时任务
---

* content
{:toc}




### 代码

代码如下：



    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.scheduling.annotation.EnableScheduling;
    import org.springframework.scheduling.annotation.Scheduled;
    /**
     * 定时任务配置类
     *
     */
    @Configuration
    @EnableScheduling // 启用定时任务
    public class SchedulingConfig {
        private final Logger logger = LoggerFactory.getLogger(getClass());
        @Scheduled(cron = "0/20 * * * * ?") // 每20秒执行一次
        public void scheduler() {
            logger.info(">>>>>>>>>>>>> 定时任务执行 ... ");
        }
    }

@Scheduled 注解用于标注这个方法是一个定时任务的方法，我们也可以使用更灵活的设置方法 @Scheduled(cron="...") ，用一个表达式来设置定时任务。 
其中 @EnableScheduling 注解的作用是发现注解@Scheduled的任务并后台执行。 