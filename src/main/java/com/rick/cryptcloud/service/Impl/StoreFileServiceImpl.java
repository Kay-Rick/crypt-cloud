package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.rick.cryptcloud.DO.CipherFK;
import com.rick.cryptcloud.DO.Document;
import com.rick.cryptcloud.DO.F;
import com.rick.cryptcloud.DO.Role;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.common.AESUtils;
import com.rick.cryptcloud.common.DSAUtils;
import com.rick.cryptcloud.common.ElgamalUtils;
import com.rick.cryptcloud.common.RotationUtils;
import com.rick.cryptcloud.dao.DocumentMapper;
import com.rick.cryptcloud.dao.FMapper;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.dao.RoleMapper;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.dao.UserRoleMapper;
import com.rick.cryptcloud.service.StoreFileService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StoreFileServiceImpl implements StoreFileService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new Gson();

    @Value("${file.downloadLocation}")
    private String downloadLocation;

    @Value("${file.uploadLocation}")
    private String uploadLocation;

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

    private static Map<String, Object> DSAKeys;

    private static long p, q;

    private static long rpk, rsk;

    private static Map<String, Long> rotationKey = new HashMap<>();

    private static CipherFK cipherFK = new CipherFK();

    private static String content = "";

    private static String operation = "rw";

    private static Integer versionFile = 1;

    private static Integer versionRole = 1;

    // TODO
    private static Integer version = 1;

    private final static String RSK = "RSK";

    private final static String RPK = "RPK";


    @Override
    public void storeFile(String username, String filename) {
        initDSA();
        
        // 初始化user
        try {
            log.info("为用户{}生成密钥，并准备写入数据库", username);
            userMapper.updateByUserName(initUser(username));
        } catch (Exception e) {
            log.error("用户{}密钥写入数据库失败：{}", username, e.getMessage());
        }
        log.info("用户：{}密钥写入数据库成功", username);
        
        // 初始化role
        List<Role> roleList = null;
        try {
            log.info("开始查询所有角色");
            roleList = roleMapper.selectAll();
            log.info("查询全部角色成功：{}", GSON.toJson(roleList));
            for (Role role : roleList) {
                try {
                    Role completeRole = initRole(role);
                    log.info("为角色{}生成密钥，并准备写入数据库，入参：{}", role.getRolename(), GSON.toJson(completeRole));
                    roleMapper.updateByRoleName(completeRole);
                } catch (Exception e) {
                    log.error("角色{}密钥写入数据库失败：{}", role.getRolename(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("查询所有角色失败：{}", e.getMessage());
        }

        // 初始化file
        try {
            Document document = initFile(filename);
            log.info("初始化文件{}生成密钥，并准备写入数据库，入参：{}", filename, GSON.toJson(document));
            documentMapper.insert(document);
        } catch (Exception e) {
            log.error("文件：{}写入数据库失败：{}", filename, e.getMessage());
        }
        
        // 初始化F
        try {
            F f = initF(filename);
            log.info("初始化F元组：{}，并准备写入数据库，入参：{}", filename, GSON.toJson(f));
            fMapper.insert(initF(filename));
        } catch (Exception e) {
            log.error("F元组：{}写入数据库失败：{}", filename, e.getMessage());
        }
        
        // 初始化映射关系
        if (null != roleList) {
            for (Role role : roleList) {
                UserRole userRole = new UserRole();
                userRole.setRolename(role.getRolename());
                userRole.setUsername(username);
                userRole.setVersion(version);
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


    private void initDSA() {
        DSAKeys = DSAUtils.initKey();
    }

    /**
     * 初始化生成用户密钥
     * 
     * @param username
     * @return
     */
    private User initUser(String username) {
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
    private Role initRole(Role role) {
        Map<String, Object> elgamalKeys = ElgamalUtils.initKey();
        role.setPublicKey(ElgamalUtils.getPublicKey(elgamalKeys));
        role.setPrivateKey(ElgamalUtils.getPrivateKey(elgamalKeys));
        role.setSignPrivate(DSAUtils.getPrivateKey(DSAKeys));
        role.setSignPublic(DSAUtils.getPublicKey(DSAKeys));
        return role;
    }


    private Document initFile(String filename) {
        Document document = new Document();
        document.setFilename(filename);
        String aeskey = AESUtils.generateAESKey();
        cipherFK.setK0(aeskey);
        cipherFK.setkT(aeskey);
        cipherFK.setT(1);
        rotationKey = getRotationKey();
        cipherFK.setRpk(String.valueOf(rotationKey.get(RPK)));
        String serialCipherKey = Base64.encodeBase64String(SerializationUtils.serialize(cipherFK));
        log.info("Base64编码后的密钥列表：{}", serialCipherKey);
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
        return rotationKey;
    }

    private F initF(String filename) {
        F f = new F();
        String cryptofile = AESUtils.encryptAES(content, cipherFK.getK0());
        f.setFilename(filename);
        f.setCryptoFile(cryptofile);
        f.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(filename + cryptofile, DSAUtils.getPrivateKey(DSAKeys))));
        return f;
    }

}
