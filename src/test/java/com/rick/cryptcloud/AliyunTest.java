package com.rick.cryptcloud;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.File;

@SpringBootTest
@ActiveProfiles("test")
public class AliyunTest {


    @Value("${file.baseLocation}")
    private String baseLocation;

    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;
    /**
     * 测试上传文件到Aliyun OSS
     */
    @Test
    public void test1() {
        String bucket = "crypt-cloud";
        String content = "Hello Rick";
        String textName = "crypt-cloud/Hello.txt";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, textName, new ByteArrayInputStream(content.getBytes()));
        ossClient.putObject(putObjectRequest);
        ossClient.shutdown();
    }

    @Test
    public void test2() {
        String bucket = "crypt-cloud";
        String objName = "crypt-cloud/Hello.txt";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.getObject(new GetObjectRequest(bucket, objName), new File(baseLocation + "download\\hello.txt"));
        ossClient.shutdown();
    }

}
