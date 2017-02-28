---
layout: post
title:  "Spring Boot自定义Banner "
date:   2017-01-06 08:27:00
categories: springboot
excerpt: Spring Boot自定义Banner 
---

* content
{:toc}




### 生成方式

实现的方式非常简单，我们只需要在SpringBoot工程的/src/main/resources目录下创建

一个banner.txt文件，然后将ASCII字符画复制进去，就能替换默认的banner了。

比如上图中的输出，就采用了下面的banner.txt内容：

    ${AnsiColor.BRIGHT_GREEN}
    ##     ##    ###    ########  ########  ##    ##    ##    ## ######## ##      ##    ##    ## ########    ###    ########
    ##     ##   ## ##   ##     ## ##     ##  ##  ##     ###   ## ##       ##  ##  ##     ##  ##  ##         ## ##   ##     ##
    ##     ##  ##   ##  ##     ## ##     ##   ####      ####  ## ##       ##  ##  ##      ####   ##        ##   ##  ##     ##
    ######### ##     ## ########  ########     ##       ## ## ## ######   ##  ##  ##       ##    ######   ##     ## ########
    ##     ## ######### ##        ##           ##       ##  #### ##       ##  ##  ##       ##    ##       ######### ##   ##
    ##     ## ##     ## ##        ##           ##       ##   ### ##       ##  ##  ##       ##    ##       ##     ## ##    ##
    ##     ## ##     ## ##        ##           ##       ##    ## ########  ###  ###        ##    ######## ##     ## ##     ##
    ${AnsiColor.BRIGHT_RED}
    Application Version: ${application.version}${application.formatted-version}
    Spring Boot Version: ${spring-boot.version}${spring-boot.formatted-version}


### 生成工具


如果让我们手工的来编辑这些字符画，显然是一件非常困难的差事。所以，我们可以借助

下面这些工具，轻松地根据文字或图片来生成用于Banner输出的字符画。

    http://patorjk.com/software/taag
    http://www.network-science.de/ascii/
    http://www.degraeve.com/img2txt.php




