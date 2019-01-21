package com.gome.stage.mapper;


import com.gome.stage.entity.HotSearchword;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IHotSearchwordDMapper {
	public List<HotSearchword> listByTitleTag(@Param("tagName")String tagName);

}
