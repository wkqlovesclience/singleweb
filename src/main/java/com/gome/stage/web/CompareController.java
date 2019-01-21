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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Description 商品对比
 * @Author yangchao-ds
 * @Date 2016/4/22.
 */
@Controller
@RequestMapping(value="/compare")
public class CompareController {


    @Autowired
    private ISpecService specService;
    
    @Autowired
 	IProdDetailService prodDetailService;

    private GomeLogger logger = GomeLoggerFactory.getLogger(CompareController.class);

    @RequestMapping(value = "/{itemId}", method = RequestMethod.GET)
    public ModelAndView compareTo(HttpServletRequest request,HttpServletResponse response, @PathVariable("itemId") String itemId){

        ModelAndView modle=new ModelAndView("compare/compare");
        try {
           
            String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
            modle.addObject("storeConfiguration", JSON.parseObject(appConfig));

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
                map.put("itemId",itemId);

                modle.addObject("compareInfo", map);
                if(CollectionUtils.isNotEmpty(result)) {
                    try {
                        modle.addObject("pictureInfo", JSONArray.parseArray(result.get(0).getData().get("商品图片").toString(),FSLinkImg.class));
                    }catch (Exception ex){
                        logger.error("商品图片信息转换失败 ",ex);
                    }
                    try {
                         modle.addObject("content", (result==null||result.size()<2||result.get(1).getContent()==null)?new ArrayList():result.get(1).getContent());
                    }catch (Exception ex){
                        logger.error("主体对应的商品名称等信息获取失败",ex);
                    }

                }

            }else {
                logger.warn("商品对比传入参数不能为空");
            }

            GuessLikeUtils gUtils = new GuessLikeUtils();
            //猜你喜欢
            try {
            	JSONObject guessData = gUtils.getJSONMap(itemId,prodDetailService);           
            	logger.info(guessData.toJSONString());
				modle.addObject("GuessLike",guessData);
			} catch (Exception e) {
				logger.error("Error : GuessLike 添加失败！" + e);
			}     

            //brid、shopid 、c1id、firstCategoryId、c3id、thirdCategoryId
            Map<String, String> compareIdMap = new HashMap<String, String>();
            compareIdMap = gUtils.getIdMap();
            modle.addObject("compareIds", compareIdMap);
            
            
            //获取tdk信息
            Map<String,String> tdk=specService.getTDK(Arrays.asList(itemId.split("-")));
            if(tdk!=null&&tdk.size()>0){
                //title description keywords
                modle.addAllObjects(tdk);
            }

        }catch (Exception ex){
            logger.error("获取商品对比信息失败 ",ex);
        }
        return modle;
    }
}
