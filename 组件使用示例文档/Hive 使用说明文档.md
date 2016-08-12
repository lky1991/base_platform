# Hive 使用说明文档  


导读：  
1. hive的原理    
2. shell操作hive  
3. java api通过jdbc连接操作hive  
4. hive在kerberos安全模式下使用实例  
5. hive的深层概念

## 一、Hive的原理  

将hql语句执行成一个个的算子，通过对相应算子集的处理，得到最终处理结果。


1.1在hadoop生态圈中属于数据仓库的角色。他能够管理hadoop中的数据，同时可以查询hadoop中的数据。

本质上讲，hive是一个SQL解析引擎。Hive可以把SQL查询转换为MapReduce中的job来运行。  
Hive是一个数据仓库基础工具在Hadoop中用来处理结构化数据。它架构在Hadoop之上，总归为大数据，并使得查询和分析方便。

最初，Hive是由Facebook开发，后来由Apache软件基金会开发，并作为进一步将它作为名义下Apache Hive为一个开源项目。它用在好多不同的公司。例如，亚马逊使用它在 Amazon Elastic MapReduce。  
  
hive有一套映射工具，可以把SQL转换为MapReduce中的job，可以把SQL中的表、字段转换为HDFS中的文件(夹)以及文件中的列。这套映射工具称之为metastore，一般存放在derby、mysql中。

深层原理概要：
[http://tech.meituan.com/hive-sql-to-mapreduce.html](http://tech.meituan.com/hive-sql-to-mapreduce.html "Hive SQL的编译过程")


## 二、shell操作hive

### 2.1 前置操作

**hive启动**  
我们的集群是由ambari管理的；启动是在ambari web界面上操作。


### 2.3 hive 的shell交互  

Hive交互Shell指执行$HIVE_HOME/bin/hive(或直接键入hive确认即可)之后，进入的有hive>提示符的交互式命令行，在这里可以执行查询语句，设置参数等等，所有的命令必须以分号结束，具体有以下命令和选项：  

-  quit：退出交互Shell  
-  exit：退出交互Shell 
-  reset：重置所有的Hive运行时配置参数，比如，之前使用set命令设置了reduce数量，使用reset之后，重置成hive-site.xml中的配置。  
-  set <key>=<value>：设置Hive运行时配置参数，优先级最高，相同key，后面的设置会覆盖前面的设置。  
-  set –v：打印出所有Hive的配置参数和Hadoop的配置参数。  
-  add命令,包括 add FILE[S] <filepath> <filepath>* 、 add JAR[S] <filepath> <filepath>* 、add ARCHIVE[S] <filepath> <filepath>* :向DistributeCache中添加一个或过个文件、jar包、或者归档，添加之后，可以在Map和Reduce task中使用。比如，自定义一个udf函数，打成jar包，在创建函数之前，必须使用add jar命令，将该jar包添加，否则会报错找不到类。  
-  list 命令,包括 list FILE[S] 、list JAR[S]  、list ARCHIVE[S]:列出当前DistributeCache中的文件、jar包或者归档。  
-  delete 命令,包括 delete FILE[S] <filepath>* 、delete JAR[S] <filepath>*  、 delete ARCHIVE[S] <filepath>* : 从DistributeCache中删除文件  
-  ! <command> : 在交互Shell中执行Linux操作系统命令并打印出结果，不常用  
-  dfs <dfs command>:在交互Shell中执行hadoop fs 命令，不常用  
-  <query string> : 最常用的，执行HQL语句，以分号结尾；  
-  source FILE <filepath> : 在交互Shell中执行一个脚本，不常用。

>注：此处的命令我们使用的只有几个，常用的使用方式是，先进入hive shell界面，再执行hql操作，最后退出。而hql语句和sql语句类似；下面有其介绍（有创建、删除、查询、修改表，加载数据等）的相关链接：  
>[http://www.cnblogs.com/liuhaitao/archive/2012/07/17/2594764.html](http://www.cnblogs.com/liuhaitao/archive/2012/07/17/2594764.html "hive 的常见hql语句")  
>[https://cwiki.apache.org/confluence/display/Hive/GettingStarted#GettingStarted-DDLOperations](https://cwiki.apache.org/confluence/display/Hive/GettingStarted#GettingStarted-DDLOperations "hive 官网hql实例介绍")


## 三、 hive的JDBC连接————Java客户端  

### 3.1 依赖

如果是用Maven，加入以下依赖  

  
	<!--下载包的第三方私有源-->
    <repositories>
        <repository>
            <id>nodpi</id>
            <name>nodpi Releases</name>
            <url>http://nexus.odpi.org:8081/nexus/content/groups/public</url>
            <layout>default</layout>
        </repository>
        <repository>
            <id>HDPReleases</id>
            <name>HDP Releases</name>
            <url>http://repo.hortonworks.com/content/repositories/releases/</url>
            <layout>default</layout>
        </repository>
    </repositories>
    <properties>
    </properties>
	<!--具体依赖-->
    <dependencies>
        <!--hdfs-->
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.7.1.2.3.2.0-2950</version>
        </dependency>
        <!--hive-->
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-beeline</artifactId>
            <version>1.2.1.2.3.2.0-2950</version>
        </dependency>
	</dependencies>
	<!--编译配置-->
    <build>
        <plugins>

            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

>注：hive的version是 经第三方hortonworks 处理后得到的小版本。

### 3.2 实际操作
我们可以通过CLI、Client、Web UI等Hive提供的用户接口来和Hive通信，但这三种方式最常用的是CLI（也就是shell命令行操作hive）；Client 是Hive的客户端，用户连接至 Hive Server。在启动 Client 模式的时候，需要指出Hive Server所在节点，并且在该节点启动 Hive Server。 WUI 是通过浏览器访问 Hive。现在主要介绍通过HiveServer2来操作Hive。
此处的实例是使用idea 的maven项目来进行的；具体的步骤如下：  
1. 下载配置文件；在ambari管理界面下载hdfs-site.xml、core-site.xml、hive-site.xml文件放入项目的resources 目录中。主要是下载hdfs服务和hive服务的具体配置。  
2. 开始编写具体代码来连接hive


>注：现在我们公司是使用的hiveserver2；故当前介绍的是连接hiveserver2 的代码。另：编写代码前请确认host是否配置正确。

示例代码如下：  

```java
    package com.dbc;
	import java.sql.SQLException;
	import java.sql.Connection;
	import java.sql.ResultSet;
	import java.sql.Statement;
	import java.sql.DriverManager;

 
	public class HiveJdbcTest {
     

		private static String driverName = "org.apache.hive.jdbc.HiveDriver";
  
	    public static void main(String[] args)
	                            throws SQLException {
	       try {
	            Class.forName(driverName);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

			Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10002/default", "", "");
	        Statement stmt = con.createStatement();
	        String tableName = "wyphao";
	        stmt.execute("drop table if exists " + tableName);
	        stmt.execute("create table " + tableName +
                                     " (key int, value string)");
	        System.out.println("Create table success!");
	        // show tables
	        String sql = "show tables '" + tableName + "'";
	        System.out.println("Running: " + sql);
	        ResultSet res = stmt.executeQuery(sql);
	        if (res.next()) {
	           System.out.println(res.getString(1));
	        }

	        // describe table
	        sql = "describe " + tableName;
	        System.out.println("Running: " + sql);
	        res = stmt.executeQuery(sql);
	        while (res.next()) {
	            System.out.println(res.getString(1) + "\t" + res.getString(2));
	        }
 
	        sql = "select * from " + tableName;
	        res = stmt.executeQuery(sql);
	        while (res.next()) {
	            System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
	        }
	        sql = "select count(1) from " + tableName;
	        System.out.println("Running: " + sql);
	        res = stmt.executeQuery(sql);
	        while (res.next()) {
	            System.out.println(res.getString(1));
	        }
	    }
	}
```

编译上面的代码，之后就可以运行。上面是用Java连接HiveServer2，

更多：  
[http://hive.apache.org/javadocs/r1.2.1/api/index.html](http://hive.apache.org/javadocs/r1.2.1/api/index.html "hive 1.2.1 api")  
[http://hive.apache.org/javadoc.html](http://hive.apache.org/javadoc.html "hive java doc ") 


## 四、 Hive在kerberos安全模式下使用实例  

>注：此处的安全模式是指集群使用了基于kerberos+ranger+knox+ldap的安全策略。

### 4.1 依赖  

版本：此处使用的经过第三方(hortonworks)重构的架包；具体依赖如下： 
	
    <dependencies>
        <!--hdfs-->
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.7.1.2.3.2.0-2950</version>
        </dependency>
		
		 <!--hive-->
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-beeline</artifactId>
            <version>1.2.1.2.3.2.0-2950</version>
        </dependency>
    </dependencies> 
	

### 4.2 客户端安全认证 

在此模式下访问hdfs前需要做安全认证，具体的认证方式如下：  
1. 添加配置文件：主要指先从ambari界面下载Hive的配置文件和hdfs，放入工程项目的resources 目录下，相应的还要添加krb5.conf 和 XX.keytab(相当于kerberos的密钥)放入此目录。  
2. 设置org.apache.hadoop.conf.Configuration 的具体配置，主要是添加步骤1中的keytab文件和其对应的principal（具体看实例）  
3. 安全认证登陆，使用SecurityUtil类的login方法实现。  
4. 开始实例化connection使用访问hive。 

>注：在运行前需要 运行 System.setProperty("java.security.krb5.conf"，krb5.conf path);来初始化基础配置这一步是在windows上才需要，在liunx服务器上则不需要。  
>其二：添加的配置文件还应包含hdfs的配置（主要是hdfs-site.xml）；因为hive依赖hdfs。

### 4.3 示例代码  
```java
    private static final Logger logger = Logger.getLogger(HdfsTest.class);
    private final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
    private final String USER_NAME_KEY = "hdfs.kerberos.principal";
    private Configuration configuration;

    public HiveTest() {
        configuration = new Configuration();
        configuration.set(KEYTAB_FILE_KEY, "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\liweiqi.keytab");//设置keytab文件
        configuration.set(USER_NAME_KEY, "liweiqi@hadoop");//设置keytab文件的principal
    }

    @Override
    public void test() throws Exception {
        System.setProperty("java.security.krb5.conf", "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\krb5.conf");//初始化基础的安全配置
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        SecurityUtil.login(configuration, KEYTAB_FILE_KEY, USER_NAME_KEY);//安全登录
        String url = "jdbc:hive2://hadoop03.domain:10000/test;principal=hive/hadoop03.domain@hadoop";
        Connection connection = DriverManager.getConnection(url, "hive", "hive");//创建hive连接
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("show databases");
        while (rs.next()) {
            logger.info(rs.getString(1));
        }
        rs.close();
        statement.close();
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        new HiveTest().test();
    }
```

## 五 hive其他概念  

**Hive的数据类型**

基本数据类型  
tinyint / smalint / int /bigint
float / double
boolean
string  

复杂数据类型  
Array/Map/Struct  

没有date /datetime 

**Hive的数据存储**

Hive的数据存储基于Hadoop HDFS
Hive没有专门的数据存储格式
存储结构主要包括：数据库、文件、表、视图
Hive默认可以直接加载文本文件（TextFile），还支持sequence file
创建表时，指定Hive数据的列分隔符与行分隔符，Hive即可解析数据


**Hive的数据模型-内部表**


与数据库中的 Table 在概念上是类似
每一个 Table 在 Hive 中都有一个相应的目录存储数据。例如，一个表test，它在 HDFS 中的路径为：/ warehouse/test。 warehouse是在hive-site.xml 中由 ${hive.metastore.warehouse.dir} 

指定的数据仓库的目录

所有的 Table 数据（不包括 External Table）都保存在这个目录中。

删除表时，元数据与数据都会被删除



**Hive的数据模型-分区表**

Partition 对应于数据库的 Partition 列的密集索引

在 Hive 中，表中的一个 Partition 对应于表下的一个目录，所有的 Partition 的数据都存储在对应的目录中

例如：test表中包含 date 和 city 两个 Partition，

‍则对应于date=20130201, city = cd 的HDFS 子目录‍：

/warehouse/test/date=20130201/city=cd

‍对应于date=20130202, city=sh 的HDFS 子目录为；‍

/warehouse/test/date=20130202/city=sh

    CREATE TABLE tmp_table //表名  
	(
		title   string, //字段名称 字段类型
		minimum_bid     double,
		quantity        bigint,
		have_invoice    bigint
	)COMMENT'注释：XXX' //表注释
		PARTITIONED BY(ptSTRING) //分区表字段（如果你文件非常之大的话，采用分区表可以快过滤出按分区字段划分的数据）
		ROW FORMAT DELIMITED   FIELDSTERMINATED BY '\001' //字段是用什么分割开的；
		STOREDAS SEQUENCEFILE; #用哪种方式存储数据，SEQUENCEFILE是hadoop自带的文件压缩格式

一些相关命令  

    SHOW TABLES; 查看所有的表  
	SHOW TABLES '*TMP*'; #支持模糊查询  
	SHOWPARTITIONS TMP_TABLE; #查看表有哪些分区  
	DESCRIBE TMP_TABLE; #查看表结构

分区表的shell 示例：  

    //创建数据文件partition_table.dat  
	//创建表  
	create table partition_table(rectime string,msisdnstring) partitioned by(daytime string,citystring) row format delimited fields terminated by '\t' stored as TEXTFILE;  

	//加载数据到分区  
	load data local inpath'/home/partition_table.dat' into table partition_tablepartition (daytime='2016-05-01',city='cd');

	//查看数据  
	select * from partition_table
	select count(*) from partition_table  

	//删除表   
	drop table partition_table




**Hive的数据模型—桶表**

定义：桶表是对数据进行哈希取值，然后放到不同文件中存储。    

    //创建表
	create table bucket_table(id string) clustered by(id) into 4 buckets; 
	//若加载数据先设置配置项 hive.enforce.bucketing = true;（在hive-site.xml文件中；默认为false）  
	//数据加载到桶表时，会对字段取hash值，然后与桶的数量取模。把数据放到对应的文件中。
	insert into table bucket_table select name from stu;   
	insert overwrite table bucket_table select name from stu;
	
	//抽样查询
	select * from bucket_table tablesample(bucket 1 out of 4 on id);


**Hive的数据模型-外部表**

指向已经在 HDFS 中存在的数据，可以创建 Partition;它和内部表在元数据的组织上是相同的，而实际数据的存储则有较大的差异。内部表的创建过程和数据加载过程（这两个过程可以在同一个语句中完成），在加载数据的过程中，实际数据会被移动到数据
仓库目录中；之后对数据对访问将会直接在数据仓库目录中完成。删除表时，表中的数据和元数据将会被同时删除。外部表只有一个过程，加载数据和创建表同时完成，并不会移动到数据仓库目录中,只是与外部数据建立一个链接。当删除一个外部表时，仅删除链接。  

    <span>
		CREATEEXTERNAL TABLE page_view
			( viewTimeINT,
			  useridBIGINT,
			  page_urlSTRING,  
			  referrer_urlSTRING, 
			  ipSTRING COMMENT 'IP Address of the User',
			  country STRING COMMENT 'country of origination‘
			)
		COMMENT 'This is the staging page view table'
    	ROW FORMAT DELIMITED FIELDSTERMINATED BY '44' LINES   TERMINATED BY '12'  
    	STORED ASTEXTFILE  
    	LOCATION 'hdfs://centos:9000/user/data/staging/page_view';
	</span>


外部表的shell 实例：   

    //创建数据文件external_table.dat
	//创建表
	create external table external_table1 (key string) ROW FORM   AT  DELIMITED FIELDS TERMINATED BY '\t' location '/home/external';

	//在HDFS创建目录/home/external
	hadoop fs -put /home/external_table.dat /home/external  

	//加载数据  
	LOAD DATA INPATH '/home/external_table1.dat' INTO TABLE external_table1;  

	//查看数据  
	select * from external_table
	select count(*) from external_table 

	//删除表
	drop table external_table