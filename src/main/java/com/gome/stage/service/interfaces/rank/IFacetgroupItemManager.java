/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc 
 */
package com.gome.stage.service.interfaces.rank;


import com.gome.stage.entity.rank.FacetGroupItem;
import com.gome.stage.utils.PaginatedList;

import java.util.List;
import java.util.Map;

public interface IFacetgroupItemManager
{
	public FacetGroupItem getById(String id);
	
	public PaginatedList<FacetGroupItem> list(Map<String, Object> paramMap);
	
	public List<FacetGroupItem> listByWheres(Map<String, Object> paramMap);
}
