package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.F;
import com.rick.cryptcloud.DO.FK;
import com.rick.cryptcloud.DO.RK;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.Enum.ResultEnum;
import com.rick.cryptcloud.VO.ResultVO;
import com.rick.cryptcloud.common.AESUtils;
import com.rick.cryptcloud.common.AliyunUtils;
import com.rick.cryptcloud.common.ElgamalUtils;
import com.rick.cryptcloud.common.RotationUtils;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.dao.UserRoleMapper;
import com.rick.cryptcloud.service.DownloadFileService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DownloadFileServiceImpl implements DownloadFileService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Gson GSON = new Gson();

    @Value("${file.tupleLocation}")
    private String tupleLocation;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleFileMapper roleFileMapper;

    @Autowired
    private AliyunUtils aliyunUtils;

    private static UserRole userRole = null;

    private static RoleFile roleFile = null;

    private static final String suffix = ".txt";

    private static long rpk;

    private static String rkTupleName;

    private static String fkTupleName;

    private static String rkContent;

    private static String fkContent;

    private static String fContent;

    private static RK rk = null;

    private static FK fk = null;

    private static F f = null;

    private static CipherFK cipherFK = null;

    @Override
    public ResultVO<String> downloadFile(String username, String filename, String privatekey) {
        if (!checkMapping(username, filename)) {
            log.info("该角色无权访问此文件");
            return new ResultVO<>(ResultEnum.FAILED);
        }
        log.info("开始下载元组文件");
        downloadTuple();
        log.info("元组文件下载完成");
        try {
            rkContent = FileUtils.readFileToString(new File(tupleLocation + rkTupleName));
            fkContent = FileUtils.readFileToString(new File(tupleLocation + fkTupleName));
            fContent = FileUtils.readFileToString(new File(tupleLocation + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("开始反序列化");
        rk = SerializationUtils.deserialize(Base64.decodeBase64(rkContent));
        fk = SerializationUtils.deserialize(Base64.decodeBase64(fkContent));
        f = SerializationUtils.deserialize(Base64.decodeBase64(fContent));
        log.info("反序列化结果：RK：{}，FK：{}，F：{}", GSON.toJson(rk), GSON.toJson(fk), GSON.toJson(f));
        log.info("开始解密元组获取文件数据");
        String textContent = decrypt(privatekey);
        log.info("解密得到文件数据为：{}", textContent);
        return new ResultVO<>(textContent);
    }

    /**
     * 检查映射关系是否存在
     * @param username
     * @param filename
     * @return
     */
    private boolean checkMapping(String username, String filename) {
        List<UserRole> userRoleList = null;
        List<RoleFile> roleFileList = null;
        try {
            log.info("开始查询映射关系");
            userRoleList = userRoleMapper.selectAll();
            roleFileList = roleFileMapper.selectAll();
        } catch (Exception e) {
            log.error("映射关系查询失败：{}", e.getMessage());
        }
        for (int i = 0; i < userRoleList.size(); i++) {
            if (username.equals(userRoleList.get(i).getUsername())) {
                for (int j = 0; j < roleFileList.size(); j++) {
                    if (userRoleList.get(i).getRolename().equals(roleFileList.get(j).getRolename())
                            && filename.equals(roleFileList.get(j).getFilename())) {
                        userRole = userRoleList.get(i);
                        roleFile = roleFileList.get(j);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 下载元组
     */
    private void downloadTuple() {
        rkTupleName = userRole.getUsername() + "_" + userRole.getRolename() + "_" + String.valueOf(userRole.getVersion()) + suffix;
        String filenameWithNoSuffix = roleFile.getFilename().substring(0, roleFile.getFilename().lastIndexOf("."));
        fkTupleName = roleFile.getRolename() + "_" + filenameWithNoSuffix + "_" + String.valueOf(roleFile.getVersionRole()) + "_" + String.valueOf(roleFile.getVersionFile()) + suffix;
        aliyunUtils.downloadToLocal(rkTupleName);
        aliyunUtils.downloadToLocal(fkTupleName);
        aliyunUtils.downloadToLocal(roleFile.getFilename());
    }

    /**
     * 解密获取文件内容
     * @param privatekey
     * @return
     */
    public String decrypt(String privatekey) {
        String rolePrivateKey = ElgamalUtils.decryptByPrivateKey(rk.getCryptoRolekey(), privatekey);
        log.info("rolePrivateKey:{}", rolePrivateKey);
        String cipherKeyList = ElgamalUtils.decryptByPrivateKey(fk.getCipherFk(), rolePrivateKey);
        log.info("开始反序列化密钥列表");
        cipherFK = SerializationUtils.deserialize(Base64.decodeBase64(cipherKeyList));
        log.info("反序列化密钥列表结果：{}", GSON.toJson(cipherFK));
        String cipherText = f.getCryptoFile();
        int round = cipherFK.getT();
        String k = cipherFK.getK0();
        rpk = Long.valueOf(cipherFK.getRpk());
        long N = cipherFK.getN();
        log.info("开始获取密钥列表");
        long[] keylist = RotationUtils.FDri(rpk, Long.parseLong(k), round, N);
        log.info("密钥列表为：{}", GSON.toJson(keylist));
        String plainText = "";
        for (int i = round - 1; i >= 0; i--) {
            plainText = AESUtils.decryptAES(cipherText, String.valueOf(keylist[i]));
            cipherText = plainText;
        }
        log.info("解密内容为：{}", plainText);
        return plainText;
    }


}
