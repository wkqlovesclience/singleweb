package com.gome.stage.service.impl;

import com.gome.stage.entity.HotSearchword;
import com.gome.stage.mapper.IHotSearchwordDMapper;
import com.gome.stage.service.IHotSearchwordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotSearchWordServiceImpl implements IHotSearchwordService {
    @Autowired
    private IHotSearchwordDMapper hotSearchwordMapper;

    @Override
    public List<HotSearchword> listByTitleTag(String tagName) {
        return hotSearchwordMapper.listByTitleTag(tagName);
    }
}
