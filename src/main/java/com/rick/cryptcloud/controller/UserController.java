package com.rick.cryptcloud.controller;

import com.google.gson.Gson;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DTO.FileContentDTO;
import com.rick.cryptcloud.Enum.ResultEnum;
import com.rick.cryptcloud.VO.ResultVO;
import com.rick.cryptcloud.service.DownloadFileService;
import com.rick.cryptcloud.service.StoreFileService;
import com.rick.cryptcloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new Gson();

    @Value("${file.downloadLocation}")
    private String downloadLocation;

    @Value("${file.uploadLocation}")
    private String uploadLocation;

    @Value("${file.tupleLocation}")
    private String tupleLocation;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreFileService storeFileService;

    @Autowired
    private DownloadFileService downloadFileService;


    @RequestMapping("all")
    public List<User> selectAllUser() {
        log.info("开始处理前端查询所有请求");
        List<User> list = userService.queryAll();
        log.info("处理完毕，返回结果");
        return list;
    }

    @RequestMapping("upload")
    public ResultVO<String> uploadFile(String username, @RequestPart("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                file.transferTo(new File(uploadLocation + file.getOriginalFilename()));
                storeFileService.storeFile(username, file.getOriginalFilename());
            } catch (Exception e) {
                log.error("{}上传失败：{}", file.getOriginalFilename(), e.getMessage());
                return new ResultVO<>(ResultEnum.FAILED, "文件上传失败");
            }
            log.info("{}上传成功", file.getOriginalFilename());
        }
        return new ResultVO<String>(ResultEnum.SUCCESS, "文件上传成功");
    }

    @RequestMapping("download")
    public ResponseEntity<InputStreamResource> downloadFile(String username, String filename, String privatekey) {
        FileContentDTO fileContentDTO = null;
        log.info("入参：username:{},filename:{}, privatekey:{}",username, filename, privatekey);
        try {
            fileContentDTO = downloadFileService.downloadFile(username, filename, privatekey);
        } catch (Exception e) {
            log.error("文件下载失败：{}", GSON.toJson(fileContentDTO));
        }
        String realFilePath = downloadLocation + filename;
        FileSystemResource file = new FileSystemResource(realFilePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        ResponseEntity<InputStreamResource> responseEntity = null;
        try {
            responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (Exception e) {
            log.error("文件：{}下载失败：{}", realFilePath, e.getMessage());
        }
        log.info("文件：{}下载成功", realFilePath);
        return responseEntity;
    }
}
