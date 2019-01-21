/**
 * @author tianyu-ds
 * @date 2014-2-16
 * @project_name topic_trunk
 * @company 国美在线有限公司
 * @desc 
 */
package com.gome.stage.service.impl;


import com.gome.stage.entity.Category;
import com.gome.stage.entity.HotLink;
import com.gome.stage.mapper.ICategoryMapper;
import com.gome.stage.service.ICategoryService;
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
public class CategoryServiceImpl implements ICategoryService {
	@Autowired
	private ICategoryMapper categoryDAO;

	/*public Category getById(String id)
	{
		return categoryDAO.getById(id);
	}
	
	@Override
	public Integer getByParentId(String parentId)
	{
		return categoryDAO.getByParentId(parentId);
	}*/

	@Override
	public PaginatedList<Category> list(Map<String, Object> paramMap)
	{
		return categoryDAO.list(paramMap);
	}
	
	
	/* (non-Javadoc)
	 * @see com.coo8.publisher.service.interfaces.rank.ICategoryManager#listByWhere(java.util.Map)
	 */
	@Override
	public List<Category> listByWhere(Map<String, Object> paramMap) {
		return categoryDAO.listByWhere(paramMap);
	}

	/*@Override
	public void add(Category category)
	{
		categoryDAO.add(category);
	}*/

	/*@Override
	public void delete(List<String> ids)
	{
		categoryDAO.delete(ids);
	}

	@Override
	public void update(Category category)
	{
		categoryDAO.update(category);
	}
	
	@Override
	public Category validateCategory(String categoryName)
	{
		return categoryDAO.validateCategory(categoryName);
	}


	@Override
	public boolean isHasUnhandleQueueData() {
		int bolckNum = categoryDAO.getUnhandleNumberOfBlockQueue();
		int productNum = categoryDAO.getUnhandleNumberOfProductQueue();
		if(bolckNum == 0 && productNum == 0){
			return false;
		}
		return true;
	}

	@Override
	public List<Map<String, Object>> getCategoryListAll() {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		Map<String,Object> firstLevelMap = new HashMap<String,Object>();
		firstLevelMap.put("qparent_id", "homeStoreRootCategory");
		firstLevelMap.put("qis_show", 1);
		List<Category> firstCategoryList = categoryDAO.listByWheres(firstLevelMap);
		for(Category firstCat:firstCategoryList){
			Map<String, Object> itemMap = new HashMap<String,Object>();

			String parentId = firstCat.getId();
			String firstName = firstCat.getCategoryName();
			
			Map<String, Object> searchMap = new HashMap<String,Object>();
			searchMap.put("qparent_id", parentId);
			searchMap.put("qis_show", 1);
			searchMap.put("qispublish", 1);
			List<Category> thirdCategoryList = categoryDAO.listByWheres(searchMap);
			
			itemMap.put("parentName", firstName);
			itemMap.put("subList", thirdCategoryList);
			
			resultList.add(itemMap);
		}
		return resultList;
	}

	@Override
	public List<Map<String, Object>> getRelatedCategoryList(Map<String, Object> paramMap) {
		return categoryDAO.getRelatedCategoryList(paramMap);
	}

	@Override
	public Category getByCid(Integer cid) {
		return categoryDAO.getByCid(cid);
	}

	@Override
	public int count(Map<String, Object> paramMap) {
		return categoryDAO.count(paramMap);
	}

	*//* (non-Javadoc)
	 * @see com.coo8.publisher.service.interfaces.rank.ICategoryManager#findHotLinkList(com.coo8.model.zhuanti.HotLink)
	 *//*
	@Override
	public List<HotLink> findHotLinkList(HotLink hotLink) {
		return categoryDAO.findHotLinkList(hotLink);
	}*/
	
	
}
