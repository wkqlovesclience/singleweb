package com.gome.stage.service.impl.rank;

import com.alibaba.fastjson.JSON;
import com.gome.stage.entity.rank.Category;
import com.gome.stage.entity.rank.FacetGroupItem;
import com.gome.stage.mapper.rank.CategoryMapper;
import com.gome.stage.mapper.rank.FacetGroupItemMapper;
import com.gome.stage.service.interfaces.rank.ICategoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryManagerImpl implements ICategoryManager
{
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private FacetGroupItemMapper facetGroupItemMapper;

    public Category getById(String id)
    {
        return categoryMapper.getById(id);
    }

    @Override
    public List<Map<String, Object>> getCategoryListAll() {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        Map<String,Object> firstLevelMap = new HashMap<String,Object>();
        firstLevelMap.put("qparent_id", "homeStoreRootCategory");
        firstLevelMap.put("qis_show", 1);
        List<Category> firstCategoryList = categoryMapper.listByWheres(firstLevelMap);
        System.out.println("查询出的"+ JSON.toJSONString(firstCategoryList));
        for(Category firstCat:firstCategoryList){
            Map<String, Object> itemMap = new HashMap<String,Object>();

            String parentId = firstCat.getId();
            String firstName = firstCat.getCategoryName();

            Map<String, Object> searchMap = new HashMap<String,Object>();
            searchMap.put("qparent_id", parentId);
            searchMap.put("qis_show", 1);
            searchMap.put("qispublish", 1);
            List<Category> thirdCategoryList = categoryMapper.listByWheres(searchMap);

            itemMap.put("parentName", firstName);
            itemMap.put("subList", thirdCategoryList);

            resultList.add(itemMap);
        }
        return resultList;
    }

    @Override
    public Category getByCid(String cid) {
        return categoryMapper.getByCid(cid);
    }

    @Override
    public Category getByFacetId(String id) {
        FacetGroupItem facetGroupItem = facetGroupItemMapper.getById(id);
        return categoryMapper.getById(facetGroupItem.getCatId());
    }

    @Override
    public List<Map<String, Object>> getRelatedCategoryList(Map<String, Object> paramMap) {
        return categoryMapper.getRelatedCategoryList(paramMap);
    }

}
