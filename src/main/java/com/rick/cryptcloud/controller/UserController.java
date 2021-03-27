package com.rick.cryptcloud.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.common.dto.FileContentDTO;
import com.rick.cryptcloud.common.Enum.DTOEnum;
import com.rick.cryptcloud.common.Enum.ResultEnum;
import com.rick.cryptcloud.VO.ResultVO;
import com.rick.cryptcloud.service.DownloadFileService;
import com.rick.cryptcloud.service.UserService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.Objects;

@RestController
@RequestMapping("user")
public class UserController {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private UserService userService;

    @Autowired
    private DownloadFileService downloadFileService;

    /**
     * 用户注册
     * @param username
     * @param email
     * @param password
     * @return
     */
    @RequestMapping("register")
    public ResultVO<String> registerUser(String username, String email, String password) {
        log.info("用户注册信息：username：{}, email：{}, password：{}", username, email, password);
        log.info("开始注册");
        BasicDTO userDTO = userService.addUser(username, email, password);
        if (userDTO.getCode() == DTOEnum.FAILED.getCode()) {
            return new ResultVO<>(ResultEnum.FAILED, userDTO.getMessage());
        }
        return new ResultVO<>(ResultEnum.SUCCESS, userDTO.getMessage());
    }


    @RequestMapping("download")
    public ResponseEntity<InputStreamResource> downloadFile(String username, String filename, String privatekey) {
        log.info("入参：username:{},filename:{}, privatekey:{}", username, filename, privatekey);
        FileContentDTO fileContentDTO = downloadFileService.downloadFile(username, filename, privatekey);
        log.info("文件下载出参：{}", GSON.toJson(fileContentDTO));
        if (DTOEnum.FAILED.getCode() == fileContentDTO.getCode()) {
            log.info("文件：{}下载失败：{}", filename, fileContentDTO.getMessage());
            return null;
        }
        String content = Objects.requireNonNull(fileContentDTO).getContent();
        long filesize = content.getBytes().length;
        log.info("文件内容下载成功：{}，文件大小：{}准备返回用户", content, filesize);
        InputStream fileInputStream = IOUtils.toInputStream(content);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        ResponseEntity<InputStreamResource> responseEntity = null;
        try {
            responseEntity = ResponseEntity.ok().headers(headers).contentLength(filesize)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(fileInputStream));
        } catch (Exception e) {
            log.error("文件：{}下载失败：{}", filename, e.getMessage());
        }
        log.info("文件：{}下载成功", filename);
        return responseEntity;
    }
}
