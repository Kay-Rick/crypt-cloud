package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.F;
import com.rick.cryptcloud.DO.FK;
import com.rick.cryptcloud.DO.RK;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.common.dto.FileContentDTO;
import com.rick.cryptcloud.common.Enum.DTOEnum;
import com.rick.cryptcloud.common.utils.AESUtils;
import com.rick.cryptcloud.common.utils.AliyunUtils;
import com.rick.cryptcloud.common.utils.ElgamalUtils;
import com.rick.cryptcloud.common.utils.RotationUtils;
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

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

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

    private static String rkTupleName;

    private static String fkTupleName;

    private static RK rk = null;

    private static FK fk = null;

    private static F f = null;

    private static String plainText;

    private static final String SUFFIX = ".txt";

    @Override
    public FileContentDTO downloadFile(String username, String filename, String privatekey) {
        if (!checkMapping(username, filename)) {
            log.info("该角色无权访问此文件");
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("开始下载元组文件");
        if (!downloadTuple()) {
            log.info("元组下载失败");
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("元组文件下载完成");
        String fContent, fkContent, rkContent;
        try {
            log.info("开始读取元组文件");
            rkContent = FileUtils.readFileToString(new File(tupleLocation + rkTupleName));
            fkContent = FileUtils.readFileToString(new File(tupleLocation + fkTupleName));
            fContent = FileUtils.readFileToString(new File(tupleLocation + filename));
        } catch (IOException e) {
            log.error("读取元组文件失败：{}", e.getMessage());
            return new FileContentDTO(DTOEnum.FAILED);
        }
        try {
            log.info("开始元组反序列化");
            rk = SerializationUtils.deserialize(Base64.decodeBase64(rkContent));
            fk = SerializationUtils.deserialize(Base64.decodeBase64(fkContent));
            f = SerializationUtils.deserialize(Base64.decodeBase64(fContent));
        } catch (Exception e) {
            log.error("元组反序列化失败：{}", e.getMessage());
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("反序列化结果：RK：{}，FK：{}，F：{}", GSON.toJson(rk), GSON.toJson(fk), GSON.toJson(f));
        log.info("开始解密元组获取文件数据");
        if (!decrypt(privatekey)) {
            log.info("该用户无法解密该文件");
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("文件解密成功：{}", plainText);
        return new FileContentDTO(plainText);
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

        if (null != userRoleList && null != roleFileList) {
            for (int i = 0; i < userRoleList.size(); i++) {
                if (username.equals(userRoleList.get(i).getUsername())) {
                    for (int j = 0; j < roleFileList.size(); j++) {
                        if (userRoleList.get(i).getRolename().equals(roleFileList.get(j).getRolename())
                                && filename.equals(roleFileList.get(j).getFilename())) {
                            userRole = userRoleList.get(i);
                            roleFile = roleFileList.get(j);
                        }
                    }
                }
            }
        }
        Integer version = getLastedVersionRole(userRole.getRolename());
        return version.equals(userRole.getVersion());
    }

    /**
     * 下载元组
     */
    private boolean downloadTuple() {
        rkTupleName = userRole.getUsername() + "_" + userRole.getRolename() + "_" + userRole.getVersion() + SUFFIX;
        String filenameWithNoSuffix = roleFile.getFilename().substring(0, roleFile.getFilename().lastIndexOf("."));
        fkTupleName = roleFile.getRolename() + "_" + filenameWithNoSuffix + "_" + roleFile.getVersionRole() + "_" + roleFile.getVersionFile() + SUFFIX;
        try {
            aliyunUtils.downloadToLocal(rkTupleName);
            aliyunUtils.downloadToLocal(fkTupleName);
            aliyunUtils.downloadToLocal(roleFile.getFilename());
        } catch (Exception e) {
            log.error("元组下载失败：{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 解密获取文件内容
     * @param privatekey
     * @return
     */
    public boolean decrypt(String privatekey) {
        String rolePrivateKey = null;
        String cipherKeyList = null;
        try {
            rolePrivateKey = ElgamalUtils.decryptByPrivateKey(rk.getCryptoRolekey(), privatekey);
            log.info("rolePrivateKey:{}", rolePrivateKey);
            cipherKeyList = ElgamalUtils.decryptByPrivateKey(fk.getCipherFk(), rolePrivateKey);
        } catch (Exception e) {
            log.error("解密失败：{}", e.getMessage());
            return false;
        }
        CipherFK cipherFK = null;
        try {
            log.info("开始反序列化密钥列表");
            cipherFK = SerializationUtils.deserialize(Base64.decodeBase64(cipherKeyList));
        } catch (Exception e){
            log.error("解密失败：{}", e.getMessage());
            return false;
        }

        log.info("反序列化密钥列表结果：{}", GSON.toJson(cipherFK));
        String cipherText = f.getCryptoFile();
        int round = cipherFK.getT();
        String kt = cipherFK.getkT();
        long rpk = Long.parseLong(cipherFK.getRpk());
        long N = cipherFK.getN();
        log.info("开始获取密钥列表");
        long[] keylist = RotationUtils.FDri(rpk, Long.parseLong(kt), round, N);
        log.info("密钥列表为：{}", GSON.toJson(keylist));
        for (int i = round - 1; i >= 0; i--) {
            try {
                plainText = AESUtils.decryptAES(cipherText, String.valueOf(keylist[i]));
                cipherText = plainText;
            } catch (Exception e) {
                log.error("解密失败：{}", e.getMessage());
                return false;
            }
        }
        log.info("解密成功,内容为：{}", plainText);
        return true;
    }


    /**
     * 获取最新的版本号帮助判断用户的权限是否被撤销
     * @param rolename
     * @return
     */
    private Integer getLastedVersionRole(String rolename) {
        log.info("开始查询UserRole信息入参：{}", rolename);
        List<UserRole> userRoleList = null;
        int result = 0;
        try {
            userRoleList = userRoleMapper.selectByRolename(rolename);
            log.info("查询UserRole信息出参：{}", GSON.toJson(userRoleList));
        } catch (Exception e) {
            log.error("查询UserRole：{}信息失败：{}", rolename, e.getMessage());
        }
        if (null != userRoleList && !userRoleList.isEmpty()) {
            for (UserRole userRole : userRoleList) {
                result = Math.max(result, userRole.getVersion());
            }
        }
        return result;
    }
}
