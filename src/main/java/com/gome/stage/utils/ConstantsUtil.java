package com.gome.stage.utils;

public class ConstantsUtil {


	// 批发大全首页模板常量
	public static final Integer WHOLESALE_HOMEPAGE = SEOPropertiesUtils.getIntValueByKey("WHOLESALE_HOMEPAGE",0);

	public static final Integer WHOLESALE_HOMEPAGE_WAP = SEOPropertiesUtils.getIntValueByKey("WHOLESALE_HOMEPAGE_WAP",0);
	// 批发大全查询一级分类默认参数
	public static final String ROOT_CATEGORY_PARAM = "homeStoreRootCategory";
	// 批发大全查询本周批发商品销量排行榜前十名参数
	public static final String WEEK_PARAM = "7";
	// 国美的网站地址
	public static final String GOME_URL = SEOPropertiesUtils.getStringValueByKey("currentEnv","http://www.gome.com.cn");
	// 批发大全分类页模板常量
	public static final Integer WHOLESALE_FLPAGE = SEOPropertiesUtils.getIntValueByKey("WHOLESALE_FLPAGE",0);
	public static final Integer WHOLESALE_FLPAGE_WAP = SEOPropertiesUtils.getIntValueByKey("WHOLESALE_FLPAGE_WAP",0);
	// 批发大全详情页模板常量
	public static final Integer WHOLESALE_DETAILPAGE = SEOPropertiesUtils.getIntValueByKey("WHOLESALE_DETAILPAGE",0);
	public static final Integer WHOLESALE_DETAILPAGE_WAP = SEOPropertiesUtils.getIntValueByKey("WHOLESALE_DETAILPAGE_WAP",0);

	// PC品牌首页模板常量
	public static final Integer PC_HOMEBRAND_TEMPLATE = SEOPropertiesUtils.getIntValueByKey("PC_HOMEBRAND_TEMPLATE",0);
	public static final Integer PC_FIRSTBRAND_TEMPLATE = SEOPropertiesUtils.getIntValueByKey("PC_FIRSTBRAND_TEMPLATE",0);

	// 品类导航模板常量
	public static final Integer CATEGORY_NAVIGATION_TEMPLATE = SEOPropertiesUtils.getIntValueByKey("CATEGORY_NAVIGATION_TEMPLATE",0);

	// 移动品牌首页模板常量
	public static final Integer WAP_HOMEBRAND_TEMPLATE = SEOPropertiesUtils
			.getIntValueByKey("WAP_HOMEBRAND_TEMPLATE",0);

	public static final Integer WAP_FIRSTBRAND_TEMPLATE = SEOPropertiesUtils
			.getIntValueByKey("WAP_FIRSTBRAND_TEMPLATE",0);

	public static final Integer WAP_SUBBRAND_TEMPLATE = SEOPropertiesUtils
			.getIntValueByKey("WAP_SUBBRAND_TEMPLATE",0);

	// 国美移动端小助手报价模板常量
	public static final Integer WAP_XZSPRICE_TEMPLATE = SEOPropertiesUtils
			.getIntValueByKey("WAP_XZSPRICE_TEMPLATE",0);

	// Item页面获取商品销量信息接口地址
	public static final String ITEMPAGE_ACCESS_URL_MODEL = "http://"
			+ SEOPropertiesUtils.getStringValueByKey("ItemPage_access_domain","")
			+ "/p/product/40/1/10/0/{cat3Id}/0/0/10/0/0x1000/1?from=seo_search";

	// Item页面模板常量
	public static final Integer ITEM_PAGE = SEOPropertiesUtils.getIntValueByKey("ITEM_PAGE",0);

	// 排行榜页搜索商品接口地址
	public static final String RANK_SEARCH_URL_MODEL = "http://"
			+ SEOPropertiesUtils.getStringValueByKey("Rank_search_domain","")
			+ "/p/product/48/1/10/0/{cat3Id}/{facetId}/0/10/0/0x1000/0?from=seo_search";
}
