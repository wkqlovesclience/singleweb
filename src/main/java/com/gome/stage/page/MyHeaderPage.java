package com.gome.stage.page;

import com.alibaba.fastjson.JSONObject;

public class MyHeaderPage extends HeaderPage{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5842372768246644910L;
	
	
	private JSONObject headerData;


	public JSONObject getHeaderData() {
		return headerData;
	}


	public void setHeaderData(JSONObject headerData) {
		this.headerData = headerData;
	}
	

	
}
