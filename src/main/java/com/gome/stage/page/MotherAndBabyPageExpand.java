package com.gome.stage.page;

import com.alibaba.fastjson.JSONObject;
import com.gome.frontSe.comm.BaseBean;
import com.gome.frontSe.comm.FSLinkImg;
import com.gome.frontSe.comm.FSLinkText;
import com.gome.frontSe.page.comm.BlastInfo;
import com.gome.frontSe.page.comm.ClothingCityFloor;
import com.gome.frontSe.page.comm.FSHeaderCatalog;
import com.gome.frontSe.page.promo.FSCatalog;
import com.gome.stage.bean.home.Slidebase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 新版首页bean
 * 
 * @author tx_liushuo Date:2014-12-16
 */
public class MotherAndBabyPageExpand extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 底部分类列表
     */
    private FSCatalog bottomCatalogs;

    /**
     * 轮播广告
     */
    private List<Slidebase> slidesAds;

    /**
     * 明星店铺
     */
    private List<BlastInfo> starStoreList;

    /**
     * 焦点图下方的促销信息列表
     */
    private List<FSLinkImg> specActiList;

    /**
     * 右侧宝贝计划下方的分类
     */
    private List<FSLinkText> babyPlanCats;

    /**
     * 本周推荐
     */
    private List<BlastInfo> recommendOfWeek;
    

    /**
     * 本周推荐右侧的品牌推荐
     */
    private BlastInfo recommendBrand;

    private Map<String, String> featureName;
    /**
     * 左侧分类
     */
    private List<FSHeaderCatalog> prdCategories;

    /**
     * 头部横向导航
     * @return
     */
    private List<FSLinkText> headerNav;

    /**
     * 专场活动
     */
    private List<FSLinkImg> promotionlist;

    /**
     * 楼层
     */
    private List<ClothingCityFloor> hyermarketFloor;

	/**
	 * 小美推荐楼层
	 */
	private JSONObject floorData;

	public JSONObject getFloorData() {
		return floorData;
	}

	public void setFloorData(JSONObject floorData) {
		this.floorData = floorData;
	}
    
	public List<Slidebase> getSlidesAds() {
		return slidesAds;
    }

	public void setSlidesAds(List<Slidebase> slidesAds) {
		this.slidesAds = slidesAds;
    }
	
	public List<BlastInfo> getStarStoreList() {
		return starStoreList;
	}

	public void setStarStoreList(List<BlastInfo> starStoreList) {
		this.starStoreList = starStoreList;
	}
	
	public List<FSLinkImg> getSpecActiList() {
	return specActiList;
    }

	public void setSpecActiList(List<FSLinkImg> specActiList) {
	this.specActiList = specActiList;
    }

	public List<FSLinkText> getBabyPlanCats() {
		return babyPlanCats;
	}

	public void setBabyPlanCats(List<FSLinkText> babyPlanCats) {
		this.babyPlanCats = babyPlanCats;
	}
	
	public List<FSHeaderCatalog> getPrdCategories() {
	return prdCategories;
    }
	
	public List<BlastInfo> getRecommendOfWeek() {
		return recommendOfWeek;
	}

	public void setRecommendOfWeek(List<BlastInfo> recommendOfWeek) {
		this.recommendOfWeek = recommendOfWeek;
	}

	public void setPrdCategories(List<FSHeaderCatalog> prdCategories) {
	this.prdCategories = prdCategories;
    }
	
	public List<ClothingCityFloor> getHyermarketFloor() {
	return hyermarketFloor;
    }

	public void setHyermarketFloor(List<ClothingCityFloor> hyermarketFloors) {
	this.hyermarketFloor = hyermarketFloors;
    }
	
	public FSCatalog getBottomCatalogs() {
		return bottomCatalogs;
	}

	public void setBottomCatalogs(FSCatalog bottomCatalogs) {
		this.bottomCatalogs = bottomCatalogs;
	}
	
	public List<FSLinkText> getHeaderNav() {
		return headerNav;
	}

	public void setHeaderNav(List<FSLinkText> headerNav) {
		this.headerNav = headerNav;
	}
	
	public List<FSLinkImg> getPromotionlist() {
		return promotionlist;
	}

	public void setPromotionlist(List<FSLinkImg> promotionlist) {
		this.promotionlist = promotionlist;
	}
	
	public Map<String, String> getFeatureName() {
		return featureName;
	}

	public void setFeatureName(Map<String, String> featureName) {
		this.featureName = featureName;
	}

	public BlastInfo getRecommendBrand() {
		return recommendBrand;
	}

	public void setRecommendBrand(BlastInfo recommendBrand) {
		this.recommendBrand = recommendBrand;
	}
	
}
