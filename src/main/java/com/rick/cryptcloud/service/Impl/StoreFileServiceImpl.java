package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.Document;
import com.rick.cryptcloud.DO.F;
import com.rick.cryptcloud.DO.FK;
import com.rick.cryptcloud.DO.RK;
import com.rick.cryptcloud.DO.Role;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.DTO.UploadDTO;
import com.rick.cryptcloud.common.AESUtils;
import com.rick.cryptcloud.common.AliyunUtils;
import com.rick.cryptcloud.common.DSAUtils;
import com.rick.cryptcloud.common.ElgamalUtils;
import com.rick.cryptcloud.common.MailUtils;
import com.rick.cryptcloud.common.RotationUtils;
import com.rick.cryptcloud.dao.DocumentMapper;
import com.rick.cryptcloud.dao.FKMapper;
import com.rick.cryptcloud.dao.FMapper;
import com.rick.cryptcloud.dao.RKMapper;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.dao.RoleMapper;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.dao.UserRoleMapper;
import com.rick.cryptcloud.service.StoreFileService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StoreFileServiceImpl implements StoreFileService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new Gson();

    @Value("${file.uploadLocation}")
    private String uploadLocation;

    @Autowired
    private AliyunUtils aliyunUtils;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private FMapper fMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleFileMapper roleFileMapper;

    @Autowired
    private RKMapper rkMapper;

    @Autowired
    private FKMapper fkMapper;

    private static Map<String, Object> DSAKeys;

    private static long p, q;

    private static long rpk, rsk;

    private static Map<String, Long> rotationKey = new HashMap<>();

    private static CipherFK cipherFK = new CipherFK();

    private static String content = "";

    private static String operation = "rw";

    private static Integer versionFile = 0;

    private static Integer versionRole = 0;

    private static Integer tag = 0;

    private final static String RSK = "RSK";

    private final static String RPK = "RPK";

    private final static String N = "N";

    private final static String suffix = ".txt";

    private final static String mailSubject = "Crypt Cloud";

    @Override
    public UploadDTO storeFile(String username, String filename) {
        
        UploadDTO uploadDTO = new UploadDTO();

        generateDSA();
        
        User user = initUser(username);
        
        List<Role> roleList = initRole();
        
        Document document = initFile(filename);

        F f = initF(filename);

        initMapping(username, filename, roleList);
        
        List<RK> RKList = initRK(user, roleList);

        List<FK> FKList = initFK(document, roleList);

        uploadTuple(RKList, FKList, f);

        sendMail(user);

        return uploadDTO;
    }

    private User initUser(String username) {
        User user = null;
        try {
            user = generateUser(username);
            log.info("为用户{}生成密钥，并准备写入数据库入参：{}", username, GSON.toJson(user));
            userMapper.updateByUserName(user);
        } catch (Exception e) {
            log.error("用户{}密钥写入数据库失败：{}", username, e.getMessage());
        }
        log.info("用户：{}密钥写入数据库成功", username);
        return userMapper.selectByUserName(username);
    }

    private List<Role> initRole() {
        List<Role> roleList = null;
        try {
            log.info("开始查询所有角色");
            roleList = roleMapper.selectAll();
            log.info("查询全部角色成功：{}", GSON.toJson(roleList));
            for (Role role : roleList) {
                try {
                    Role completeRole = generateRole(role);
                    log.info("为角色{}生成密钥，并准备写入数据库，入参：{}", role.getRolename(), GSON.toJson(completeRole));
                    roleMapper.updateByRoleName(completeRole);
                } catch (Exception e) {
                    log.error("角色{}密钥写入数据库失败：{}", role.getRolename(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("查询所有角色失败：{}", e.getMessage());
        }
        log.info("更新roleList中数据");
        roleList = roleMapper.selectAll();
        log.info("更新roleList数据完成：{}",GSON.toJson(roleList));
        return roleList;
    }


    public Document initFile(String filename) {
        Document document = null;
        try {
            document = generateFile(filename);
            log.info("初始化文件{}生成密钥，并准备写入数据库，入参：{}", filename, GSON.toJson(document));
            documentMapper.insert(document);
        } catch (Exception e) {
            log.error("文件：{}写入数据库失败：{}", filename, e.getMessage());
        }
        return document;
    }


    public F initF(String filename) {  
        F f = null;        
        try {
            f = generateF(filename);
            log.info("初始化F元组：{}，并准备写入数据库，入参：{}", filename, GSON.toJson(f));
            fMapper.insert(f);
        } catch (Exception e) {
            log.error("F元组：{}写入数据库失败：{}", filename, e.getMessage());
        }
        return f;
    }

    public void initMapping(String username, String filename, List<Role> roleList) {
        if (null != roleList) {
            for (Role role : roleList) {
                UserRole userRole = new UserRole();
                userRole.setRolename(role.getRolename());
                userRole.setUsername(username);
                userRole.setVersion(versionRole);
                try {
                    log.info("添加用户角色映射关系：{}", GSON.toJson(userRole));
                    userRoleMapper.insert(userRole);
                } catch (Exception e) {
                    log.error("添加用户角色映射：{}失败：{}", GSON.toJson(userRole), e.getMessage());
                }
                RoleFile roleFile = new RoleFile();
                roleFile.setFilename(filename);
                roleFile.setRolename(role.getRolename());
                roleFile.setOperation(operation);
                roleFile.setVersionFile(versionFile);
                roleFile.setVersionRole(versionRole);
                try {
                    log.info("添加角色文件映射关系：{}", GSON.toJson(roleFile));
                    roleFileMapper.insert(roleFile);
                } catch (Exception e) {
                    log.error("添加角色文件映射：{}失败：{}", GSON.toJson(roleFile), e.getMessage());
                }

            }
        }
    }

    private List<RK> initRK(User user, List<Role> roleList) {
       List<RK> RKList = new ArrayList<>();
        if (null != roleList) {
            for (Role role : roleList) {
                RK rk = new RK();
                rk.setVersionRole(versionRole);
                rk.setRolename(role.getRolename());
                rk.setUsername(user.getUsername());
                rk.setCryptoRolekey(ElgamalUtils.encryptByPublicKey(role.getPrivateKey(), user.getPublicKey()));
                rk.setCryptoRolesign(ElgamalUtils.encryptByPublicKey(role.getSignPrivate(), user.getPublicKey()));
                String info = rk.getVersionRole() + rk.getUsername() + rk.getRolename() + rk.getCryptoRolekey() + rk.getCryptoRolesign();
                rk.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(info, DSAUtils.getPrivateKey(DSAKeys))));
                RKList.add(rk);
                try {
                    log.info("准备插入RK元组：{}", GSON.toJson(rk));
                    rkMapper.insert(rk);
                } catch (Exception e) {
                    log.error("插入RK元组：{}失败：{}", GSON.toJson(rk), e.getMessage());
                    continue;
                }
                log.info("插入RK元组：{}成功", GSON.toJson(rk));
            }
        }
        return RKList;
        
    }

    private List<FK> initFK(Document document, List<Role> roleList) {
        List<FK> FKList = new ArrayList<>();
        if (null != roleList) {
            for (Role role : roleList) {
                FK fk = new FK();
                fk.setRolename(role.getRolename());
                fk.setFilename(document.getFilename());
                fk.setVersionRole(versionRole);
                fk.setVersionFile(versionFile);
                fk.setOperation(operation);
                fk.setTag(tag);
                fk.setCipherFk(ElgamalUtils.encryptByPublicKey(document.getCipherKey(), role.getPublicKey()));
                String info = fk.getVersionRole() + fk.getVersionFile() + fk.getFilename() + fk.getRolename() + fk.getCipherFk();
                fk.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(info, DSAUtils.getPrivateKey(DSAKeys))));
                FKList.add(fk);
                try {
                    log.info("准备插入FK元组：{}", GSON.toJson(fk));
                    fkMapper.insert(fk);
                } catch (Exception e) {
                    log.error("插入FK元组：{}失败：{}", GSON.toJson(fk), e.getMessage());
                    continue;
                }
                log.info("插入FK元组：{}成功", GSON.toJson(fk));
            }
        }
        return FKList;
    }


    private void uploadTuple(List<RK> RKList, List<FK> FKList, F f) {
        if (null != RKList) {
            for (RK rk : RKList) {
                String rkTupleName = rk.getUsername() + "_" + rk.getRolename() + "_" + String.valueOf(rk.getVersionRole()) + suffix;
                String content = Base64.encodeBase64String(SerializationUtils.serialize(rk));
                log.info("上传RK元组：{}，Base64编码后内容：{}",rkTupleName, content);
                aliyunUtils.uploadToServer(rkTupleName, content);
                log.info("上传RK元组：{}成功", rkTupleName);
            }
        }

        if (null != FKList) {
            for (FK fk : FKList) {
                String filenameWithNoSuffix = fk.getFilename().substring(0, fk.getFilename().lastIndexOf("."));
                String fkTupleName = fk.getRolename() + "_" + filenameWithNoSuffix + "_" + String.valueOf(fk.getVersionRole()) + "_" + String.valueOf(fk.getVersionFile()) + suffix;
                String content = Base64.encodeBase64String(SerializationUtils.serialize(fk));
                log.info("上传FK元组：{}，Base64编码后内容：{}", fkTupleName, content);
                aliyunUtils.uploadToServer(fkTupleName, content);
                log.info("上传FK元组：{}成功", fkTupleName);
            }
        }

        if (null != f) {
            String fTupleName = f.getFilename();
            String content = Base64.encodeBase64String(SerializationUtils.serialize(f));
            log.info("上传F元组：{}，Base64编码后内容：{}", fTupleName, content);
            aliyunUtils.uploadToServer(fTupleName, content);
            log.info("上传F元组：{}成功", fTupleName);
        }
    }


    private void sendMail(User user) {
        String mailContent = "Your Security Key is：" + user.getPrivateKey();
        mailUtils.sendMail(user.getMail(), mailSubject, mailContent);
    }

    private void generateDSA() {
        DSAKeys = DSAUtils.initKey();
    }

    /**
     * 初始化生成用户密钥
     * 
     * @param username
     * @return
     */
    private User generateUser(String username) {
        User user = new User();
        user.setUsername(username);
        Map<String, Object> elgamalKeys = ElgamalUtils.initKey();
        user.setPublicKey(ElgamalUtils.getPublicKey(elgamalKeys));
        user.setPrivateKey(ElgamalUtils.getPrivateKey(elgamalKeys));
        user.setSignPrivate(DSAUtils.getPrivateKey(DSAKeys));
        user.setSignPublic(DSAUtils.getPublicKey(DSAKeys));
        return user;
    }

    /**
     * 
     * @param role
     * @return
     */
    private Role generateRole(Role role) {
        Map<String, Object> elgamalKeys = ElgamalUtils.initKey();
        role.setPublicKey(ElgamalUtils.getPublicKey(elgamalKeys));
        role.setPrivateKey(ElgamalUtils.getPrivateKey(elgamalKeys));
        role.setSignPrivate(DSAUtils.getPrivateKey(DSAKeys));
        role.setSignPublic(DSAUtils.getPublicKey(DSAKeys));
        return role;
    }


    private Document generateFile(String filename) {
        Document document = new Document();
        document.setFilename(filename);
        String aeskey = AESUtils.generateAESKey();
        cipherFK.setK0(aeskey);
        cipherFK.setkT(aeskey);
        cipherFK.setT(1);
        rotationKey = getRotationKey();
        cipherFK.setRpk(String.valueOf(rotationKey.get(RPK)));
        cipherFK.setRsk(String.valueOf(rotationKey.get(RSK)));
        cipherFK.setN(rotationKey.get(N));
        String serialCipherKey = Base64.encodeBase64String(SerializationUtils.serialize(cipherFK));
        log.info("Base64编码序列化的密钥列表：{}", serialCipherKey);
        document.setCipherKey(serialCipherKey);
        try {
            log.info("读取文档：{}", filename);
            content = FileUtils.readFileToString(new File(uploadLocation + filename));
            document.setContent(content);
        } catch (IOException e) {
            log.info("文档：{}读取失败：{}", filename, e.getMessage());
        }
        return document;
    }

    /**
     * 获得旋转密钥
     * @return
     */
    private Map<String, Long> getRotationKey() {
        p = RotationUtils.genPrime();
        q = RotationUtils.genPrime();
        rpk = RotationUtils.getRpk(RotationUtils.genfi(p, q));
        rsk = RotationUtils.getRsk(rpk, RotationUtils.genfi(p, q));
        rotationKey.put(RPK, rpk);
        rotationKey.put(RSK, rsk);
        rotationKey.put(N, p * q);
        return rotationKey;
    }

    private F generateF(String filename) {
        F f = new F();
        String cryptofile = AESUtils.encryptAES(content, cipherFK.getK0());
        f.setFilename(filename);
        f.setCryptoFile(cryptofile);
        f.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(filename + cryptofile, DSAUtils.getPrivateKey(DSAKeys))));
        return f;
    }


}
