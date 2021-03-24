package com.rick.cryptcloud.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import com.rick.cryptcloud.DTO.BasicDTO;
import com.rick.cryptcloud.Enum.DTOEnum;
import com.rick.cryptcloud.common.AESUtils;
import com.rick.cryptcloud.common.AliyunUtils;
import com.rick.cryptcloud.common.DSAUtils;
import com.rick.cryptcloud.common.ElgamalUtils;
import com.rick.cryptcloud.common.RotationUtils;
import com.rick.cryptcloud.dao.DocumentMapper;
import com.rick.cryptcloud.dao.FKMapper;
import com.rick.cryptcloud.dao.FMapper;
import com.rick.cryptcloud.dao.RKMapper;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.dao.RoleMapper;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.dao.UserRoleMapper;
import com.rick.cryptcloud.service.RoleService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Gson GSON = new Gson();

    @Value("${file.uploadLocation}")
    private String uploadLocation;
    
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
    private DocumentMapper documentMapper;

    @Autowired
    private RKMapper rkMapper;

    @Autowired
    private FMapper fMapper;

    @Autowired
    private FKMapper fkMapper;

    private static Integer versionRole = 0;
    
    private static String operation = "rw";

    private static Integer versionFile = 0;

    private static Integer tag = 0;

    private final static String RSK = "RSK";

    private final static String RPK = "RPK";

    private final static String N = "N";

    private static final String suffix = ".txt";

    @Override
    public BasicDTO addRole(String rolename) {
        Map<String, Object> elgamalKey = ElgamalUtils.initKey();
        Map<String, Object> DSAKey = DSAUtils.initKey();
        Role role = new Role();
        role.setRolename(rolename);
        role.setPrivateKey(ElgamalUtils.getPrivateKey(elgamalKey));
        role.setPublicKey(ElgamalUtils.getPublicKey(elgamalKey));
        role.setSignPrivate(DSAUtils.getPrivateKey(DSAKey));
        role.setSignPublic(DSAUtils.getPublicKey(DSAKey));
        log.info("插入角色入参：{}", GSON.toJson(role));
        try {
            roleMapper.insert(role);            
        } catch (Exception e) {
            log.error("插入角色:{}失败：{}", rolename, e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        log.info("插入角色：{}成功", rolename);
        return new BasicDTO(DTOEnum.SUCCESS);
    }

    @Override
    public BasicDTO assignUser(String username, String rolename) {
        // 1.插入映射关系
        UserRole userRole = new UserRole();
        userRole.setRolename(rolename);
        userRole.setUsername(username);
        userRole.setVersion(versionRole);
        try {
            log.info("添加用户角色映射关系：{}", GSON.toJson(userRole));
            userRoleMapper.insert(userRole);
        } catch (Exception e) {
            log.error("添加用户角色映射：{}失败：{}", GSON.toJson(userRole), e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        // 2.插入RK元组
        User user = userMapper.selectByUserName(username);
        Role role = roleMapper.selectByRoleName(rolename);
        RK rk = new RK();
        rk.setVersionRole(versionRole);
        rk.setRolename(rolename);
        rk.setUsername(username);
        rk.setCryptoRolekey(ElgamalUtils.encryptByPublicKey(role.getPrivateKey(), user.getPublicKey()));
        rk.setCryptoRolesign(ElgamalUtils.encryptByPublicKey(role.getSignPrivate(), user.getPublicKey()));
        String info = rk.getVersionRole() + rk.getUsername() + rk.getRolename() + rk.getCryptoRolekey()
                + rk.getCryptoRolesign();
        rk.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(info, role.getSignPrivate())));
        try {
            log.info("准备插入RK元组入参：{}", GSON.toJson(rk));
            rkMapper.insert(rk);
        } catch (Exception e) {
            log.error("插入RK元组：{}失败：{}", GSON.toJson(rk), e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        log.info("插入RK元组成功");
        // 3.上传RK元组
        String rkTupleName = rk.getUsername() + "_" + rk.getRolename() + "_" + String.valueOf(rk.getVersionRole())
                + suffix;
        String content = Base64.encodeBase64String(SerializationUtils.serialize(rk));
        log.info("上传RK元组：{}，Base64编码后内容：{}", rkTupleName, content);
        aliyunUtils.uploadToServer(rkTupleName, content);
        log.info("上传RK元组：{}成功", rkTupleName);
        return new BasicDTO(DTOEnum.SUCCESS);
    }


    @Override
    public BasicDTO uploadFile(String rolename, String filename) {
        Role role = roleMapper.selectByRoleName(rolename);
        // 1.插入映射关系
        RoleFile roleFile = new RoleFile();
        roleFile.setFilename(filename);
        roleFile.setRolename(rolename);
        roleFile.setOperation(operation);
        roleFile.setVersionFile(versionFile);
        roleFile.setVersionRole(versionRole);
        try {
            log.info("添加角色文件映射关系：{}", GSON.toJson(roleFile));
            roleFileMapper.insert(roleFile);
        } catch (Exception e) {
            log.error("添加角色文件映射：{}失败：{}", GSON.toJson(roleFile), e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        // 2.生成file、F元组和FK元组
        CipherFK cipherFK= new CipherFK();
        Document document = generateFile(filename, cipherFK);
        try {
            log.info("插入file数据库入参：{}", GSON.toJson(document));
            documentMapper.insert(document);
        } catch (Exception e) {
            log.error("插入file数据库失败：{}", e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        F f = generateF(document, cipherFK, role.getSignPrivate());
        FK fk = generateFK(role, document);
        try {
            log.info("F元组：{}，准备写入数据库，入参：{}", filename, GSON.toJson(f));
            fMapper.insert(f);
        } catch (Exception e) {
            log.error("F元组：{}写入数据库失败：{}", filename, e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        
        log.info("F元组写入数据库成功");
        try {
            log.info("FK元组：{}，准备写入数据库，入参：{}", filename, GSON.toJson(fk));
            fkMapper.insert(fk);
        } catch (Exception e) {
            log.error("FK元组：{}写入数据库失败：{}", filename, e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        log.info("FK元组写入数据库成功");
        
        // 3.上传FK元组和F元组
        String filenameWithNoSuffix = fk.getFilename().substring(0, fk.getFilename().lastIndexOf("."));
        String fkTupleName = fk.getRolename() + "_" + filenameWithNoSuffix + "_" + String.valueOf(fk.getVersionRole())
                + "_" + String.valueOf(fk.getVersionFile()) + suffix;
        String fkContent = Base64.encodeBase64String(SerializationUtils.serialize(fk));
        log.info("上传FK元组：{}，Base64编码后内容：{}", fkTupleName, fkContent);
        aliyunUtils.uploadToServer(fkTupleName, fkContent);
        log.info("上传FK元组：{}成功", fkTupleName);
        String fTupleName = f.getFilename();
        String fContent = Base64.encodeBase64String(SerializationUtils.serialize(f));
        log.info("上传F元组：{}，Base64编码后内容：{}", fTupleName, fContent);
        aliyunUtils.uploadToServer(fTupleName, fContent);
        log.info("上传F元组：{}成功", fTupleName);
        return new BasicDTO(DTOEnum.SUCCESS);
    }
    

    private Document generateFile(String filename, CipherFK cipherFK) {
        Document document = new Document();
        document.setFilename(filename);
        String aeskey = AESUtils.generateAESKey();
        cipherFK.setK0(aeskey);
        cipherFK.setkT(aeskey);
        cipherFK.setT(1);
        Map<String, Long> rotationKey = getRotationKey();
        cipherFK.setRpk(String.valueOf(rotationKey.get(RPK)));
        cipherFK.setRsk(String.valueOf(rotationKey.get(RSK)));
        cipherFK.setN(rotationKey.get(N));
        String serialCipherKey = Base64.encodeBase64String(SerializationUtils.serialize(cipherFK));
        log.info("Base64编码序列化的密钥列表：{}", serialCipherKey);
        document.setCipherKey(serialCipherKey);
        try {
            log.info("读取文档：{}", filename);
            String content = FileUtils.readFileToString(new File(uploadLocation + filename));
            document.setContent(content);
        } catch (IOException e) {
            log.info("文档：{}读取失败：{}", filename, e.getMessage());
        }
        return document;
    }

    /**
     * 获得旋转密钥
     * 
     * @return
     */
    private Map<String, Long> getRotationKey() {
        long p = RotationUtils.genPrime();
        long q = RotationUtils.genPrime();
        long rpk = RotationUtils.getRpk(RotationUtils.genfi(p, q));
        long rsk = RotationUtils.getRsk(rpk, RotationUtils.genfi(p, q));
        Map<String, Long> rotationKey = new HashMap<>();
        rotationKey.put(RPK, rpk);
        rotationKey.put(RSK, rsk);
        rotationKey.put(N, p * q);
        return rotationKey;
    }

    private F generateF(Document document, CipherFK cipherFK, String signkey) {
        F f = new F();
        String cryptofile = AESUtils.encryptAES(document.getContent(), cipherFK.getK0());
        f.setFilename(document.getFilename());
        f.setCryptoFile(cryptofile);
        f.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(document.getFilename() + cryptofile, signkey)));
        return f;
    }

    private FK generateFK(Role role, Document document) {
        FK fk = new FK();
        fk.setRolename(role.getRolename());
        fk.setFilename(document.getFilename());
        fk.setVersionRole(versionRole);
        fk.setVersionFile(versionFile);
        fk.setOperation(operation);
        fk.setTag(tag);
        fk.setCipherFk(ElgamalUtils.encryptByPublicKey(document.getCipherKey(), role.getPublicKey()));
        String info = fk.getVersionRole() + fk.getVersionFile() + fk.getFilename() + fk.getRolename()
                + fk.getCipherFk();
        fk.setSignature(DSAUtils.getSignature(DSAUtils.signatureData(info, role.getSignPrivate())));
        return fk;
    }
}
