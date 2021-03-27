package com.rick.cryptcloud.service.Impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.common.dto.BasicDTO;
import com.rick.cryptcloud.common.Enum.DTOEnum;
import com.rick.cryptcloud.common.utils.DSAUtils;
import com.rick.cryptcloud.common.utils.ElgamalUtils;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private UserMapper userMapper;

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
}
