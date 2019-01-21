package com.gome.stage.service;

import com.gome.stage.entity.News;

import java.util.List;
import java.util.Map;

public interface NewService {
    public int getNewsCountByTid(Integer title_id);

    public List<News> findNewsList(Map<String, Object> search);

    public List<News> findRecentNewsList();
}
