package com.gome.stage.mapper.rank;


import com.gome.stage.entity.rank.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryMapper {
    public Category getById(@Param("id") String id);

    public List<Category> listByWheres(Map<String, Object> map);

    public Category getByCid(@Param("cid") String cid);

    //排行榜列表页:获取同一父类的三级分类
    public List<Map<String,Object>> getRelatedCategoryList(Map<String, Object> paramMap);
}
