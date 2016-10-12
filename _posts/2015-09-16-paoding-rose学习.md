---
layout: post
title:  "paoding-rose学习"
date:   2015-09-16 14:07:05
categories: paoding-rose
excerpt: paoding-rose学习。
---

* content
{:toc}

---

### paoding-rose简介

    由人人网、糯米网提供的、基于Servlet规范、Spring“规范”的开放源代码WEB开发框架
	   
	基于IoC容器 (使用Spring 2.5.6).
	收集最佳实践，形成规范和惯例，引导按规范惯例，简便开发.
	收集通用功能，形成一些可使用的组件，提高生产效率.
	特性的插拔，使用基于组合而非继承的设计.
	提供可扩展的点，保持框架的可扩展性.
	注重使用简易性的同时，注重内部代码设计和实现.


---

### 基础环境

*  java version "1.8.0_60" 
*  java和maven 环境变量配置
*  eclipse 开发工具，安装maven，jetty插件

---

### maven简介

- maven是基于项目对象模型(POM)，可以通过一小段描述信息来管理项目的构建，报告和文档的软件项目管理工具。
- maven对一个项目进入了固定的默认目录定义： 
  + src/main/java            写主要的java实现
  + src/main/resources       写主要的配置文件
  + src/test/java            写test case
  + src/test/resources       写test case所需要的配置文件
  + src/main/webapp          [war项目特有]web项目的对外目录
  + src/main/webapp/WEB-INF  [war项目特有]web项目配置web.xml目录

---

### 导入项目

![项目导入](http://oi62.tinypic.com/33b3bwk.jpg "项目导入")

#### 导入进来后都  **maven install**
---

### 项目实例

![项目实例](http://oi62.tinypic.com/2dtq6np.jpg "项目实例")

* 在src/main/java上右键 -> new -> package -> name: com.topshare
* 在com.topshare上右键 -> new -> package ->com.topshare.controllers [controllers是rose框架默认的加载controller的package name]
* 在com.topshare.controllers上右键 -> new -> class -> TestController [*Controller是rose框架默认的controller层的class后缀]
* 打开TestController这个类
* 在public class TestController添加注解@Path("") [Path注解是rose框架提供的标识每个controller的对外访问时的基础路径]
* 在TestController中添加方法

##### url对照规则

![项目实例](http://oi60.tinypic.com/1z6czl5.jpg "url对照规则")

##### 上述代码可以从浏览器访问：http://localhost:8080/Test/Comment

![项目实例](http://oi59.tinypic.com/aw2ges.jpg "url对照规则")

#####上述代码可以从浏览器访问：http://localhost:8080/Test/topic/123
---

注意标注在类(class)上的注解“@Path("Test ")”，这意味着，这个类中定义的所有API的URI，都必须以“Test”开 头，比如“/ Test /xxx”和“/ Test /yyy”等（但“Test”不一定是整个URI的第一级，比如“/aaa / Test /bbb”）。
 
1.在controllers路径下新建一个叫做“manager”的文件夹。

2.将TestController从“airshuttle.controllers”移动到“airshuttle.controllers.manage”

![项目实例](http://oi57.tinypic.com/jqlrnd.jpg "url对照规则")

#####上述代码可以从浏览器访问：http://localhost:8080/manage/Comment

---

##### url返回结果

* 渲染页面并返回

airshuttle.controllers.manage下

![项目实例](http://oi61.tinypic.com/vxf8sh.jpg)

![项目实例](http://oi57.tinypic.com/fdhdw1.jpg)

上述代码可以从浏览器访问：http://localhost:8080/manage/topic/123

通过rose提供类**net.paoding.rose.web.var.Model**来设置变量名和变量值，然后在视图文件中用“${paramName}”的方式得到变量值。

#####数据插入

    //插入用户
    @Get("/insertUser/{username}")
    public String insertUser(Invocation inv,@Param("username") String username)
    {
    	TAirshuttleUser sessionUser = this.getSessionUser(inv);
        Integer curUserId = sessionUser == null ? null : sessionUser.getId();
        Date d=new Date();//实例化时间
    	TAirshuttleUser user=new TAirshuttleUser();
    	user.setUsername(username); //用户名
    	user.setSex(0); //性别
    	user.setTelephone("13888888888"); //手机号
    	user.setCreateTime(d);//创建时间
    	user.setDescription("秒杀fj");
    	//密码加密
    	DBEncrypt dBEncrypt = new DBEncrypt();
    	user.setPassword(dBEncrypt.eCode("123456"));
    Integer i=this.userDao.insert(user);
    ResponseObject po=new ResponseObject();
    po.setSuccess(false);
    po.setErrorMessage("插入失败");
    if(i>0)
    {
    	po=ResponseObject.newSuccessResponseObject("插入成功");
    	return "@"+this.returnObjectToJson(po);
    	
    }
    	return "@"+this.returnObjectToJson(po);
    }
    
---

### 拦截器支持

需要注意几点：
applicationContext.xml 配置Interceptor

* 拦截器要放在controllers下
* 继承net.paoding.rose.web.ControllerInterceptorAdapter
* 按照实现的方法名，在controller执行前、中、后执行： 

	   before：在controller执行前执行。
	   after：在controller执行中（后）执行，如果一个返回抛出了异常，则不会进来。
	   afterCompletion：在controller执行后执行，不论是否异常，都会进来。
	   isForAction：定义满足某条件的才会被拦截。

#### 全局拦截器

    import net.paoding.rose.web.ControllerInterceptorAdapter;
    import net.paoding.rose.web.Invocation;
    public class AccessTrackInterceptor extends ControllerInterceptorAdapter {
        public AccessTrackInterceptor() {
          setPriority(5);
            }
            @Override
            public Object before(Invocation inv) throws Exception {
               System.out.println("AccessTrackInterceptor -> 我是内层");
            return super.before(inv);
            }
            @Override
            public void afterCompletion(final Invocation inv, Throwable ex) throws Exception {
            // TODO ....
            }
        }

---

#### 局部拦截器

    import java.lang.annotation.Annotation;
    import net.paoding.rose.web.ControllerInterceptorAdapter;
    import net.paoding.rose.web.Invocation;
    import org.apache.log4j.Logger;
    import com.topshare.airshuttle.controllers.PriCheckRequired;
    import com.topshare.airshuttle.controllers.manager.TestController;
    public class GlobalInterceptor extends ControllerInterceptorAdapter {
        protected static final Logger logger = Logger.getLogger(GlobalInterceptor.class);
            @Override
            public Class<? extends Annotation> getRequiredAnnotationClass() {
            return PriCheckRequired.class; // 这是一个注解，只有标过的controller才会接受这个拦截器的洗礼。
            }
            public GlobalInterceptor() {
            setPriority(29600);
            }
        @Override
        protected Object after(Invocation inv, Object instruction) throws Exception {
        //System.out.println("GlobalInterceptor -> after");
        return super.after(inv, instruction);
    }
    @Override
    protected Object before(Invocation inv) throws Exception {
    	 System.out.println("GlobalInterceptor -> 我是外层");
        return super.before(inv);
    }
    }

---

### ErrorHandler支持

    package com.topshare.airshuttle.controllers;
    import org.apache.log4j.Logger;
    import com.topshare.airshuttle.common.util.ResponseObject;
    import net.paoding.rose.web.ControllerErrorHandler;
    import net.paoding.rose.web.Invocation;
    public class ErrorHandler extends BaseController implements ControllerErrorHandler {
       private Logger logger = Logger.getLogger(this.getClass());
     public Object onError(Invocation inv, Throwable ex) throws Throwable {
    	ResponseObject ro = new ResponseObject();
    	logger.error("handle err:", ex);
        if (ex instanceof Exception) {
    	ro.setSuccess(false);
            ro.setErrorMessage("系统出现异常，请稍候再试");
         return "@"+this.returnObjectToJson(ro);
        }
        return "@error";
    }
    }
    
+ 放在controllers目录下，和controller们在一起（幸福快乐地生活）。
+ 一般来讲，ErrorHandler都是用在web项目里，在最快层起作用。
+ 所有的方法都可以尽情地向处throws Exception了。
+ 不需要再try了。



---

### 参考文献
* [五四陈科学院](http://www.54chen.com/rose.html)
