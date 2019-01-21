package com.gome.stage.service;

import com.gome.stage.entity.Keywords;
import com.gome.stage.entity.Title;

import java.util.List;
import java.util.Map;

public interface TitleService {

//    public List<Map<String, Object>> queryTitleIndexPageData();

    public List<Map<String, Object>> queryTitleIndexPageData();

    public List<Map<String, Object>> getTitleCindexCount(String site);

    public Map<String, Object> getTitleMapWithPage(Map<String, Object> serarch);

    public Map<String, Object> getTitleMap(Map<String, Object> search);

    public Title getTitleById(Integer rfid);

    public List<String> getRelatedGoodIdByTitleId(Integer title_id);

    public String getGoodIdByTitleId(Integer title_id);

    public List<Keywords> findRMLinkByTitleId(Integer title_id);

    public List<String> findTagByTitleId(Integer title_id);

    public List<Title> findRecentZhuanti();

    public List<Title> findZhuanTiByTagName(String tagName);

    public List<Title> findZhuanTiByTagId(Integer tagId);

    public List<Title> findZhuanTiByTitlePath(String titlePath);
}
