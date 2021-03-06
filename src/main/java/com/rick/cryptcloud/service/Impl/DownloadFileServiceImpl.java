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
            log.info("??????????????????????????????");
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("????????????????????????");
        if (!downloadTuple()) {
            log.info("??????????????????");
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("????????????????????????");
        String fContent, fkContent, rkContent;
        try {
            log.info("????????????????????????");
            rkContent = FileUtils.readFileToString(new File(tupleLocation + rkTupleName));
            fkContent = FileUtils.readFileToString(new File(tupleLocation + fkTupleName));
            fContent = FileUtils.readFileToString(new File(tupleLocation + filename));
        } catch (IOException e) {
            log.error("???????????????????????????{}", e.getMessage());
            return new FileContentDTO(DTOEnum.FAILED);
        }
        try {
            log.info("????????????????????????");
            rk = SerializationUtils.deserialize(Base64.decodeBase64(rkContent));
            fk = SerializationUtils.deserialize(Base64.decodeBase64(fkContent));
            f = SerializationUtils.deserialize(Base64.decodeBase64(fContent));
        } catch (Exception e) {
            log.error("???????????????????????????{}", e.getMessage());
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("?????????????????????RK???{}???FK???{}???F???{}", GSON.toJson(rk), GSON.toJson(fk), GSON.toJson(f));
        log.info("????????????????????????????????????");
        if (!decrypt(privatekey)) {
            log.info("??????????????????????????????");
            return new FileContentDTO(DTOEnum.FAILED);
        }
        log.info("?????????????????????{}", plainText);
        return new FileContentDTO(plainText);
    }

    /**
     * ??????????????????????????????
     * @param username
     * @param filename
     * @return
     */
    private boolean checkMapping(String username, String filename) {
        List<UserRole> userRoleList = null;
        List<RoleFile> roleFileList = null;
        try {
            log.info("????????????????????????");
            userRoleList = userRoleMapper.selectAll();
            roleFileList = roleFileMapper.selectAll();
        } catch (Exception e) {
            log.error("???????????????????????????{}", e.getMessage());
        }

        if (null != userRoleList && null != roleFileList) {
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
        }
        return false;
    }

    /**
     * ????????????
     */
    private boolean downloadTuple() {
        rkTupleName = userRole.getUsername() + "_" + userRole.getRolename() + "_" + String.valueOf(userRole.getVersion()) + SUFFIX;
        String filenameWithNoSuffix = roleFile.getFilename().substring(0, roleFile.getFilename().lastIndexOf("."));
        fkTupleName = roleFile.getRolename() + "_" + filenameWithNoSuffix + "_" + String.valueOf(roleFile.getVersionRole()) + "_" + String.valueOf(roleFile.getVersionFile()) + SUFFIX;
        try {
            aliyunUtils.downloadToLocal(rkTupleName);
            aliyunUtils.downloadToLocal(fkTupleName);
            aliyunUtils.downloadToLocal(roleFile.getFilename());
        } catch (Exception e) {
            log.error("?????????????????????{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * ????????????????????????
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
            log.error("???????????????{}", e.getMessage());
            return false;
        }
        CipherFK cipherFK = null;
        try {
            log.info("??????????????????????????????");
            cipherFK = SerializationUtils.deserialize(Base64.decodeBase64(cipherKeyList));
        } catch (Exception e){
            log.error("???????????????{}", e.getMessage());
            return false;
        }

        log.info("?????????????????????????????????{}", GSON.toJson(cipherFK));
        String cipherText = f.getCryptoFile();
        int round = cipherFK.getT();
        String kt = cipherFK.getkT();
        long rpk = Long.parseLong(cipherFK.getRpk());
        long N = cipherFK.getN();
        log.info("????????????????????????");
        long[] keylist = RotationUtils.FDri(rpk, Long.parseLong(kt), round, N);
        log.info("??????????????????{}", GSON.toJson(keylist));
        for (int i = round - 1; i >= 0; i--) {
            try {
                plainText = AESUtils.decryptAES(cipherText, String.valueOf(keylist[i]));
                cipherText = plainText;
            } catch (Exception e) {
                log.error("???????????????{}", e.getMessage());
                return false;
            }
        }
        log.info("????????????,????????????{}", plainText);
        return true;
    }

}
