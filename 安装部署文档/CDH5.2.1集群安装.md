# CDH5.2.1集群安装

- 集群节点数量1个
 
192.168.4.220	 cdh

CDH版本：5.2.1

一、安装CentOS6.7（64位）并搭建基本环境，包括：

1).主机参数优化

2).主机/etc/hosts文件修改

二、下载clouder-manger的安装包及parcel文件：

1).cloudera Manager地址：

http://archive.cloudera.com/cm5/cm/5/下载

cloudera-manager-el6-cm5.2.1_x86_64.tar.gz 

2).CDH安装包地址：

http://archive-primary.cloudera.com/cdh5/parcels/5.2.1/分别下载

CDH-5.2.1-1.cdh5.2.1.p0.12-el6.parcel

CDH-5.2.1-1.cdh5.2.1.p0.12-el6.parcel.sha1

manifest.json

3).JDK下载地址http://archive-primary.cloudera.com/cm5/redhat/6/x86_64/cm/5.2.1/RPMS/x86_64/
jdk-6u31-linux-amd64.rpm

oracle-j2sdk1.7-1.7.0+update67-1.x86_64.rpm

4).JDBC驱动

http://download.softagency.net/MySQL/Downloads/Connector-J/下载

mysql-connector-java-5.1.38.tar.gz

三、安装JDK环境

```
# rpm -Uvh jdk-6u31-linux-amd64.rpm
# rpm -Uvh oracle-j2sdk1.7-1.7.0+update67-1.x86_64.rpm
```

四、部署CDM和CDH

上传cloudera-manager-el6-cm5.2.1_x86_64.tar.gz到主机/opt目录下

```
# cd /opt/
# tar xf cloudera-manager-el6-cm5.2.1_x86_64.tar.gz
```

五、mysql 数据库配置

1).创建数据库

创建cloudera数据库:

mysql >create database cloudera character set utf8;
mysql >grant all privileges on cloudera.* to cloudera@localhost identified by '123456';
mysql >grant all privileges on cloudera.* to cloudera@'%' identified by '123456';
mysql >flush privileges;
mysql >use cloudera

创建Hive数据库:

mysql >create database hive default character set latin1;

字符必须为拉丁(latin1),如果uft8可能会报错Specified key was too long; max key length is 767 bytes

mysql >grant all privileges on hivedb .* to hive@localhost identified by '123456';

mysql >grant all privileges on hivedb .* to hivedb@'%' identified by '123456';
  mysql >flush privileges;

创建report数据库

mysql >create database report character set utf8;
mysql >grant all privileges on report.* to report@localhost identified by '123456';
mysql >grant all privileges on report.* to report@'%' identified by '123456';
mysql >flush privileges;

2).初始化数据库


`# /opt/cm-5.2.1/share/cmf/schema/scm_prepare_database.sh  -h 192.168.4.126 mysql cloudera cloudera 123456`

六、在master节点配置cloudera manager 数据库并启动cm的server及agent程序

1).拷贝mysql-connector-java-5.1.7-bin.jar 到 /usr/share/java 下并重命名mysql-connector-java.jar

`# mv mysql-connector-java-5.1.38-bin.jar mysql-connector-java.jar`

2).启动cm server ：

```
# /opt/cm-5.2.1/etc/init.d/cloudera-scm-server start
# /opt/cm-5.2.1/etc/init.d/cloudera-scm-server status
```

![](img/42.png)

3).启动cm agent ：chkconfig cloudera-scm-agent on

`#useradd --system --home=/opt/cm-5.3.2/run/cloudera-scm-server/ --no-create-home --shell=/bin/false --comment "Cloudera SCM User" cloudera-scm`

/etc/cloudera-scm-agent/config.ini 将配置文件中的host 改成 cdh-master

```
# /opt/cm-5.2.1/etc/init.d/cloudera-scm-agent start
# /opt/cm-5.2.1/etc/init.d/cloudera-scm-agent status
```

七、启动浏览器，开始集群web安装配置

http://ip:7180

账号：admin 密码:admin




