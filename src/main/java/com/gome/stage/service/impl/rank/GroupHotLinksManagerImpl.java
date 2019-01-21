/**
 * 
 */
package com.gome.stage.service.impl.rank;


import com.gome.stage.entity.rank.GroupHotLinks;
import com.gome.stage.mapper.rank.GroupHotLinksMapper;
import com.gome.stage.service.interfaces.rank.IGroupHotLinksManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangfufu
 *
 */
@Service
public class GroupHotLinksManagerImpl implements IGroupHotLinksManager {

	@Autowired
	private GroupHotLinksMapper groupHotLinksMapper;
	
	public List<GroupHotLinks> getGroupHotLinksListbygroupid(String thirdCateId){
		 return groupHotLinksMapper.getGroupHotLinksListbygroupid(thirdCateId);
	 }
	
}
