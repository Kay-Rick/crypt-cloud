package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    Role selectByRoleName(String rolename);

    List<Role> selectAll();

    int updateByRoleName(Role record);
}