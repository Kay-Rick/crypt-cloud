package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import com.rick.cryptcloud.MQ.FKUpdate;
import com.rick.cryptcloud.MQ.RKUpdate;
import com.rick.cryptcloud.common.AliyunUtils;
import com.rick.cryptcloud.common.DSAUtils;
import com.rick.cryptcloud.common.ElgamalUtils;
import com.rick.cryptcloud.common.RotationUtils;
import com.rick.cryptcloud.dao.FKMapper;
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
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private RabbitTemplate rabbitTemplate;

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

    @Autowired
    private FKMapper fkMapper;

    private static final String EXCHANGE_NAME = "Crypt-Cloud";

    private static final String RK_ROUTING_KEY = "RK";

    private static final String FK_ROUTING_KEY = "FK";

    private static final String RK_VERSION_ROLE = "RK_VERSION_ROLE";

    private static final String RK_CRYPTO_ROLE_KEY = "RK_CRYPTO_ROLE_KEY";

    private static final String RK_CRYPTO_ROLE_SIGN = "RK_CRYPTO_ROLE_SIGN";

    private static final String RK_SIGNATURE = "RK_SIGNATURE";

    private static final String FK_VERSION_ROLE = "FK_VERSION_ROLE";

    private static final String FK_CIPHER_KEY = "FK_CIPHER_KEY";

    private static final String FK_SIGNATURE = "FK_SIGNATURE";

    private static final int MAX_ROUND = 20;

    private static final String suffix = ".txt";

    @Override
    public void revokeUserRole(String username, String rolename) {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    log.info("消息发送成功");
                } else {
                    log.error("消息发送失败：{}", cause);
                }
            }
        });
        if (null != username && null != rolename) {
            // 重新为role生成密钥
            Map<String, Object> elgamalKey = generateElgamal();
            Map<String, Object> DSAKey = generateDSA();
            // 删除userrole映射
            log.info("查所有UserRole的映射");
            List<UserRole> userRoleList = userRoleMapper.selectAll();
            log.info("查询UserRole映射出参：{}", GSON.toJson(userRoleList));
            for (UserRole userRole : userRoleList) {
                if (!username.equals(userRole.getUsername()) || !rolename.equals(userRole.getRolename())) {
                    RK rk = new RK();
                    RKUpdate rkUpdate = new RKUpdate();
                    userRole.setVersion(userRole.getVersion() + 1);
                    log.info("查询其余用户：{}信息", username);
                    User user = userMapper.selectByUserName(username);
                    log.info("查询其余用户信息出参：{}", GSON.toJson(user));
                    // 更新RK
                    rk.setRolename(rolename);
                    rk.setUsername(username);
                    // 需更新的RK内容
                    rk.setVersionRole(userRole.getVersion());
                    rk.setCryptoRolekey(ElgamalUtils.encryptByPublicKey(ElgamalUtils.getPrivateKey(elgamalKey), user.getPublicKey()));
                    rk.setCryptoRolesign(ElgamalUtils.encryptByPublicKey(DSAUtils.getPrivateKey(DSAKey), user.getPublicKey()));
                    String rkInfo = rk.getVersionRole() + rk.getUsername() + rk.getRolename() + rk.getCryptoRolekey() + rk.getCryptoRolesign();
                    rk.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(rkInfo, DSAUtils.getPrivateKey(DSAKey))));
                    try {
                        log.info("开始更新RK信息：{}", GSON.toJson(rk));
                        rkMapper.updateCrypt(rk);
                    } catch (Exception e) {
                        log.error("更新RK：{}失败", GSON.toJson(rk));
                    }
                    try {
                        log.info("开始更新userRole信息：{}", GSON.toJson(userRole));
                        userRoleMapper.updateVersion(userRole);
                    } catch (Exception e) {
                        log.error("更新userRole：{}失败", GSON.toJson(userRole));
                    }

                    // 添加需要更新的信息
                    Map<String, Object> rkUpdateMap = new HashMap<>();
                    rkUpdateMap.put(RK_VERSION_ROLE, rk.getVersionRole());
                    rkUpdateMap.put(RK_CRYPTO_ROLE_KEY, rk.getCryptoRolekey());
                    rkUpdateMap.put(RK_CRYPTO_ROLE_SIGN, rk.getCryptoRolesign());
                    rkUpdateMap.put(RK_SIGNATURE, rk.getSignature());
                    
                    rkUpdate.setRolename(rk.getRolename());
                    rkUpdate.setUsername(rk.getUsername());
                    rkUpdate.setVersionRole(rk.getVersionRole());
                    rkUpdate.setUpdateInfo(rkUpdateMap);
                    sendUpdate(RK_ROUTING_KEY, GSON.toJson(rkUpdate));
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
                        FK fk = null;
                        String fkContent;
                        try {
                            log.info("FK元组开始反序列化");
                            fkContent = FileUtils.readFileToString(new File(updatePrefix + fkTupleName));
                            fk = SerializationUtils.deserialize(Base64.decodeBase64(fkContent));
                            log.info("FK元组：{}序列化成功",GSON.toJson(fk));
                        } catch (IOException e) {
                            log.error("FK元组反序列化失败:{}", e.getMessage());
                        }
                        // 更新roleFile映射中role的version
                        roleFile.setVersionRole(roleFile.getVersionRole() + 1);
                        try {
                            log.info("开始更新roleFile信息：{}", GSON.toJson(roleFile));
                            roleFileMapper.updateRoleVersion(roleFile);
                        } catch (Exception e) {
                            log.error("更新roleFile信息失败：{}", e.getMessage());
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
                        // 安全模式：添加加密层，使密钥列表多一个密钥
                        if (cipherFK.getT() < MAX_ROUND) {
                            cipherFK.setT(cipherFK.getT() + 1);
                        }
                        log.info("密钥列表更新完成：{}", GSON.toJson(cipherFK));

                        fk.setVersionRole(roleFile.getVersionRole());
                        // 用新生成的role密钥重新加密密钥列表
                        fk.setCipherFk(ElgamalUtils.encryptByPublicKey(
                                Base64.encodeBase64String(SerializationUtils.serialize(cipherFK)),
                                ElgamalUtils.getPublicKey(elgamalKey)));
                        String fkInfo = fk.getVersionRole() + fk.getVersionFile() + fk.getFilename() + fk.getRolename()
                                + fk.getCipherFk();
                        fk.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(fkInfo, role.getSignPrivate())));

                        try {
                            log.info("开始更新FK信息：{}", GSON.toJson(fk));
                            fkMapper.updateCrypt(fk);
                        } catch (Exception e) {
                            log.error("更新FK元组失败：{}", e.getMessage());
                        }

                        role.setPrivateKey(ElgamalUtils.getPrivateKey(elgamalKey));
                        role.setPublicKey(ElgamalUtils.getPublicKey(elgamalKey));
                        role.setSignPrivate(DSAUtils.getPrivateKey(DSAKey));
                        role.setSignPublic(DSAUtils.getPublicKey(DSAKey));
                        try {
                            log.info("开始更新Role信息：{}", GSON.toJson(role));
                            roleMapper.updateByRoleName(role);
                        } catch (Exception e) {
                            log.error("更新Role失败：{}", e.getMessage());
                        }
                        FKUpdate fkUpdate = new FKUpdate();
                        Map<String, Object> fkUpdateMap = new HashMap<>();
                        fkUpdateMap.put(FK_CIPHER_KEY, cipherFK);
                        fkUpdateMap.put(FK_VERSION_ROLE, fk.getVersionRole());
                        fkUpdateMap.put(FK_SIGNATURE, fk.getSignature());

                        fkUpdate.setFilename(roleFile.getFilename());
                        fkUpdate.setRolename(roleFile.getRolename());
                        fkUpdate.setVersionFile(roleFile.getVersionFile());
                        fkUpdate.setVersionRole(roleFile.getVersionRole());
                        fkUpdate.setUpdateInfo(fkUpdateMap);
                        sendUpdate(FK_ROUTING_KEY, GSON.toJson(fkUpdate));

                        // TODO：找到要更新的file对应的角色重新加密
/*                         for (int j = 0; j < roleFileList.size(); j++) {
                            if (roleFile.getFilename().equals(roleFileList.get(j).getFilename())) {
                                Role updateRole = null;
                                try {
                                    log.info("开始查询role");
                                    updateRole = roleMapper.selectByRoleName(roleFileList.get(j).getRolename());
                                    log.info("查询Role：{}成功", GSON.toJson(updateRole));
                                } catch (Exception e) {
                                    log.error("查询Role：{}失败：{}", GSON.toJson(updateRole), e.getMessage());
                                }
                                log.info("查询fk入参：rolename：{}，filename：{}", updateRole.getRolename(), roleFile.getFilename());
                                FK fk2 = fkMapper.selectByName(updateRole.getRolename(), roleFile.getFilename());

                                fk2.setVersionRole(roleFile.getVersionRole());
                                fk2.setCipherFk(ElgamalUtils.encryptByPublicKey(
                                        Base64.encodeBase64String(SerializationUtils.serialize(cipherFK)),
                                        updateRole.getPublicKey()));
                                String fk2Info = fk2.getVersionRole() + fk2.getVersionFile() + fk2.getFilename()
                                        + fk2.getRolename() + fk2.getCipherFk();
                                fk2.setSignature(
                                        DSAUtils.getSignature(DSAUtils.signatureData(fk2Info, updateRole.getSignPrivate())));
                                try {
                                    log.info("开始更新FK信息：{}", GSON.toJson(fk2));
                                    fkMapper.updateCrypt(fk2);
                                } catch (Exception e) {
                                    log.error("更新FK元组失败：{}", e.getMessage());
                                }

                                FKUpdate fkUpdate2 = new FKUpdate();
                                Map<String, Object> fkUpdateMap2 = new HashMap<>();
                                fkUpdateMap2.put(FK_CIPHER_KEY, cipherFK);
                                fkUpdate2.setFilename(fk2.getFilename());
                                fkUpdate2.setRolename(fk2.getRolename());
                                fkUpdate2.setVersionFile(fk2.getVersionFile());
                                fkUpdate2.setVersionRole(fk2.getVersionRole());
                                fkUpdate2.setUpdateInfo(fkUpdateMap2);
                                sendUpdate(FK_ROUTING_KEY, GSON.toJson(fkUpdate2));                                
                            }
                        } */
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


    /**
     * 发送MQ委派更新
     * @param routingKey
     * @param content
     */
    private void sendUpdate(String routingKey, String content) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, content);
    }
}
