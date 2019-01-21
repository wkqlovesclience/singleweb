package com.gome.stage.mapper;


import com.gome.stage.entity.Keywords;
import com.gome.stage.entity.Title;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TitleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Title record);

    int insertSelective(Title record);

    Title selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Title record);

    int updateByPrimaryKeyWithBLOBs(Title record);

    int updateByPrimaryKey(Title record);

    List<String> getRelatedGoodIdByTitleId(@Param("titleId") Integer title_id);

    String getGoodIdByTitleId(@Param("titleId") Integer title_id);

    List<Keywords> findRMLinkByTitleId(@Param("titleId")Integer title_id);

    List<String> findTagByTitleId(@Param("titleId")Integer title_id);

    public List<Title> findRecentZhuanti();

    public List<Title> findZhuanTiByTagName(@Param("tagName") String tagName);

    public List<Title> findZhuanTiByTagId(@Param("tagId") Integer tagId);

    public List<Title> findZhuanTiByTitlePath(@Param("titlePath") String titlePath);



}