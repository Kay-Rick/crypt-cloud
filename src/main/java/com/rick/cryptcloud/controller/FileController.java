package com.rick.cryptcloud.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.Document;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.VO.FileVO;
import com.rick.cryptcloud.VO.ResultVO;
import com.rick.cryptcloud.common.Enum.ResultEnum;
import com.rick.cryptcloud.service.DocumentService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("file")
public class FileController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    public ResultVO<List<FileVO>> getFileList() {
        List<FileVO> results = new ArrayList<>();
        List<Document> fileList = documentService.getAllFiles();
        if (null == fileList || fileList.isEmpty()) {
            return new ResultVO<>(ResultEnum.FAILED, null);
        }
        for (Document document : fileList) {
            FileVO fileVO = new FileVO();
            fileVO.setId(document.getId());
            fileVO.setFilename(document.getFilename());
            String cipherKeyList = document.getCipherKey();
            String fileType = StringUtils.substring(document.getFilename(), document.getFilename().lastIndexOf("."));
            fileVO.setFileType(fileType);
            fileVO.setCipherFK(cipherKeyList);

            CipherFK cipherFK = null;
            try {
                log.info("开始反序列化密钥列表");
                cipherFK = SerializationUtils.deserialize(Base64.decodeBase64(cipherKeyList));
            } catch (Exception e){
                log.error("反序列化密钥列表失败：{}", e.getMessage());
            }
            log.info("反序列化密钥列表结果：{}", GSON.toJson(cipherFK));
            log.info("查询新的密钥列表");
            Long [] key = documentService.getCipherList(cipherFK.getK0());
            log.info("查询新的密钥列表出参：{}", GSON.toJson(key));
            fileVO.setCipherFKList(key);
            log.info("开始获取文件：{}所属角色信息", document.getFilename());
            List<RoleFile> roleFileList = documentService.getRoleFiles(document.getFilename());
            log.info("获取文件所属角色信息出参：{}", GSON.toJson(roleFileList));
            if (null != roleFileList && !roleFileList.isEmpty()) {
                List<String> rolenameList = new ArrayList<>();
                for (RoleFile roleFile : roleFileList) {
                    rolenameList.add(roleFile.getRolename());
                }
                fileVO.setRoleNameList(rolenameList);
            }
            results.add(fileVO);

        }
        return new ResultVO<>(ResultEnum.SUCCESS, results);
    }
}
