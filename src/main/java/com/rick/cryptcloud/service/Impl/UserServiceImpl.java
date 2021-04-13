package com.rick.cryptcloud.service.Impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.RoleFile;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.DO.UserRole;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.common.Enum.DTOEnum;
import com.rick.cryptcloud.common.dto.UserDTO;
import com.rick.cryptcloud.common.utils.DSAUtils;
import com.rick.cryptcloud.common.utils.ElgamalUtils;
import com.rick.cryptcloud.dao.RoleFileMapper;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.dao.UserRoleMapper;
import com.rick.cryptcloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleFileMapper roleFileMapper;

    @Override
    public BasicDTO addUser(String username, String email, String password) {
        Map<String, Object> elgamalKey = ElgamalUtils.initKey();
        Map<String, Object> DSAKey = DSAUtils.initKey();
        User user = new User();
        user.setUsername(username);
        user.setMail(email);
        user.setPassword(password);
        user.setPrivateKey(ElgamalUtils.getPrivateKey(Objects.requireNonNull(elgamalKey)));
        user.setPublicKey(ElgamalUtils.getPublicKey(elgamalKey));
        user.setSignPrivate(DSAUtils.getPrivateKey(Objects.requireNonNull(DSAKey)));
        user.setSignPublic(DSAUtils.getPublicKey(DSAKey));
        try {
            log.info("插入数据入参：{}", GSON.toJson(user));
            userMapper.insert(user);
        } catch (Exception e) {
            log.info("user：{}插入数据库失败：{}", username, e.getMessage());
            return new BasicDTO(DTOEnum.FAILED);
        }
        log.info("插入数据库成功");
        return new BasicDTO(DTOEnum.SUCCESS);
    }

    @Override
    public List<User> getUsers() {
        return userMapper.selectAll();
    }

    @Override
    public List<UserDTO> getUserRoles(User user) {
        List<UserDTO> result = new ArrayList<>();
        log.info("查询用户映射入参：{}", user.getUsername());
        List<UserRole> userRoleList = null;
        try {
            userRoleList = userRoleMapper.selectByUsername(user.getUsername());
            log.info("查询用户映射出参：{}", GSON.toJson(userRoleList));
        } catch (Exception e) {
            log.error("查询用户：{}映射失败：{}", user.getUsername(), e.getMessage());
        }
        if (null != userRoleList && !userRoleList.isEmpty()) {
            for (UserRole userRole : userRoleList) {
                log.info("查询角色映射入参：{}", userRole.getRolename());
                List<RoleFile> roleFileList = null;
                try {
                    roleFileList = roleFileMapper.selectByRolename(userRole.getRolename());
                    log.info("查询角色映射出参：{}", GSON.toJson(roleFileList));
                } catch (Exception e) {
                    log.error("查询角色：{}映射失败：{}", userRole.getRolename(), e.getMessage());
                }

                if (null != roleFileList) {
                    for (RoleFile roleFile : roleFileList) {
                        UserDTO userDTO = new UserDTO(DTOEnum.SUCCESS);
                        userDTO.setUsername(user.getUsername());
                        userDTO.setRolename(userRole.getRolename());
                        userDTO.setFilename(roleFile.getFilename());
                        userDTO.setOperation(roleFile.getOperation());
                        result.add(userDTO);
                    }
                }
                else {
                    UserDTO userDTO = new UserDTO(DTOEnum.SUCCESS);
                    userDTO.setUsername(user.getUsername());
                    userDTO.setRolename(userRole.getRolename());
                    result.add(userDTO);
                }
            }
        }

        else {
            UserDTO userDTO = new UserDTO(DTOEnum.FAILED);
            userDTO.setUsername(user.getUsername());
            result.add(userDTO);
        }
        return result;
    }
}
