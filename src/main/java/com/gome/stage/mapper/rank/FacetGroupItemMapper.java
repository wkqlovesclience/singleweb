/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc 
 */
package com.gome.stage.mapper.rank;

import com.gome.stage.entity.rank.FacetGroupItem;
import com.gome.stage.utils.PaginatedList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FacetGroupItemMapper
{
	public FacetGroupItem getById(@Param("id") String id);

	public PaginatedList<FacetGroupItem> list(Map<String, Object> paramMap);
	
	public List<FacetGroupItem> listByWheres(Map<String, Object> paramMap);

	public void add(FacetGroupItem facetGroupItem);

	public void delete(List<String> ids);

	public void update(FacetGroupItem facetGroupItem);
}
