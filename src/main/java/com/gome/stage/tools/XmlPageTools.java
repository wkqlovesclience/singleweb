package com.gome.stage.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gome.stage.interfaces.IXmlPageDataService;

import java.util.Map;

public class XmlPageTools {
  private IXmlPageDataService xmlPageDataService;

  /**
   * @param serviceName xmlPageDataService       依赖estore的服务
   *                    xmlPageDataServiceHippo  脱离estore的服务
   */
  private XmlPageTools(String serviceName) {
    xmlPageDataService = SpringTools.getContext().getBean(serviceName, IXmlPageDataService.class);
  }

  private static class SingletionHolder {
    private static XmlPageTools generalService = new XmlPageTools("xmlPageDataService");
    private static XmlPageTools hippoService   = new XmlPageTools("xmlPageDataServiceHippo");
  }

  public static XmlPageTools getInstance(String serviceName) {
    if ("hippo".equals(serviceName)) {
      return SingletionHolder.hippoService;
    }
    return SingletionHolder.generalService;
  }

  public static XmlPageTools getInstance() {
    return SingletionHolder.generalService;
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
