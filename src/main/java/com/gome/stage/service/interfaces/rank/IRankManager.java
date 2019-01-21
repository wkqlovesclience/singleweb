package com.gome.stage.service.interfaces.rank;

import com.gome.stage.entity.rank.RankIndexFloor;
import com.gome.stage.entity.rank.Titles;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IRankManager {

    /**
     * 查询排行榜首页楼层信息
     * @return 楼层信息
     */
    public List<RankIndexFloor> queryRankIndexShowFloor();


    /**
     * 根据标题创建时间查询此标题创建之前最近N个专题信息
     *
     * @param createtime
     *            创建时间
     * @param topnum
     *            最新前N个
     * @return 专题信息
     */
    List<Titles> findCurrentTitlesByCreateTime(Date createtime, int topnum);


    /**
     * 根据三级分类id和筛选项id查询商品销量信息
     * @param cat3Id 三级分类id
     * @param facetId 筛选项id
     * @return 销量信息
     */
    public List<Map<String, Object>> querySaleInfosByFacetId(String cat3Id, String facetId);

    /**
     * 根据三级分类id查询该分类下最新商品排行榜
     * @param cat3Id 三级分类id
     * @return 排行榜信息
     */
    public List<Map<String, Object>> queryNewSaleInfosByCat3Id(String cat3Id);

    /**
     * 根据三级分类id查询该分类下最热商品排行榜
     * @param cat3Id 三级分类id
     * @return 排行榜信息
     */
    public List<Map<String, Object>> queryHotSaleInfosByCat3Id(String cat3Id);
}
