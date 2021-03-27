package com.rick.cryptcloud.dao;

import com.rick.cryptcloud.DO.Document;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Document record);

    Document selectByPrimaryKey(Integer id);

    List<Document> selectAll();

    int updateByPrimaryKey(Document record);
}