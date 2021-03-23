package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.FK;
import com.rick.cryptcloud.DO.RK;
import com.rick.cryptcloud.DO.Role;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.common.AliyunUtils;
import com.rick.cryptcloud.common.DSAUtils;
import com.rick.cryptcloud.common.ElgamalUtils;
import com.rick.cryptcloud.common.RotationUtils;
import com.rick.cryptcloud.dao.RKMapper;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.dao.RoleMapper;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.dao.UserRoleMapper;
import com.rick.cryptcloud.service.RevokeService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RevokeServiceImpl implements RevokeService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new Gson();

    @Value("${file.updateLocationPrefix}")
    private String updatePrefix;

    @Autowired
    private AliyunUtils aliyunUtils;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleFileMapper roleFileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RKMapper rkMapper;

    private static final int MAX_ROUND = 20;

    private static final String suffix = ".txt";

    @Override
    public void revokeUserRole(String username, String rolename) {
        if (null != username && null != rolename) {
            // 重新为role生成密钥
            Map<String, Object> elgamalKey = generateElgamal();
            Map<String, Object> DSAKey = generateDSA();
            RK rk = new RK();
            FK fk = null;
            // 删除userrole映射
            log.info("查所有UserRole的映射");
            List<UserRole> userRoleList = userRoleMapper.selectAll();
            log.info("查询UserRole映射出参：{}", GSON.toJson(userRoleList));
            for (UserRole userRole : userRoleList) {
                if (!username.equals(userRole.getUsername()) || !rolename.equals(userRole.getRolename())) {
                    log.info("查询其余用户：{}信息", username);
                    User user = userMapper.selectByUserName(username);
                    // 更新RK
                    rk.setRolename(rolename);
                    rk.setUsername(username);
                    userRole.setVersion(userRole.getVersion() + 1);

                    // 需更新的RK内容
                    rk.setVersionRole(userRole.getVersion());
                    rk.setCryptoRolekey(ElgamalUtils.encryptByPublicKey(ElgamalUtils.getPrivateKey(elgamalKey),
                            user.getPublicKey()));
                    rk.setCryptoRolesign(
                            ElgamalUtils.encryptByPublicKey(DSAUtils.getPrivateKey(DSAKey), user.getPublicKey()));
                    String info = rk.getVersionRole() + rk.getUsername() + rk.getRolename() + rk.getCryptoRolekey()
                            + rk.getCryptoRolesign();
                    rk.setSignature(
                            DSAUtils.getSignature(DSAUtils.signatureData(info, DSAUtils.getPrivateKey(DSAKey))));
                    // TODO: 更新内容发送MQ委派更新存储在云中的内容：rk.setXXX()  => Cloud

                    try {
                        log.info("开始更新RK信息：{}", GSON.toJson(rk));
                        rkMapper.updateByRoleName(rk);
                    } catch (Exception e) {
                        log.error("更新RK：{}失败", GSON.toJson(rk));
                    }
                    try {
                        log.info("开始更新userRole信息：{}", GSON.toJson(userRole));
                        userRoleMapper.updateVersion(userRole);
                        log.info("更新userRole信息成功");
                    } catch (Exception e) {
                        log.error("更新userRole：{}失败", GSON.toJson(userRole));
                    }
                
                }
                
                // 更新FK元组
                log.info("开始查询roleFile映射");
                List<RoleFile> roleFileList = roleFileMapper.selectAll();
                log.info("查询roleFile出参：{}", roleFileList);
                for (RoleFile roleFile : roleFileList) {
                    if (rolename.equals(roleFile.getRolename())) {
                        String filenameWithNoSuffix = roleFile.getFilename().substring(0,
                                roleFile.getFilename().lastIndexOf("."));
                        String fkTupleName = roleFile.getRolename() + "_" + filenameWithNoSuffix + "_"
                                + String.valueOf(roleFile.getVersionRole()) + "_"
                                + String.valueOf(roleFile.getVersionFile()) + suffix;
                        log.info("下载FK元组");
                        aliyunUtils.downloadToUpdate(fkTupleName);
                        log.info("下载FK元组成功");
                        String fkContent;
                        try {
                            log.info("FK元组开始反序列化");
                            fkContent = FileUtils.readFileToString(new File(updatePrefix + fkTupleName));
                            fk = SerializationUtils.deserialize(Base64.decodeBase64(fkContent));
                            log.info("FK元组：{}序列化成功",GSON.toJson(fk));
                        } catch (IOException e) {
                            log.error("FK元组：{}反序列化失败:{}", GSON.toJson(fk), e.getMessage());
                        }
                        Role role = null;
                        try {
                            log.info("开始查询role");
                            role = roleMapper.selectByRoleName(roleFile.getRolename());
                            log.info("查询Role：{}成功", GSON.toJson(role));
                        } catch (Exception e) {
                            log.error("查询Role：{}失败：{}", GSON.toJson(role), e.getMessage());
                        }
                        String cipherList = ElgamalUtils.decryptByPrivateKey(fk.getCipherFk(), role.getPrivateKey());
                        CipherFK cipherFK = SerializationUtils.deserialize(Base64.decodeBase64(cipherList));
                        log.info("反序列化得密钥列表：{}", GSON.toJson(cipherFK));
                        log.info("开始更新密钥列表");
                        
                        String rsk = cipherFK.getRsk();
                        String kt = cipherFK.getKT();
                        Long N = cipherFK.getN();
                        Long next = RotationUtils.BDri(Long.valueOf(rsk), Long.valueOf(kt), N);
                        cipherFK.setKT(String.valueOf(next));
                        if (cipherFK.getT() < MAX_ROUND) {
                            cipherFK.setT(cipherFK.getT() + 1);
                        }
                        log.info("密钥列表更新完成：{}", GSON.toJson(cipherFK));
                        cipherList = Base64.encodeBase64String(SerializationUtils.serialize(cipherFK));

                        // 找到要更新的file对应的角色重新加密
                        for (int j = 0; j < roleFileList.size(); j++) {
                            if (roleFile.getRolename().equals(roleFileList.get(j).getRolename())) {
                                Role updateRole = null;
                                try {
                                    log.info("开始查询role");
                                    updateRole = roleMapper.selectByRoleName(roleFile.getRolename());
                                    log.info("查询Role：{}成功", GSON.toJson(updateRole));
                                } catch (Exception e) {
                                    log.error("查询Role：{}失败：{}", GSON.toJson(updateRole), e.getMessage());
                                }
                                
                                // 需更新的密钥列表内容
                                String updateCipherFK = ElgamalUtils.encryptByPublicKey(cipherList, updateRole.getPublicKey());
                                // TODO：发送MQ委派更新FK元组 fk.setCipherFk(updateCipherFK)  ==> Cloud
                            }
                        }
                    }
                }
            }
            
        }
    }

    @Override
    public void revokeRoleFile(String rolename, String filename) {

    }

    private Map<String, Object> generateElgamal() {
        return ElgamalUtils.initKey();
    }

    private Map<String, Object> generateDSA() {
        return DSAUtils.initKey();
    }
}
