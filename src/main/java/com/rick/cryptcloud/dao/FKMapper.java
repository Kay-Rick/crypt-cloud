package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.FK;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FKMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FK record);

    FK selectByPrimaryKey(Integer id);
    
    FK selectByName(@Param("rolename") String rolename, @Param("filename") String filename);

    List<FK> selectAll();

    int updateCrypt(FK record);
}