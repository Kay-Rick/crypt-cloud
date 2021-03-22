package com.rick.cryptcloud.common;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AliyunUtils {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.bucket}")
    private String bucket;

    @Value("${aliyun.locationPrefix}")
    private String cloudPrefix;

    @Value("${file.tupleLocation}")
    private String localPrefix;

    @Value("${file.updateLocation}")
    private String updatePrefix;

    private OSS ossClient;

    public void uploadToServer(String filename, String content) {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cloudPrefix + filename,
                new ByteArrayInputStream(content.getBytes()));
        log.info("准备上传文件：{}", filename);
        ossClient.putObject(putObjectRequest);
        log.info("文件：{}上传成功", filename);
        ossClient.shutdown();
    }

    public void downloadToLocal(String filename) {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("准备下载文件：{}到本地", filename);
        ossClient.getObject(new GetObjectRequest(bucket, cloudPrefix + filename), new File(localPrefix + filename));
        log.info("文件：{}下载完成", filename);
        ossClient.shutdown();
    }

    public void downloadToUpdate(String filename) {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("准备下载文件：{}到本地", filename);
        ossClient.getObject(new GetObjectRequest(bucket, cloudPrefix + filename), new File(updatePrefix + filename));
        log.info("文件：{}下载完成", filename);
        ossClient.shutdown();
    }
}
