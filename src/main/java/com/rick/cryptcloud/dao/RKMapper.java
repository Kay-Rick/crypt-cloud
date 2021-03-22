package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.RK;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RKMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RK record);

    RK selectByPrimaryKey(Integer id);

    List<RK> selectAll();

    int updateByRoleName(RK record);
}