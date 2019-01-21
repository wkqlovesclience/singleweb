package com.gome.stage.page;

import com.alibaba.fastjson.JSONObject;

public class GomeIndexPage extends HomePage {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 楼层数据
	 */
	private JSONObject floorData;

	public JSONObject getFloorData() {
		return floorData;
	}

	public void setFloorData(JSONObject floorData) {
		this.floorData = floorData;
	}
	
}
