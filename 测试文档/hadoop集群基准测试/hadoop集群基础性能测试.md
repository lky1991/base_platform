

## hadoop 基准测试之基础测试  


### 前言  


> 基础性能测试主要指只测试大数据平台的某个特定组件或应用。例如GridMix是面向Hadoop集 群的测试基准；TeraSort只针对文本数据的排序；雅虎 开发的YCSB对比NoSQL数据库的性能，其目的是评 估键值和云数据库[7] ；Facebook的LinkBench专门用于 研究与开发 Research & Development 46 测试存储社交图谱和网络服务的数据库。本文主要介绍使用hadoop自带的性能测试工具来达到测试目的。


## 测试工具  


hadoop自带的两个架包；在hadoop2.7中名为：  
hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar  
hadoop-mapreduce-examples-2.7.1.2.3.2.0-2950.jar


## 测试对象  
hadoop 的hdfs的IO性能，mapRedece 的性能，NameNode负载性能以及hadoop集群在小作业重复运行时的稳定性。


## 测试方法及内容  


### 测试HDFS的IO性能  


使用工具：hadoop自带的性能测试架包hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar的estDFSIO来达到测试效果


#### 读测试  
运行测试命令：
```shell
    hadoop jar /path/hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar TestDFSIO -read -nrFiles 16 -fileSize 1GB 
```
查看结果命令：
```shell
    hdfs dfs -cat /benchmarks/TestDFSIO/io_read/part-*
```
说明：将从HDFS中读取16个1G的文件。


#### 写测试  
命令：
```shell
    hadoop jar /path/hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar TestDFSIO  -write -nrFiles 16 -fileSize 1GB
```
```shell
    hdfs dfs -cat /benchmarks/TestDFSIO/io_write/part-*
```
说明：往HDFS中写入10个100M的文件，结果将会写到一个本地文件TestDFSIO_results.log查看运行效果。

    
### 测试NameNode负载  
使用工具：hadoop自带的性能测试架包hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar架包的nnbench来测试；具体命令
```shell
    hadoop jar /path/hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar nnbench -operation create_write -maps 12 -reduces 6 -blockSize 1 -bytesToWrite 0 -numberOfFiles 1000 -replicationFactorPerFile 3  -readFileAfterOpen true -baseDir /benchmarks/NNBench-`hostname -s`
```

说明：上面实例命令是用12个mapper和6个reducer来创建1000个文件。修改三个参数重复12次；


    
### 小作业重复运行  
测试工具：使用hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar架包的mrbench来测试达到测试效果（小作业重复运行主要用来检查在机群上小作业的运行是否可重复以及运行是否高效）。  
命令：
```shell
    hadoop jar /path/hadoop-mapreduce-client-jobclient-2.7.1.2.3.2.0-2950-tests.jar mrbench -numRuns 20
```
说明：调整小作业的数量来测试集群在小作业的重复运行过程中是否能保持高效。



#### 排序测试  
测试工具：使用hadoop-mapreduce-examples-2.7.1.2.3.2.0-2950.jar架包的TeraSort来测试hadoop 的排序能力。一个完整的TeraSort测试需要按以下三步执行：  

- 用TeraGen生成随机数据对输入数据运行TeraSort用TeraValidate验证排好序的输出数据
- 运行TeraSort对数据进行排序
- 运行TeraValidate来验证TeraSort输出的数据是否有序

命令：  
生成数据：

```shell
 hadoop jar hadoop-mapreduce-examples-2.7.1.2.3.2.0-2950.jar teragen <number of 100-byte rows> <output dir>
```
 数据排序：
```shell
    hadoop jar /path/hadoop-mapreduce-examples-2.7.1.2.3.2.0-2950.jar terasort /examples/terasort-input /examples/terasort-output
```
 检查数据：
```shell
     hadoop jar /path/hadoop-mapreduce-examples-2.7.1.2.3.2.0-2950.jar teravalidate /examples/terasort-output /examples/terasort-validate
```


