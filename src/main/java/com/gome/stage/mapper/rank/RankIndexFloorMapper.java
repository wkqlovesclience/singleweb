package com.gome.stage.mapper.rank;

import com.gome.stage.entity.rank.RankIndexFloor;
import com.gome.stage.entity.rank.Titles;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface RankIndexFloorMapper {

    /**  
     *  
     * Description:   
     *  
     * @param: 查询三级分类排行榜楼层
     * @return: java.util.List<RankIndexFloor>  
     * @auther: qinruixin-ds  
     * @date: 2018/11/6 20:39
     */
    public List<RankIndexFloor> getShowFloor();

    /**
     * 根据标题创建时间查询此标题创建之前最近N个专题信息
     *
     * @param createtime
     *            创建时间
     * @param topnum
     *            最新前N个findCurrentTitlesByCreateTime
     * @return 专题信息
     */
    public List<Titles> findCurrentTitlesByCreateTime(@Param("createtime") Date createtime, @Param("topnum") int topnum);
}
