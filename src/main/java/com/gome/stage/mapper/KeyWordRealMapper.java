package com.gome.stage.mapper;


import com.gome.stage.entity.KeyWordReal;

public interface KeyWordRealMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyWordReal record);

    int insertSelective(KeyWordReal record);

    KeyWordReal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(KeyWordReal record);

    int updateByPrimaryKey(KeyWordReal record);
}