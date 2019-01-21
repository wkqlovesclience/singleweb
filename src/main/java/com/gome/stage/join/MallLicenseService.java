package com.gome.stage.join;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.gome.captcha.facade.ICaptchaFacade;
import com.gome.stage.bean.index.MerchantInfo;
import com.gome.stage.bean.index.MerchantLicense;
import com.gome.stage.interfaces.item.IShopService;
import com.gome.stage.interfaces.whale.IMerchantService;
import com.gome.stage.item.ShopItem;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;

public class MallLicenseService {
	protected GomeLogger logger = GomeLoggerFactory
			.getLogger(MallLicenseService.class);
	private IMerchantService merchantService;
	private IShopService shopService;
	@Autowired
	private ICaptchaFacade captchaFacade;
	public IMerchantService getMerchantService() {
		return merchantService;
	}

	public void setMerchantService(IMerchantService merchantService) {
		this.merchantService = merchantService;
	}

	public IShopService getShopService() {
		return shopService;
	}

	public void setShopService(IShopService shopService) {
		this.shopService = shopService;
	}

	public ICaptchaFacade getCaptchaFacade() {
		return captchaFacade;
	}

	public void setCaptchaFacade(ICaptchaFacade captchaFacade) {
		this.captchaFacade = captchaFacade;
	}

	public MerchantInfo getMerchantDetail(String merchantId){
		MerchantInfo merInfo = new MerchantInfo();
		try {
			String companyName = merchantService.findCompanyNameByMerId(merchantId);
			merInfo = getMerchantInfo(merchantId);
			if(merInfo != null && companyName != null){
				merInfo.setCompanyName(companyName);
			}
			
		} catch (Exception e) {
			logger.error("MallLicenseController get MerchantInfo is error !!   merchantId : " + merchantId);
			logger.error(e+"");
		}
		return merInfo;		
	}
	
	public JSONObject getLicense(String merchantId){
		JSONObject object = new JSONObject();
		if(StringUtils.isBlank(merchantId)){
			return object;
		}
		try {
			MerchantLicense merLicense = merchantService.findMerchantLicenseById(merchantId);
			if(merLicense != null){
				object.put("license", merLicense);
			}
		} catch (Exception e) {
			logger.error("MallLicenseController get MerchantLicense is error !!   merchantId : " + merchantId);
			logger.error(e+"");
		}
		return object;	
	}
	
	
	public MerchantInfo getMerchantInfo(String merchantId){
		MerchantInfo merInfo = new MerchantInfo();
		try {
			ShopItem shopItem = shopService.getShopInfoByShopId(merchantId);
			if(shopItem == null || shopItem.getShopId() == null){
				return merInfo;
			}
			merInfo.setMerchantId(merchantId);
			merInfo.setSupplierNo(shopItem.getSupplierNo());
			merInfo.setMerchantName(shopItem.getShopName());
			merInfo.setGoodsMatch(shopItem.getGoodsMatch());
			merInfo.setGoodsMatchCmp(shopItem.getGoodsMatchCmp());
			merInfo.setServiceScore(shopItem.getServiceScore());
			merInfo.setServiceScoreCmp(shopItem.getServiceScoreCmp());
			merInfo.setDeliverySpeed(shopItem.getDeliverySpeed());
			merInfo.setDeliverySpeedCmp(shopItem.getDeliverySpeedCmp());
			merInfo.setShopHotLine(shopItem.getShopHotLine());
			merInfo.setProvince(shopItem.getProvince());
			merInfo.setCity(shopItem.getCity());
			merInfo.setShopLogo(shopItem.getShopLogoUrl());
			merInfo.setContractStart(shopItem.getContractStart());
			merInfo.setContractEnd(shopItem.getContractEnd());
			Double match = 0.0;
			int count = 0;
			if(merInfo.getGoodsMatch() != null){
				match = merInfo.getGoodsMatch();
				count = count + 1;
			}
			Double speed = 0.0;
			if(merInfo.getDeliverySpeed() != null){
				speed = merInfo.getDeliverySpeed();
				count = count + 1;
			}
			Double score = 0.0;
			if(merInfo.getServiceScore() != null){
				score = merInfo.getServiceScore();
				count = count + 1;
			}
			Double totalScore = 4.5;
			if(count != 0){
				totalScore = (match + speed + score)/count;
			}else {
				totalScore = 4.5;
				merInfo.setGoodsMatch(4.5);
				merInfo.setServiceScore(4.5);
				merInfo.setDeliverySpeed(4.5);
			}
			merInfo.setTotalScore(totalScore);
			
		} catch (Exception e) {
			logger.error(e+"");
		}
		return merInfo;
	}
	
	
	public String checkFace(String code,String uuid,String type){
		String authenticResult = "";
		try {
			authenticResult = captchaFacade.validateCaptcha(code,uuid, type,null);
		} catch (Exception e) {
			logger.error(e+"");
		}
		return authenticResult;
	}
}
