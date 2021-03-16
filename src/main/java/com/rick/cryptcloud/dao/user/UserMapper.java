package com.rick.cryptcloud.dao.user;

import com.rick.cryptcloud.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    List<User> queryAll();
}
