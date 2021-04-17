package com.rick.cryptcloud.service.Impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.Document;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.common.utils.RotationUtils;
import com.rick.cryptcloud.dao.CipherFKMapper;
import com.rick.cryptcloud.dao.DocumentMapper;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private RoleFileMapper roleFileMapper;

    @Autowired
    private CipherFKMapper cipherFKMapper;

    @Override
    public List<Document> getAllFiles() {
        log.info("开始查询所有文件");
        return documentMapper.selectAll();
    }

    @Override
    public List<RoleFile> getRoleFiles(String filename) {
        log.info("开始查询文件角色映射关系入参：filename：{}", filename);
        List<RoleFile> result = null;
        try {
            result = roleFileMapper.selectByFilename(filename);
            log.info("查询文件角色映射出参：{}", GSON.toJson(result));
        } catch (Exception e) {
            log.error("查询文件：{}映射失败：{}", filename, e.getMessage());
        }
        return result;
    }

    @Override
    public Long[] getCipherList(String k0) {
        log.info("开始查询密钥列表入参：k0：{}", k0);
        CipherFK cipherFK = null;
        try {
            cipherFK = cipherFKMapper.selectByk0(k0);
            log.info("查询密钥出参：{}", GSON.toJson(cipherFK));
        } catch (Exception e) {
            log.error("查询密钥列表k0：{}失败：{}", k0, e.getMessage());
        }
        int round = cipherFK.getT();
        String kt = cipherFK.getkT();
        long rpk = Long.parseLong(cipherFK.getRpk());
        long N = cipherFK.getN();
        log.info("开始获取密钥列表");
        long [] keylist = RotationUtils.FDri(rpk, Long.parseLong(kt), round, N);
        log.info("密钥列表为：{}", GSON.toJson(keylist));
        Long [] key = new Long[keylist.length];
        for (int i = 0; i < keylist.length; i++) {
            key[i] = keylist[i];
        }
        return key;
    }
}
