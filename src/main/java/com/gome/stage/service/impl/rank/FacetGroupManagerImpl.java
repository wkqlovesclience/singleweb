/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc 
 */
package com.gome.stage.service.impl.rank;

import com.gome.stage.entity.rank.FacetGroup;
import com.gome.stage.mapper.rank.FacetGroupMapper;
import com.gome.stage.service.interfaces.rank.IFacetgroupManager;
import com.gome.stage.utils.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FacetGroupManagerImpl implements IFacetgroupManager
{
	@Autowired
	private FacetGroupMapper facetGroupMapper;

	@Override
	public FacetGroup getById(String id)
	{
		return facetGroupMapper.getById(id);
	}

	@Override
	public PaginatedList<FacetGroup> list(Map<String, Object> paramMap)
	{
		return facetGroupMapper.list(paramMap);
	}

	public FacetGroupMapper getFacetGroupMapper()
	{
		return facetGroupMapper;
	}

	public void setFacetGroupMapper(FacetGroupMapper facetGroupMapper)
	{
		this.facetGroupMapper = facetGroupMapper;
	}

	@Override
	public List<FacetGroup> listByWheres(Map<String, Object> paramMap) {
		return facetGroupMapper.listByWheres(paramMap);
	}

}
