package com.szl.base.liweiqi.example.security.hdfs;

import com.szl.base.liweiqi.example.security.Test;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 2016/5/11.
 */
public class HdfsTest implements Test {
    private static final Logger logger = Logger.getLogger(HdfsTest.class);
    private final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
    private final String USER_NAME_KEY = "hdfs.kerberos.principal";
    private Configuration configuration;

    public HdfsTest() throws IOException {
        configuration = new Configuration();
        configuration.set(KEYTAB_FILE_KEY, "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\liweiqi.keytab");
        configuration.set(USER_NAME_KEY, "liweiqi@hadoop");
    }

    private void rpcTest() throws IOException {
        SecurityUtil.login(configuration, KEYTAB_FILE_KEY, USER_NAME_KEY);
        FileSystem fs = FileSystem.get(configuration);
        FileStatus status = fs.getFileStatus(new Path("/liweiqi"));
        logger.info(status.getPermission().toString());
        fs.close();
    }

    private void httpTest() throws IOException, AuthenticationException, URISyntaxException {
        SecurityUtil.login(configuration, KEYTAB_FILE_KEY, USER_NAME_KEY);
        WebHdfsFileSystem webHdfsFileSystem = (WebHdfsFileSystem) FileSystem.get(new URI("webhdfs://hadoop01.domain:50070/"), configuration);
        webHdfsFileSystem.setConf(configuration);
        FileStatus status = webHdfsFileSystem.getFileStatus(new Path("webhdfs://hadoop01.domain:50070/liweiqi"));
        logger.info(status.getPermission().toString());
        webHdfsFileSystem.close();
    }

    @Override
    public void test() throws Exception {
        System.setProperty("java.security.krb5.conf", "C:\\Users\\Administrator\\Desktop\\test\\src\\main\\resources\\krb5.conf");
        httpTest();
        rpcTest();
    }
}
