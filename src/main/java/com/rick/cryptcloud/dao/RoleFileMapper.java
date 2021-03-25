package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.RoleFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleFile record);

    RoleFile selectByPrimaryKey(Integer id);

    List<RoleFile> selectAll();

    int updateRoleVersion(RoleFile record);
}