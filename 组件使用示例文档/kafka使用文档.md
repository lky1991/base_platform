# **kafka 使用文档** 


>注：本次写的kafka使用文档都是基于java语言的。针对的使用人群是使用kafka做开发的技术人员。本文简单的介绍了kafka的特点和使用。原理的深度解析，后期再弄。

导读：  
1. kafka的定义及特点  
2. kafka的工作原理  
3. kafka的使用实例    
4. kafka在kerberos安全模式下使用实例

  
## 一、kafka定义及特点 


**1.1 kafka定义**  
Kafka 是一个基于分布式的消息发布-订阅系统；是一个消息处理的中间键。 
 
**1.2 kafka特点**  

- 以时间复杂度为O(1)的方式提供消息持久化能力，即使对TB级以上数据也能保证常数时间复杂度的访问性能。  
- 高吞吐率。即使在非常廉价的商用机器上也能做到单机支持每秒100K条以上消息的传输。  
- 支持Kafka Server间的消息分区，及分布式消费，同时保证每个Partition内的消息顺序传输。  
- 同时支持离线数据处理和实时数据处理。  

**1.3 使用kafka的优势**  

- 解耦：在项目启动之初来预测将来项目会碰到什么需求，是极其困难的。消息系统在处理过程中间插入了一个隐含的、基于数据的接口层，两边的处理过程都要实现这一接口。这允许你独立的扩展或修改两边的处理过程，只要确保它们遵守同样的接口约束。  
- 冗余：有些情况下，处理数据的过程会失败。除非数据被持久化，否则将造成丢失。消息队列把数据进行持久化直到它们已经被完全处理，通过这一方式规避了数据丢失风险。许多消息队列所采用的"插入-获取-删除"范式中，在把一个消息从队列中删除之前，需要你的处理系统明确的指出该消息已经被处理完毕，从而确保你的数据被安全的保存直到你使用完毕。
- 扩展性：因为消息队列解耦了你的处理过程，所以增大消息入队和处理的频率是很容易的，只要另外增加处理过程即可。不需要改变代码、不需要调节参数。扩展就像调大电力按钮一样简单。  
- 灵活性 & 峰值处理能力：在访问量剧增的情况下，应用仍然需要继续发挥作用，但是这样的突发流量并不常见；如果为以能处理这类峰值访问为标准来投入资源随时待命无疑是巨大的浪费。使用消息队列能够使关键组件顶住突发的访问压力，而不会因为突发的超负荷的请求而完全崩溃。
- 可恢复性：系统的一部分组件失效时，不会影响到整个系统。消息队列降低了进程间的耦合度，所以即使一个处理消息的进程挂掉，加入队列中的消息仍然可以在系统恢复后被处理。  
- 顺序保证：在大多使用场景下，数据处理的顺序都很重要。大部分消息队列本来就是排序的，并且能保证数据会按照特定的顺序来处理。Kafka保证一个Partition内的消息的有序性。  
- 缓冲：在任何重要的系统中，都会有需要不同的处理时间的元素。例如，加载一张图片比应用过滤器花费更少的时间。消息队列通过一个缓冲层来帮助任务最高效率的执行———写入队列的处理会尽可能的快速。该缓冲有助于控制和优化数据流经过系统的速度。  
- 异步通信：很多时候，用户不想也不需要立即处理消息。消息队列提供了异步处理机制，允许用户把一个消息放入队列，但并不立即处理它。想向队列中放入多少消息就放多少，然后在需要的时候再去处理它们。


## 二、kafka的工作原理 


**2.1 kafka的核心**  
kafka的核心是消息队列技术（Message Queue）是————发布/订阅 (Publish/Subscribe) 模式；发布/订阅功能使消息的分发可以突破目的队列地理指向的限制，使消息按照特定的主题甚至内容进行分发，用户或应用程序可以根据主题或内容接收到所需要的消息。发布/订阅功能使得发送者和接收者之间的耦合关系变得更为松散，发送者不必关心接收者的目的地址，而接收者也不必关心消息的发送地址，而只是根据消息的主题进行消息的收发。


## 三、kafka的使用实例 

**3.1 引入依赖**
  
在分析kafka的api前先引入写客户端代码需要的kafka依赖: 
  

    <repositories>
        <repository>
            <releases>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
                <checksumPolicy>warn</checksumPolicy>
            </releases>
            <snapshots>
                <enabled>false</enabled>
                <updatePolicy>never</updatePolicy>
                <checksumPolicy>fail</checksumPolicy>
            </snapshots>
			<!--引入第三方私有源位置-->
            <id>HDPReleases</id>
            <name>HDP Releases</name>
            <url>http://repo.hortonworks.com/content/repositories/releases/</url>
            <layout>default</layout>
        </repository>
    </repositories>
	<!--具体依赖-->
    <dependencies>
        <!--Kafka -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka_2.10</artifactId>
            <version>0.8.2.2.3.2.0-2950</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
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




**3.2 生产数据**

***3.2.1 生产数据的API***

实例化produce的3个核心类：  
1. kafka.producer.ProducerConfig： produce需要的配置项（包括连接的ip，端口相应配置参数，用一个Properties装）  
2. kafka.javaapi.producer.Producer:将ProducerConfig的对象加入实现连接。  
3. kafka.producer.KeyedMessage:把消息封装，将话题、消息、格式等封装到一起。


***3.2.2 生产数据方式介绍***  
kafka对生产数据的传输方式有同步（sync）和异步（async）之分。同步在相同情况下比异步的性能差，但更能保证数据的安全性。因此在数据传输的方式下有以下几种方式：  
1. 异步方式传输数据（能极大的提高数据的吞吐量，提高数据处理的性能，正常情况下不会发生数据丢失，但灾难的测试情况下可能发生少量数据丢失）    
2. 同步_ack 模式能保证消息至少被发送一次到服务器，保证数据无丢失。但可能存在少量数据重复，且性能较异步发送方式慢10倍加。    
3. 同步_ack_batch 模式则是对消息的批量传输。既能保证数据无丢失，也能有效提高数据的传输性能。缺点也是数据存在少量重复且客户机中数据会部分缓存在内存中。若客户机发生故障可能发生这部分数据丢失（这需要其他方式保证数据的完备）。

  
针对以上3种数据传输的方式，可以分一下场景使用：  
1. 数据量大，且允许少量数据丢失，采用异步_noack模式  
2. 数据量不大，数据不允许丢失，采用同步_ack模式  
3. 数据量大，数据不允许丢失，Producer采用同步_ack_batch模式




***3.2.3 生产数据示例代码***   

实例1：使用byte[]为单位传消息，使用异步_noack模式传输数据

```java  
    final static String topic = "mytopic";

    Properties props = new Properties();
    props.put("metadata.broker.list", "192.168.0.167:6667,192.168.0.168:6667,192.168.0.170:6667");//生产消息的ip和端口，生产的消息就向这个ip的的端口中发送
    props.put("zk.connectiontimeout.ms", "6000");//设置连接超时的时间（单位：毫秒）
	props.put("producer.type", "async");//异步的方式传输数据，这也是默认的，可以不写。
    ProducerConfig config=new ProducerConfig(props);
    String msg = "This is a test message";
    /**构造数据发送对象*/
    Producer<Integer, byte[]> producer=new Producer<>(config);//实例化一个生产者对象
  
    KeyedMessage<Integer,byte[]> keyedMessage =	new KeyedMessage<>(topic,msg.getBytes());//装配消息，设置消息的话题，也可设置key值（此处没使用这个构造方法）。
    producer.send(keyedMessage);//发送消息
```
       
    


实例2：使用String为单位传消息 采用同步_ack模式

```java
	Properties props = new Properties();
	props.put("metadata.broker.list","cluster-01.domain:6667,cluster-02.domain:6667");
	props.put("serializer.class", "kafka.serializer.StringEncoder");//传输数据的单位为String（不设置，即默认是byte[]）
	// key.serializer.class默认为serializer.class
	props.put("key.serializer.class", "kafka.serializer.StringEncoder");
	// 可选配置，如果不配置，则使用默认的partitioner
	//props.put("partitioner.class", "com.catt.kafka.demo.PartitionerDemo");
	props.put("producer.type", "sync");//使用同步的方式传输数据
	props.put("request.required.acks", "1");// 触发acknowledgement机制，保证数据至少被发送一次到服务器。否则是fire and forget，可能会引起数据丢失。
	ProducerConfig config = new ProducerConfig(props);
	Producer<String, String> producer = new Producer<>(config);
	// 产生并发送消息
	String ip = "192.168.2.";//rnd.nextInt(255);
	String msg = ",www.example.com," + ip;
	//如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
	/**构造数据发送对象*/
	while(true){
    	 KeyedMessage<String,String> keyedMessage = new KeyedMessage<>(topic,msg);
     	producer.send(keyedMessage);
     	try {
        	 TimeUnit.SECONDS.sleep(1);//producer.send(keyedMessage);
      	} catch (InterruptedException e) {
        	        e.printStackTrace();
      	}
	}
```

实例3：批量传输数据  
关键的配置文件：props.put("producer.type","sync");//采用同步的方式传输数据，在这种批量操作下，能有效提高数据的处理性能，但客户机中总存放有最近时间内的缓存数据，若一方出问题，则这部分数据将丢失。

```java
    Properties props = new Properties();
    props.put("metadata.broker.list","192.168.99.80:6667");
    props.put("zk.connectiontimeout.ms", "6000");
    props.put("producer.type","sync");//同步方式传输数据props.put("batch.size",（int）data);//batch的大小。有默认值。16384
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("key.serializer.class", "kafka.serializer.StringEncoder");
    ProducerConfig config=new ProducerConfig(props);
    int i = 10;//571  -397
    String msg ="";
    /**构造数据发送对象*/
        Producer<String, String> producer=new Producer<String,String>(config);

        List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>(100);
        for (int j = 0; j <= 10000; j++) {
            KeyedMessage<String, String> message =
                    new KeyedMessage<String, String>(topic, j + "", "Message" + j);
            messages.add(message);//批量组装消息
            if (j % 100 == 0) {
                producer.send(messages);//批量发送消息
                messages.clear();
            }
        }
        producer.send(messages);
```

***3.2.4 生产数据深层要点***

常用配置参数的了解：   

1. metadata.broker.list：连接服务器的ip：端口(一般为6667)  
2. request.required.acks：信息的安全策略，可以有效防止数据丢失。(默认为0：这意味着生产者从未等待一个确认从broker（相同的行为为0.7）。此选项提供了最低的延迟，但最弱的持久性保证（当服务器失败时可能造成部分数据丢失） 
3. batch.size：消息在内存中的处理阀值
3. producer.type：消息发送的方式（有同步和异步默认是异步）  
4. serializer.class：消息采用的序列化方式，默认是byte[]			String的话配置文件做如下修改：

        props.put("serializer.class","kafka.serializer.StringEncoder");
		props.put("key.serializer.class", kafka.serializer.StringEncoder");


异步发送：  
对于可伸缩的消息系统而言，异步非阻塞式操作是不可或缺的。在Kafka中，生产者有个选项（producer.type=async）可用指定使用异步分发出产请求（produce request）。这样就允许用一个内存队列（in-memory queue）把生产请求放入缓冲区，然后再以某个时间间隔或者事先配置好的批量大小将数据批量发送出去。因为一般来说数据会从一组以不同的数据速度生产数据的异构的机器中发布出，所以对于代理而言，这种异步缓冲的方式有助于产生均匀一致的流量，因而会有更佳的网络利用率和更高的吞吐量。  
同步发送：同步则相比异步不会做把生产请求放入缓冲区做前期的累计处理，而是直接将消息发送到服务器端。  

>注：kafka的生产数据的ip，最好在客户机的host中加入访问机（服务器）的ip，hostname等，这样避免访问出错。

更多：[http://kafka.apache.org/documentation.html#producerconfigs](http://kafka.apache.org/documentation.html#producerconfigs "kafka producer 相关参数文档")



**3.3 kafka消费数据**  

***3.3.1 kafka消费数据api***    

使用的核心api:  
1. kafka.consumer.ConsumerConfig :consumer需要的配置项（包括连接的ip，端口相应配置参数，用一个Properties装)  
2. kafka.javaapi.consumer.ConsumerConnector : 代表kafka的消费连接的实例化类；实例化方式：kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig)  
3. kafka.consumer.KafkaStream：kafka消费的数据流，一般放在一个集合（list）中，是由ConsumerConnector类提供的方法createMessageStreamsByFilter实例化的。  

 

***3.3.2 kafka消费代码示例***   

示例代码：
  
```java
    private static final ConsumerConfig connectorConfig;
   
    static { //设置配置文件
        Properties properties = new Properties();
        properties.put("zookeeper.connect","192.168.1.26:2181,192.168.1.25:2181,192.168.1.30:2181");//zookeeper的连接
        properties.put("group.id","crawler-test");//设置消费的组，这样修改这个组名后又可以重新消费以前消费过的数据。
        //properties.put("auto.offset.reset","smallest");//设置从offsets 的什么位置消费
        //properties.put("consumer.id","test");
        connectorConfig=new ConsumerConfig(properties);
    }

    private final ConsumerConnector connector;
    private final String topic;

    public KafkaConsume() {
        this.topic = "mytopic"; //默认的话题
        this.connector = kafka.consumer.Consumer.createJavaConsumerConnector(connectorConfig);//创建消费的连接
    }

    /**
      目标：实现将kafka中的数据流消费出来
     */
    public void consume() throws IOException {

        Whitelist whitelist = new Whitelist(topic);
        List<KafkaStream<byte[], byte[]>> partitions = connector.createMessageStreamsByFilter(whitelist);
        //消费
        for (KafkaStream<byte[], byte[]> partition : partitions) {//遍历分区片
            ConsumerIterator<byte[], byte[]> iterator = partition.iterator();//遍历所得的消费流
            while (iterator.hasNext()) {
                MessageAndMetadata<byte[], byte[]> next = iterator.next();
                    String message = new String(next.message(), "utf-8") + "\n";
					deal(message);//处理消息
            }
        }
        close();
    }
```
  

***3.3.4 kafka消费要点***  
1. 注意消费的配置项的设置，在同一段代码运行第二次时是消费不出数据的，因为kafak的数据在同一个group.id下只能消费一次。  
2. 这个消费的代码只能按顺序消费到当前的offsets后面的数据；在更深的消费需求请看链接：[http://kafka.apache.org/documentation.html#consumerapi](http://kafka.apache.org/documentation.html#consumerapi "官方kafka消费实例")


## 四、kafka在kerberos安全模式下使用实例

>注：此处的安全模式是指集群使用了基于kerberos+ranger+knox+ldap的安全策略。

### 4.1 依赖

版本：此处使用的经过第三方(hortonworks)重构的架包；具体依赖如下：

    <dependencies>

       <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka_2.10</artifactId>
            <version>0.8.2.2.3.2.0-2950</version>
       </dependency>

       <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>0.8.2.2.3.2.0-2950</version>
        </dependency>

        <dependency>
            <groupId>org.jasypt</groupId>
            <artifactId>jasypt-spring31</artifactId>
            <version>1.9.2</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-common</artifactId>
            <version>2.7.1.2.3.2.0-2950</version>
        </dependency>

    </dependencies>  

### 4.2 客户端安全认证 

 在此模式下访问kafka前需要做安全认证，具体的认证方式如下：  
1. 添加配置文件：添加krb5.conf 和 XX.keytab(相当于kerberos的密钥)以及kafka_client_jaas.conf 配置文件放入此目录。  
2. 设置org.apache.hadoop.conf.Configuration 的具体配置，主要是添加步骤1中的keytab文件和其对应的principal（具体看实例）  
3. 安全认证登陆，使用SecurityUtil类的login方法实现。  
4. 开始使用访问kafka并构造数据发送对象。 

>注：在运行前需要：  
>运行 System.setProperty("java.security.krb5.conf"，krb5.conf path);来初始化基础配置（这一步是在windows上才需要，在liunx服务器上则不需要）。  
>运行 System.setProperty("java.security.auth.login.config", "kafka_client_jaas.conf");来初始化kafka的安全策略配置。

 
### 4.3 示例代码  


``` java
final static String topic = "test_topic";
    public static void main(String[] args) {
        System.setProperty("java.security.krb5.conf", "D:\\ideawork\\kafka_security\\src\\main\\resources\\krb5.conf");//初始化基础的安全配置
        System.setProperty("java.security.auth.login.config", "D:\\ideawork\\kafka_security\\src\\main\\resources\\kafka_client_jaas.conf");//初始化kafka的安全策略配置
        String principalName = "liweiqi@hadoop";//设置keytab文件的principal
        String keyTabPath = "D:\\ideawork\\kafka_security\\src\\main\\resources\\liweiqi.keytab";//设置keytab文件
        Configuration configuration=new Configuration();
        configuration.set("keyTabPath",keyTabPath);
        configuration.set("principalName",principalName);
        try {
            SecurityUtil.login(configuration,"principalName", "keyTabPath");//安全登录
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            long events = Long.parseLong("3");
            Random rnd = new Random();

            Properties props = new Properties();
            props.put("metadata.broker.list","hadoop01:6667");
            props.put("zk.connectiontimeout.ms", "6000");
            props.put("producer.type","async");
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("key.serializer.class", "kafka.serializer.StringEncoder");
            props.put("request.required.acks", "1");
            props.put("security.protocol", "PLAINTEXTSASL");
            ProducerConfig config = new ProducerConfig(props);
            int i = 10;//571  -397
            String msg ="";
            /**构造数据发送对象*/
            Producer<String, String> producer=new Producer<String,String>(config);
            List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>(100);
            for (int j = 0; j <= 10000; j++) {
                KeyedMessage<String, String> message =
                        new KeyedMessage<String, String>(topic, j + "", "Message" + j);
                messages.add(message);
                if (j % 100 == 0) {
                    producer.send(messages);
                    System.out.println("j = " + j);
                    messages.clear();
                }
            }
            producer.send(messages);

        } catch (Throwable th) {
            th.printStackTrace();

        }
    }
```









