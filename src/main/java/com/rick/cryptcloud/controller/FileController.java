/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-30 15:30:07
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-01 16:51:52
 * @Description: 测试文件上传、下载
 */
package com.rick.cryptcloud.controller;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file")
public class FileController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${file.downloadLocation}")
    private String downloadLocation;

    @Value("${file.uploadLocation}")
    private String uploadLocation;

    /**
     * 文件上传
     * @param username
     * @param file
     * @param files
     * @return
     */
    @RequestMapping("/upload")
    @ResponseBody
    public String upload(String username, @RequestPart("photo") MultipartFile file,
            @RequestPart("files") MultipartFile[] files) {
        log.info("上传的信息：username={}, fileSize={}, fileNum={}", username, file.getSize(), files.length);
        if (!file.isEmpty()) {
            try {
                file.transferTo(new File(uploadLocation + file.getOriginalFilename()));
            } catch (Exception e) {
                log.error("{}上传失败：{}", file.getOriginalFilename(), e.getMessage());
                return "上传失败";
            }
            log.info("{}上传成功", file.getOriginalFilename());
        }
        if (files.length > 0) {
            for (MultipartFile item : files) {
                try {
                    item.transferTo(new File(uploadLocation + item.getOriginalFilename()));
                } catch (Exception e) {
                    log.error("{}上传失败：{}", item.getOriginalFilename(), e.getMessage());
                    return "上传失败";
                }
                log.info("{}上传成功", item.getOriginalFilename());
            }
        }
        return "上传成功";
    }

    /**
     * 文件下载
     * @param id
     * @return
     */
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) {
        String realFilePath = downloadLocation + id.toString() + ".jpg";
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
