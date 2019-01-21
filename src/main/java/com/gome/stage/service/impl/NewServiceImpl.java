package com.gome.stage.service.impl;

import com.gome.stage.entity.News;
import com.gome.stage.mapper.NewsMapper;
import com.gome.stage.service.NewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NewServiceImpl implements NewService {
    @Autowired
    private NewsMapper newsMapper;
    @Override
    public int getNewsCountByTid(Integer title_id) {
        return newsMapper.getNewsCountByTid(title_id);
    }

    @Override
    public List<News> findNewsList(Map<String, Object> search){
        return newsMapper.findNewsList(search);
    }

    @Override
    public List<News> findRecentNewsList(){
        return newsMapper.findRecentNewsList();
    }
}
