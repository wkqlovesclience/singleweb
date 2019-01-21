package com.gome.stage.service;

import com.gome.stage.entity.KeyWord;
import com.gome.stage.entity.Keywords;
import com.gome.stage.entity.Title;


import java.util.List;

public interface KeyWordService {
    public String getKeywordByTitleId(int TitleId);


    public List<KeyWord> findTagsByTitleId(Integer title_id, Integer type);


    public List<Title> findZhuanTiByTagName(String tagName);

    public List<Keywords> getRMLinkByTitleId(Integer titleId);

    public Keywords getTagnameById(int tagid);
}
