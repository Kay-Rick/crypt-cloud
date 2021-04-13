package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserRole record);

    List<UserRole> selectByUsername(String username);

    List<UserRole> selectAll();

    int updateVersion(UserRole record);
}