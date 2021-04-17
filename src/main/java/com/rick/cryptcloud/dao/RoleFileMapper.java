package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.RoleFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleFileMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(RoleFile record);

    List<RoleFile> selectByRolename(String rolename);

    List<RoleFile> selectByFilename(String filename);

    List<RoleFile> selectAll();

    int updateRoleVersion(RoleFile record);
}