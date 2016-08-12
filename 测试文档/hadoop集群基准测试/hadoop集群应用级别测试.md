## hadoop集群应用级别测试
### 前言
应用级别测试主要指其测试工具针对于具体应用端到端的测试。这类的测试工具主要有BigBench，是基 于TPC-DS开发的端到端大数据测试工具，面向零售业 务，模拟电子商务的整个流程，主要测试MapReduce 和并行DBMS ，其优点是应用场景结合非常紧密，行业针对性很强。本文主要针对BigBench来进行简单测试。

### 测试工具
BigBench

环境要求：  

- java1.7 64位
- hadoop2.3
- others（具体测试还需具体的服务支持）


### 测试对象
主要有负载、功耗、吞吐量测试。


### 测试方法和内容
下载BigBench：[https://github.com/intel-hadoop/Big-Data-Benchmark-for-Big-Bench](https://github.com/intel-hadoop/Big-Data-Benchmark-for-Big-Bench "BigBench 的下载地址")

命令：  
```shell
git clone https://github.com/intel-hadoop/Big-Data-Benchmark-for-Big-Bench.git
```  

修改配置
主要修改下载文件的"conf/userSettings.conf"位置文件，修改项为：

- BIG_BENCH_HADOOP_LIBS_NATIVE  （这是一个可选的配置项，配置和可提升hdfs的速度）
- BIG_BENCH_HADOOP_CONF hadoop的核心配置文件的目录路径

测试运行

命令：
```shell
./bigBench runBenchmark[模块名] -m[任务] 2 ...
```  

说明：runBenchmark 运行驱动程序，具体使用查看帮助命令：
```shell
./bigBench runBenchmark -h
```

比较结果

结果在两个文件中查看

1. BigBenchResult.txt（包含驱动程序的系统输出消息）
2. BigBenchTimes.csv（其中包含所有测量的时间戳/持续时间）。

###  总结
这种测试对环境要求挺高，在修改配置文件时要注意一个测试可能会涉及到多个服务及组件。



