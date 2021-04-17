package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.CipherFK;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CipherFKMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(CipherFK record);

    CipherFK selectByk0(String k0);

    List<CipherFK> selectAll();

    int updateByk0(CipherFK record);
}