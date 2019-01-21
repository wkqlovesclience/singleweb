package com.gome.stage.mapper;

import com.gome.stage.entity.Label;
import com.gome.stage.entity.LabelIndex;
import com.gome.stage.entity.NewsRelatedLabel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LabelMapper {
	
	public Label getLabelnameById(@Param("id") int id);

	public List<NewsRelatedLabel> getNewsRelatedLabel(Map<String, Object> paramMap);

	public int updateLabelStates(@Param("id") int id);

	public List<Label> getNewsRelatedLabelByNewsID(@Param("newsId") int newsId);

	public List<Label> getLabelRelEverySearch();

	public List<LabelIndex> getLabelIndex(Map<String, Object> search);

	public List<Map<String, Object>> getLabelCindexCount();

	public List<LabelIndex> getLabelIndexWithPage(Map<String, Object> search);

}
