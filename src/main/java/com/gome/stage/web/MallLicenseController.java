package com.gome.stage.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gome.stage.bean.index.MerchantLicense;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gome.stage.bean.index.MerchantInfo;
import com.gome.stage.join.MallLicenseService;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.tools.AppConfigTools;

import freemarker.template.Configuration;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/single")
public class MallLicenseController {

    private GomeLogger logger = GomeLoggerFactory.getLogger(MallLicenseController.class);
    @Autowired
    private MallLicenseService mallLicenseService;


    @RequestMapping(value = "/storelisence/{merchantId}", method = RequestMethod.GET)
    public ModelAndView getCouponDetail(HttpServletRequest request,
                                        HttpServletResponse response, @PathVariable("merchantId") String merchantId) {


        ModelAndView modle = new ModelAndView("storelisence/shopLisence");
        if (StringUtils.isBlank(merchantId)) {
            return modle;
        }
        try {
            MerchantInfo merInfo = mallLicenseService.getMerchantDetail(merchantId);
            String appConfig = JSON.toJSONString(AppConfigTools.getAppConfig());
            modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
            modle.addObject("model", merInfo);

        } catch (Exception e) {
            logger.error("MallLicenseController get MerchantInfo is error !!   merchantId : " + merchantId);
            logger.error(e + "");
        }
        return modle;
    }
    @RequestMapping(value = "/storelisencetest/{merchantId}", method = RequestMethod.GET)
    public void getCouponDetailtest(HttpServletRequest request,
                                        HttpServletResponse response, @PathVariable("merchantId") String merchantId) {
    	String string =null;

            MerchantInfo merInfo = mallLicenseService.getMerchantDetail(merchantId);
            response.setHeader("Content-type", "text/html;charset=UTF-8");
    		try {
    			OutputStream out = response.getOutputStream();
    			out.write(JSON.toJSONString(merInfo).getBytes("UTF-8"));
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    }
    @RequestMapping(value = "license", method = RequestMethod.GET)
    @ResponseBody
    public String getLicense(HttpServletRequest request, HttpServletResponse response) {
        String merchantId = request.getParameter("merchantId");
        String callback = request.getParameter("callback");
        String result = "";
        if (StringUtils.isBlank(merchantId)) {
            return result;
        }
        try {
            JSONObject object = mallLicenseService.getLicense(merchantId);

            // 判断店铺是否为食品分类
            Map<String, String> appConfig1 = AppConfigTools.getAppConfig();
            String qualification = appConfig1.get("qualification");

            boolean contains = false;
            if (qualification != null) {
                List<String> qualifications = Arrays.asList(qualification.split(","));

                MerchantLicense license = (MerchantLicense) object.get("license");

                contains = qualifications.contains(license.getQualification());
            }
            object.put("foodFlag", contains);

            String appConfig = JSON.toJSONString(appConfig1);
            object.put("storeConfiguration", JSON.parseObject(appConfig));
            if (StringUtils.isNotBlank(callback)) {
                result = callback + "(" + object.toJSONString() + ")";
            } else {
                result = "license(" + object.toJSONString() + ")";
            }
        } catch (Exception e) {
            logger.error("MallLicenseController get MerchantLicense is error !!   merchantId : " + merchantId);
            logger.error(e + "");
        }
        return result;
    }

    @RequestMapping(value = "checkFacade", method = RequestMethod.GET)
    @ResponseBody
    public String checkFacade(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");
        String uuid = request.getParameter("uuid");
        String type = request.getParameter("type");
        String callback = request.getParameter("callback");
        String result = "";
        if (StringUtils.isBlank(code)) {
            return result;
        }
        try {
            JSONObject object = new JSONObject();
            String authenticResult = mallLicenseService.checkFace(code, uuid, type);
            String appConfig = JSON.toJSONString(AppConfigTools.getAppConfig());
            object.put("storeConfiguration", JSON.parseObject(appConfig));
            if ("success".equals(authenticResult)) {
                object.put("result", "success");
                object.put("msg", "验证码验证成功!");
            } else if ("emptyStorage".equals(authenticResult)) {
                object.put("result", "false");
                object.put("msg", "验证码超时[" + authenticResult + "]");
            } else {
                object.put("result", "false");
                object.put("msg", "验证码错误!");
            }
            result = callback + "(" + object.toJSONString() + ")";
        } catch (Exception e) {
            logger.error("MallLicenseController checkFacade is error !!   ");
            logger.error(e + "");
        }
        return result;
    }

}
