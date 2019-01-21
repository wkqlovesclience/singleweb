package com.gome.stage.page;

import com.alibaba.fastjson.JSONObject;

public class MyFooterPage extends FooterPage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5842372768246644910L;
	
	
	private JSONObject footerData;
	

	public JSONObject getFooterData() {
		return footerData;
	}

	public void setFooterData(JSONObject footerData) {
		this.footerData = footerData;
	}
}
