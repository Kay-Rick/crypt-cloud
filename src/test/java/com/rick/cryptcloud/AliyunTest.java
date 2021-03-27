package com.rick.cryptcloud;

import com.rick.cryptcloud.common.utils.AliyunUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
public class AliyunTest {

    @Autowired
    private AliyunUtils aliyunUtils;


    // @Value("${file.baseLocation}")
    // private String baseLocation;

    // @Value("${aliyun.endpoint}")
    // private String endpoint;

    // @Value("${aliyun.accessKeyId}")
    // private String accessKeyId;

    // @Value("${aliyun.accessKeySecret}")
    // private String accessKeySecret;
    

    // @Test
    // public void test1() {
    //     String bucket = "crypt-cloud";
    //     String content = "Hello Rick";
    //     String textName = "crypt-cloud/Hello.txt";
    //     OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    //     PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, textName, new ByteArrayInputStream(content.getBytes()));
    //     PutObjectResult result= ossClient.putObject(putObjectRequest);
    //     ossClient.shutdown();
    // }

    // @Test
    // public void test2() {
    //     String bucket = "crypt-cloud";
    //     String objName = "crypt-cloud/Hello.txt";
    //     OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    //     ossClient.getObject(new GetObjectRequest(bucket, objName), new File(baseLocation + "download\\hello.txt"));
    //     ossClient.shutdown();
    // }

    @Test
    public void test3() {
        String filename = "Hello.txt";
        String content = "Hello Rick";
        aliyunUtils.uploadToServer(filename, content);
    }

    @Test
    public void test4() {
        String filename = "Hello.txt";
        aliyunUtils.downloadToLocal(filename);
    }

}
