package com.gome.stage.web;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gome.qiantai.model.AppraiseSEO;
import com.gome.qiantai.model.GoodsAppraiseInfoVO;
import com.gome.qiantai.service.IAppraiseSEOService;
import com.gome.qiantai.service.IGoodsAppraiseQueryService;
import com.gome.stage.ENUM.RankFilter;
import com.gome.stage.ENUM.RankTemplate;
import com.gome.stage.bean.Ranking.NewProductRank;
import com.gome.stage.bean.Ranking.SeoBrandSale;
import com.gome.stage.bean.Ranking.SimpleProduct;
import com.gome.stage.entity.rank.*;
import com.gome.stage.interfaces.item.IGomeProductService;
import com.gome.stage.interfaces.item.IPriceService;
import com.gome.stage.interfaces.seo.newrank.IGomeNewProductRankService;
import com.gome.stage.interfaces.seo.newrank.IGomeSEOSaleRank;
import com.gome.stage.interfaces.whale.IProductInfoService;
import com.gome.stage.item.GomePrice;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.service.interfaces.rank.*;
import com.gome.stage.utils.PublishUtil;
import com.gome.stage.utils.SEOPropertiesUtils;
import com.gome.stage.utils.StringUtils;
import com.gome.tapho.interfaces.spec.IProdSpecService;
import org.gome.search.dubbo.idl.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import redis.Gcache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "/newtop10")
public class RankController {

	private GomeLogger logger = GomeLoggerFactory
			.getLogger(RankController.class);

	// 商品上架状态123
	private final int ON_SALE_STATUS = 4;
	// LIVE_WAP域名
	private final String WAP_HOST_LIVE= "m.gome.com.cn";
	// UAT_WAP域名
	private final String WAP_HOST_UAT= "m.atguat.com.cn";

	@Autowired
	private IGomeNewProductRankService gomeNewProductRankService;
	@Autowired
	private IGomeProductService gomeProductService;
	@Autowired
	private IPriceService priceService;
	@Autowired
	private IGoodsAppraiseQueryService goodsAppraiseQueryService;
	@Autowired
	private IGomeSEOSaleRank gomeSEOSaleRank;
	@Autowired
	private IProdSpecService prodSpecService;
	@Autowired
	private IAppraiseSEOService appraiseSEOService;
	@Autowired
	private IRankManager rankManager;
	@Autowired
	private ICategoryManager categoryManager;
	@Autowired
	private IFacetgroupManager facetGroupManager;
	@Autowired
	private IFacetgroupItemManager facetGroupItemManager;
	@Autowired
	private IGroupHotLinksManager groupHotLinksManager;
	@Autowired
	@Qualifier("gcache_productCard")
	private Gcache cache;
	@Autowired
	private DubboService dubboSearchService;
	@Autowired
	private IProductInfoService productInfoService;




	/*排行榜首页
	 *  /topic10
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getPCTopic10Index(HttpServletRequest request, HttpServletResponse response
	) {
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		String seoWapHost = WAP_HOST_LIVE;
		if(!"live".equals(env)){
			   seoWapHost = WAP_HOST_UAT;
		}
		String header = request.getHeader("host");
		System.out.println("当前域名为"+header);
		System.out.println("header的参数为"+JSON.toJSONString(getHeadersInfo(request)));
		if(seoWapHost.equals(header)){
			return getWAPTop10Index();
		}
		System.out.println(header);

		ModelAndView modle = new ModelAndView(RankTemplate.PC_INDEX.getValue());
		modle.addObject("env",env);
		//1.排行榜首页：获取所有的分类排行榜(手机摄影数码及其子类)
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		modle.addObject("categorys", categorys);

		//2,全网新品排行榜
		if(cache.exists("seonewrank")){
			System.out.println("redis中有seonewrank");
			String seonewrank = cache.get("seonewrank");
			List<Map<String, Object>> seoNewRankMaps = JSON.parseObject(seonewrank, new TypeReference<List<Map<String, Object>>>() {
			});
			modle.addObject("all_newrankMap", seoNewRankMaps);
		}else {
			System.out.println("redis中没有seonewrank");
			List<NewProductRank> allnewrank = gomeNewProductRankService.getTenAllGomeNewProductRank();
			logger.info("新品排行榜数量为"+allnewrank.size());

			//全网新品排行榜获取价格并重新封装
			List<Map<String,Object>> allNewRankMap1 = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> allNewRankMap2 = new ArrayList<Map<String,Object>>();
			HashSet<String> productId = new HashSet<String>();

			for(int i=0;i<allnewrank.size();i++)
			{
				NewProductRank pro = allnewrank.get(i);
				Map<String,Object> info = new HashMap<String,Object>();
				info.put("product",allnewrank.get(i));


				//判断产品上下架状态
				int state = gomeProductService.getProudctState(pro.getProductId());
				if(state != ON_SALE_STATUS)
				{
					continue;
				}
				//获取产品价格
				Double price = null;
				GomePrice gomePrice = priceService.getGomePrice(pro.getSkuId());
				if(gomePrice != null)
				{
					price = gomePrice.getFinalPrice();
				}
				info.put("price", price);

				//上架时间
				info.put("time", PublishUtil.dateToYYYYMMDD(new Date(pro.getPutawayTime())));

				// 商品PC地址
				info.put("purl", getProductPageURL(pro.getProductId(), pro.getSkuId()));

				//获取产品推荐指数
				int recommend = 0;
				recommend = getevaluateNum("99");
				info.put("recommend", recommend);

				//获取产品评论数量
				int commentNum=0;
				String good = "99%";
				GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(pro.getProductId());
				if (goodsAppraiseInfoVO != null) {
					commentNum = goodsAppraiseInfoVO.getTotalNum();
					good = goodsAppraiseInfoVO.getHaoping()+"%";
				}
				info.put("commentNum", commentNum);
				info.put("good", good);
				if(!productId.contains(pro.getProductId())){
					productId.add(pro.getProductId());
					allNewRankMap2.add(info);
				}
			}

			if(allNewRankMap2.size() < 10 && cache.exists("seonewrank")){
				String seonewrank = cache.get("seonewrank");
				allNewRankMap1 = JSON.parseObject(seonewrank, new TypeReference<List<Map<String, Object>>>(){});
				for (Map<String, Object> stringObjectMap : allNewRankMap1) {
					JSONObject product =(JSONObject)stringObjectMap.get("product");
					if(!productId.contains(product.getString("productId"))){
						productId.add(product.getString("productId"));
						allNewRankMap2.add(stringObjectMap);
					}
					if(allNewRankMap2.size() > 10)
						break;
				}
			}
			try{
				String allNewRankMapString = JSON.toJSONString(allNewRankMap2);
				cache.set("seonewrank",allNewRankMapString);
			}catch (Exception e) {
				logger.info("添加缓存失败"+"seonewrank");
				e.printStackTrace();
			}
			modle.addObject("all_newrankMap", allNewRankMap2);
        }

		//3,全网爆品排行榜
		if(cache.exists("seohotrank")){
			System.out.println("redis中有seohotrank");
			String seohotrank = cache.get("seohotrank");
			System.out.println("缓存中的seohotrank为"+seohotrank);
			List<Map<String, Object>> seoHotRankMaps = JSON.parseObject(seohotrank, new TypeReference<List<Map<String, Object>>>() {
			});
			modle.addObject("all_hotrankMap", seoHotRankMaps);
		}else {
			System.out.println("redis中没有seohotrank");
			List<SimpleProduct> allhotrank = gomeSEOSaleRank.getAllGomeSaleRank();
			//全国爆品排行榜获取价格并重新封装
			List<Map<String, Object>> allHotRankMap = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < allhotrank.size(); i++) {
				SimpleProduct pro = allhotrank.get(i);
				Map<String, Object> info = new HashMap<String, Object>();
				info.put("product", allhotrank.get(i));
				//判断产品上下架状态
				int state = gomeProductService.getProudctState(pro.getProductId());
				if (state != ON_SALE_STATUS) {
					continue;
				}

				//获取产品价格
				Double price = null;
				GomePrice gomePrice = priceService.getGomePrice(pro.getSkuId());
				if (gomePrice != null) {
					price = gomePrice.getFinalPrice();
				}
				info.put("price", price);

				//获取产品评论数量
				int commentNum = 0;
				String good = "99%";
				GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(pro.getProductId());
				if (goodsAppraiseInfoVO != null) {
					commentNum = goodsAppraiseInfoVO.getTotalNum();
					good = goodsAppraiseInfoVO.getHaoping() + "%";
				}
				info.put("commentNum", commentNum);
				info.put("good", good);

				// 商品PC地址
				info.put("purl", getProductPageURL(pro.getProductId(), pro.getSkuId()));

				//获取产品推荐指数
				int recommend = 0;
				recommend = getevaluateNum(good);
				info.put("recommend", recommend);

				//获取销量占比
				double saleRate = 0.00;
				saleRate = PublishUtil.getProgressNum(pro.getSaleNum(), allhotrank.get(0).getSaleNum());
				info.put("saleRate", saleRate);

				allHotRankMap.add(info);
			}
			modle.addObject("all_hotrankMap", allHotRankMap);
            try {
                cache.set("seohotrank",JSON.toJSONString(allHotRankMap));
            } catch (Exception e) {
                logger.info("添加缓存失败"+"seohotrank");
                e.printStackTrace();
            }
        }

        //4.三级分类排行榜
		List<RankIndexFloor> floors = rankManager.queryRankIndexShowFloor();
		System.out.println(floors.size());
		System.out.println(JSON.toJSONString(floors));
		  //楼层展示信息
		List<Map<String, Object>> floorinfos = new ArrayList<Map<String, Object>>();
		for (RankIndexFloor floor : floors) {
			String categoryId3 = floor.getCategoryId();
			//获取分类cid
			Category cat = categoryManager.getById(categoryId3);
			if (categoryId3 != null) {
				List<Map<String, Object>> list_hot_product = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> list_brand_product = new ArrayList<Map<String, Object>>();
				if(cache.exists("seocategoryhot"+categoryId3) && cache.exists("seocategorylistbrand"+categoryId3)){
					System.out.println("redis存在排行榜"+"seocategoryhot"+categoryId3);
					String seocategoryhot = cache.get("seocategoryhot" + categoryId3);
					String seocategorylistbrand = cache.get("seocategorylistbrand"+categoryId3);
					list_hot_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
					});
					list_brand_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
					});
				}else {
					System.out.println("redis不存在存在排行榜"+"seocategoryhot"+categoryId3);
					continue;
				}
				List<Map<String, Object>> newSalerankMap = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list_hot_product.size(); i++) {
					Map<String, Object> stringObjectMap = list_hot_product.get(i);
					Map<String, Object> info = new HashMap<String, Object>();
					//获取产品推荐指数
					int recommend = 0;
					recommend = getevaluateNum("99");
					info.put("recommend", recommend);
					info.put("purl",stringObjectMap.get("purl"));
					info.put("skuName",stringObjectMap.get("pn"));
					info.put("iurl",stringObjectMap.get("iurl"));
					info.put("price",stringObjectMap.get("price"));
					//搜索接口查出的评论数
					//info.put("commentNum",stringObjectMap.get("evaluateCount"));
					//dubbo接口查出的评论数
					int commentNum = 0;
					GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(stringObjectMap.get("pid").toString());
					if (goodsAppraiseInfoVO != null) {
						commentNum = goodsAppraiseInfoVO.getTotalNum();
					}
					info.put("commentNum", commentNum);
					newSalerankMap.add(info);
				}
				Map<String, Object> maxSaleBrand = list_brand_product.get(1);
				List<Map<String, Object>> newBrandrankMap = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list_brand_product.size(); i++) {
					Map<String, Object> stringObjectMap = list_hot_product.get(i);
					Map<String, Object> brankMap = new HashMap<String, Object>();
					//获取销量比列
					double saleRate = 0.00;
					saleRate = PublishUtil.getProgressNum((Integer) stringObjectMap.get("brandSaleNum"), (Integer) maxSaleBrand.get("brandSaleNum"));
					brankMap.put("saleRate", saleRate);

					//查询品牌fasctid
					String facetid = prodSpecService.getFacetIdByBrandId(Long.parseLong(stringObjectMap.get("brandId").toString()));
					brankMap.put("facetid", facetid);
					newBrandrankMap.add(brankMap);
				}

				Map<String, Object> floorinfo = new HashMap<String, Object>();
				floorinfo.put("categoryId3", categoryId3);
				floorinfo.put("cat", cat);
				floorinfo.put("rank_saleMap", newSalerankMap);
				floorinfo.put("rank_brandMap", newBrandrankMap);
				floorinfo.put("categoryName", floor.getCategoryName());
				floorinfos.add(floorinfo);
			}
		}
		modle.addObject("index_floors", floorinfos);

//		//查询三级分类排行榜楼层
//		if(cache.exists("seoindexfloorsrank")){
//			System.out.println("redis中有seoindexfloorsrank");
//			String seoindexfloorsrank = cache.get("seoindexfloorsrank");
//			List<Map<String, Object>> seoIndexFloorsRankMaps = JSON.parseObject(seoindexfloorsrank, new TypeReference<List<Map<String, Object>>>() {
//			});
//			modle.addObject("index_floors", seoIndexFloorsRankMaps);
//		}else {
//			System.out.println("redis中没有seoindexfloorsrank");
//			List<RankIndexFloor> floors = rankManager.queryRankIndexShowFloor();
//			System.out.println(floors.size());
//			System.out.println(JSON.toJSONString(floors));
//			//楼层展示信息
//			List<Map<String, Object>> floorinfos = new ArrayList<Map<String, Object>>();
//			for (RankIndexFloor floor : floors) {
//				String categoryId3 = floor.getCategoryId();
//				//获取分类cid
//				Category cat = categoryManager.getById(categoryId3);
//				if (categoryId3 != null) {
////					//商品销量Top10  最多查50条最多显示10条
////					List<SimpleProduct> salerank = gomeSEOSaleRank.getProdctThirdCateRank(categoryId3);
////					//品牌销量Top10
////					List<SeoBrandSale> brandrank = gomeSEOSaleRank.getBrandSalesRank(categoryId3);
//					List<Map<String, Object>> list_hot_product = new ArrayList<Map<String, Object>>();
//					List<Map<String, Object>> list_brand_product = new ArrayList<Map<String, Object>>();
//					if(cache.exists("seocategoryhot"+categoryId3) && cache.exists("seocategorylistbrand"+categoryId3)){
//						System.out.println("redis存在排行榜"+"seocategoryhot"+categoryId3);
//						String seocategoryhot = cache.get("seocategoryhot" + categoryId3);
//						String seocategorylistbrand = cache.get("seocategorylistbrand"+categoryId3);
//						list_hot_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
//						});
//						list_brand_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
//						});
//					}else {
//						System.out.println("redis不存在存在排行榜"+"seocategoryhot"+categoryId3);
//						continue;
//					}
//					List<Map<String, Object>> newSalerankMap = new ArrayList<Map<String, Object>>();
//					for (int i = 0; i < list_hot_product.size(); i++) {
//						Map<String, Object> stringObjectMap = list_hot_product.get(i);
//						Map<String, Object> info = new HashMap<String, Object>();
//						//获取产品推荐指数
//						int recommend = 0;
//						recommend = getevaluateNum("99");
//						info.put("recommend", recommend);
//						info.put("purl",stringObjectMap.get("purl"));
//						info.put("skuName",stringObjectMap.get("pn"));
//						info.put("iurl",stringObjectMap.get("iurl"));
//						info.put("price",stringObjectMap.get("price"));
//						//搜索接口查出的评论数
//						//info.put("commentNum",stringObjectMap.get("evaluateCount"));
//						//dubbo接口查出的评论数
//						int commentNum = 0;
//						GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(stringObjectMap.get("pid").toString());
//						if (goodsAppraiseInfoVO != null) {
//							commentNum = goodsAppraiseInfoVO.getTotalNum();
//						}
//						info.put("commentNum", commentNum);
//						newSalerankMap.add(info);
//					}
////					List<Map<String, Object>> salerankMap = new ArrayList<Map<String, Object>>();
////					for (int i = 0; i < salerank.size(); i++) {
////						SimpleProduct pro = salerank.get(i);
////						Map<String, Object> info = new HashMap<String, Object>();
////						info.put("product", pro);
////						int state = gomeProductService.getProudctState(pro.getProductId());
////						if (state != ON_SALE_STATUS) {
////							continue;
////						}
////
////						Double price = null;
////						GomePrice gomePrice = priceService.getGomePrice(pro.getSkuId());
////						if (gomePrice != null) {
////							price = gomePrice.getFinalPrice();
////						}
////						info.put("price", price);
////
////						//获取产品评论数量
////						int commentNum = 0;
////						GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(pro.getProductId());
////						if (goodsAppraiseInfoVO != null) {
////							commentNum = goodsAppraiseInfoVO.getTotalNum();
////						}
////						info.put("commentNum", commentNum);
////
////						//获取产品推荐指数
////						int recommend = 0;
////						recommend = getevaluateNum("99");
////						info.put("recommend", recommend);
////
////						// 商品PC地址
////						info.put("purl", getProductPageURL(pro.getProductId(), pro.getSkuId()));
////
////						salerankMap.add(info);
////					}
//					Map<String, Object> maxSaleBrand = list_brand_product.get(1);
//					List<Map<String, Object>> newBrandrankMap = new ArrayList<Map<String, Object>>();
//					for (int i = 0; i < list_brand_product.size(); i++) {
//						Map<String, Object> stringObjectMap = list_hot_product.get(i);
//						Map<String, Object> brankMap = new HashMap<String, Object>();
//						//获取销量比列
//						double saleRate = 0.00;
//						saleRate = PublishUtil.getProgressNum((Integer) stringObjectMap.get("brandSaleNum"), (Integer) maxSaleBrand.get("brandSaleNum"));
//						brankMap.put("saleRate", saleRate);
//
//						//查询品牌fasctid
//						String facetid = prodSpecService.getFacetIdByBrandId(Long.parseLong(stringObjectMap.get("brandId").toString()));
//						brankMap.put("facetid", facetid);
//						newBrandrankMap.add(brankMap);
//					}
//
////					List<Map<String, Object>> brandrankMap = new ArrayList<Map<String, Object>>();
////					for (SeoBrandSale info : brandrank) {
////						Map<String, Object> brankMap = new HashMap<String, Object>();
////						brankMap.put("brandSale", info);
////
////						//获取销量比列
////						double saleRate = 0.00;
////						saleRate = PublishUtil.getProgressNum(info.getBrandSaleNum(), brandrank.get(0).getBrandSaleNum());
////						brankMap.put("saleRate", saleRate);
////						//查询品牌fasctid
////						String facetid = prodSpecService.getFacetIdByBrandId(Long.parseLong(info.getBrandId()));
////						brankMap.put("facetid", facetid);
////						brandrankMap.add(brankMap);
////					}
//
//					Map<String, Object> floorinfo = new HashMap<String, Object>();
//					floorinfo.put("categoryId3", categoryId3);
//					floorinfo.put("cat", cat);
//					floorinfo.put("rank_saleMap", newSalerankMap);
//					floorinfo.put("rank_brandMap", newBrandrankMap);
//					floorinfo.put("categoryName", floor.getCategoryName());
//					floorinfos.add(floorinfo);
//				}
//			}
//			modle.addObject("index_floors", floorinfos);
//            try {
//                cache.set("seoindexfloorsrank",JSON.toJSONString(floorinfos));
//            } catch (Exception e) {
//                logger.info("添加缓存失败"+"seoindexfloorsrank");
//                e.printStackTrace();
//            }
//        }

		//热门专题
		List<Titles> hottitle = rankManager.findCurrentTitlesByCreateTime(new Date(), 8);
		System.out.println("热门主题的数量为"+hottitle.size());
		modle.addObject("hot_title", hottitle);

		//最新商品评论
		if(cache.exists("seohotcommentsrank")){
			System.out.println("redis中有seohotcommentsrank");
			String seohotcommentsrank = cache.get("seohotcommentsrank");
			List<Map<String, Object>> seoHotCommentsRankMaps = JSON.parseObject(seohotcommentsrank, new TypeReference<List<Map<String, Object>>>() {
			});
			modle.addObject("hot_comment", seoHotCommentsRankMaps);
		}else {
			System.out.println("redis中没有seohotcommentsrank");
			List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getTop10AppraiseSEO(30);
			ArrayList<AppraiseSEO> appraiseSEOS = new ArrayList<AppraiseSEO>();
			if(null != appraiseSEOList && !appraiseSEOList.isEmpty()){
				for (AppraiseSEO appraiseSEO : appraiseSEOList) {
					  if(appraiseSEO.getContent().length() > 9){
						  appraiseSEOS.add(appraiseSEO);
					  }
					  if(appraiseSEOS.size() == 5){
					  	  break;
					  }
				}
			}
			modle.addObject("hot_comment", appraiseSEOS);
            try {
                cache.set("seohotcommentsrank",JSON.toJSONString(appraiseSEOS));
            } catch (Exception e) {
                logger.info("添加缓存失败"+"seohotcommentsrank");
                e.printStackTrace();
            }
        }
		return modle;
	}



	/*排行榜列表页
	 *  /topic10/1
	 */
	@RequestMapping(value = "/{content}", method = RequestMethod.GET)
	public ModelAndView getPCTopic10List(HttpServletRequest request, HttpServletResponse response,
										 @PathVariable(value = "content",required = true) String content) {
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		String seoWapHost = WAP_HOST_LIVE;
		if(!"live".equals(env)){
			seoWapHost = WAP_HOST_UAT;
		}
		String header = request.getHeader("host");
		System.out.println("当前域名为"+header);
		//属于移动端的交给移动端逻辑处理
		if(seoWapHost.equals(header)){
			return getWAPTop10List(content);
		}
		System.out.println(header);

		if(content.indexOf("-") > -1){
			if(content.indexOf("listbrand") > -1){
				return new ModelAndView("forward:/newtop10/listbrand/"+content);
			}
			return new ModelAndView("forward:/newtop10/facet/"+content);
		}

		ModelAndView model = new ModelAndView(RankTemplate.PC_LIST.getValue());
		model.addObject("env",env);
		//1.排行榜首页：获取所有的分类排行榜(手机摄影数码及其子类)
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		model.addObject("categorys", categorys);
		//2.根据三级分类ID查询当前页面的三级分类信息
		Category category = categoryManager.getByCid(content);
		model.addObject("rank_category_info", category);
		//3.根据当前三级分类ID查询期对应的筛选项信息   品牌  价格  功能特点是固定死的  每个分类下都是这几个筛选项
		//品牌筛选项   先根据三级分类catxxxid 去查找对应的筛选项
		//获取筛选项  再根据是想象的groupid去查找对应下面的具体内容
		List<FacetGroupItem> facets_brand = null;
		List<FacetGroupItem> facets_price = null;
		List<FacetGroupItem> facets_other = null;
		//品牌筛选项
		if(cache.exists("seofacetsbrand"+category.getId())){
			String seofacetsbrand = cache.get("seofacetsbrand"+category.getId());
			if(!seofacetsbrand.equals("no")){
				facets_brand = JSON.parseObject(seofacetsbrand, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_brand", facets_brand);
				System.out.println("缓存中有seofacetsbrand"+category.getId());
			}
		}else {
			facets_brand = queryFacetsListByGroupType(1, category.getId());
            try {
				if(facets_brand == null || facets_brand.isEmpty()){
					cache.set("seofacetsbrand"+category.getId(),"no");
				}else{
					model.addObject("facets_brand", facets_brand);
					cache.set("seofacetsbrand"+category.getId(),JSON.toJSONString(facets_brand));
				}
            }catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsbrand"+category.getId());
                e.printStackTrace();
            }
            System.out.println("缓存中没有seofacetsbrand"+category.getId());
		}
		//价格筛选项
		if(cache.exists("seofacetsprice"+category.getId())){
			String seofacetsprice = cache.get("seofacetsprice"+category.getId());
			if(!seofacetsprice.equals("no")){
				facets_price = JSON.parseObject(seofacetsprice, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_price", facets_price);
				System.out.println("缓存中有seofacetsprice,且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsprice,且为no"+category.getId());
			}
		}else {
			facets_price = queryFacetsListByGroupType(2, category.getId());
			System.out.println("价格筛选项"+JSON.toJSONString(facets_price));
			try {
				if(facets_price == null || facets_price.isEmpty()){
					cache.set("seofacetsprice"+category.getId(),"no");
				}else{
					model.addObject("facets_price", facets_price);
					cache.set("seofacetsprice"+category.getId(),JSON.toJSONString(facets_price));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsprice"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsprice"+category.getId());
		}
		//功能特点筛选项
		if(cache.exists("seofacetsother"+category.getId())){
			String seofacetsother = cache.get("seofacetsother"+category.getId());
			if(!seofacetsother.equals("no")){
				facets_other = JSON.parseObject(seofacetsother, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_other", facets_other);
				System.out.println("缓存中有seofacetsother且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsother且为no"+category.getId());
			}
		}else {
			facets_other = queryFacetsListByGroupType(3, category.getId());
			try {
				if(facets_other == null || facets_other.isEmpty()){
					cache.set("facets_other"+category.getId(),"no");
				}else{
					model.addObject("facets_other", facets_other);
					cache.set("facets_other"+category.getId(),JSON.toJSONString(facets_other));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsother"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsother"+category.getId());
		}

		//新品排行榜
		if(cache.exists("seocategorynew"+category.getId())){
			String seocategorynew = cache.get("seocategorynew" + category.getId());
			List<Map<String, Object>> list_new_product = JSON.parseObject(seocategorynew, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_new_product", list_new_product);
			System.out.println("缓存中有seocategorynew"+category.getId());
		}else {
			List<Map<String, Object>> list_new_product = queryNewSaleInfosByCat3Id(category.getId());
			model.addObject("list_new_product", list_new_product);
			try {
				cache.set("seocategorynew"+category.getId(),JSON.toJSONString(list_new_product));
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seocategorynew"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seocategorynew"+category.getId());
		}

		//爆品排行榜
		if(cache.exists("seocategoryhot"+category.getId())){
			String seocategoryhot = cache.get("seocategoryhot" + category.getId());
			List<Map<String, Object>> list_hot_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_hot_product", list_hot_product);
			System.out.println("缓存中有seocategoryhot"+category.getId());
		}else {
			//List<Map<String, Object>> list_hot_product = rankManager.queryHotSaleInfosByCat3Id(category.getId());
			List<Map<String, Object>> list_hot_product = getHotSaleInfoByCat3IdFromSearch(category.getCategoryName());

			model.addObject("list_hot_product", list_hot_product);
			try {
				cache.set("seocategoryhot"+category.getId(),JSON.toJSONString(list_hot_product));
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seocategoryhot"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seocategoryhot"+category.getId());
		}

		//各个纬度展示商品数据
        if(cache.exists("seocategoryfacet"+category.getId())){
			String seocategoryfacet = cache.get("seocategoryfacet" + category.getId());
			List<Map<String, Object>> list_facets_product = JSON.parseObject(seocategoryfacet, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_facets_product", list_facets_product);
			System.out.println("缓存中有seocategoryfacet"+category.getId());
		}else {
			List<Map<String, Object>> list_facets_product = new ArrayList<Map<String, Object>>();
			//品牌纬度
			Map<String, Object> brandProductInfo = getFacetRankInfo("brand", "品牌", facets_brand,category.getCategoryName());
			if(null != brandProductInfo){
				list_facets_product.add(brandProductInfo);
				System.out.println("查询的品牌排行榜不为空");
			}
			//功能特点纬度
			Map<String, Object> otherProductInfo = getFacetRankInfo("other", "功能特点", facets_other,category.getCategoryName());
			if(null != otherProductInfo){
				list_facets_product.add(otherProductInfo);
				System.out.println("查询的功能特点品牌排行榜不为空");
			}
			//价格纬度
			Map<String, Object> priceProductInfo = getFacetRankInfo("price", "价格", facets_price,category.getCategoryName());
			if(null != priceProductInfo){
				list_facets_product.add(priceProductInfo);
				System.out.println("查询的价格排行榜不为空");
			}
			model.addObject("list_facets_product", list_facets_product);
			try {
				cache.set("seocategoryfacet"+category.getId(),JSON.toJSONString(list_facets_product));
			}catch (Exception e){
				logger.info("添加缓存失败"+"seocategoryfacet"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seocategoryfacet"+category.getId());
		}

        //进度条进度计算
		if(cache.exists("seocategorylistbrand"+category.getId())){
			String seocategorylistbrand = cache.get("seocategorylistbrand" + category.getId());
			List<Map<String, Object>> list_brand = JSON.parseObject(seocategorylistbrand, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_brand", list_brand);
			System.out.println("缓存中有seocategorylistbrand"+category.getId());
		}else {
			List<SeoBrandSale> querylist = gomeSEOSaleRank.getBrandSalesRank(category.getId());
			if (querylist != null && !querylist.isEmpty()) {
				List<Map<String, Object>> brandlist = new ArrayList<Map<String, Object>>();
				//进度条进度计算
				int maxSaleNum = querylist.get(0).getBrandSaleNum();
				for (SeoBrandSale saleinfo : querylist) {
					Map<String, Object> info = new HashMap<String, Object>();
					//进度条进度
					info.put("progressNum", PublishUtil.getProgressNum(saleinfo.getBrandSaleNum(), maxSaleNum));
					info.put("brandId", saleinfo.getBrandId());
					info.put("brandLogoUrl", saleinfo.getBrandLogoUrl());
					info.put("brandName", saleinfo.getBrandName());
					info.put("brandSaleNum", saleinfo.getBrandSaleNum());
					String facetid = prodSpecService.getFacetIdByBrandId(Long.parseLong(saleinfo.getBrandId()));
					info.put("facetid", facetid);
					brandlist.add(info);
				}
				model.addObject("list_brand", brandlist);
				try {
					cache.set("seocategorylistbrand"+category.getId(),JSON.toJSONString(brandlist));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategorylistbrand"+category.getId());
					e.printStackTrace();
				}
				System.out.println("缓存中没有seocategorylistbrand"+category.getId());
			}else {
				System.out.println("缓存中没有seocategorylistbrand，且查询为null");
			}
		}

		List<GroupHotLinks> list = groupHotLinksManager.getGroupHotLinksListbygroupid(category.getId());
		if(list==null || list.isEmpty()){
			list = groupHotLinksManager.getGroupHotLinksListbygroupid(category.getParentId());
		}
		model.addObject("list_hot_links", list);
		TypeReference<Object> objectTypeReference = new TypeReference<Object>(){};
		return model;
	}


	/*排行榜筛选页  /newtop10/new-cid    t_category表的cid值
	                /newtop10/hot-cid
	                /newtop10/brand-id    t_facetgroupitem的id值
	                /newtop10/price-id    t_facetgroupitem的id值
	                /newtop10/other-id    t_facetgroupitem的id值
	  brand说明是品牌的筛选项 1是t_facetgroupitem表的id
	 *  /topic10
	 */
	@RequestMapping(value = "/facet/{content}", method = RequestMethod.GET)
	public ModelAndView getPCTopic10Facet(HttpServletRequest request, HttpServletResponse response,
										  @PathVariable(value = "content",required = true) String content
	) {
		String pageType = content.substring(0,content.indexOf("-"));
		String id = content.substring(content.indexOf("-")+1,content.length());
		Category category = null;
		FacetGroupItem facetGroupItem = null;
		//1.hot new 获取分类对象   brand price other 获取筛选对象
		if(pageType.equals("hot") || pageType.equals("new")){
			category = categoryManager.getByCid(id);
		}else {
			//2.通过页面的facet的id值反向获取对应三级分类
			facetGroupItem = facetGroupItemManager.getById(id);
			category = categoryManager.getById(facetGroupItem.getCatId());
		}
		ModelAndView model = new ModelAndView(RankTemplate.PC_FACET.getValue());
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		model.addObject("env",env);
		//2.获取所有的分类排行榜(手机摄影数码及其子类)
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		model.addObject("categorys", categorys);
		model.addObject("rank_category_info", category);
		//3.根据当前三级分类ID查询期对应的筛选项信息   品牌  价格  功能特点是固定死的  每个分类下都是这几个筛选项
		//品牌筛选项   先根据三级分类catxxxid 去查找对应的筛选项
		//获取筛选项  再根据是想象的groupid去查找对应下面的具体内容
		List<FacetGroupItem> facets_brand = null;
		List<FacetGroupItem> facets_price = null;
		List<FacetGroupItem> facets_other = null;
		//品牌筛选项
		if(cache.exists("seofacetsbrand"+category.getId())){
			String seofacetsbrand = cache.get("seofacetsbrand"+category.getId());
			if(!seofacetsbrand.equals("no")){
				facets_brand = JSON.parseObject(seofacetsbrand, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_brand", facets_brand);
				System.out.println("缓存中有seofacetsbrand"+category.getId());
			}
		}else {
			facets_brand = queryFacetsListByGroupType(1, category.getId());
			try {
				if(facets_brand == null || facets_brand.isEmpty()){
					cache.set("seofacetsbrand"+category.getId(),"no");
				}else{
					model.addObject("facets_brand", facets_brand);
					cache.set("seofacetsbrand"+category.getId(),JSON.toJSONString(facets_brand));
				}
			}catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsbrand"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsbrand"+category.getId());
		}
		//价格筛选项
		if(cache.exists("seofacetsprice"+category.getId())){
			String seofacetsprice = cache.get("seofacetsprice"+category.getId());
			if(!seofacetsprice.equals("no")){
				facets_price = JSON.parseObject(seofacetsprice, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_price", facets_price);
				System.out.println("缓存中有seofacetsprice,且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsprice,且为no"+category.getId());
			}
		}else {
			facets_price = queryFacetsListByGroupType(2, category.getId());
			System.out.println("价格筛选项"+JSON.toJSONString(facets_price));
			try {
				if(facets_price == null || facets_price.isEmpty()){
					cache.set("seofacetsprice"+category.getId(),"no");
				}else{
					model.addObject("facets_price", facets_price);
					cache.set("seofacetsprice"+category.getId(),JSON.toJSONString(facets_price));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsprice"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsprice"+category.getId());
		}
		//功能特点筛选项
		if(cache.exists("seofacetsother"+category.getId())){
			String seofacetsother = cache.get("seofacetsother"+category.getId());
			if(!seofacetsother.equals("no")){
				facets_other = JSON.parseObject(seofacetsother, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_other", facets_other);
				System.out.println("缓存中有seofacetsother且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsother且为no"+category.getId());
			}
		}else {
			facets_other = queryFacetsListByGroupType(3, category.getId());
			try {
				if(facets_other == null || facets_other.isEmpty()){
					cache.set("facets_other"+category.getId(),"no");
				}else{
					model.addObject("facets_other", facets_other);
					cache.set("facets_other"+category.getId(),JSON.toJSONString(facets_other));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsother"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsother"+category.getId());
		}

         //热门链接
		List<GroupHotLinks> list = groupHotLinksManager.getGroupHotLinksListbygroupid(category.getId());
		if(list==null || list.isEmpty()){
			list = groupHotLinksManager.getGroupHotLinksListbygroupid(category.getParentId());
		}
		model.addObject("list_hot_links", list);


		if(pageType.equals("hot")) {
			//查询最热商品
//			List<Map<String, Object>> saleList = rankManager.queryHotSaleInfosByCat3Id(category.getId());
			List<Map<String, Object>> list_hot_product = null;
			if(cache.exists("seocategoryhot"+category.getId())){
				String seocategoryhot = cache.get("seocategoryhot" + category.getId());
				list_hot_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
				});
				System.out.println("缓存中有seocategoryhot"+category.getId());
			}else {
				list_hot_product = rankManager.queryHotSaleInfosByCat3Id(category.getId());
				model.addObject("list_hot_product", list_hot_product);
				try {
					cache.set("seocategoryhot"+category.getId(),JSON.toJSONString(list_hot_product));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategoryhot"+category.getId());
					e.printStackTrace();
				}
				System.out.println("缓存中没有seocategoryhot"+category.getId());
			}
			model.addObject("list_saleinfo", list_hot_product);
			//筛选项名
			model.addObject("facetName", "爆品");
			model.addObject("pageType", "hot");
			model.addObject("facetInfo", category);
		}else if(pageType.equals("new")){
			//查询最热商品
//			List<Map<String, Object>> saleList = rankManager.queryNewSaleInfosByCat3Id(category.getId());
			List<Map<String, Object>> list_new_product = null;
			//新品排行榜
			if(cache.exists("seocategorynew"+category.getId())){
				String seocategorynew = cache.get("seocategorynew" + category.getId());
				list_new_product = JSON.parseObject(seocategorynew, new TypeReference<List<Map<String, Object>>>() {
				});
				System.out.println("缓存中有seocategorynew"+category.getId());
			}else {
				list_new_product = rankManager.queryNewSaleInfosByCat3Id(category.getId());
				model.addObject("list_new_product", list_new_product);
				try {
					cache.set("seocategorynew"+category.getId(),JSON.toJSONString(list_new_product));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategorynew"+category.getId());
					e.printStackTrace();
				}
				System.out.println("缓存中没有seocategorynew"+category.getId());
			}
			model.addObject("list_saleinfo", list_new_product);
			model.addObject("facetName", "新品");
			model.addObject("pageType", "new");
			model.addObject("facetInfo", category);
		}else {
			//查询筛选的商品
//			List<Map<String, Object>> saleList = rankManager.querySaleInfosByFacetId(category.getId(), facetGroupItem.getFacetId());
			List<Map<String, Object>> saleList = null;
			if(cache.exists("seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId())){
				String seofacetproduct = cache.get("seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
				saleList = JSON.parseObject(seofacetproduct, new TypeReference<List<Map<String, Object>>>() {
				});
				System.out.println("缓存中有seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
			}else{
				saleList = querySaleInfosByFacetIdFromSearch(category.getCategoryName(),facetGroupItem.getFacetId());
				System.out.println("缓存中没有seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
			}
			model.addObject("list_saleinfo", saleList);
			//筛选项名
			model.addObject("facetName", facetGroupItem.getValue());
			model.addObject("pageType", pageType);
			model.addObject("facetInfo", facetGroupItem);
		}
		return model;
	}

	/*排行榜品牌页  /top10/listbrand-cid.html
	 *  /topic10
	 */
	@RequestMapping(value = "/listbrand/{content}", method = RequestMethod.GET)
	public ModelAndView getPCTopic10ListBrand(HttpServletRequest request, HttpServletResponse response,
											  @PathVariable(value = "content",required = true) String content
	) {
		String cid = content.substring(content.indexOf("-")+1,content.length());
		ModelAndView model = new ModelAndView(RankTemplate.PC_BRAND.getValue());
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		model.addObject("env",env);
		//1.排行榜首页：获取所有的分类排行榜(手机摄影数码及其子类)
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		model.addObject("categorys", categorys);
		//2.通过页面的facet的id值反向获取对应三级分类
		Category category = categoryManager.getByCid(cid);
		model.addObject("rank_category_info", category);
		//3.根据当前三级分类ID查询期对应的筛选项信息   品牌  价格  功能特点是固定死的  每个分类下都是这几个筛选项
		//品牌筛选项   先根据三级分类catxxxid 去查找对应的筛选项
		//获取筛选项  再根据是想象的groupid去查找对应下面的具体内容
		List<FacetGroupItem> facets_brand = null;
		List<FacetGroupItem> facets_price = null;
		List<FacetGroupItem> facets_other = null;
		//品牌筛选项
		if(cache.exists("seofacetsbrand"+category.getId())){
			String seofacetsbrand = cache.get("seofacetsbrand"+category.getId());
			if(!seofacetsbrand.equals("no")){
				facets_brand = JSON.parseObject(seofacetsbrand, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_brand", facets_brand);
				System.out.println("缓存中有seofacetsbrand"+category.getId());
			}
		}else {
			facets_brand = queryFacetsListByGroupType(1, category.getId());
			try {
				if(facets_brand == null || facets_brand.isEmpty()){
					cache.set("seofacetsbrand"+category.getId(),"no");
				}else{
					model.addObject("facets_brand", facets_brand);
					cache.set("seofacetsbrand"+category.getId(),JSON.toJSONString(facets_brand));
				}
			}catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsbrand"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsbrand"+category.getId());
		}
		//价格筛选项
		if(cache.exists("seofacetsprice"+category.getId())){
			String seofacetsprice = cache.get("seofacetsprice"+category.getId());
			if(!seofacetsprice.equals("no")){
				facets_price = JSON.parseObject(seofacetsprice, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_price", facets_price);
				System.out.println("缓存中有seofacetsprice,且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsprice,且为no"+category.getId());
			}
		}else {
			facets_price = queryFacetsListByGroupType(2, category.getId());
			System.out.println("价格筛选项"+JSON.toJSONString(facets_price));
			try {
				if(facets_price == null || facets_price.isEmpty()){
					cache.set("seofacetsprice"+category.getId(),"no");
				}else{
					model.addObject("facets_price", facets_price);
					cache.set("seofacetsprice"+category.getId(),JSON.toJSONString(facets_price));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsprice"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsprice"+category.getId());
		}
		//功能特点筛选项
		if(cache.exists("seofacetsother"+category.getId())){
			String seofacetsother = cache.get("seofacetsother"+category.getId());
			if(!seofacetsother.equals("no")){
				facets_other = JSON.parseObject(seofacetsother, new TypeReference<List<FacetGroupItem>>() {
				});
				model.addObject("facets_other", facets_other);
				System.out.println("缓存中有seofacetsother且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsother且为no"+category.getId());
			}
		}else {
			facets_other = queryFacetsListByGroupType(3, category.getId());
			try {
				if(facets_other == null || facets_other.isEmpty()){
					cache.set("facets_other"+category.getId(),"no");
				}else{
					model.addObject("facets_other", facets_other);
					cache.set("facets_other"+category.getId(),JSON.toJSONString(facets_other));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsother"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsother"+category.getId());
		}

		List<GroupHotLinks> list = groupHotLinksManager.getGroupHotLinksListbygroupid(category.getId());
		if(list==null || list.isEmpty()){
			list = groupHotLinksManager.getGroupHotLinksListbygroupid(category.getParentId());
		}
		model.addObject("list_hot_links", list);


		//进度条进度计算
		if(cache.exists("seocategorylistbrand"+category.getId())){
			String seocategorylistbrand = cache.get("seocategorylistbrand" + category.getId());
			List<Map<String, Object>> list_brand = JSON.parseObject(seocategorylistbrand, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_brand", list_brand);
			System.out.println("缓存中有seocategorylistbrand"+category.getId());
		}else {
			List<SeoBrandSale> querylist = gomeSEOSaleRank.getBrandSalesRank(category.getId());
			if (querylist != null && !querylist.isEmpty()) {
				List<Map<String, Object>> brandlist = new ArrayList<Map<String, Object>>();
				//进度条进度计算
				int maxSaleNum = querylist.get(0).getBrandSaleNum();
				for (SeoBrandSale saleinfo : querylist) {
					Map<String, Object> info = new HashMap<String, Object>();
					//进度条进度
					info.put("progressNum", PublishUtil.getProgressNum(saleinfo.getBrandSaleNum(), maxSaleNum));
					info.put("brandId", saleinfo.getBrandId());
					info.put("brandLogoUrl", saleinfo.getBrandLogoUrl());
					info.put("brandName", saleinfo.getBrandName());
					info.put("brandSaleNum", saleinfo.getBrandSaleNum());
					String facetid = prodSpecService.getFacetIdByBrandId(Long.parseLong(saleinfo.getBrandId()));
					info.put("facetid", facetid);
					brandlist.add(info);
				}
				model.addObject("list_brand", brandlist);
				try {
					cache.set("seocategorylistbrand"+category.getId(),JSON.toJSONString(brandlist));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategorylistbrand"+category.getId());
					e.printStackTrace();
				}
			}
			System.out.println("缓存中没有seocategorylistbrand"+category.getId());
		}
		return model;
	}


	public ModelAndView getWAPTop10Index(){
		ModelAndView model = new ModelAndView("vm_rank/wap_topic_index");
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		model.addObject("env",env);
		//1.排行榜首页：获取所有的分类排行榜(手机摄影数码及其子类)
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		model.addObject("list",categorys);
		//2.最新评论
		//最新商品评论
		if(cache.exists("seohotcommentsrank")){
			System.out.println("redis中有seohotcommentsrank");
			String seohotcommentsrank = cache.get("seohotcommentsrank");
			List<Map<String, Object>> seoHotCommentsRankMaps = JSON.parseObject(seohotcommentsrank, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("hot_comment", seoHotCommentsRankMaps);
		}else {
			System.out.println("redis中没有seohotcommentsrank");
			List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getTop10AppraiseSEO(30);
			ArrayList<AppraiseSEO> appraiseSEOS = new ArrayList<AppraiseSEO>();
			if(null != appraiseSEOList && !appraiseSEOList.isEmpty()){
				for (AppraiseSEO appraiseSEO : appraiseSEOList) {
					if(appraiseSEO.getContent().length() > 9){
						appraiseSEOS.add(appraiseSEO);
					}
					if(appraiseSEOS.size() == 5){
						break;
					}
				}
			}
			model.addObject("hot_comment", appraiseSEOS);
			try {
				cache.set("seohotcommentsrank",JSON.toJSONString(appraiseSEOS));
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seohotcommentsrank");
				e.printStackTrace();
			}
		}
		//3.热门专题
		List<Titles> hottitle = rankManager.findCurrentTitlesByCreateTime(new Date(), 8);
		System.out.println("热门主题的数量为"+hottitle.size());
		model.addObject("hot_title", hottitle);
		//4.热门排行榜
		List<RankIndexFloor> floors = rankManager.queryRankIndexShowFloor();
		if(null != floors){
			for (RankIndexFloor floor : floors) {
				String categoryId = floor.getCategoryId();
				Category byId = categoryManager.getById(categoryId);
				floor.setCategoryId(byId.getCid()+"");
			}
		}
		model.addObject("hot_rank", floors);
		return model;
	}

	public ModelAndView getWAPTop10List(String content){
		if(content.indexOf("-") > -1){
			return getWAPTop10Filter(content);
		}

		ModelAndView model = new ModelAndView("vm_rank/wap_topic_list");
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		model.addObject("env",env);
		//1.根据三级分类ID查询当前页面的三级分类信息
		Category category = categoryManager.getByCid(content);
		model.addObject("rank_key",category);
		model.addObject("r_modId",category.getCid()/1000);

		//2.新品排行榜
		if(cache.exists("seocategorynew"+category.getId())){
			String seocategorynew = cache.get("seocategorynew" + category.getId());
			List<Map<String, Object>> list_new_product = JSON.parseObject(seocategorynew, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_new_product", list_new_product);
			System.out.println("缓存中有seocategorynew"+category.getId());
		}else {
			List<Map<String, Object>> list_new_product = queryNewSaleInfosByCat3Id(category.getId());
			model.addObject("list_new_product", list_new_product);
			try {
				cache.set("seocategorynew"+category.getId(),JSON.toJSONString(list_new_product));
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seocategorynew"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seocategorynew"+category.getId());
		}

		//3.爆品排行榜
		if(cache.exists("seocategoryhot"+category.getId())){
			String seocategoryhot = cache.get("seocategoryhot" + category.getId());
			List<Map<String, Object>> list_hot_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_hot_product", list_hot_product);
			System.out.println("缓存中有seocategoryhot"+category.getId());
		}else {
			//List<Map<String, Object>> list_hot_product = rankManager.queryHotSaleInfosByCat3Id(category.getId());
			List<Map<String, Object>> list_hot_product = getHotSaleInfoByCat3IdFromSearch(category.getCategoryName());

			model.addObject("list_hot_product", list_hot_product);
			try {
				cache.set("seocategoryhot"+category.getId(),JSON.toJSONString(list_hot_product));
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seocategoryhot"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seocategoryhot"+category.getId());
		}

		//4.十大品牌排行榜  进度条进度计算
		if(cache.exists("seocategorylistbrand"+category.getId())){
			String seocategorylistbrand = cache.get("seocategorylistbrand" + category.getId());
			List<Map<String, Object>> list_brand = JSON.parseObject(seocategorylistbrand, new TypeReference<List<Map<String, Object>>>() {
			});
			model.addObject("list_brand", list_brand);
			System.out.println("缓存中有seocategorylistbrand"+category.getId());
		}else {
			List<SeoBrandSale> querylist = gomeSEOSaleRank.getBrandSalesRank(category.getId());
			if (querylist != null && !querylist.isEmpty()) {
				List<Map<String, Object>> brandlist = new ArrayList<Map<String, Object>>();
				//进度条进度计算
				int maxSaleNum = querylist.get(0).getBrandSaleNum();
				for (SeoBrandSale saleinfo : querylist) {
					Map<String, Object> info = new HashMap<String, Object>();
					//进度条进度
					info.put("progressNum", PublishUtil.getProgressNum(saleinfo.getBrandSaleNum(), maxSaleNum));
					info.put("brandId", saleinfo.getBrandId());
					info.put("brandLogoUrl", saleinfo.getBrandLogoUrl());
					info.put("brandName", saleinfo.getBrandName());
					info.put("brandSaleNum", saleinfo.getBrandSaleNum());
					String facetid = prodSpecService.getFacetIdByBrandId(Long.parseLong(saleinfo.getBrandId()));
					info.put("facetid", facetid);
					brandlist.add(info);
				}
				model.addObject("list_brand", brandlist);
				try {
					cache.set("seocategorylistbrand"+category.getId(),JSON.toJSONString(brandlist));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategorylistbrand"+category.getId());
					e.printStackTrace();
				}
				System.out.println("缓存中没有seocategorylistbrand"+category.getId());
			}else {
				System.out.println("缓存中没有seocategorylistbrand，且查询为null");
			}
		}
		//5.商品评价
		List<Map<String, Object>> productCommentList = getListFromRankingAppraiseList("cat10000070", 1);
		model.addObject("commentList",productCommentList);
		System.out.println("相关评论为"+JSON.toJSONString(productCommentList));
		//6.相关商品排行榜
		// 移动排行榜列表页—相关产品排行榜
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentId", category.getParentId());
		map.put("cid", category.getCid());
		List<Map<String, Object>> likeRankList = categoryManager.getRelatedCategoryList(map);
		model.addObject("likeRankList",likeRankList);
		//7.所有排行榜
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		model.addObject("allRankList",categorys);
		//7.根据当前三级分类ID查询期对应的筛选项信息   品牌  价格  功能特点是固定死的  每个分类下都是这几个筛选项
		//品牌筛选项   先根据三级分类catxxxid 去查找对应的筛选项
		//获取筛选项  再根据是想象的groupid去查找对应下面的具体内容
		List<FacetGroupItem> facets_brand = null;
		List<FacetGroupItem> facets_other = null;
		//品牌筛选项
		if(cache.exists("seofacetsbrand"+category.getId())){
			String seofacetsbrand = cache.get("seofacetsbrand"+category.getId());
			if(!seofacetsbrand.equals("no")){
				facets_brand = JSON.parseObject(seofacetsbrand, new TypeReference<List<FacetGroupItem>>() {
				});

				Map<String, Object> brandList = new HashMap<String, Object>();
				brandList.put("facetGroupType", 1);
				brandList.put("facetGroupName", "品牌");
				brandList.put("subList", facets_brand);

				model.addObject("brandList", facets_brand);
				System.out.println("缓存中有seofacetsbrand"+category.getId());
				System.out.println(JSON.toJSONString(brandList));
			}
		}else {
			facets_brand = queryFacetsListByGroupType(1, category.getId());
			try {
				if(facets_brand == null || facets_brand.isEmpty()){
					cache.set("seofacetsbrand"+category.getId(),"no");
				}else{
					Map<String, Object> brandList = new HashMap<String, Object>();
					brandList.put("facetGroupType", 1);
					brandList.put("facetGroupName", "品牌");
					brandList.put("subList", facets_brand);
					model.addObject("brandList", facets_brand);
					System.out.println(JSON.toJSONString(brandList));
					cache.set("seofacetsbrand"+category.getId(),JSON.toJSONString(facets_brand));
				}
			}catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsbrand"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsbrand"+category.getId());
		}
		//功能特点筛选项
		if(cache.exists("seofacetsother"+category.getId())){
			String seofacetsother = cache.get("seofacetsother"+category.getId());
			if(!seofacetsother.equals("no")){
				facets_other = JSON.parseObject(seofacetsother, new TypeReference<List<FacetGroupItem>>() {
				});
				Map<String, Object> otherList = new HashMap<String, Object>();
				otherList.put("facetGroupType", 3);
				otherList.put("facetGroupName", "功能特点");
				otherList.put("subList", facets_other);
				model.addObject("otherList", facets_other);
				System.out.println(JSON.toJSONString(otherList));
				System.out.println("缓存中有seofacetsother且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsother且为no"+category.getId());
			}
		}else {
			facets_other = queryFacetsListByGroupType(3, category.getId());
			try {
				if(facets_other == null || facets_other.isEmpty()){
					cache.set("facets_other"+category.getId(),"no");
				}else{
					Map<String, Object> otherList = new HashMap<String, Object>();
					otherList.put("facetGroupType", 3);
					otherList.put("facetGroupName", "功能特点");
					otherList.put("subList", facets_other);
					model.addObject("otherList", facets_other);
					System.out.println(JSON.toJSONString(otherList));
					cache.set("facets_other"+category.getId(),JSON.toJSONString(facets_other));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsother"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsother"+category.getId());
		}
		return model;
	}

	public ModelAndView getWAPTop10Filter(String content){
		String pageType = content.substring(0,content.indexOf("-"));
		String id = content.substring(content.indexOf("-")+1,content.length());
		Category category = null;
		FacetGroupItem facetGroupItem = null;
		//1.hot new 获取分类对象   brand price other 获取筛选对象
		if(pageType.equals("hot") || pageType.equals("new")){
			category = categoryManager.getByCid(id);
		}else {
			//2.通过页面的facet的id值反向获取对应三级分类
			facetGroupItem = facetGroupItemManager.getById(id);
			category = categoryManager.getById(facetGroupItem.getCatId());
		}

		ModelAndView model = new ModelAndView("vm_rank/wap_topic_filter");
		String env = SEOPropertiesUtils.getStringValueByKey("gome.seo.env","live");
		model.addObject("env",env);
		model.addObject("pageType",pageType);
		model.addObject("id",id);

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		model.addObject("rankYear",year);

		//1.根据三级分类ID查询当前页面的三级分类信息
		model.addObject("rank_key",category);
		model.addObject("r_modId",category.getCid()/1000);

		//2.根据当前三级分类ID查询期对应的筛选项信息   品牌  价格  功能特点是固定死的  每个分类下都是这几个筛选项
		//先根据三级分类catxxxid 去查找对应的筛选项 获取筛选项  再根据是想象的groupid去查找对应下面的具体内容
		List<FacetGroupItem> facets_brand = null;
		List<FacetGroupItem> facets_other = null;
		//品牌筛选项
		if(cache.exists("seofacetsbrand"+category.getId())){
			String seofacetsbrand = cache.get("seofacetsbrand"+category.getId());
			if(!seofacetsbrand.equals("no")){
				facets_brand = JSON.parseObject(seofacetsbrand, new TypeReference<List<FacetGroupItem>>() {
				});

				Map<String, Object> brandList = new HashMap<String, Object>();
				brandList.put("facetGroupType", 1);
				brandList.put("facetGroupName", "品牌");
				brandList.put("subList", facets_brand);

				model.addObject("brandList", facets_brand);
				System.out.println("缓存中有seofacetsbrand"+category.getId());
				System.out.println(JSON.toJSONString(brandList));
			}
		}else {
			facets_brand = queryFacetsListByGroupType(1, category.getId());
			try {
				if(facets_brand == null || facets_brand.isEmpty()){
					cache.set("seofacetsbrand"+category.getId(),"no");
				}else{
					Map<String, Object> brandList = new HashMap<String, Object>();
					brandList.put("facetGroupType", 1);
					brandList.put("facetGroupName", "品牌");
					brandList.put("subList", facets_brand);
					model.addObject("brandList", facets_brand);
					System.out.println(JSON.toJSONString(brandList));
					cache.set("seofacetsbrand"+category.getId(),JSON.toJSONString(facets_brand));
				}
			}catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsbrand"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsbrand"+category.getId());
		}
		//功能特点筛选项
		if(cache.exists("seofacetsother"+category.getId())){
			String seofacetsother = cache.get("seofacetsother"+category.getId());
			if(!seofacetsother.equals("no")){
				facets_other = JSON.parseObject(seofacetsother, new TypeReference<List<FacetGroupItem>>() {
				});
				Map<String, Object> otherList = new HashMap<String, Object>();
				otherList.put("facetGroupType", 3);
				otherList.put("facetGroupName", "功能特点");
				otherList.put("subList", facets_other);
				model.addObject("otherList", facets_other);
				System.out.println(JSON.toJSONString(otherList));
				System.out.println("缓存中有seofacetsother且不为no"+category.getId());
			}else {
				System.out.println("缓存中有seofacetsother且为no"+category.getId());
			}
		}else {
			facets_other = queryFacetsListByGroupType(3, category.getId());
			try {
				if(facets_other == null || facets_other.isEmpty()){
					cache.set("facets_other"+category.getId(),"no");
				}else{
					Map<String, Object> otherList = new HashMap<String, Object>();
					otherList.put("facetGroupType", 3);
					otherList.put("facetGroupName", "功能特点");
					otherList.put("subList", facets_other);
					model.addObject("otherList", facets_other);
					System.out.println(JSON.toJSONString(otherList));
					cache.set("facets_other"+category.getId(),JSON.toJSONString(facets_other));
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetsother"+category.getId());
				e.printStackTrace();
			}
			System.out.println("缓存中没有seofacetsother"+category.getId());
		}

		//active属性  点击变红
		model.addObject(pageType+"type", "active");
		//3.当前分类或筛选项排行榜主数据
		if(pageType.equals("hot")) {
			model.addObject("facetFilterName", RankFilter.getEnumValueByKey(pageType));
			//爆品排行榜
			if(cache.exists("seocategoryhot"+category.getId())){
				String seocategoryhot = cache.get("seocategoryhot" + category.getId());
				List<Map<String, Object>> list_hot_product = JSON.parseObject(seocategoryhot, new TypeReference<List<Map<String, Object>>>() {
				});
				model.addObject("productList", list_hot_product);
				System.out.println("缓存中有seocategoryhot"+category.getId());
			}else {
				//List<Map<String, Object>> list_hot_product = rankManager.queryHotSaleInfosByCat3Id(category.getId());
				List<Map<String, Object>> list_hot_product = getHotSaleInfoByCat3IdFromSearch(category.getCategoryName());

				model.addObject("productList", list_hot_product);
				try {
					cache.set("seocategoryhot"+category.getId(),JSON.toJSONString(list_hot_product));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategoryhot"+category.getId());
					e.printStackTrace();
				}
				System.out.println("缓存中没有seocategoryhot"+category.getId());
			}
		}else if(pageType.equals("new")){
			model.addObject("facetFilterName", RankFilter.getEnumValueByKey(pageType));
			//新品排行榜
			if(cache.exists("seocategorynew"+category.getId())){
				String seocategorynew = cache.get("seocategorynew" + category.getId());
				List<Map<String, Object>> list_new_product = JSON.parseObject(seocategorynew, new TypeReference<List<Map<String, Object>>>() {
				});
				model.addObject("productList", list_new_product);
				System.out.println("缓存中有seocategorynew"+category.getId());
			}else {
				List<Map<String, Object>> list_new_product = queryNewSaleInfosByCat3Id(category.getId());
				model.addObject("productList", list_new_product);
				try {
					cache.set("seocategorynew"+category.getId(),JSON.toJSONString(list_new_product));
				} catch (Exception e) {
					logger.info("添加缓存失败"+"seocategorynew"+category.getId());
					e.printStackTrace();
				}
				System.out.println("缓存中没有seocategorynew"+category.getId());
			}
		}else {
			model.addObject("facetFilterName",facetGroupItem.getValue());
			//查询筛选的商品
//			List<Map<String, Object>> saleList = rankManager.querySaleInfosByFacetId(category.getId(), facetGroupItem.getFacetId());
			List<Map<String, Object>> saleList = null;
			if(cache.exists("seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId())){
				String seofacetproduct = cache.get("seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
				if(!seofacetproduct.equals("no")){
					cache.get("seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
					saleList = JSON.parseObject(seofacetproduct, new TypeReference<List<Map<String, Object>>>() {
					});
					model.addObject("productList", saleList);
					System.out.println("缓存中有seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
				}
			}else{
				saleList = querySaleInfosByFacetIdFromSearch(category.getCategoryName(),facetGroupItem.getFacetId());
				model.addObject("productList", saleList);
				System.out.println("缓存中没有seofacetproduct"+facetGroupItem.getCatId()+facetGroupItem.getFacetId());
			}
		}
		//6.相关商品排行榜
		// 移动排行榜列表页—相关产品排行榜
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentId", category.getParentId());
		map.put("cid", category.getCid());
		List<Map<String, Object>> likeRankList = categoryManager.getRelatedCategoryList(map);
		model.addObject("likeRankList",likeRankList);
		//7.所有排行榜
		List<Map<String, Object>> categorys = categoryManager.getCategoryListAll();
		model.addObject("allRankList",categorys);
		System.out.println("所有排行榜数据为"+JSON.toJSONString(categorys));
		System.out.println(model.getViewName());
		return model;
	}


	public List<Map<String, Object>> getHotSaleInfoByCat3IdFromSearch(String productWordName) {
		JSONObject searchData = dubboSearchService.productSearch(getDefaultSearchJSONObject(productWordName));
		JSONArray friendProductSearchInfoArray = searchData.getJSONObject("content").getJSONObject("prodInfo").getJSONArray("products");
		System.out.println(JSON.toJSONString(friendProductSearchInfoArray));
//		return null;
		int maxSaleNum = 0;
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		if(null !=friendProductSearchInfoArray && friendProductSearchInfoArray.size()>0){
			for(int i=0;i<friendProductSearchInfoArray.size();i++){
				JSONObject job = friendProductSearchInfoArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				//销量
				if(i==0){
					maxSaleNum = job.getInteger("salesVolume");
				}
				Map<String, Object> info = new HashMap<String, Object>();
				String pId = job.getString("pId");
				info.put("pid", pId);
				// SkuID
				String sid = job.getString("skuId");
				info.put("sid", sid);
				// 商品名称
				info.put("pn", job.getString("name"));
				// 商品PC地址
				info.put("purl", getProductPageURL(pId, sid));
				// 商品Wap地址
				info.put("wapUrl", getProductPageWapURL(pId, sid));
				// 商品 图片
				info.put("iurl", job.getString("sImg"));

				int evaluateCount = 0;
				String good = "99%";
				GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(pId);
				if (goodsAppraiseInfoVO != null) {
					evaluateCount = goodsAppraiseInfoVO.getTotalNum();
					good = goodsAppraiseInfoVO.getHaoping() + "%";
				}

				//评论数
				info.put("evaluateCount", evaluateCount);
				//推荐指数
				info.put("evaluateNum", getevaluateNum(good));
				// 获取商品价格
				Double price = null;
				if (sid != null) {
					GomePrice gomePrice = priceService.getGomePrice(sid);
					if (gomePrice != null) {
						price = gomePrice.getFinalPrice();
					}
				}
				info.put("price", price);
				//好评度
				info.put("good", good);
				//进度条
				info.put("progressNum", PublishUtil.getProgressNum(job.getInteger("salesVolume"), maxSaleNum));
				returnList.add(info);
			}
		}
		return returnList;
	}

	public int getevaluateNum(String good){
		try {
			int g = Integer.parseInt(good);
			if(g > 90){
				return 5;
			}else {
				return 4;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 5;
		}
	}

	/**
	 * 查询商品评论信息
	 *
	 * @param pid 商品productID
	 * @return 评论信息
	 */
	private GoodsAppraiseInfoVO queryProductEvaluate(String pid) {
		//查询评论
		GoodsAppraiseInfoVO goodsAppraiseInfoVO = goodsAppraiseQueryService.queryInfoVO(pid);
		return goodsAppraiseInfoVO;
	}



	private List<FacetGroupItem> queryFacetsListByGroupType(Integer facetGroupType, String thirdCatId) {
		List<FacetGroupItem> resultList = new ArrayList<FacetGroupItem>();
		Map<String, Object> searchMap = getFirstLevelQueryMap(facetGroupType, thirdCatId);
		List<FacetGroup> facetList = facetGroupManager.listByWheres(searchMap);
		for (FacetGroup fGroup : facetList) {
			Map<String, Object> itemSearchMap = getItemLevelQueryMap(fGroup);
			List<FacetGroupItem> facetsItemList = facetGroupItemManager.listByWheres(itemSearchMap);
			resultList.addAll(facetsItemList);
		}
		return resultList;
	}

	private Map<String, Object> getFirstLevelQueryMap(Integer facetGroupType, String thirdCatId) {
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("qcategoryid", thirdCatId);
		searchMap.put("qis_show", 1);
		searchMap.put("qtype", facetGroupType);
		return searchMap;
	}

	private Map<String, Object> getItemLevelQueryMap(FacetGroup fGroup) {
		Map<String, Object> itemSearchMap = new HashMap<String, Object>();
		itemSearchMap.put("qcatid", fGroup.getCategoryId());
		itemSearchMap.put("qis_show", 1);
		itemSearchMap.put("parent", fGroup.getGroupId());
		return itemSearchMap;
	}


	/**
	 *
	 * Description:获取按销量排序的10条商品数据
	 *
	 * @param:
	 * @return: com.alibaba.fastjson.JSONObject
	 * @auther: qinruixin-ds
	 * @date: 2018/11/29 11:11
	 */
	public JSONObject getDefaultSearchJSONObject(String productWordName) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("from","robot");
		jsonObject.put("question",productWordName);
		jsonObject.put("catId","0");
		jsonObject.put("regionId","11010200");
		jsonObject.put("market","10");
		jsonObject.put("facets","0");
		jsonObject.put("productTag","0");
		jsonObject.put("sort","10");
		jsonObject.put("priceTag","0");
		jsonObject.put("pageSize","48");
		jsonObject.put("pageNumber","1");
		jsonObject.put("instock","1");
		jsonObject.put("sale","0");
		return jsonObject;
	}

	public List<Map<String, Object>> queryNewSaleInfosByCat3Id(String cat3Id) {
		List<Map<String, Object>> returnList1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> returnList2 = new ArrayList<Map<String, Object>>();
		HashSet<String> productIds = new HashSet<String>();
		List<NewProductRank> queryList = gomeNewProductRankService.getThridCateNewProductRank(cat3Id);
		for (int i=0;queryList!=null&&i<queryList.size();i++) {
			NewProductRank product = queryList.get(i);
			Map<String, Object> info = new HashMap<String, Object>();
			// 商品ID
			String pid = product.getProductId();
			// 判断上下架状态
			int state = gomeProductService.getProudctState(pid);
			if (state != ON_SALE_STATUS) {
				continue;
			}
			info.put("pid", pid);
			// SkuID
			String sid = product.getSkuId();
			info.put("sid", sid);
			// 商品名称
			info.put("pn", product.getSkuName());
			// 商品PC地址
			info.put("purl", getProductPageURL(pid, sid));
			// 商品Wap地址
			info.put("wapUrl", getProductPageWapURL(pid, sid));
			// 商品 图片
			info.put("iurl", product.getImg1());
			int evaluateCount = 0;
			String good = "99%";
			GoodsAppraiseInfoVO goodsAppraiseInfoVO = queryProductEvaluate(pid);
			if (goodsAppraiseInfoVO != null) {
				evaluateCount = goodsAppraiseInfoVO.getTotalNum();
				good = goodsAppraiseInfoVO.getHaoping() + "%";
			}
			info.put("evaluateCount", evaluateCount);
			info.put("evaluateNum", getevaluateNum(good));
			// 获取商品价格
			Double price = null;
			if (sid != null) {
				GomePrice gomePrice = priceService.getGomePrice(sid);
				if (gomePrice != null) {
					price = gomePrice.getFinalPrice();
				}
			}
			info.put("price", price);
			//上架时间
			info.put("time", PublishUtil.dateToYYYYMMDD(new Date(product.getPutawayTime())));
			//好评度
			info.put("good", good);
			if(!productIds.contains(pid)){
				productIds.add(pid);
				returnList1.add(info);
			}
		}
		if(returnList1.size() < 10 && cache.exists("seocategorynew"+cat3Id)){
			String seocategorynew = cache.get("seocategorynew"+cat3Id);
			returnList2 = JSON.parseObject(seocategorynew, new TypeReference<List<Map<String, Object>>>(){});
			for (Map<String, Object> stringObjectMap : returnList2) {
				String pid =(String)stringObjectMap.get("pid");
				if(!productIds.contains(pid)){
					productIds.add(pid);
					returnList1.add(stringObjectMap);
				}
				if(returnList1.size() == 10)
					break;
			}
		}
		return returnList1;
	}

	private Map<String, Object> getFacetRankInfo(String type, String rankName, List<FacetGroupItem> facetList, String categoryName) {
		Map<String, Object> rankFacteInfo = new HashMap<String, Object>();
		rankFacteInfo.put("name", rankName);
		List<Map<String, Object>> rankFacteList = new ArrayList<Map<String, Object>>();
		for (int i = 0; facetList != null && i < facetList.size() && i < 6; i++) {
            List<Map<String, Object>> list_facet = null;
			FacetGroupItem facet = facetList.get(i);
			Map<String, Object> facetinfo = new HashMap<String, Object>();
			facetinfo.put("path", type + "-" + facet.getId());
			facetinfo.put("name", facet.getValue());
            if("价格".equals(rankName)){
                list_facet = querySaleInfosByPriceFromSearch(categoryName,
                        getSearchPriceName(facet.getValue()));
            }else {
                list_facet = querySaleInfosByFacetIdFromSearch(categoryName,
                        facet.getFacetId());
            }
			try {
				if(null != list_facet && !list_facet.isEmpty() && list_facet.size() >= 5){
					cache.set("seofacetproduct"+facet.getCatId()+facet.getFacetId(),JSON.toJSONString(list_facet));
					facetinfo.put("list", list_facet);
					rankFacteList.add(facetinfo);
				}else {
					cache.set("seofacetproduct"+facet.getCatId()+facet.getFacetId(),"no");
				}
			} catch (Exception e) {
				logger.info("添加缓存失败"+"seofacetproduct"+facet.getCatId()+facet.getFacetId());
				e.printStackTrace();
			}
		}
		if(rankFacteList.isEmpty()){
			System.out.println("查询"+rankName+"为空");
			return null;
		}
		rankFacteInfo.put("list", rankFacteList);
		return rankFacteInfo;
	}

	public List<Map<String, Object>> querySaleInfosByFacetIdFromSearch(String categoryName, String facetId) {
		JSONObject searchData = dubboSearchService.productSearch(getHotFacetsSearchJSONObject(categoryName,facetId));
		JSONArray friendProductSearchInfoArray = searchData.getJSONObject("content").getJSONObject("prodInfo").getJSONArray("products");
		int maxSaleNum = 0;
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		if(null !=friendProductSearchInfoArray && friendProductSearchInfoArray.size()>0){
			for(int i=0;i<friendProductSearchInfoArray.size();i++){
				JSONObject pro = friendProductSearchInfoArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				Map<String, Object> info = new HashMap<String, Object>();
				String pid = pro.getString("pId");
				String skuId = pro.getString("skuId");
				//productID
				info.put("pid", pid);
				// SkuID
				info.put("sid", skuId);
				// 商品名称
				info.put("pn", pro.getString("name"));
				// 商品PC地址
				info.put("purl", pro.getString("sUrl"));
				// 商品PC地址
				info.put("wapUrl",getProductPageWapURL(pid,skuId));
				// 商品 图片
				info.put("iurl", pro.getString("sImg"));
				//评论数
				info.put("evaluateCount", pro.getString("evaluateCount"));
				// 推荐指数
				info.put("evaluateNum", getevaluateNum("99"));
				// 获取商品价格
				Double price = null;
				if (skuId != null) {
					GomePrice gomePrice = priceService.getGomePrice(skuId);
					if (gomePrice != null) {
						price = gomePrice.getFinalPrice();
					}
				}
				info.put("price", price);
				returnList.add(info);
			}
		}
		return returnList;
	}

    public List<Map<String, Object>> querySaleInfosByPriceFromSearch(String categoryName, String priceName) {
        JSONObject searchData = dubboSearchService.productSearch(getHotPriceSearchJSONObject(categoryName,priceName));
        JSONArray friendProductSearchInfoArray = searchData.getJSONObject("content").getJSONObject("prodInfo").getJSONArray("products");
        int maxSaleNum = 0;
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        if(null !=friendProductSearchInfoArray && friendProductSearchInfoArray.size()>0){
            for(int i=0;i<friendProductSearchInfoArray.size();i++){
                JSONObject pro = friendProductSearchInfoArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                Map<String, Object> info = new HashMap<String, Object>();
                String pid = pro.getString("pId");
                String skuId = pro.getString("skuId");
                //productID
                info.put("pid", pid);
                // SkuID
                info.put("sid", skuId);
                // 商品名称
                info.put("pn", pro.getString("name"));
                // 商品PC地址
                info.put("purl", pro.getString("sUrl"));
                // 商品PC地址
                info.put("wapUrl",getProductPageWapURL(pid,skuId));
                // 商品 图片
                info.put("iurl", pro.getString("sImg"));
                //评论数
                info.put("evaluateCount", pro.getString("evaluateCount"));
                // 推荐指数
                info.put("evaluateNum", getevaluateNum("99"));
                // 获取商品价格
                Double price = null;
                if (skuId != null) {
                    GomePrice gomePrice = priceService.getGomePrice(skuId);
                    if (gomePrice != null) {
                        price = gomePrice.getFinalPrice();
                    }
                }
                info.put("price", price);
                returnList.add(info);
            }
        }
        return returnList;
    }

	/**
	 *
	 * Description:获取按销量排序的10条有货加筛选项的商品数据
	 *
	 * @param:
	 * @return: com.alibaba.fastjson.JSONObject
	 * @auther: qinruixin-ds
	 * @date: 2018/11/29 11:11
	 */
	public JSONObject getHotFacetsSearchJSONObject(String productWordName,String facets) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("from","robot");
		jsonObject.put("question",productWordName);
		jsonObject.put("catId","0");
		jsonObject.put("regionId","11010200");
		jsonObject.put("market","10");
		jsonObject.put("facets",facets);
		jsonObject.put("productTag","0");
		jsonObject.put("sort","10");
		jsonObject.put("priceTag","0");
		jsonObject.put("pageSize","48");
		jsonObject.put("pageNumber","1");
		jsonObject.put("instock","1");
		jsonObject.put("sale","0");
		return jsonObject;
	}

    /**
     * Description:获取按销量排序的10条有货加筛选项的商品数据
     * @param:
     * @return: com.alibaba.fastjson.JSONObject
     * @auther: qinruixin-ds
     * @date: 2018/11/29 11:11
     */
    public JSONObject getHotPriceSearchJSONObject(String productWordName,String priceName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from","robot");
        jsonObject.put("question",productWordName);
        jsonObject.put("catId","0");
        jsonObject.put("regionId","11010200");
        jsonObject.put("market","10");
        jsonObject.put("facets","0");
        jsonObject.put("productTag","0");
        jsonObject.put("sort","10");
        jsonObject.put("priceTag","1");
        jsonObject.put("price",priceName);
        jsonObject.put("pageSize","48");
        jsonObject.put("pageNumber","1");
        jsonObject.put("instock","1");
        jsonObject.put("sale","0");
        return jsonObject;
    }
    public String getSearchPriceName(String priceName){
        if(priceName.contains("-")){
            String[] split = priceName.split("-");
            return split[0]+"x"+split[1];
        }else {
            return (priceName.substring(0,priceName.indexOf("以上")))+"x*";
        }
    }
	private String getProductPageWapURL(String pid, String sid) {
		return product_wapUrl + pid + "-" + sid + ".html";
	}
	private String getProductPageURL(String pid, String sid) {
		return  product_purl + pid + "-" + sid + ".html";
	}
	private String product_purl = SEOPropertiesUtils.getStringValueByKey("product_purl","//item.gome.com.cn/");
	private String product_wapUrl = SEOPropertiesUtils.getStringValueByKey("product_wapUrl","//item.m.gome.com.cn/product-");
	private List<Map<String, Object>> getListFromRankingAppraiseList(String catId,int type) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			// 取查询三级分类下的商品id
			List<Map<String, String>> list = productInfoService.getFixedProductsByCatId(catId,
					SEOPropertiesUtils.getIntValueByKey("rank_appraise_num",10));
			List<String> productIds = new ArrayList<String>();
			for (Map<String, String> map : list) {
				String productId = map.get("PRODUCTID");
				productIds.add(productId);
			}
			System.out.println(JSON.toJSONString("producatid的list为"+productIds));
			List<AppraiseSEO> appriseSEOList = appraiseSEOService.getThirdCategoryAppraiseSEO(productIds);
			for (AppraiseSEO appraiseSEO : appriseSEOList) {
				if (appraiseSEO != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (type == 0) {
						// PC端
						map.put("detailHref", appraiseSEO.getPrdUrl());
					} else {
						// 移动端
						String detailHref = "";
						String productId = appraiseSEO.getProductId();
						String skuId = appraiseSEO.getSkuId();
						if (StringUtils.isEmpty(skuId)) {
							detailHref = SEOPropertiesUtils.getStringValueByKey("rank_appraise_wapproduct","http://item.m.gome.com.cn/product")+ "-"
									+ productId + ".html";
						} else {
							detailHref = SEOPropertiesUtils.getStringValueByKey("rank_appraise_wapproduct","http://item.m.gome.com.cn/product") + "-"
									+ productId + "-" + skuId + ".html";
						}
						map.put("detailHref", detailHref);
					}
					// 评论时间
					map.put("imgUrl", appraiseSEO.getImageUrl());
					// 使用心得
					map.put("name", appraiseSEO.getProductName());
					// 评论用户等级
					map.put("price", appraiseSEO.getGoodsPrice());
					// 推荐点
					map.put("prdPraise", appraiseSEO.getGoodCommentPercent());
					// 评价人数
					map.put("praiseTotal", appraiseSEO.getTotalCount());
					// 评论内容
					map.put("prdContent", appraiseSEO.getContent());
					// 用户评论id-用户昵称
					map.put("uesrNick", appraiseSEO.getUserId());
					// 提交评论时间
					map.put("dateBuy", appraiseSEO.getPostTime());
					String productId = appraiseSEO.getProductId();
					String brandUrl = "";
					if (productId != null && !"".equals(productId)) {
						String facetId = prodSpecService.getBrandFacetIdByProductId(productId);
						if (!StringUtils.isEmpty(facetId)) {
							if (type == 0) {
								brandUrl = SEOPropertiesUtils.getStringValueByKey("rank_appraise_pcbrand","//brand.gome.com.cn/") + facetId + "-"
										+ catId + ".html";
							} else {
								brandUrl = SEOPropertiesUtils.getStringValueByKey("rank_appraise_wapbrand","http://m.gome.com.cn/brand/") + facetId + "-"
										+ catId + ".html";
							}
						}
					}
					map.put("brand", appraiseSEO.getBrand());
					map.put("brandUrl", brandUrl);
					map.put("productId", appraiseSEO.getProductId());
					map.put("skudId", appraiseSEO.getSkuId());
					resultList.add(map);
				}
			}
			return resultList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private Map<String, String> getHeadersInfo(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}
}
