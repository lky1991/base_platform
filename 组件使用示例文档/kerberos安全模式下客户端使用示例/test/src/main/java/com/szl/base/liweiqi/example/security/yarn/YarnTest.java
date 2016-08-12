package com.szl.base.liweiqi.example.security.yarn;

import com.szl.base.liweiqi.example.security.Test;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;

/**
 * Created by Administrator on 2016/5/16.
 */
public class YarnTest implements Test {
    private Configuration configuration = new Configuration();

    @Override
    public void test() throws Exception {
        YarnClient client = YarnClient.createYarnClient();
        client.init(configuration);
        client.start();
        YarnClientApplication application = client.createApplication();
        GetNewApplicationResponse response = application.getNewApplicationResponse();
        ApplicationSubmissionContext context = application.getApplicationSubmissionContext();
        ApplicationId id = context.getApplicationId();
        context.setKeepContainersAcrossApplicationAttempts(true);
        context.setApplicationName("liweiqi_test");
    }
}
