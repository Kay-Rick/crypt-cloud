package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.FK;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FKMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FK record);

    FK selectByPrimaryKey(Integer id);

    List<FK> selectAll();

    int updateByPrimaryKey(FK record);
}