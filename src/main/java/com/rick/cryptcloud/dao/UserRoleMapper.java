package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserRole record);

    List<UserRole> selectByUsername(String username);

    List<UserRole> selectByRolename(String rolename);

    List<UserRole> selectByName(@Param("username")String username, @Param("rolename") String rolename);

    List<UserRole> selectAll();

    int updateVersion(UserRole record);
}