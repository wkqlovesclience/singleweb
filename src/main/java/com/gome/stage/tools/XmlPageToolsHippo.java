package com.gome.stage.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gome.stage.interfaces.IXmlPageDataService;

import java.util.Map;

public class XmlPageToolsHippo {
	private IXmlPageDataService xmlPageDataService;

	private XmlPageToolsHippo() {
		xmlPageDataService = SpringTools.getContext().getBean("xmlPageDataServiceHippo", IXmlPageDataService.class);
	}

	private static class SingletionHolder {
		private static XmlPageToolsHippo instance = new XmlPageToolsHippo();
	}

	public static XmlPageToolsHippo getInstance() {
		return SingletionHolder.instance;
	}

	public JSONObject getPage(String xmlPath) {
		String json = xmlPageDataService.getPage(xmlPath);
		return getJsonObject(json);
	}

	public JSONObject getPage(String xmlPath, Map<String, String> params) {
		String json = xmlPageDataService.getPage(xmlPath, params);
		return getJsonObject(json);
	}

	public JSONObject getPage(String xmlPath, String parentKeyword) {
		String json = xmlPageDataService.getPage(xmlPath, parentKeyword);
		return getJsonObject(json);
	}

	public JSONObject getPage(String xmlPath, String parentKeyword, String handler) {
		String json = xmlPageDataService.getPage(xmlPath, parentKeyword, handler);
		return getJsonObject(json);
	}

	private JSONObject getJsonObject(String json) {
		JSONObject jsonObject = JSON.parseObject(json);
		jsonObject.put("storeConfiguration", AppConfigTools.getAppConfig());
		return jsonObject;
	}
}
