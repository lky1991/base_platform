package com.szl.base.liweiqi.example.security.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by dbc on 2016/5/16.
 * kerberos authentication about hbase
 */
public class HbaseKerTest {


    private static final Logger logger = Logger.getLogger(HbaseKerTest.class);

    private final String KEYTAB_FILE_PATH_KEY = "hbase.keytab.file";
    private final String USER_NAME_KEY = "hbase.kerberos.principal";
    private Configuration configuration;

    public HbaseKerTest() {
        configuration = HBaseConfiguration.create();
        configuration.set("hadoop.security.authentication", "Kerberos");
        configuration.set(KEYTAB_FILE_PATH_KEY, "E:\\KerberosAuthentication\\HbaseKerAuth\\src\\main\\resources\\liweiqi.keytab");
        // 这个可以理解成用户名信息，也就是Principal
        configuration.set(USER_NAME_KEY, "liweiqi@hadoop");
        UserGroupInformation.setConfiguration(configuration);
    }


    private void rpcTest() throws IOException {
        SecurityUtil.login(configuration, KEYTAB_FILE_PATH_KEY, USER_NAME_KEY);
        Connection connection = ConnectionFactory.createConnection(configuration);
        Table table = connection.getTable(TableName.valueOf("crawler:tmall_shop"));
        System.out.println("tablename:" + new String(table.getName().getName()));
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.security.krb5.conf", "E:\\KerberosAuthentication\\HbaseKerAuth\\src\\main\\resources\\krb5.conf");
        new HbaseKerTest().rpcTest();
    }

}
