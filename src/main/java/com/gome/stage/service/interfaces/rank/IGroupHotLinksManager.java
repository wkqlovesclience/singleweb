package com.gome.stage.service.interfaces.rank;


import com.gome.stage.entity.rank.GroupHotLinks;

import java.util.List;

/**
 * @author wangfufu
 *
 */
public interface IGroupHotLinksManager {

	List<GroupHotLinks> getGroupHotLinksListbygroupid(String thirdCateId);
}
