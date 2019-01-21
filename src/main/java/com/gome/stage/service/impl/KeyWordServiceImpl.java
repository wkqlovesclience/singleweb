package com.gome.stage.service.impl;

import com.gome.stage.entity.KeyWord;
import com.gome.stage.entity.Keywords;
import com.gome.stage.entity.Title;
import com.gome.stage.mapper.KeyWordMapper;
import com.gome.stage.service.KeyWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KeyWordServiceImpl implements KeyWordService {
    @Autowired
    private KeyWordMapper keyWordMapper;
    @Override
    public String getKeywordByTitleId(int titleId) {
        List<String> ss = keyWordMapper.getKeywordByTitleId(titleId);
        String str = "";
        for (String s : ss) {
            str += s + ",";
        }
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    @Override
    public List<KeyWord> findTagsByTitleId(Integer title_id, Integer type) {
        return keyWordMapper.findTagsByTitleId(title_id,type);
    }

    @Override
    public List<Title> findZhuanTiByTagName(String tagName){
        return keyWordMapper.findZhuanTiByTagName(tagName);
    }

    @Override
    public List<Keywords> getRMLinkByTitleId(Integer titleId){
        return keyWordMapper.getRMLinkByTitleId(titleId);
    }

    @Override
    public Keywords getTagnameById(int tagid){
        return keyWordMapper.getTagnameById(tagid);
    }
}
