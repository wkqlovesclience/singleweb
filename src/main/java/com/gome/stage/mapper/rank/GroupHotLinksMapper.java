/**
 * 
 */
package com.gome.stage.mapper.rank;


import com.gome.stage.entity.rank.GroupHotLinks;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wangfufu
 *
 */
public interface GroupHotLinksMapper {
  List<GroupHotLinks> getGroupHotLinksListbygroupid(@Param("groupid") String thirdCateId);
}
