package com.gome.stage.mapper;



import com.gome.stage.entity.Title;
import com.gome.stage.entity.TitleIndex;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TitleIndexMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TitleIndex record);

    int insertSelective(TitleIndex record);

    TitleIndex selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TitleIndex record);

    int updateByPrimaryKey(TitleIndex record);

//    List<TitleIndex> getTitleIndex(@Param("cindex")String cindex);
    List<Title> getTitleIndex(Map<String,Object> map);

    List<TitleIndex> getTitleIndexByUpdate(Map<String, Object> search);

    List<Map<String, Object>> getTitleCindexCount(@Param("site") String site);

    List<TitleIndex> getTitleIndexWithPage(Map<String, Object> search);

    List<TitleIndex> getTitleIndexWithLetter(Map<String, Object> search);


}