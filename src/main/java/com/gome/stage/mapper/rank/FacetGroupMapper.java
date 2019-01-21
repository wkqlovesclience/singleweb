/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc 
 */
package com.gome.stage.mapper.rank;

import com.gome.stage.entity.rank.FacetGroup;
import com.gome.stage.utils.PaginatedList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface FacetGroupMapper
{
	public FacetGroup getById(@Param("id") String id);

	public PaginatedList<FacetGroup> list(Map<String, Object> paramMap);

	public void add(FacetGroup facetGroup);

	public void delete(List<String> ids);

	public void update(FacetGroup facetGroup);
	
	public List<FacetGroup> listByWheres(Map<String, Object> paramMap);
}
