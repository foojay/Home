---
layout: post
title:  "Jekyll安装"
date:   2015-09-13 17:16:05
categories: jekyll
excerpt: Jekell安装。
---
* content
{:toc}



##Jekyll是什么?
一个在线编辑markdown文档的编辑器jekyll是一个简单的免费的Blog生成工具，类似WordPress。但是和WordPress又有很大的不同，原因是jekyll只是一个生成静态网页的工具，不需要数据库支持。但是可以配合第三方服务,例如Disqus。最关键的是jekyll可以免费部署在Github上，而且可以绑定自己的域名。
##安装步骤

### 安装Ruby
    1.下载ruby: http://rubyinstaller.org/downloads/

    2 我这里下载的是: rubyinstaller-2.1.5-x64.exe

    3.next, next, next(尽量全选, 选择添加到Path环境变量).
    
    4.我自己的安装路径:

        D:/Ruby21-x64
    
        (如果手动添加Path环境变量, 则变量是D:/Ruby21-x64/bin)
    
        验证, 输入如下命令行:
    
        C:/Users/Administrator>ruby -v

![Ruby测试](http://foojay.github.io/Home/img/20150120142338627.png)
    
    5. 更新ruby和gems系统。输入 “gem update --system" ，并安装rspec ，命令是：“gem install rspec”
    
    6. 安装其他组件之前一定要先修改镜像文件。使用国内的淘宝镜像，此步骤在DOS中进行。

       $ gem sources --remove http://rubygems.org/  // 删除官方链接
     
       $ gem sources -a http://ruby.taobao.org/  //添加淘宝镜像链接
     
       $ gem sources -l  //查询是否替换成功
       
       
###安装 DevKit
    1.前往 http://rubyinstaller.org/downloads/下载下载同系统及 Ruby 版本相对应的 DevKit 安装包
    
    2.运行安装包并解压缩至某文件夹，如 C:\DevKit
    
    3.通过初始化来创建 config.yml 文件。在命令行窗口内，输入下列命令：
       cd “C:\DevKit”
       
       ruby dk.rb init
       
       notepad config.yml
       
    4.在打开的记事本窗口中，于末尾添加新的一行 - D:\Ruby22（这里的路径是我自己的安装位置），保存文件并退出。

    5.回到命令行窗口内，审查（非必须）并安装。

      ruby dk.rb review
    
      ruby dk.rb install
    
       如下提示：
       $ ruby dk.rb install
         [INFO] Updating convenience notice gem override for 'D:/Ruby22'
         [INFO] Installing 'D:/Ruby22/lib/ruby/site_ruby/devkit.rb'


###安装 Jekyll
    

    确保 gem 已经正确安装

        gem -v

    输出示例：

        2.0.14

    安装 Jekyll gem

        gem install jekyll
        

    在一串的提示命令之完成之后，就可以用jekyll -v测试jekyll是否安装成功了，如果能够输出版本号，就说明安装成功了
    
    
###安装 Pygments

Jekyll 里默认的语法高亮插件是 Pygments。 它需要安装 Python 并在网站的配置文件_config.yml 里将 highlighter 的值设置为pygments。

不久之前，Jekyll 还添加另一个高亮引擎名为 Rouge， 尽管暂时不如 Pygments 支持那么多的语言，但它是原生 Ruby 程序，而不需要使用 Python

###安装 Python

    1.前往 http://www.python.org/download/
    2.下载合适的 Python windows 安装包，如 Python 2.7.6 Windows Installer。 请注意，Python 2 可能会更合适，因为暂时 Python 3 可能不会正常工作。
    安装
    3. 添加安装路径 (如： C:\Python27) 至 PATH。

    4.检验 Python 安装是否成功

        python –V

    输出示例：

        Python 2.7.6



## 参考资料

* [
oukongli的专栏：Windows 上安装 Jekyll ](http://blog.csdn.net/kong5090041/article/details/38408211)
* [win7下安装jekyll——在github上创建自己的博客](http://www.cnblogs.com/hutaoer/archive/2013/02/06/3078873.html)

