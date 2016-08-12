package com.szl.base.liweiqi.example.security.hive;

import com.szl.base.liweiqi.example.security.hdfs.HdfsTest;
import com.szl.base.liweiqi.example.security.Test;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Administrator on 2016/5/16.
 */
public class HiveTest implements Test {
    private static final Logger logger = Logger.getLogger(HdfsTest.class);
    private final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
    private final String USER_NAME_KEY = "hdfs.kerberos.principal";
    private Configuration configuration;

    public HiveTest() {
        configuration = new Configuration();
        configuration.set(KEYTAB_FILE_KEY, "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\liweiqi.keytab");
        configuration.set(USER_NAME_KEY, "liweiqi@hadoop");
    }

    @Override
    public void test() throws Exception {
        System.setProperty("java.security.krb5.conf", "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\krb5.conf");
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        SecurityUtil.login(configuration, KEYTAB_FILE_KEY, USER_NAME_KEY);
        String url = "jdbc:hive2://hadoop03.domain:10000/test;principal=hive/hadoop03.domain@hadoop";
        Connection connection = DriverManager.getConnection(url, "hive", "hive");
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
}
