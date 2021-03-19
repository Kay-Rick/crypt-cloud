package com.rick.cryptcloud.controller;

import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.service.StoreFileService;
import com.rick.cryptcloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Value("${file.downloadLocation}")
    private String downloadLocation;

    @Value("${file.uploadLocation}")
    private String uploadLocation;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private StoreFileService storeFileService;

    @RequestMapping("all")
    public List<User> selectAllUser() {
        log.info("开始处理前端查询所有请求");
        List<User> list = userService.queryAll();
        log.info("处理完毕，返回结果");
        return list;
    }

    @RequestMapping("upload")
    public String uploadFile(String username, @RequestPart("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                file.transferTo(new File(uploadLocation + file.getOriginalFilename()));
                storeFileService.storeFile(username, file.getOriginalFilename());
            } catch (Exception e) {
                log.error("{}上传失败：{}", file.getOriginalFilename(), e.getMessage());
                return "Failed";
            }
            log.info("{}上传成功", file.getOriginalFilename());
        }
        return "Success";
    }
}
