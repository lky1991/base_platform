package com.szl.base.liweiqi.example.security.kafka;

/**
 * Created by caoluyang on 2016/5/17.
 */


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestProducer {
    final static String topic = "test_topic";
    public static void main(String[] args) {
        System.setProperty("java.security.krb5.conf", "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\krb5.conf");
        System.setProperty("java.security.auth.login.config", "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\kafka_client_jaas.conf");
        String principalName = "liweiqi@hadoop";
        String keyTabPath = "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\liweiqi.keytab";
        Configuration configuration=new Configuration();
        configuration.set("keyTabPath",keyTabPath);
        configuration.set("principalName",principalName);
        try {
            SecurityUtil.login(configuration,"principalName", "keyTabPath");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Properties props = new Properties();
            props.put("metadata.broker.list","hadoop01:6667");
            props.put("zk.connectiontimeout.ms", "6000");
            props.put("producer.type","async");
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("key.serializer.class", "kafka.serializer.StringEncoder");
            props.put("request.required.acks", "1");
            props.put("security.protocol", "PLAINTEXTSASL");
            ProducerConfig config = new ProducerConfig(props);
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

}
