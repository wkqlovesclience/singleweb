package com.gome.stage.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.frontSe.comm.CompareInfo;
import com.gome.frontSe.comm.FSLinkImg;
import com.gome.frontSe.interfaces.IProdDetailService;
import com.gome.stage.interfaces.specs.ISpecService;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.tools.AppConfigTools;
import com.gome.stage.utils.GuessLikeUtils;

import freemarker.template.Configuration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Description 移动端 wap商品对比,返回json数据
 * @author liujinhua
 *
 */

@Controller
@RequestMapping(value="/wapCompare")
public class WapCompareController {

  

    @Autowired
    private ISpecService specService;
    
    @Autowired
 	IProdDetailService prodDetailService;

    private GomeLogger logger = GomeLoggerFactory.getLogger(WapCompareController.class);

    //无线请求  发回wap端商品对比页  json数据
    @ResponseBody
    @RequestMapping(value = "/jsonData/{itemId}", method = RequestMethod.GET)
    public Object compareTo(HttpServletRequest request,HttpServletResponse response, @PathVariable("itemId") String itemId){

        
        JSONObject jsonObj = new JSONObject();
        try {
      
            String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
            jsonObj.put("storeConfiguration", JSON.parseObject(appConfig));

            if(StringUtils.isNotBlank(itemId)){
                List<CompareInfo> result=specService.getCompareInfo(Arrays.asList(itemId.split("-")));
                Map<String,Object> map = new HashMap<String, Object>();
                int number=0;
                try {
                    number=result.get(0).getData().get("商品图片").size();
                }catch (Exception ex){

                }
                map.put("number",number);
                map.put("result",result);

                jsonObj.put("compareInfo", map);
                if(CollectionUtils.isNotEmpty(result)) {
                    try {     
                        jsonObj.put("pictureInfo", JSONArray.parseArray(result.get(0).getData().get("商品图片").toString(),FSLinkImg.class));
                    }catch (Exception ex){
                        logger.error("Commodity [picture information] conversion [failure] ! ",ex);
                    }
                   
                }

            }else {
                logger.warn("goods parameter insert [not Null]!");
            }

            GuessLikeUtils gUtils = new GuessLikeUtils();
            //猜你喜欢
            try {
            	JSONObject guessData = gUtils.getJSONMap(itemId,prodDetailService);           
            	logger.info(guessData.toJSONString());
				jsonObj.put("GuessLike",guessData);
			} catch (Exception e) {
				logger.error("Error : GuessLike add failuer！" + e);
			}     

            //brid、shopid 、c1id、firstCategoryId、c3id、thirdCategoryId
            Map<String, String> compareIdMap = new HashMap<String, String>();
            compareIdMap = gUtils.getIdMap();
            jsonObj.put("compareIds", compareIdMap);
            
            
            //获取tdk信息
            Map<String,String> tdk=specService.getTDK(Arrays.asList(itemId.split("-")));
            if(tdk!=null&&tdk.size()>0){
                //title description keywords
                jsonObj.put("tdk", tdk);
            }

        }catch (Exception ex){
            logger.error("Call goods information [failure] ",ex);
        }
        return jsonObj.toJSONString();
    }
}
