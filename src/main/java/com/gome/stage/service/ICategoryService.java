/**
 * @author tianyu-ds
 * @date 2014-2-16
 * @project_name topic_trunk
 * @company 国美在线有限公司
 * @desc 
 */
package com.gome.stage.service;

import com.gome.stage.entity.Category;
import com.gome.stage.utils.PaginatedList;


import java.util.List;
import java.util.Map;

public interface ICategoryService
{
	/*public Category getById(String id);
	
	public Category getByCid(Integer cid);
	
	public int count(Map<String, Object> paramMap);*/
	
	public PaginatedList<Category> list(Map<String, Object> paramMap);
	
	public List<Category> listByWhere(Map<String, Object> paramMap);
	
	/*public void add(Category category);
	
	public void delete(List<String> ids);
	
	public void update(Category category);
	
	public Category validateCategory(String categoryName);
	
	public Integer getByParentId(String parentId);
	
	public boolean isHasUnhandleQueueData();
	
	//排行榜首页：获取所有的分类排行榜
	public List<Map<String,Object>> getCategoryListAll();
	
	//排行榜列表页:获取同一父类的三级分类
	public List<Map<String,Object>> getRelatedCategoryList(Map<String, Object> paramMap);
	
	public List<HotLink> findHotLinkList(HotLink hotLink);*/
}
