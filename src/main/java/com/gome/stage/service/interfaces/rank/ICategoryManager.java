package com.gome.stage.service.interfaces.rank;

import com.gome.stage.entity.rank.Category;

import java.util.List;
import java.util.Map;


public interface ICategoryManager
{
	//根据id获取分类对象
	public Category getById(String id);

	//排行榜首页：获取所有的分类排行榜
	public List<Map<String, Object>> getCategoryListAll();

	//根据id获取分类对象
	public Category getByCid(String cid);

	//根据t_facetgroupitem的id获取分类对象
	public Category getByFacetId(String id);

    //排行榜列表页:获取同一父类的三级分类
    public List<Map<String,Object>> getRelatedCategoryList(Map<String, Object> paramMap);
}
