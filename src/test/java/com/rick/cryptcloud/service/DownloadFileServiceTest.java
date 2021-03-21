package com.rick.cryptcloud.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DownloadFileServiceTest {
    private final static String privatekey = "MHkCAQAwUAYGKw4HAgEBMEYCIQD4iMDIkbm8WLrXyKDHNcEZEuBdWRqFb729GEgPjw+AMwIhAJYjnFXlwQ3xSpHNFYHRs98FkH0xSTC/pgQMJCH0TqGQBCICIEVV5Lh46Br9g+hhnSdiSYSOos0ZmLo/eXUQiA8gbyzu";

    @Autowired
    private DownloadFileService downloadFileService; 

    @Test
    public void test1() {
        downloadFileService.downloadFile("Rick", "Hello.txt", privatekey);
    }
}
