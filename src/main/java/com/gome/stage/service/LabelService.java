package com.gome.stage.service;



import com.gome.stage.entity.Label;
import com.gome.stage.entity.LabelIndex;
import com.gome.stage.entity.NewsRelatedLabel;

import java.util.List;
import java.util.Map;

public interface LabelService {

	Label getLabelnameById(int it);

	public List<NewsRelatedLabel> getNewsRelatedLabel(Map<String, Object> map);

	public List<Label> getNewsRelatedLabelByNewsID(int news);

	public int updateLabelStates(int id);

	public List<Label> getLabelRelEverySearch();

	void queryLabelIndexPageData(String site, Map<String, Object> data);

	public List<LabelIndex> getLabelIndex(Map<String, Object> search);

	public List<Map<String, Object>> getLabelCindexCount();

	public Map<String, Object> getLabelMapWithPage(Map<String, Object> serarch);
	
}
