/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc 
 */
package com.gome.stage.service.impl.rank;

import com.gome.stage.entity.rank.FacetGroupItem;
import com.gome.stage.mapper.rank.FacetGroupItemMapper;
import com.gome.stage.service.interfaces.rank.IFacetgroupItemManager;
import com.gome.stage.utils.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FacetGroupItemManagerImpl implements IFacetgroupItemManager
{
	@Autowired
	private FacetGroupItemMapper facetGroupItemMapper;

	@Override
	public FacetGroupItem getById(String id)
	{
		return facetGroupItemMapper.getById(id);
	}

	@Override
	public PaginatedList<FacetGroupItem> list(Map<String, Object> paramMap)
	{
		return facetGroupItemMapper.list(paramMap);
	}

	public FacetGroupItemMapper getFacetGroupItemMapper()
	{
		return facetGroupItemMapper;
	}

	public void setFacetGroupItemMapper(FacetGroupItemMapper facetGroupItemMapper)
	{
		this.facetGroupItemMapper = facetGroupItemMapper;
	}

	@Override
	public List<FacetGroupItem> listByWheres(Map<String, Object> paramMap) {
		return facetGroupItemMapper.listByWheres(paramMap);
	}
}
