package com.gome.stage.service.impl;


import com.gome.stage.entity.Keywords;
import com.gome.stage.entity.Title;
import com.gome.stage.mapper.TitleIndexMapper;
import com.gome.stage.mapper.TitleMapper;
import com.gome.stage.entity.TitleIndex;
import com.gome.stage.service.TitleService;
import com.gome.stage.utils.PaginatedArrayList;
import com.gome.stage.utils.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class TitleServiceImpl implements TitleService {
    private static List<Map<String, Object>>[] indeListArray = new List[27];
    private static char[] charArray = new char[27];
    static {
        /*
         * 初始化数据集
         */
        for (int i = 0; i < indeListArray.length; i++) {
            indeListArray[i] = new ArrayList<Map<String, Object>>();
            if (i == (indeListArray.length - 1)) {
                charArray[i] = '0';
            } else {
                charArray[i] = (char) (65 + i);
            }
        }
    }
    @Autowired
    private TitleMapper titleMapper;
    @Autowired
    private TitleIndexMapper titleIndexMapper;

    private final static String FIRSTPAGE_INDEXLIST_KEY = "cindexList"; // 专题索引KEY
    private final static String FIRSTPAGE_NEWSLIST_KEY = "newsList"; // 知识列表页KEY

    /**
     * 获得专题首页索引列表
     *
     */

    public List<Map<String, Object>> queryTitleIndexPageData() {

        List<Map<String, Object>> topicData = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < charArray.length; i++) {
            Map<String,Object> map = new HashMap<String, Object>();
            Map<String,Object> dataMap = new HashMap<String, Object>();
            if (Character.isDigit(charArray[i])){
                map.put("numberindex",String.valueOf(charArray[i]));
            }else {
                map.put("cindex",String.valueOf(charArray[i]));
            }
            List<Title> cindexTopicData = titleIndexMapper.getTitleIndex(map);
            dataMap.put(String.valueOf(charArray[i]),cindexTopicData);
            topicData.add(dataMap);
        }
        return topicData;

    }



    /*public List<Map<String, Object>> queryTitleIndexPageData() {



        List<TitleIndex> manualIndexList = titleIndexMapper.getTitleIndex();

        List<Map<String, Object>> mapList = processTitleIndexList2Map(manualIndexList, "gome");
        return mapList;

    }
    */
    public List<Map<String, Object>> getTitleCindexCount(String site) {
        List<Map<String, Object>> cindexCountList = titleIndexMapper.getTitleCindexCount(site);
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        Map<String, Object> data = new HashMap<String, Object>();
        if (null != cindexCountList) {
            for (Map<String, Object> map : cindexCountList) {
                String cindex = (String) map.get("cindex");
                Long counts = (Long) map.get("counts");
                data.put(cindex, counts);
            }
        }
        retList.add(data);
        return retList;
    }

    public Map<String, Object> getTitleMapWithPage(Map<String, Object> search) {
        PaginatedList<TitleIndex> paginatedArrayList = new PaginatedArrayList<TitleIndex>(
                (Integer) search.get("totalCount2"), (Integer) search.get("pageNumber"),
                (Integer) search.get("pageSize"));
        search.put("begin",paginatedArrayList.getStartPos());
        search.put("pagesize",paginatedArrayList.getPageSize());

        List<TitleIndex> titleIndexList = titleIndexMapper.getTitleIndexWithPage(search);

        Map<String, Object> data = new HashMap<String, Object>();

        if (null != titleIndexList) {
            data.put(FIRSTPAGE_INDEXLIST_KEY, titleIndexList);
        }
        return data;
    }


    public Map<String, Object> getTitleMap(Map<String, Object> search) {
        List<TitleIndex> titleIndexList = titleIndexMapper.getTitleIndexWithLetter(search);
        Map<String, Object> data = new HashMap<String, Object>();
        if (null != titleIndexList) {
            data.put(FIRSTPAGE_INDEXLIST_KEY, titleIndexList);
        }
        return data;
    }


    @Override
    public Title getTitleById(Integer rfid) {
        return titleMapper.selectByPrimaryKey(rfid);
    }
    @Override
    public List<String> getRelatedGoodIdByTitleId(Integer title_id){
        return titleMapper.getRelatedGoodIdByTitleId(title_id);
    }

    @Override
    public String getGoodIdByTitleId(Integer title_id){
        return titleMapper.getGoodIdByTitleId(title_id);
    }
    @Override
    public List<Keywords> findRMLinkByTitleId(Integer title_id){
        return titleMapper.findRMLinkByTitleId(title_id);
    }

    @Override
    public List<String> findTagByTitleId(Integer title_id){
        return titleMapper.findTagByTitleId(title_id);
    }


    /**
     *
     * @param manualIndexList
     * @return
     */
   /* @SuppressWarnings("unchecked")
    private List<Map<String, Object>> processTitleIndexList2Map(List<TitleIndex> manualIndexList,String site) {


        if (null == manualIndexList || manualIndexList.isEmpty()) {
            return null;
        }

        List<Map<String, Object>>[] indeListArr = new List[27];

        char[] charArr = new char[27];

        *//*
         * 初始化数据集
         *//*
        for (int i = 0; i < indeListArr.length; i++) {
            indeListArr[i] = new ArrayList<Map<String, Object>>();
            if (i == (indeListArr.length - 1)) {
                charArr[i] = '0';
            } else {
                charArr[i] = (char) (65 + i);
            }
        }

        *//*
         * 筛选数据
         *//*
        for (TitleIndex index : manualIndexList) {

            Map<String, Object> data = new HashMap<String, Object>();
            String cindex = index.getCindex();
            String tname = index.getTitleName();
            int titleId = index.getTitleId();
            String paths = index.getTitlePath();
            if (null == paths || "".equals(paths)) {
                paths = String.valueOf(titleId);
            }
            char cx = '0';
            if (null != cindex && !"".equals(cindex.trim())) {
                cx = cindex.trim().charAt(0);
            }
            data.put("titleId",titleId);
            data.put("titlename", tname);
            data.put("paths", paths);

            switch (cx) {

                case 'A':
                    indeListArr[0].add(data);
                    break;
                case 'B':
                    indeListArr[1].add(data);
                    break;
                case 'C':
                    indeListArr[2].add(data);
                    break;
                case 'D':
                    indeListArr[3].add(data);
                    break;
                case 'E':
                    indeListArr[4].add(data);
                    break;
                case 'F':
                    indeListArr[5].add(data);
                    break;
                case 'G':
                    indeListArr[6].add(data);
                    break;
                case 'H':
                    indeListArr[7].add(data);
                    break;
                case 'I':
                    indeListArr[8].add(data);
                    break;
                case 'J':
                    indeListArr[9].add(data);
                    break;
                case 'K':
                    indeListArr[10].add(data);
                    break;
                case 'L':
                    indeListArr[11].add(data);
                    break;
                case 'M':
                    indeListArr[12].add(data);
                    break;
                case 'N':
                    indeListArr[13].add(data);
                    break;
                case 'O':
                    indeListArr[14].add(data);
                    break;
                case 'P':
                    indeListArr[15].add(data);
                    break;
                case 'Q':
                    indeListArr[16].add(data);
                    break;
                case 'R':
                    indeListArr[17].add(data);
                    break;
                case 'S':
                    indeListArr[18].add(data);
                    break;
                case 'T':
                    indeListArr[19].add(data);
                    break;
                case 'U':
                    indeListArr[20].add(data);
                    break;
                case 'V':
                    indeListArr[21].add(data);
                    break;
                case 'W':
                    indeListArr[22].add(data);
                    break;
                case 'X':
                    indeListArr[23].add(data);
                    break;
                case 'Y':
                    indeListArr[24].add(data);
                    break;
                case 'Z':
                    indeListArr[25].add(data);
                    break;
                case '0':
                    indeListArr[26].add(data);
                    break;
                case '1':
                    indeListArr[26].add(data);
                    break;
                case '2':
                    indeListArr[26].add(data);
                    break;
                case '3':
                    indeListArr[26].add(data);
                    break;
                case '4':
                    indeListArr[26].add(data);
                    break;
                case '5':
                    indeListArr[26].add(data);
                    break;
                case '6':
                    indeListArr[26].add(data);
                    break;
                case '7':
                    indeListArr[26].add(data);
                    break;
                case '8':
                    indeListArr[26].add(data);
                    break;
                case '9':
                    indeListArr[26].add(data);
                    break;
            }

        }
        *//*
         * 设置数据
         *//*

        List<Map<String, Object>> indeList = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < indeListArr.length; j++) {
            Map<String, Object> tmpMap = new HashMap<String, Object>();
            if (null != indeListArr[j] && !indeListArr[j].isEmpty()) {

                *//**
                 * 描述：增加自动更新部分专题，总共11个关键词
                 * 修改人：suchao@gomeplus.com
                 * 时间：2017-08-15 10:00:00
                 * 代码块：begin
                 *//*

                //开始追加数据
                Map<String, Object> search = new HashMap<String, Object>();
                search.put("site", site); // 站点类型
                if(charArr[j] == '0'){
                    search.put("numberindex","0");
                }else{
                    search.put("cindex", String.valueOf(charArr[j]));
                }
                //动态获取top
                int indexListCount = indeListArr[j].size();
                if(indexListCount>6){
                    indeListArr[j].subList(6,indexListCount).clear();indexListCount = 6;
                }
                search.put("top", 11 - indexListCount);
                //对索引进行处理
                List<TitleIndex> topIndexList = titleIndexMapper.getTitleIndexByUpdate(search);
                for(TitleIndex item:topIndexList){
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("titlename", item.getTitleName());
                    String paths = item.getTitlePath();
                    if (null == paths || "".equals(paths)) {
                        paths = String.valueOf(item.getTitleId());
                    }
                    data.put("paths", paths);
                    indeListArr[j].add(data);
                }
                *//**
                 * 描述：增加自动更新部分专题，总共11个关键词
                 * 修改人：suchao@gomeplus.com
                 * 时间：2017-08-15 10:00:00
                 * 代码块：end
                 *//*

                //返回数据
                tmpMap.put("cindex", charArr[j]);
                tmpMap.put("list", indeListArr[j]);
                indeList.add(tmpMap);
            }

        }
        *//*
         * 添加索引数据
         *//*
        return indeList;
    }*/

    public List<Title> findRecentZhuanti(){
        return titleMapper.findRecentZhuanti();
    }

    @Override
    public List<Title> findZhuanTiByTagName(String tagName){
        return titleMapper.findZhuanTiByTagName(tagName);
    }

    @Override
    public List<Title> findZhuanTiByTagId(Integer tagId) {
        return titleMapper.findZhuanTiByTagId(tagId);
    }

    @Override
    public List<Title> findZhuanTiByTitlePath(String titlePath) {
        return titleMapper.findZhuanTiByTitlePath(titlePath);
    }

}
