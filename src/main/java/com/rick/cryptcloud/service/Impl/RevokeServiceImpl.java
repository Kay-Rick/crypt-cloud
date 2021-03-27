package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.FK;
import com.rick.cryptcloud.DO.RK;
import com.rick.cryptcloud.DO.Role;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.common.Enum.DTOEnum;
import com.rick.cryptcloud.MQ.FKUpdate;
import com.rick.cryptcloud.MQ.FKUpdateInfo;
import com.rick.cryptcloud.MQ.RKUpdate;
import com.rick.cryptcloud.MQ.RKUpdateInfo;
import com.rick.cryptcloud.common.utils.AliyunUtils;
import com.rick.cryptcloud.common.utils.DSAUtils;
import com.rick.cryptcloud.common.utils.ElgamalUtils;
import com.rick.cryptcloud.common.utils.RotationUtils;
import com.rick.cryptcloud.dao.*;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.service.RevokeService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RevokeServiceImpl implements RevokeService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

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
    private CipherFKMapper cipherFKMapper;

    @Autowired
    private FKMapper fkMapper;

    private static final String EXCHANGE_NAME = "Crypt-Cloud";

    private static final String RK_ROUTING_KEY = "RK";

    private static final String FK_ROUTING_KEY = "FK";

    private static final int MAX_ROUND = 20;

    private static final String SUFFIX = ".txt";

    @Override
    public BasicDTO revokeUserRole(String username, String rolename) {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功");
            } else {
                log.error("消息发送失败：{}", cause);
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
            // 更新FK元组
            log.info("开始查询roleFile映射");
            List<RoleFile> roleFileList = roleFileMapper.selectAll();
            log.info("查询roleFile出参：{}", roleFileList);
            for (UserRole userRole : userRoleList) {
                if (!username.equals(userRole.getUsername()) || !rolename.equals(userRole.getRolename())) {
                    RK rk = new RK();
                    RKUpdate rkUpdate = new RKUpdate();
                    userRole.setVersion(userRole.getVersion() + 1);
                    log.info("查询其余用户：{}信息", userRole.getUsername());
                    User user = userMapper.selectByUserName(userRole.getUsername());
                    log.info("查询其余用户信息出参：{}", GSON.toJson(user));
                    // 更新RK
                    rk.setRolename(userRole.getRolename());
                    rk.setUsername(userRole.getUsername());
                    // 需更新的RK内容
                    rk.setVersionRole(userRole.getVersion());
                    rk.setCryptoRolekey(ElgamalUtils.encryptByPublicKey(ElgamalUtils.getPrivateKey(elgamalKey), user.getPublicKey()));
                    rk.setCryptoRolesign(ElgamalUtils.encryptByPublicKey(DSAUtils.getPrivateKey(DSAKey), user.getPublicKey()));
                    String rkInfo = rk.getVersionRole() + rk.getUsername() + rk.getRolename() + rk.getCryptoRolekey() + rk.getCryptoRolesign();
                    rk.setSignature(DSAUtils.getSignature(Objects.requireNonNull(DSAUtils.signatureData(rkInfo, DSAUtils.getPrivateKey(DSAKey)))));
                    try {
                        log.info("开始更新RK信息：{}", GSON.toJson(rk));
                        rkMapper.updateCrypt(rk);
                    } catch (Exception e) {
                        log.error("更新RK：{}失败", GSON.toJson(rk));
                        return new BasicDTO(DTOEnum.FAILED);
                    }
                    try {
                        log.info("开始更新userRole信息：{}", GSON.toJson(userRole));
                        userRoleMapper.updateVersion(userRole);
                    } catch (Exception e) {
                        log.error("更新userRole：{}失败", GSON.toJson(userRole));
                        return new BasicDTO(DTOEnum.FAILED);
                    }

                    // RK需要更新的信息
                    RKUpdateInfo rkUpdateInfo = new RKUpdateInfo();
                    rkUpdateInfo.setCryptoRolekey(rk.getCryptoRolekey());
                    rkUpdateInfo.setCryptoRolesign(rk.getCryptoRolesign());
                    rkUpdateInfo.setSignature(rk.getSignature());
                    rkUpdateInfo.setVersionRole(rk.getVersionRole());

                    rkUpdate.setRolename(rk.getRolename());
                    rkUpdate.setUsername(rk.getUsername());
                    rkUpdate.setUpdateInfo(rkUpdateInfo);
                    sendUpdate(RK_ROUTING_KEY, GSON.toJson(rkUpdate));
                }
            }
            
            for (RoleFile roleFile : roleFileList) {
                if (rolename.equals(roleFile.getRolename())) {
                    String filenameWithNoSuffix = roleFile.getFilename().substring(0,
                            roleFile.getFilename().lastIndexOf("."));
                    String fkTupleName = roleFile.getRolename() + "_" + filenameWithNoSuffix + "_"
                            + roleFile.getVersionRole() + "_"
                            + roleFile.getVersionFile() + SUFFIX;
                    log.info("下载FK元组");
                    aliyunUtils.downloadToUpdate(fkTupleName);
                    log.info("下载FK元组成功");
                    FK fk = null;
                    String fkContent;
                    try {
                        log.info("FK元组开始反序列化");
                        fkContent = FileUtils.readFileToString(new File(updatePrefix + fkTupleName));
                        fk = SerializationUtils.deserialize(Base64.decodeBase64(fkContent));
                        log.info("FK元组：{}序列化成功", GSON.toJson(fk));
                    } catch (IOException e) {
                        log.error("FK元组反序列化失败:{}", e.getMessage());
                        return new BasicDTO(DTOEnum.FAILED);
                    }
                    // 更新roleFile映射中role的version
                    roleFile.setVersionRole(roleFile.getVersionRole() + 1);
                    try {
                        log.info("开始更新roleFile信息：{}", GSON.toJson(roleFile));
                        roleFileMapper.updateRoleVersion(roleFile);
                    } catch (Exception e) {
                        log.error("更新roleFile信息失败：{}", e.getMessage());
                        return new BasicDTO(DTOEnum.FAILED);
                    }

                    Role role = null;
                    try {
                        log.info("开始查询role");
                        role = roleMapper.selectByRoleName(roleFile.getRolename());
                        log.info("查询Role：{}成功", GSON.toJson(role));
                    } catch (Exception e) {
                        log.error("查询Role：{}失败：{}", GSON.toJson(role), e.getMessage());
                        return new BasicDTO(DTOEnum.FAILED);
                    }
                    String cipherList = ElgamalUtils.decryptByPrivateKey(Objects.requireNonNull(fk).getCipherFk(), Objects.requireNonNull(role).getPrivateKey());
                    CipherFK cipherFK = SerializationUtils.deserialize(Base64.decodeBase64(cipherList));
                    log.info("反序列化得密钥列表：{}", GSON.toJson(cipherFK));

                    log.info("开始更新密钥列表");
                    String rsk = cipherFK.getRsk();
                    String kt = cipherFK.getkT();
                    Integer N = cipherFK.getN();
                    Long next = RotationUtils.BDri(Long.parseLong(rsk), Long.parseLong(kt), N);
                    cipherFK.setkT(String.valueOf(next));
                    // 安全模式：添加加密层，使密钥列表多一个密钥
                    if (cipherFK.getT() < MAX_ROUND) {
                        cipherFK.setT(cipherFK.getT() + 1);
                    }
                    log.info("密钥列表更新完成：{}", GSON.toJson(cipherFK));
                    try {
                        log.info("开始更新数据库CipherFK密钥列表");
                        cipherFKMapper.updateByk0(cipherFK);
                    } catch (Exception e) {
                        log.error("更新CipherFK数据库失败：{}", e.getMessage());
                    }
                    fk.setVersionRole(roleFile.getVersionRole());
                    // 用新生成的role密钥重新加密密钥列表
                    fk.setCipherFk(ElgamalUtils.encryptByPublicKey(
                            Base64.encodeBase64String(SerializationUtils.serialize(cipherFK)),
                            ElgamalUtils.getPublicKey(elgamalKey)));
                    String fkInfo = fk.getVersionRole() + fk.getVersionFile() + fk.getFilename() + fk.getRolename()
                            + fk.getCipherFk();
                    fk.setSignature(DSAUtils.getSignature(Objects.requireNonNull(DSAUtils.signatureData(fkInfo, role.getSignPrivate()))));

                    try {
                        log.info("开始更新FK信息：{}", GSON.toJson(fk));
                        fkMapper.updateCrypt(fk);
                    } catch (Exception e) {
                        log.error("更新FK元组失败：{}", e.getMessage());
                        return new BasicDTO(DTOEnum.FAILED);
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
                        return new BasicDTO(DTOEnum.FAILED);
                    }
                    FKUpdate fkUpdate = new FKUpdate();
                    // FK需要更新的信息
                    FKUpdateInfo fkUpdateInfo = new FKUpdateInfo();
                    fkUpdateInfo.setCipherFk(cipherFK);
                    fkUpdateInfo.setCipherFKText(fk.getCipherFk());
                    fkUpdateInfo.setVersionRole(fk.getVersionRole());
                    fkUpdateInfo.setSignature(fk.getSignature());

                    fkUpdate.setFilename(roleFile.getFilename());
                    fkUpdate.setRolename(roleFile.getRolename());
                    fkUpdate.setVersionFile(roleFile.getVersionFile());
                    fkUpdate.setOperation(fk.getOperation());
                    fkUpdate.setTag(fk.getTag());
                    fkUpdate.setUpdateInfo(fkUpdateInfo);
                    sendUpdate(FK_ROUTING_KEY, GSON.toJson(fkUpdate));
                    // TODO：找到要更新的file对应的角色重新加密
                }
            }
        }

        return new BasicDTO(DTOEnum.SUCCESS);
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
