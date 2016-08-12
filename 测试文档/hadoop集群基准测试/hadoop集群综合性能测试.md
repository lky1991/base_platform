## hadoop集群综合测试
### 前言
>综合类的测试工具，主要指该工具模拟几类典型应用，覆盖大数据软件平台的多个功能组件，比如英特尔的Hibench是针对Hadoop和Hive平台的基准测试工具，其负载按照业务能分为微型负载、搜索业务、机器学习和分析请求等四类（现已增加到流式测试了);BigDataBench是中科院计算所提出的大数据测试工具，覆盖了结构数据、半结构数据和非结构数据,其负载模拟了搜索引擎、社交网络和电子商务等业务模型。本文主要通过介绍hibench来带大家了解综合类的测试工具。



### 测试工具
HiBench 5.0

支持的软件版本：

- paython >=2.6; java >=1.7;
- CDH4/CDH5 release of MR1 and MR2.
- Apache release of Hadoop 1.x and Hadoop 2.x
- CDH4/CDH5 release of MR1 and MR2.
- Spark1.2 - 1.6
- Storm 0.9.3
- Samza 0.8.0

工具介绍：Hibench是基于大数据平台的综合类测试工具，它覆盖了微型负载、搜索业务、机器学习、分析请求、流式处理等测试业务。覆盖了大数据处理集群中的hadoop、spark、storm、等组件或服务的性能测试。


### 测试对象
说明:Hibench 作为一个综合的测试工具主要针对以下测试对象：

1. hadoop 基准测试，主要指hadoop的基础指标，hdfs的io性能，namenode 的负载能力，排序等。
2. spark的基准测试，主要测试spark作业的相应性能指标
3. 网络搜索的基准测试，主要指PageRank (pagerank)和Nutch indexing (nutchindexing)
4. 测试hive sql 的scan、join、aggregate等测试
5. 机器学习方面的性能指标获取
6. Streaming based Micro benchmarks:



### 测试方法
1. 下载Hibench [https://github.com/intel-hadoop/HiBench](https://github.com/intel-hadoop/HiBench "HiBench地址")
2. 编译Hibench 
	  >1、确认环境，版本不存在问题。  
	  >2、编译hibench，命令：
	  >
	  >```shell  
	  >cd src
	  >```
	  >
	  >```shell
	  > mvn clean package -D spark1.4 -D MR2
	  >```
		
3. 配置Hibench
	>1、进入hibench的conf目录下，创建真实的配置文件（已经有一同名临时文件存在）；命令：
	>````shell
	>  cp 99-user_defined_properties.conf.template 99-user_defined_properties.conf
	>````
	>
	>2、编辑配置文件，主要确保配置参数：  
	>
	>-  hibench.hadoop.home  

	>       The Hadoop installation location（测试集群值为：/usr/hdp/current/hadoop-client）
	>-  hibench.spark.home           

	>       The Spark installation location（测试中的值为：/usr/hdp/current/spark-client）
	>-  hibench.hdfs.master      

	>       HDFS master （hdfs://hadoop01:8020）
	>-  hibench.spark.master     

	>       SPARK master (yarn-client)

	>3、针对hadoop/spark 的版本来调整参数：  
	>
	>- hibench.hadoop.executable
	>- hibench.hadoop.version
	>- hibench.spark.version

	>4、调整配置文件内容来选择相应测试方向
	>      主要针对conf目录下的benchmarks.lst 和languages.lst文件，benchmarks.lst 文件控制有哪些测试的用例，包含：
	>      
	>- #aggregation
	>- #join
	>- #kmeans
	>- #pagerank
	>- #scan
	>- #sleep
	>- #sort
	>- wordcount
	>- #bayes
	>- #terasort
	>- #nutchindexing
	>- #dfsioe

	>这儿只是释放出了wordcount 来测试（所有都放出的情况下是你的环境中各种hibench测试的服务都安装好且配置正确）
	>同时languages.lst文件则控制了测试的语言，分别有：  
	>
	>- mapreduce
	>- #spark/java
	>- #spark/scala
	>- #spark/python
	>这儿只需要做hadoop 的mapreduce就能支持wordcount测试了。不用放出spark。

4. 运行hibench测试
	>方式一：命令：
	>```shell
	>bin/run-all.sh
	>```
	>
	>方式二：命令：
	>
	>数据准备命令：
	>```shell workloads/测试名/prepare/prepare.sh
	>```
	>
	>运行：
	>```shell
	>workloads/wordcount/mapreduce/bin/run.sh
	>```
	>
5. 在文档中查看结果
	>hadoop 的hdfs文件系统下的HiBench目录内存有测试结果
	>


### 总结
Hibench针对集群的多个业务场景，在各个场景都有广泛应用。在本次的测试中，发现在hadoop的基准测试（微负载）方面，测试中Hibench 的测试结果是调用hadoop自带的测试架包来得到结果的。
