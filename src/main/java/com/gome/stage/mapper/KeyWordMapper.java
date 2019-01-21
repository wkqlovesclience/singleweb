package com.gome.stage.mapper;


import com.gome.stage.entity.KeyWord;
import com.gome.stage.entity.Keywords;
import com.gome.stage.entity.Title;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KeyWordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Keywords record);

    int insertSelective(Keywords record);

    KeyWord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Keywords record);

    int updateByPrimaryKey(Keywords record);

    List<String> getKeywordByTitleId(@Param("value") int TitleId);

    List<KeyWord> findTagsByTitleId(@Param("title_id") Integer title_id, @Param("type") Integer type);

    List<Title> findZhuanTiByTagName(@Param("tagName") String tagName);

    List<Keywords> getRMLinkByTitleId(@Param("titleId") Integer titleId);

    Keywords getTagnameById(@Param("tagid") Integer tagid);
}