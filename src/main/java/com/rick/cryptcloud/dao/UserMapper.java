package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    User selectByUserName(String username);

    List<User> selectAll();

    int updateByUserName(User record);
}