package com.gome.stage.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gome.stage.join.GomeJoinService;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.tools.AppConfigTools;

import freemarker.template.Configuration;


@Controller
@RequestMapping(value="/join")
public class GomeJoinController {
	private static final String KEYWORD = "/gome-join";
	private GomeLogger logger = GomeLoggerFactory.getLogger(GomeJoinController.class);
	
	@Autowired
	private GomeJoinService gomeJoinService;
	
//	public GomeJoinService getGomeJoinService() {
//		return gomeJoinService;
//	}
//
//
//	public void setGomeJoinService(GomeJoinService gomeJoinService) {
//		this.gomeJoinService = gomeJoinService;
//	}


	@RequestMapping(value = "/{keyword}", method = RequestMethod.GET)
	public ModelAndView getJoinInfo(HttpServletRequest request,
			HttpServletResponse response,@PathVariable("keyword") String keyword
			){
		 ModelAndView modle=new ModelAndView("merchantJoin/join");
		if(StringUtils.isEmpty(keyword)){
			logger.warn("GomeJoinService is error !! the keyword is null!");
			keyword = KEYWORD;
		}else {
			keyword = "/" + keyword ;
		}
		
		try {
		
            String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
            modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
			JSONObject jsb =  gomeJoinService.getModelInfoBykw(keyword);
			modle.addObject("joinInfo", jsb);
			
		} catch (Exception e) {
			logger.error("获取商家入驻信息错误 ！！ (keyword= "+ keyword + ")!" );
			logger.error(e);
		}
		
		if(logger.isInfoEnabled()){
			logger.info("model:"+JSONObject.toJSONString(modle.getModel()));
		}
		return modle;
	}
}
