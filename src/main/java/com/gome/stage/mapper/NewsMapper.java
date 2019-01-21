package com.gome.stage.mapper;


import com.gome.stage.entity.News;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NewsMapper {

    int getNewsCountByTid(@Param("title_id") Integer title_id);

    List<News> findNewsList(Map<String, Object> search);

    List<News> findRecentNewsList();

}