---
layout: post
title:  "windows搭建mysql主从复制"
date:   2017-01-06 08:27:00
categories: mysql
excerpt: windows搭建mysql主从复制
---

* content
{:toc}




### 创建两个版本相同的mysql

1、首先要在本地建立两个mysql服务，指定不同的端口(3306,3307)

从服务器版本一定要高于等于主服务器的版本。

如下：

    环境:
    OS:Windows XP
    DB:MYSQL5.5
     
    1.正常安装第一个mysql(安装步骤省略)
     
    2.在控制面板里停止第一个mysql服务
     
    3.将C:\Program Files\MySQL目录下的所有目录和文件copy到另外一个路径,我这里是copy到E盘
     
    4.创建第二个mysql的my.ini文件
    第一个mysql的my.ini文件默认是在如下路径
    C:\Documents and Settings\All Users\Application Data\MySQL\MySQL Server 5.5\my.ini
    copy该ini文件存放到E:\MySQL\mysql_base,这个路径可以随意定义,并修改如下内容:
    [client]
    port=3307 #第一个数据库的默认端口是3306 这里需要另外启用一个端口
    # The TCP/IP Port the MySQL Server will listen on
    port=3307
    # Path to installation directory. All paths are usually resolved relative to this.
    basedir="E:\MySQL\MySQL Server 5.5\"         #第二个数据库basedir
    # Path to the database root
    datadir="E:\MySQL\MySQL Server 5.5\data\"    #第二个数据库datadir
     
    5.创建启动服务（此时在控制面板中可以看到增加了一个新的服务）
    mysqld install MySQL2  --defaults-file="E:\MySQL\mysql_base\ini\my.ini"
     
    6.修改注册表
    HKEY_LOCAL_MACHINE-->SYSTEM-->CurrentControlSet-->Services
    找到刚才创建的MySQL2,将ImagePath修改成如下":
    "E:\MySQL\MySQL Server 5.5\bin\mysqld" --defaults-file="E:\MySQL\mysql_base\ini\my.ini" MySQL2

### 详细步骤

1、然后修改主配置文件:

    [mysqld]
    server-id = 1   
    binlog-do-db=test #要同步的数据库
    #binlog-ignore-db=mysql   #不同步的数据库,如果指定了binlog-do-db这里应该可以不用指定的
    log-bin=mysql-bin #要生成的二进制日记文件名称

修改从配置文件:

    [mysqld]
    server-id = 2
    log-bin    = mysql-bin
    replicate-do-db=test

修改auto.cnf文件(在mysql 的data目录下)的uuid：//不能相同

命令查看是否相同

show variables like 'server_id';


3、在主服务器上建立帐户并授权slave: 

        create user 'test'@'127.0.0.1' identified by '123456';
        GRANT REPLICATION SLAVE ON *.* TO 'test'@'127.0.0.1'; 
        #生效。该操作很重要！
         FLUSH PRIVILEGES;

这里我指定数据库（test.*）时报错，而指定全库（*.*）时会成功。

4、保持主从mysql的test数据库初始状态一致。

一般是先将所有的表加读锁，然后copy磁盘上的数据库文件夹。我这里直接停止服务，然

后将数据文件拷贝过去。

5、在主数据库里面运行show master status;记下file和position字段对应的参数。

mysql> show master status;
+------------------+----------+--------------+------------------+
| File | Position | Binlog_Do_DB | Binlog_Ignore_DB |
+------------------+----------+--------------+------------------+
| mysql-bin.000001 | 107 | test | |
+------------------+----------+--------------+------------------+
1 row in set (0.00 sec)

6、在从库设置它的master：
  先暂停  stop slave;

    mysql> change master to master_host='127.0.0.1',master_port=3306,master_user='test',master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=107;
    Query OK, 0 rows affected (0.19 sec)

这里的master_log_file和master_log_pos对应刚才show master status记下的参数。

7、在从库开启从数据库复制功能。

 start slave;

    mysql> start slave;
    Query OK, 0 rows affected (0.00 sec)

在从库可以通过show slave status\G来查看一些参数。


8. 现在你对主服务器的任何更新操作都将同步到从服务器，你可以试试建表，插入数据，

删除数据，看是否同步成功(从服务器的更新不会同步到主服务器)。



### 常见错误整理


          Last_SQL_Errno: 1194
            Last_SQL_Error: Error 'Table 'traincenter' is marked as crashed and should be repaired' on query. Default database: 'basketballman'. Query: 'update traincenter set points='4',pointstime='1361912066'  where uid = '1847482697' limit 1'
            解决方法：myisam表traincenter损坏，直接repair table即可。至于为什么myisam类型表比innodb更容易损坏，我觉得有两个原因：1，innodb有double write机制，损坏或者half write的页可以用它恢复，第二innodb是事务引擎，都有操作都是事务的，而myisam是非事务的，存在写一半但是操作终止情况。


        Last_IO_Errno: 1236
        Last_IO_Error: Got fatal error 1236 from master when reading data from binary log: 'Could not find first log file name in binary log index file'
        解决方法：主库上的binlog文件已经不存在但是在index file中确有相应记录存在。我这里发生这个错误的原因在于由于复制中断时间很长，报警出来一直没人处理，这个中断时间超过master上binlog超期时间，等恢复复制时需要的binlog已经由于其超期而被删掉，没办法只好重建这个实例了。以大家都要引以为戒。


        Last_IO_Errno: 1593
        Last_IO_Error: Fatal error: The slave I/O thread stops because master and slave have equal MySQL server ids; these ids must be different for replication to work (or the --replicate-same-server-id option must be used on slave but this does not always make sense; please check the manual before using it).
        解决方法：主从配置的server-id一样，而在主从复制环境中server-id一样的binlog events都会被过滤掉。具体server-id的含义可以了解一下复制原理。这个一般是因为拷贝配置文件时忘记修改server-id导致，遇到这类问题也比较容易，平时操作谨慎一点即可。


        Last_Errno: 1053
        Last_Error: Query partially completed on the master (error on master: 1053) and was aborted. There is a chance that your master is inconsistent at this point. If you are sure that your master is ok, run this query manually on the slave and then restart the slave with SET GLOBAL SQL_SLAVE_SKIP_COUNTER=1; START SLAVE; . Query: 'insert into ...
        解决方法：查询在master上部分完成，然后终止了。这马上又能想到是myisam表，结果也正是这样。由于myisam不支持事务所以可能存在一个查询完成一部分然后失败的情况。解决方法一般也就是提示信息给出的跳过一个binlog event。不过确认跳过之前最好还是查询一下master上是否真的存在相应的记录，因为错误信息同时还会给出它认为在master上执行一部分然后终止的查询语句。


        Last_SQL_Errno: 1666
        Last_SQL_Error: Error executing row event: 'Cannot execute statement: impossible to write to binary log since statement is in row format and BINLOG_FORMAT = STATEMENT.' 
        解决方法：这个案例的背景是做一个ABC结构的复制，B、C中设定的binlog_format=statement，A中的是MIXED，所以当B尝试重做A过来的relay log，然后记录binlog（传给C）时发现relay log的binlog_format与自己设定的binlog_format不一致。我当时就是直接先更改BC的binlog_format=MIXED解决。


        Last_Errno: 1032
        Last_Error: Could not execute Update_rows event on table db.table; Can't find record in 'table', Error_code: 1032; handler error HA_ERR_KEY_NOT_FOUND; the event's master log mysql-bin.000064, end_log_pos 158847
        解决方法：这个是在binlog_format=row复制下发生的。原因是因为row格式复制是最严格的，所以在mysql看来如果在从库上找不到要更新的这条记录，那么就代表主从数据不一致，因此报错。另外顺便说一句，对于row格式binlog，如果某个更新操作实际上并没有更新行，这个操作是不会记binlog的，因为row格式的binlog宗旨就是只记录发生了改变的行。所以这个解决办法根据你自己实际应用来定，最好的方法还是重做slave吧，这样更放心。


        Last_Errno: 28
        Last_Error: Error in Append_block event: write to '/tmp/SQL_LOAD-32343798-72213798-1.data' failed
        解决方法： 首先说错误原因：主库执行load data infile，同步到从库后load data infile存放的文件默认是放在/tmp(由参数slave_load_tmpdir控制)，而/tmp空间不够因此报错。因此只要将从库上slave_load_tmpdir设置到一个磁盘空间足够大的分区就行。


        1、show slave status中Slave_IO_State: Waiting to reconnect after a failed registration on master
        解决方法：
        在master上执行
        grant replication slave on *.* to 'repl'@'%' identified by 'password';
        FLUSH PRIVILEGES;
        然后重新stop slave 和start slave就可以

    2、show slave status中Slave_IO_Running: No
    Last_IO_Error: Fatal error: The slave I/O thread stops because master and slave have equal MySQL server UUIDs; these UUIDs must be different for replication to work.
    解决办法：每个库的uuid应该是不一样的，修改auto.cnf文件(在mysql 的data目录下)的uuid：
    [auto]
    server-uuid=6dcee5be-8cdb-11e2-9408-90e2ba2e2ea6
    按照这个16进制格式，随便改下，重启mysql即可。





### 常用mysql命令

mysqld  //启动mysql 服务

mysql -uroot -p123456 -P3306 -h127.0.0.1  登入mysql


show master status; //在主数据库里面运行show master status;

create user 'repl'@'127.0.0.1' identified by 'asdf'; //创建登入用户

GRANT REPLICATION SLAVE ON *.* TO 'repl'@'127.0.0.1'; //指定访问权限



//查看server_id

show variables like 'server_id';


在从库设置它的master：

    mysql> change master to master_host='127.0.0.1',master_port=3306,master_user='repl',master_password='asdf',master_log_file='mysql-bin.000001',master_log_pos=107;
    Query OK, 0 rows affected (0.19 sec)

定义

    master_host:主服务器的ip；
    master_port:端口号；（如果默认3306的话不需要指定）；
    mstart_user:登陆主服务器mysql用户；
    master_password:登陆主服务器mysql密码；
    master_log_pos:从主服务器复制文件的第几个位置进行复制；即前面记录的master的状态中的File域的值  //主服务器执行命令查看 show master status;
    master_log_file:主服务器中的数据库复制文件；即前面记录的master的状态中的Position域的值   //主服务器执行命令查看 show master status;



stop slave; //停止从服务器复制功能 

start slave;//启动从服务器复制功能 


show slave status\G //查看从数据库参数状态

一般查看以下2个状态，任意一个不是YES 就查原因

    Slave_IO_Running: Yes    //此状态必须YES
    Slave_SQL_Running: Yes     //此状态必须YES
