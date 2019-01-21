package com.gome.stage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.stage.interfaces.IXmlPageDataService;
import com.gome.stage.tools.SpringTools;
import org.apache.commons.lang3.StringUtils;

/**
 * 首页楼层的标签页商品JSON组装
 * Created by xj_xiaocheng on 2014/12/18.
 */
public class HomeFloorUtils {

    public static String getFloorTabPrdJson(String floor, String tab) {

        IXmlPageDataService xmlPageDataService = SpringTools.getContext().getBean("xmlPageDataService", IXmlPageDataService.class);
        String jsonText = xmlPageDataService.getPage("indexFloorTabPrd.xml", "/indexFloor/floor_" + floor + "/tab_" + tab);

        JSONObject retJson = new JSONObject();
        retJson.put("floor", floor);
        retJson.put("tab", tab);

        if(StringUtils.isEmpty(jsonText)) {
            retJson.put("msg", "数据为空");
            return retJson.toJSONString();
        }

        JSONObject json = JSON.parseObject(jsonText);
        JSONArray array = json.getJSONObject("children").getJSONObject("m3Product").getJSONObject("children").getJSONObject("d").getJSONArray("templates");

        if (array != null && array.size() > 0) {
            JSONArray retArray = new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                JSONObject temp = array.getJSONObject(i);
                if (temp == null) {
                    continue;
                }
                JSONArray prdList = temp.getJSONArray("prdList");
                if(prdList != null && prdList.size() > 0)
                    retArray.add(prdList.get(0));
            }
            retJson.put("list", retArray);
        }

        return retJson.toJSONString();
    }

    public static String getFloorTabPrdJson2015(String floor, String tab) {

        IXmlPageDataService xmlPageDataService = SpringTools.getContext().getBean("xmlPageDataServiceHippo", IXmlPageDataService.class);
        String jsonText = xmlPageDataService.getPage("indexFloorTabPrd2015.xml", "/indexPage/floor/floor1and7_" + floor + "/tab_" + tab);

        JSONObject retJson = new JSONObject();

        if(StringUtils.isEmpty(jsonText)) {
            retJson.put("msg", "数据为空");
            return retJson.toJSONString();
        }

        JSONObject json = JSON.parseObject(jsonText);
        JSONArray array = json.getJSONObject("children").getJSONObject("m3Product").getJSONObject("children").getJSONObject("d").getJSONArray("templates");

        if (array != null && array.size() > 0) {
            JSONArray retArray = new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                JSONObject temp = array.getJSONObject(i);
                if (temp == null) {
                    continue;
                }
                JSONArray prdList = temp.getJSONArray("prdList");
                if(prdList != null && prdList.size() > 0)
                    retArray.add(prdList.get(0));
            }
            retJson.put("list", retArray);
        }

        return retJson.toJSONString();
    }
}
