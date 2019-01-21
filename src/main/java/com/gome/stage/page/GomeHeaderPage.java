package com.gome.stage.page;

import com.alibaba.fastjson.JSONObject;

public class GomeHeaderPage extends HeaderPage {
	
	private static final long serialVersionUID = 1L;
	

	private JSONObject jsonData;

	public JSONObject getJsonData() {
		return jsonData;
	}

	public void setJsonData(JSONObject jsonData) {
		this.jsonData = jsonData;
	}
}
