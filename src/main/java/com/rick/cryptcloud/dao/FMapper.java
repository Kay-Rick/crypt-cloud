package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.F;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(F record);

    F selectByPrimaryKey(Integer id);

    List<F> selectAll();

    int updateByPrimaryKey(F record);
}