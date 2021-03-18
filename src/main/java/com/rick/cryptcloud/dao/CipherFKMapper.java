package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.CipherFK;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CipherFKMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CipherFK record);

    CipherFK selectByPrimaryKey(Integer id);

    List<CipherFK> selectAll();

    int updateByPrimaryKey(CipherFK record);
}