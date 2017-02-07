---
layout: post
title:  " Spring Boot 集成Mybatis  "
date:   2017-02-05 13:39:00
categories: springboot
excerpt:  Spring Boot 集成Mybatis
---

* content
{:toc}


Spring Boot 集成MyBatis有两种方式，一种简单的方式就是使用MyBatis官方提供的：
  
    mybatis-spring-boot-starter

另外一种方式就是仍然用类似mybatis-spring的配置方式，这种方式需要自己写一些代码，但是可以很方便的控制MyBatis的各项配置。

### Mybatis-Spring-Boot-Starter方式

在pom.xml中添加依赖：

    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>


在application.properties中增加配置：

    mybatis.mapperLocations: classpath:mapper/*.xml
    mybatis.typeAliasesPackage: com.wt.mapper


除了上面常见的两项配置，还有：

    mybatis.config：mybatis-config.xml配置文件的路径
    mybatis.typeHandlersPackage：扫描typeHandlers的包
    mybatis.checkConfigLocation：检查配置文件是否存在
    mybatis.executorType：设置执行模式（SIMPLE, REUSE, BATCH），默认为SIMPLE


### Mybatis-Spring方式

这种方式和平常的用法比较接近。需要添加mybatis依赖和mybatis-spring依赖。

 <!--Mybatis-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.2.2</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.2.8</version>
        </dependency>

然后创建一个MyBatisConfig配置类：

    /**
     - MyBatis基础配置
     *
     */
    @Configuration
    @EnableTransactionManagement
    public class MyBatisConfig implements TransactionManagementConfigurer {
        @Autowired
        DataSource dataSource;
        @Bean(name ="sqlSessionFactory")
        public SqlSessionFactory sqlSessionFactoryBean() {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            bean.setTypeAliasesPackage("com.wt.model");
    /*        //分页插件
            PageHelper pageHelper = new PageHelper();
            Properties properties = new Properties();
            properties.setProperty("reasonable", "true");
            properties.setProperty("supportMethodsArguments", "true");
            properties.setProperty("returnPageInfo", "check");
            properties.setProperty("params", "count=countSql");
            pageHelper.setProperties(properties);
            //添加插件
            bean.setPlugins(new Interceptor[]{pageHelper});*/
            //添加XML目录
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                bean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
                return bean.getObject();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        @Bean
        public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
        @Bean
        @Override
        public PlatformTransactionManager annotationDrivenTransactionManager() {
            return new DataSourceTransactionManager(dataSource);
        }
    }

上面代码创建了一个SqlSessionFactory和一个SqlSessionTemplate，为了支持注解事务，增加了@EnableTransactionManagement注解，并且反回了一个PlatformTransactionManagerBean。

另外应该注意到这个配置中没有MapperScannerConfigurer，如果我们想要扫描MyBatis的Mapper接口，我们就需要配置这个类，这个配置我们需要单独放到一个类中。

    /**
     * MyBatis扫描接口
     * 
     */
    @Configuration
    //TODO 注意，由于MapperScannerConfigurer执行的比较早，所以必须有下面的注解
    @AutoConfigureAfter(MyBatisConfig.class)
    public class MyBatisMapperScannerConfig {
        @Bean
        public MapperScannerConfigurer mapperScannerConfigurer() {
            MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
            mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
            mapperScannerConfigurer.setBasePackage("com.wt.mapper");
            return mapperScannerConfigurer;
        }
    }

这个配置一定要注意@AutoConfigureAfter(MyBatisConfig.class)，必须有这个配置，否则会有异常。原因就是这个类执行的比较早，由于sqlSessionFactory还不存在，后续执行出错。

或者直接用注解 如下：

在MyBatisConfig类上加注解@MapperScan("com.wt.mapper")
MyBatisMapperScannerConfig这个类可不用


### Spring Boot 集成druid


添加pom依赖

    <!-- 使用数据源 -->
         <dependency>
              <groupId>com.alibaba</groupId>
             <artifactId>druid</artifactId>
             <version>1.0.14</version>
         </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

连接池的配置信息 
application.yaml

    spring:
        datasource:
            name: test
            url: jdbc:mysql://192.168.16.137:3306/test
            username: root
            password:
            # 使用druid数据源
            #type: com.alibaba.druid.pool.DruidDataSource
            driver-class-name: com.mysql.jdbc.Driver
            filters: stat
            maxActive: 20
            initialSize: 1
            maxWait: 60000
            minIdle: 1
            timeBetweenEvictionRunsMillis: 60000
            minEvictableIdleTimeMillis: 300000
            validationQuery: select 'x'
            testWhileIdle: true
            testOnBorrow: false
            testOnReturn: false
            poolPreparedStatements: true
            maxOpenPreparedStatements: 20


application.properties

    #连接池的配置信息  
    spring.datasource.initialSize=5  
    spring.datasource.minIdle=5  
    spring.datasource.maxActive=20  
    spring.datasource.maxWait=60000  
    spring.datasource.timeBetweenEvictionRunsMillis=60000  
    spring.datasource.minEvictableIdleTimeMillis=300000  
    spring.datasource.validationQuery=SELECT 1 FROM DUAL  
    spring.datasource.testWhileIdle=true  
    spring.datasource.testOnBorrow=false  
    spring.datasource.testOnReturn=false  
    spring.datasource.poolPreparedStatements=true  
    spring.datasource.maxPoolPreparedStatementPerConnectionSize=20  
    spring.datasource.filters=stat,wall,log4j  
    spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000 





代码

    /**
     * MyBatis基础配置
     *
     */
    @Configuration
    @EnableTransactionManagement
    public class MyBatisConfig implements TransactionManagementConfigurer {
        
        
        /**
      * 创建数据源
      * @return
      */
     @Bean  
      @ConfigurationProperties(prefix = "spring.datasource")  
      public DataSource druidDataSource() {  
          DruidDataSource druidDataSource = new DruidDataSource();  
          return druidDataSource;  
      }  
        
        /**
          * 根据数据源创建SqlSessionFactory
          */
          @Bean(name ="sqlSessionFactory")
          public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
              SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
          
              bean.setDataSource(druidDataSource()); //指定数据源(这个必须有，否则报错)
              bean.setTypeAliasesPackage("com.wt.model"); //指定基包
      /*        //分页插件
              PageHelper pageHelper = new PageHelper();
              Properties properties = new Properties();
              properties.setProperty("reasonable", "true");
              properties.setProperty("supportMethodsArguments", "true");
              properties.setProperty("returnPageInfo", "check");
              properties.setProperty("params", "count=countSql");
              pageHelper.setProperties(properties);
              //添加插件
              bean.setPlugins(new Interceptor[]{pageHelper});*/
              //添加XML目录
              ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
              try {
                  bean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml")); //指定xml文件位置
                  return bean.getObject();
              } catch (Exception e) {
                  e.printStackTrace();
                  throw new RuntimeException(e);
              }
          }
          @Bean
          public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
              return new SqlSessionTemplate(sqlSessionFactory);
          }
          @Bean
          @Override
          public PlatformTransactionManager annotationDrivenTransactionManager() {
              return new DataSourceTransactionManager(druidDataSource());
          }
      }

   @Primary表示这里定义的DataSource将覆盖其他来源的DataSource