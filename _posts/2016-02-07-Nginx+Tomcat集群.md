---
layout: post
title:  "Nginx+Tomcat集群"
date:   2016-01-06 08:27:00
categories: tomcat
excerpt:  Nginx+Tomcat集群
---

* content
{:toc}




### 布署Tomcat集群

准备两个版本一致的Tomcat，分别起名为tomcat1，tomcat2。

打开tomcat中的conf目录中的server.xml，找到下面这行

1）

    <Server port="8005" shutdown="SHUTDOWN">

记得：

一定要把tomcat2中的这边的”SHUTDOWN”的port改成另一个端口号，两个tomcat如果是在集群环境中，此处的端口号绝不能一样。

2）找到

    <Connector port="8080" protocol="HTTP/1.1"

确保tomcat2中此处的端口不能为8080，我们就使用9090这个端口吧

3）把两个tomcat中原有的https的配置，整段去除

4）找到

    <Connector port="8080" protocol="HTTP/1.1"
               URIEncoding="UTF-8"  minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" disableUploadTimeout="true" connectionTimeout="20000"
                   acceptCount="300"  maxThreads="300" maxProcessors="1000" minProcessors="5"
                   useURIValidationHack="false"
                             compression="on" compressionMinSize="2048"
                             compressableMimeType="text/html,text/xml,text/javascript,text/css,text/plain"
               redirectPort="8443" />

确保tomcat2中这边的redirectPort为9443

5）找到

    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443"

改为：

    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443"
                    URIEncoding="UTF-8"  minSpareThreads="25" maxSpareThreads="75"
                            enableLookups="false" disableUploadTimeout="true" connectionTimeout="20000"
                            acceptCount="300"  maxThreads="300" maxProcessors="1000" minProcessors="5"
                            useURIValidationHack="false"
                                      compression="on" compressionMinSize="2048"
    compressableMimeType="text/html,text/xml,text/javascript,text/css,text/plain"
    />

确保tomcat2的server.xml中此处的8009被改成了9009且其它内容与上述内容一致（redirectPort不要忘了改成9443）

6）找到

    <Engine name="Standalone" defaultHost="localhost" jvmRoute="jvm1">      

改成

     <!-- You should set jvmRoute to support load-balancing via AJP ie :
      <Engine name="Standalone" defaultHost="localhost" jvmRoute="jvm1">        
     -->
    <Engine name="Standalone" defaultHost="localhost" jvmRoute="tomcat1">

同时把tomcat2中此处内容改成

     <!-- You should set jvmRoute to support load-balancing via AJP ie :
     <Engine name="Standalone" defaultHost="localhost" jvmRoute="jvm1">        
     -->
    <Engine name="Standalone" defaultHost="localhost" jvmRoute="tomcat2">

7）

在刚才的

    <Engine name="Standalone" defaultHost="localhost" jvmRoute="tomcat1">

的下面与在

     <!-- The request dumper valve dumps useful debugging information about
      the request and response data received and sent by Tomcat.
       Documentation at: /docs/config/valve.html -->
     <!--
    <Valve className="org.apache.catalina.valves.RequestDumperValve"/>
    -->

之上，在这之间加入如下一大陀的东西：

               <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster" 
                  channelSendOptions="6"> 
              <Manager className="org.apache.catalina.ha.session.BackupManager" 
                    expireSessionsOnShutdown="false" 
                    notifyListenersOnReplication="true" 
                    mapSendOptions="6"/> 
           <Channel className="org.apache.catalina.tribes.group.GroupChannel"> 
             <Membership className="org.apache.catalina.tribes.membership.McastService" 
                         bind="127.0.0.1" 
                         address="228.0.0.4" 
                         port="45564" 
                         frequency="500" 
                         dropTime="3000"/> 
             <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver" 
                       address="auto" 
                       port="4001" 
                       selectorTimeout="100" 
                       maxThreads="6"/> 
             <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter"> 
               <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender" timeout="60000"/>  
             </Sender> 
             <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/> 
             <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatch15Interceptor"/> 
             <Interceptor className="org.apache.catalina.tribes.group.interceptors.ThroughputInterceptor"/> 
           </Channel> 
              <Valve className="org.apache.catalina.ha.tcp.ReplicationValve" 
                  filter=".*\.gif;.*\.js;.*\.jpg;.*\.png;.*\.htm;.*\.html;.*\.css;.*\.txt;"/> 
              <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/> 
         </Cluster>

此处有一个Receiver port=”xxxx”，两个tomcat中此处的端口号必须唯一，即tomcat中我们使用的是port=4001，那么我们在tomcat2中将使用port=4002

8）把系统环境变更中的CATALINA_HOME与TOMCAT_HOME这两个变量去除掉

9）在每个tomcat的webapps目录下布署同样的一个工程，在布署工程前先确保你把工程中的WEB-INF\we b.xml文件做了如下的修改，在web.xml文件的最未尾即“</web-app>”这一行前加入如下的一行：

    <distributable/>

使该工程中的session可以被tomcat的集群节点进行轮循复制。


###  Nginx配置

    http {
        include       mime.types;
        default_type  application/octet-stream;
    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';
    #access_log  logs/access.log  main;
    sendfile        on;
    #tcp_nopush     on;
    #keepalive_timeout  0;
    keepalive_timeout  65;
    #gzip  on;
    upstream blog.ha97.com {        
    #upstream的负载均衡，weight是权重，可以根据机器配置定义权重。weigth参数表示权值，权值越高被分配到的几率越大。       
    server 172.16.1.182:8080 weight=1;        
    server 172.16.1.182:9090 weight=1;        
    } 


    server {
            listen       8093;
            server_name  localhost;
            #charset koi8-r;
            #access_log  logs/host.access.log  main;
            
        location / { 
               proxy_pass http://blog.ha97.com/; 
              }
        
        #error_page  404              /404.html;
        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   d:/eclipse/website/wtwebsite-manage-web/src/main/webapp;
        }
        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}
        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}
        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }