package com.szl.base.liweiqi.example.security.spark;

import com.szl.base.liweiqi.example.security.Test;
import com.szl.base.liweiqi.example.security.hdfs.HdfsTest;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by Administrator on 2016/5/17.
 */
public class SparkTest implements Test {
    private static final Logger logger = Logger.getLogger(HdfsTest.class);
    private final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
    private final String USER_NAME_KEY = "hdfs.kerberos.principal";
    private Configuration configuration;
    @Override
    public void test() throws Exception {
        System.setProperty("java.security.krb5.conf", "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\krb5.conf");

        configuration = new Configuration();
        configuration.set(KEYTAB_FILE_KEY, "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\liweiqi.keytab");
        configuration.set(USER_NAME_KEY, "liweiqi@hadoop");
        SecurityUtil.login(configuration, KEYTAB_FILE_KEY, USER_NAME_KEY);


        SparkConf conf = new SparkConf().setAppName("test security").setMaster("yarn-client");
        JavaSparkContext sc = new JavaSparkContext(conf);
        String filePath = "hdfs://hadoop01.domain:8020/liweiqi/install.log";
        JavaRDD<String> rdd = sc.textFile(filePath);
        long count = rdd.count();
        logger.info(count);
    }

    public static void main(String[] args) throws Exception {
        new SparkTest().test();
    }
}
