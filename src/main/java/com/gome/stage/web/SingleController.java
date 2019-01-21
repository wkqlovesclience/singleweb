package com.gome.stage.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.tools.AppConfigTools;

import freemarker.template.Configuration;

@Controller
@RequestMapping(value = "/single")
public class SingleController {

	private GomeLogger logger = GomeLoggerFactory
			.getLogger(SingleController.class);

	@RequestMapping(value = "/coupon/{couponId}/{activeId}", method = RequestMethod.GET)
	public ModelAndView getCouponDetail(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("couponId") String couponId,
			@PathVariable("activeId") String activeId) {
	

		ModelAndView modle = new ModelAndView("coupons/index");
		modle.addObject("couponId", couponId);
		modle.addObject("activeId", activeId);
		String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
		modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
		return modle;
	}

	/*
	 * 门店定位列表页 
	 * lng->经度 lat->维度 storeName->门店名称
	 * www.gome.com.cn/vbuycity/index.html?
	 */
	@RequestMapping(value = "/vbuycity", method = RequestMethod.GET)
	public ModelAndView getVBuyCity(HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value="lng",required=false) String lng,
			@RequestParam(value="lat",required=false) String lat,
			@RequestParam(value="storeName",required=false) String storeName) {
		
		ModelAndView modle = new ModelAndView("vbuycity/index");
		modle.addObject("lng", lng);
		modle.addObject("lat", lat);
		modle.addObject("storeName", storeName);
		String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
		modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
		return modle;
	}
	/*
	 * 去门店找她页面
	 * storeId->门店ID storeCode->门店Code staffCode->员工编码
	 * www.gome.com.cn/vbuyshop/index.html?storeId=xxx&storeCode=xxx&staffCode=xxx
	 */
	@RequestMapping(value = "/vbuyshop", method = RequestMethod.GET)
	public ModelAndView getVBuyShop(HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value="storeId",required=true) String storeId,
			@RequestParam(value="storeCode",required=true) String storeCode,
			@RequestParam(value="staffCode",required=false) String staffCode) {
		ModelAndView modle = new ModelAndView("vbuycity/guide-reserve");
		modle.addObject("storeId", storeId);
		modle.addObject("storeCode", storeCode);
		modle.addObject("staffCode", staffCode);
		String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
		modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
		return modle;
	}

	/*
	 * 预约成功页面
	 * storeId->门店ID reserveTime->预约时间
	 * www.gome.com.cn/reservesuccess/index.html?storeId=xxx&reserveTime=xxx
	 */
	@RequestMapping(value = "/reservesuccess", method = RequestMethod.GET)
	public ModelAndView getVBuyCity(HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value="storeId",required=true) String storeId,
			@RequestParam(value="reserveTime",required=true) String reserveTime) {
		ModelAndView modle = new ModelAndView("vbuycity/reserve-success");
		modle.addObject("storeId", storeId);
		modle.addObject("reserveTime", reserveTime);
		String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig());
		modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
		return modle;
	}
	/**
	 * 海外购协议阅读
	 * www.gome.com.cn/haiwaigouRead/index.html
	 */
	@RequestMapping(value = "/haiwaigouRead", method = RequestMethod.GET)
	public ModelAndView haiwaigouRead(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modle = new ModelAndView("haiwaigouRead/index");
		String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig());
		modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
		return modle;
	}
	/**
	 * 
	 * downpage下载页
	 * 
	 */
	@RequestMapping(value = "/downPage", method = RequestMethod.GET)
	public ModelAndView downPage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modle = new ModelAndView("downpage/index");
		String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig());
		modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
		return modle;
	}
}
