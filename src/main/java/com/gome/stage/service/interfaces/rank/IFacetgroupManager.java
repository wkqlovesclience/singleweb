package com.gome.stage.service.interfaces.rank;

import com.gome.stage.entity.rank.FacetGroup;
import com.gome.stage.utils.PaginatedList;

import java.util.List;
import java.util.Map;


public interface IFacetgroupManager
{
	public FacetGroup getById(String id);
	
	public PaginatedList<FacetGroup> list(Map<String, Object> paramMap);
	
	public List<FacetGroup> listByWheres(Map<String, Object> paramMap);
}
