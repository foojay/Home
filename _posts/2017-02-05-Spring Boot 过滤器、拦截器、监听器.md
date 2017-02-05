---
layout: post
title:  "Spring Boot 过滤器、拦截器、监听器"
date:   2017-02-05 10:39:00
categories: springboot
excerpt: 过滤器、拦截器、监听器、servelet
---

* content
{:toc}


### 主函数

 【Application.java】包含main函数，像普通java程序启动即可。

此外，该类中还包含和数据库相关的DataSource，SqlSeesion配置内容。

注：@MapperScan(“cn.no7player.mapper”) 表示Mybatis的映射路径（package路径）

    import org.apache.ibatis.session.SqlSessionFactory;
    import org.apache.log4j.Logger;
    import org.mybatis.spring.SqlSessionFactoryBean;
    import org.mybatis.spring.annotation.MapperScan;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
    import org.springframework.jdbc.datasource.DataSourceTransactionManager;
    import org.springframework.transaction.PlatformTransactionManager;
     
    import javax.sql.DataSource;
     
    @EnableAutoConfiguration
    @SpringBootApplication
    @ComponentScan
    @MapperScan("com.wt.mapper")
    public class Application {
        private static Logger logger = Logger.getLogger(Application.class);
     
        //DataSource配置
        @Bean
        @ConfigurationProperties(prefix="spring.datasource")
        public DataSource dataSource() {
            return new org.apache.tomcat.jdbc.pool.DataSource();
        }
     
        //提供SqlSeesion
        @Bean
        public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
     
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource());
     
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
     
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
     
            return sqlSessionFactoryBean.getObject();
        }
     
        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }
     
        /**
         * Main Start
         */
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
            logger.info("============= SpringBoot Start Success =============");
        }
     
    }

### Servlet

通过代码注册Servlet示例代码：

主函数不需要@ServletComponentScan注解

    import org.springboot.sample.servlet.MyServlet;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.boot.context.embedded.ServletRegistrationBean;
    import org.springframework.boot.web.servlet.ServletComponentScan;
    import org.springframework.context.annotation.Bean;
    import org.springframework.web.servlet.DispatcherServlet;
    @SpringBootApplication
    public class SpringBootSampleApplication {
        /**
         * 使用代码注册Servlet（不需要@ServletComponentScan注解）
         */
        @Bean
        public ServletRegistrationBean servletRegistrationBean() {
            return new ServletRegistrationBean(new MyServlet(), "/xs/*");// ServletName默认值为首字母小写，即myServlet
        }
        public static void main(String[] args) {
            SpringApplication.run(SpringBootSampleApplication.class, args);
        }
        }


使用注解注册Servlet示例代码

主函数需要@ServletComponentScan注解

Servlet需要@WebServlet 注解

    import java.io.IOException;
    import java.io.PrintWriter;
    import javax.servlet.ServletException;
    import javax.servlet.annotation.WebServlet;
    import javax.servlet.http.HttpServlet;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    /**
     * Servlet
     *
     */
    @WebServlet(urlPatterns="/xs/myservlet", description="Servlet的说明") // 不指定name的情况下，name默认值为类全路径，即org.springboot.sample.servlet.MyServlet2
    public class MyServlet2 extends HttpServlet{
        private static final long serialVersionUID = -8685285401859800066L;
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            System.out.println(">>>>>>>>>>doGet2()<<<<<<<<<<<");
            doPost(req, resp);
        }
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            System.out.println(">>>>>>>>>>doPost2()<<<<<<<<<<<");
            resp.setContentType("text/html");  
            PrintWriter out = resp.getWriter();  
            out.println("<html>");  
            out.println("<head>");  
            out.println("<title>Hello World</title>");  
            out.println("</head>");  
            out.println("<body>");  
            out.println("<h1>大家好，我的名字叫Servlet2</h1>");  
            out.println("</body>");  
            out.println("</html>"); 
        }
    }

有个问题：DispatcherServlet 默认拦截“/”，MyServlet 拦截“/xs/*”，MyServlet2 拦截“/xs/myservlet”，那么在我们访问 http://localhost:8080/xs/myservlet 的时候系统会怎么处理呢？如果访问 http://localhost:8080/xs/abc 的时候又是什么结果呢？结果是“匹配的优先级是从精确到模糊，复合条件的Servlet并不会都执行”

既然系统DispatcherServlet 默认拦截“/”，那么我们是否能做修改呢，答案是肯定的，我们在主函数中添加代码：

    /**
       * 修改DispatcherServlet默认配置
       *
       * @param dispatcherServlet
       */
      @Bean
      public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
          ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
          registration.getUrlMappings().clear();
          registration.addUrlMappings("*.do");
          registration.addUrlMappings("*.json");
          return registration;
      }


### 过滤器

使用注解标注过滤器

    import java.io.IOException;
    import javax.servlet.Filter;
    import javax.servlet.FilterChain;
    import javax.servlet.FilterConfig;
    import javax.servlet.ServletException;
    import javax.servlet.ServletRequest;
    import javax.servlet.ServletResponse;
    import javax.servlet.annotation.WebFilter;
    /**
     - 使用注解标注过滤器
     - @WebFilter将一个实现了javax.servlet.Filter接口的类定义为过滤器
     - 属性filterName声明过滤器的名称,可选
     - 属性urlPatterns指定要过滤 的URL模式,也可使用属性value来声明.(指定要过滤的URL模式是必选属性)
     - 
     */
    @WebFilter(filterName="myFilter",urlPatterns="/*")
    public class MyFilter implements Filter {
        @Override
        public void destroy() {
            System.out.println("过滤器销毁");
        }
        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            System.out.println("执行过滤操作");
            chain.doFilter(request, response);
        }
        @Override
        public void init(FilterConfig config) throws ServletException {
            System.out.println("过滤器初始化");
        }
    }

### 监听器

使用注解标注监听器

    import javax.servlet.ServletContextEvent;
    import javax.servlet.ServletContextListener;
    import javax.servlet.annotation.WebListener;
    /**
     - 使用@WebListener注解，实现ServletContextListener接口
     *
     */
    @WebListener
    public class MyServletContextListener implements ServletContextListener {
        @Override
        public void contextInitialized(ServletContextEvent sce) {
            System.out.println("ServletContex初始化");
            System.out.println(sce.getServletContext().getServerInfo());
        }
        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            System.out.println("ServletContex销毁");
        }
    }


    import javax.servlet.annotation.WebListener;
    import javax.servlet.http.HttpSessionEvent;
    import javax.servlet.http.HttpSessionListener;
    /**
     * 监听Session的创建与销毁
     *
     */
    @WebListener
    public class MyHttpSessionListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            System.out.println("Session 被创建");
        }
        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            System.out.println("ServletContex初始化");
        }
    }


注意不要忘记在主函数上添加 @ServletComponentScan 注解。

在启动的过程中我们会看到输出：

    ServletContex初始化
    Apache Tomcat/8.0.30
    过滤器初始化


服务启动后，随便访问一个页面，会看到输出：

    执行过滤操作
    Session 被创建

至于如何使用代码的方式注册Filter和Listener，请参考Servlet的介绍。不同的是需要使用 FilterRegistrationBean 和 ServletListenerRegistrationBean 这两个类。

### 拦截器

实现自定义拦截器只需要3步：
1、创建我们自己的拦截器类并实现 HandlerInterceptor 接口。
2、创建一个Java类继承WebMvcConfigurerAdapter，并重写 addInterceptors 方法。
2、实例化我们自定义的拦截器，然后将对像手动添加到拦截器链中（在addInterceptors方法中添加）。

** 创建拦截器

        package com.wt.interceptor;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.stereotype.Component;
        import org.springframework.web.servlet.HandlerInterceptor;
        import org.springframework.web.servlet.ModelAndView;
        @Component
        public class MyInterceptor implements HandlerInterceptor {
             private Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
            @Override
            public void afterCompletion(HttpServletRequest arg0,
                    HttpServletResponse arg1, Object arg2, Exception arg3)
                    throws Exception {
                
                logger.info(">>>MyInterceptor1>>>>>>>在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）");
            }
            @Override
            public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                    Object arg2, ModelAndView arg3) throws Exception {
                logger.info(">>>MyInterceptor1>>>>>>>请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）");
                
            }
            @Override
            public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                    Object arg2) throws Exception {
                // TODO Auto-generated method stub
                logger.info(">>>MyInterceptor1>>>>>>>在请求处理之前进行调用（Controller方法调用之前）");
                return true;
            }
        }

** 创建一个Java类继承WebMvcConfigurerAdapter，并重写 addInterceptors 方法，然后将对像手动添加到拦截器链中

        package com.wt.config;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
        import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
        import com.wt.interceptor.MyInterceptor;
        @Configuration
        public class MyWebAppConfigurer extends WebMvcConfigurerAdapter{
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // 多个拦截器组成一个拦截器链
                // addPathPatterns 用于添加拦截规则
                // excludePathPatterns 用户排除拦截
                registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**");
                super.addInterceptors(registry);
            }
        }




### 参考文献

* [小单的博客专栏](http://blog.csdn.net/catoop/article/details/50501688)

