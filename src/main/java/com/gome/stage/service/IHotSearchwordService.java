package com.gome.stage.service;



import com.gome.stage.entity.HotSearchword;

import java.util.List;


public interface IHotSearchwordService {
	public List<HotSearchword> listByTitleTag(String tagName);
}
