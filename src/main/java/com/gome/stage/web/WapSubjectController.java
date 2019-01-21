package com.gome.stage.web;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.frontSe.comm.FSGoods;
import com.gome.frontSe.comm.FSImgBase;
import com.gome.frontSe.comm.FSLinkImg;
import com.gome.frontSe.interfaces.IProdDetailService;
import com.gome.qiantai.model.AppraiseSEO;
import com.gome.qiantai.model.GoodsQuestionSEO;
import com.gome.qiantai.service.IAppraiseSEOService;
import com.gome.qiantai.service.IGoodsAppraiseQueryService;
import com.gome.qiantai.service.IGoodsQuestionSEOService;
import com.gome.singleweb.service.SingleHttpClientUtil;
import com.gome.stage.bean.Ranking.RankReturnValue;
import com.gome.stage.bean.Ranking.RankingTypeEnum;
import com.gome.stage.bean.Ranking.redis.RankingPro;
import com.gome.stage.entity.*;
import com.gome.stage.interfaces.item.ICategoryDetailService;
import com.gome.stage.interfaces.item.IGomeProductService;
import com.gome.stage.interfaces.item.IPriceService;
import com.gome.stage.interfaces.whale.IProductInfoService;
import com.gome.stage.item.GoodsInfo;
import com.gome.stage.item.ProductBrandBean;
import com.gome.stage.item.ProductItem;
import com.gome.stage.item.Relevant;
import com.gome.stage.service.KeyWordService;
import com.gome.stage.service.LabelService;
import com.gome.stage.service.NewService;
import com.gome.stage.service.TitleService;
import com.gome.stage.utils.HttpClientUtil;
import com.gome.stage.utils.StringUtils;
import com.gome.tapho.bean.spec.PrdSpecBean;
import com.gome.tapho.interfaces.spec.IProdSpecService;
import org.gome.search.dubbo.idl.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * wangkeqiang-ds
 *
 * 移动端专题Controller
 *
 * 专题控制层代码
 * 逻辑：
 * 		1：从titleIndex表中按照A-Z  0-9取数据，0-9的索引为0
 * 		2：从titlegood表中去专题相关连数据，及专题综述页及相关商品推荐的数据源。
 * 		3：title表为业务人员上传的基本专题数据，经过筛选后插入titleindex表，具体筛选操作为a：定时任务、	b：业务人员点击上传，此两种操作与本模块无关，本模块只负责取数据
 * 	详细逻辑参看下文注释
 */
@Controller
@RequestMapping(value = "/mtopicnew")
public class WapSubjectController {
    Logger logger = LoggerFactory.getLogger(WapSubjectController.class);
    private static final String CINDEX_COUNTS = "cindex_count";
    private static final String SITE = "gome";
    private static final String CINDEX_KEY = "cindex_key";
    private static final String CINDEX = "cindex";
    private static final String TITLEKEY = "title_key";
    private static final String DESC_KEY = "desc_key";
    private static final String TAGKEY = "tag_key";
    private final static String CACHE_KEY_SHOW_GOODS = "cache_wapzhuanti_common_show_goods_";
    /* 存放共用部分数据 key : 排行榜信息 */
    private final static String CACHE_KEY_MAIN_RANKINGS = "cache_wapzhuanti_common_rankings_";
    /* 存放共用部分数据 key : 专题主商品相关信息 */
    private final static String CACHE_KEY_MAIN_GOODS = "cache_wapzhuanti_common_main_goodsinfo_";
    private Integer pageSize = 120;
    private Map<String, Object> cindexKeyList;
    @Autowired
    private TitleService titleService;
    @Autowired
    @Qualifier("gcache_productCard")
    private Gcache cache;
    @Autowired
    private NewService newService;
    @Autowired
    private KeyWordService keyWordService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private IGomeProductService gomeProductService;
    @Autowired
    private IGoodsAppraiseQueryService goodsAppraiseQueryService;
    @Autowired
    private IProdDetailService prodDetailService;
    @Autowired
    private IPriceService priceService;
    @Autowired
    private IProductInfoService productInfoService;
    @Autowired
    private IProdSpecService prodSpecService;

    @Autowired
    private IAppraiseSEOService appraiseSEOService;

    @Autowired
    private IGoodsQuestionSEOService goodsQuestionSEOService;

    @Autowired
    private com.gome.stage.interfaces.item.IProductInfoService itemProductInfoService;

    @Autowired
    private ICategoryDetailService categoryDetailService;

    @Autowired
    private SingleHttpClientUtil singleHttpClientUtil;

    @Autowired
    private DubboService dubboSearchService;

    private static List<String> cindexList = new ArrayList<String>();

    private static  List<String> conditionList = new ArrayList<String>();
    static {
        int start = (int)'A';
        int end = (int)'Z' + 1;
        for(int i = start ; i < end ; i++){
            cindexList.add(String.valueOf((char)i));
        }
        cindexList.add("0");
        conditionList.add("content");
        conditionList.add("price");
        conditionList.add("images");
        conditionList.add("comments");
        conditionList.add("consulting");
        conditionList.add("product");
    }


    /**
     * 专题首页，及列表展示页数据
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView getproductWordsList(HttpServletRequest request,HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        if(!requestURI.endsWith("/")) {
            int indexOf = requestURI.indexOf("topic");
            String uri = requestURI.substring(indexOf-1);
            return new ModelAndView("redirect301:"+uri+"/");
        }
        ModelAndView modelAndView = new ModelAndView("vm_topic_wap/vm-subjectTitle");
        List<Map<String, Object>> titleIndexPageData = titleService.queryTitleIndexPageData();
        modelAndView.addObject("cindex_key",cindexList);
        modelAndView.addObject("cindexList",titleIndexPageData);
        List<Map<String, Object>> recentNews = getRecentNews();
        modelAndView.addObject("recentNews",recentNews);
        return modelAndView;
    }

    /**
     * keyword包含以下几种情况：
     * 		1：专题id  2：标签Id  3：专题索引A-Z  0-9 4：无效数字  5:ADFFDGDFG...等历史数据，即topic/id中id不为数字的历史数据
     *
     * 	··先判断keyword是否是number，如果是按专题id/标签id处理，如果不是按历史数据和专题索引处理
     *
     * 	     getTitleCondition同样适用此逻辑
     */

    @RequestMapping(value = "/{keyWord}", method = RequestMethod.GET)
    public ModelAndView getTitleIndexOrTitleData(@PathVariable(value = "keyWord",required = true) String keyWord,HttpServletRequest request,HttpServletResponse response) {
        if (!isInteger(keyWord)) {
            String path = "topic/"+keyWord;
            List<Title> zhuanTiByTitlePath = titleService.findZhuanTiByTitlePath(path);
            if (zhuanTiByTitlePath!=null&&!zhuanTiByTitlePath.isEmpty()){
                Integer titleId = zhuanTiByTitlePath.get(0).getId();
                return new ModelAndView("redirect301:/mtopic/"+titleId);
            }
        }
        if (isInteger(keyWord)){
            if (keyWord.length()==5){
                String requestURI = request.getRequestURI();
                if(!requestURI.endsWith("/")) {
                    int indexOf = requestURI.indexOf("mtopic");
                    String uri = requestURI.substring(indexOf-1);
                    return new ModelAndView("redirect301:"+uri+"/");
                }
                String titleId = keyWord;
                titleId = titleId.replaceAll("[^(0-9)]", "");
                if (StringUtils.isNullOrBlank(titleId)){
                    return new ModelAndView("redirect301:/mtopic/");
                }
                Title title = titleService.getTitleById(Integer.valueOf(titleId));
                if(title == null){
                    return new ModelAndView("redirect301:/mtopic/");
                }
                ModelAndView modelAndView = new ModelAndView("vm_topic_wap/vm-productSummarize");

                List<Map<String, Object>> productComments = null;
                List<Map<String, Object>> productAdvisory = null;
                if (cache.exists("seowaptopic_commentsZongShu/"+titleId)){
                    try {
                        String productCommentsString = cache.get("seowaptopic_commentsZongShu/"+titleId);
                        productComments = (List<Map<String, Object>>) JSON.parse(productCommentsString);
                    } catch (Exception e) {
                        productComments = getProductComments(Integer.valueOf(titleId), "zongshu_product_comment");
                        String commentsZongShuString = JSON.toJSONString(productComments);
                        cache.set("seowaptopic_commentsZongShu/"+titleId,commentsZongShuString);
                    }
                }else {
                    productComments = getProductComments(Integer.valueOf(titleId), "zongshu_product_comment");
                    String commentsZongShuString = JSON.toJSONString(productComments);
                    cache.set("seowaptopic_commentsZongShu/"+titleId,commentsZongShuString);
                }

                if (cache.exists("seotopic_advisoryZongShu/"+titleId)){
                    try {
                        String productAdvisoryString = cache.get("seotopic_advisoryZongShu/"+titleId);
                        productAdvisory = (List<Map<String, Object>>) JSON.parse(productAdvisoryString);
                    } catch (Exception e) {
                        productAdvisory = getProductAdvisory(Integer.valueOf(titleId),5);
                        String advisoryZongShuString = JSON.toJSONString(productAdvisory);
                        cache.set("seotopic_advisoryZongShu/"+titleId,advisoryZongShuString);
                    }
                }else {
                    productAdvisory = getProductAdvisory(Integer.valueOf(titleId),5);
                    String advisoryZongShuString = JSON.toJSONString(productAdvisory);
                    cache.set("seotopic_advisoryZongShu/"+titleId,advisoryZongShuString);
                }

                List<Map<String, Object>> productRelation = getProductRelationFromRedis(titleId);
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);
                List<Map<String, Object>> newsReleated = getNewsReleated(Integer.valueOf(titleId));
                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                modelAndView.addObject("productRelation",productRelation);
                modelAndView.addObject("newsReleated",newsReleated);
                modelAndView.addObject("productComments",productComments);
                modelAndView.addObject("productAdvisory",productAdvisory);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                int newsCount = newService.getNewsCountByTid(title.getId());//获得专题中文章数量
                title.setNewsCount(newsCount);
                title.setKeywords(keyWordService.getKeywordByTitleId(title.getId()));
                modelAndView.addObject("newsCount", newsCount);
                List<KeyWord> tagList = keyWordService.findTagsByTitleId(title.getId(), 2);   //标签 ： type =2;
                modelAndView.addObject("tag_list", tagList);
                modelAndView.addObject(TITLEKEY, title);
                String goodsId = title.getGoodsId();
                logger.info("QueryItemByIdDataPreInterceptor_doIntercept_goodsId=" + goodsId);

                Map<String, Object> item = null;
                if (cache.exists("seotopic_item/"+titleId)){
                    try {
                        String itemString = cache.get("seotopic_item/" + titleId);
                        item = (Map<String, Object>) JSON.parse(itemString);
                    } catch (Exception e) {
                        item  = HttpClientUtil.getATGProductInfoByID(goodsId,gomeProductService,goodsAppraiseQueryService,prodDetailService,priceService);
                    }
                }else {
                    item  = HttpClientUtil.getATGProductInfoByID(goodsId,gomeProductService,goodsAppraiseQueryService,prodDetailService,priceService);
                }

                if (null != item) {
                    String newsGoodsImagesUrl = null;
                    if (cache.exists("seotopic_imgUrl/"+titleId)){
                        try {
                            newsGoodsImagesUrl = cache.get("seotopic_imgUrl/"+titleId);
                        } catch (Exception e) {
                            newsGoodsImagesUrl = getNewsGoods(goodsId);
                            cache.set("seotopic_imgUrl/"+titleId,newsGoodsImagesUrl);
                        }
                    }else {
                        newsGoodsImagesUrl = getNewsGoods(goodsId);
                        cache.set("seotopic_imgUrl/"+titleId,newsGoodsImagesUrl);
                    }
                    modelAndView.addAllObjects(item);
                    //获取第一张专题商品图片
                    modelAndView.addObject("imgUrl", newsGoodsImagesUrl);
                }

                String brandName = null;
                String facetId = null;
                ProductItem product = null;

                if (cache.exists("seotopic_brandname/"+titleId)){
                    try {
                        brandName = cache.get("seotopic_brandname/"+titleId);
                    } catch (Exception e) {
                        product = productInfoService.getProdDetailByProductId(goodsId);
                        ProductBrandBean brandinfo = productInfoService.getBrandByBrandId(product.getBrandId());
                        brandName = brandinfo.getcName();
                    }
                }else {
                    product = productInfoService.getProdDetailByProductId(goodsId);
                    ProductBrandBean brandinfo = productInfoService.getBrandByBrandId(product.getBrandId());
                    brandName = brandinfo.getcName();
                }

                if (cache.exists("seotopic_facetId/"+titleId)){
                    try {
                        facetId = cache.get("seotopic_facetId/"+titleId);
                    } catch (Exception e) {
                        facetId = prodSpecService.getFacetIdByBrandId( Long.parseLong(product.getBrandId()));                    }
                }else {
                    facetId = prodSpecService.getFacetIdByBrandId( Long.parseLong(product.getBrandId()));
                }

                modelAndView.addObject("brandname", brandName);
                modelAndView.addObject("facetId", facetId);
                String skuIdTemp = null;
                try {
                    if(title!=null && null !=title.getSkuId()){
                        skuIdTemp = title.getSkuId();
                    }else {
                        //注意，有大萝卜坑，一定要加非空判断
                        if (product!=null && product.getSkuIds()!=null){
                            String[] skuIds = product.getSkuIds().split(":");
                            if (skuIds!=null && skuIds.length!=0){
                                skuIdTemp = skuIds[0];
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                modelAndView.addObject("skuId", skuIdTemp);
                return modelAndView;
            }
        }
        if (keyWord.length()==1){
            String requestURI = request.getRequestURI();
            if(!requestURI.endsWith(".html")) {
                int indexOf = requestURI.indexOf("mtopic");
                String uri = requestURI.substring(indexOf-1);
                return new ModelAndView("redirect301:"+uri+".html");
            }
//            String firstLetter = keyWord.substring(0,1);
            if (cindexList.contains(keyWord)){
                ModelAndView modelAndView = new ModelAndView("vm_topic_wap/vm-subjectTitlePage");
                Map<String, Object> search = new HashMap<String, Object>();
                search.put("pageSize", pageSize);
                search.put("site", SITE);
                if ("0".equals(keyWord))
                {
                    search.put("cstart", "0");
                    search.put("cend", "9");
                }
                else
                {
                    search.put("cindex", keyWord);
                }
                Map<String, Object> cdata = titleService.getTitleMap(search);
                modelAndView.addAllObjects(cdata);
                modelAndView.addObject(CINDEX_KEY,cindexList);
                modelAndView.addObject(CINDEX,keyWord);
                return modelAndView;
            }
        }
        return new ModelAndView("redirect301:/mtopic/");
    }


    /**
     * 专题下：详情页 综述页 图片页  报价页  相关商品页  咨询页
     * 通过判断condition的值 执行具体的方法
     */
    @RequestMapping(value = "/{titleId}/{condition}", method = RequestMethod.GET)
    public ModelAndView getTitleCondition(@PathVariable(value = "titleId",required = true)String titleId, @PathVariable(value = "condition",required = true)String condition, HttpServletRequest request, HttpServletResponse response){
        if (!conditionList.contains(condition)){
            String path = "topic/"+titleId+"/"+condition;
            List<Title> zhuanTiByTitlePath = titleService.findZhuanTiByTitlePath(path);
            if (zhuanTiByTitlePath!=null && !zhuanTiByTitlePath.isEmpty()){
                Integer titleIdTemp = zhuanTiByTitlePath.get(0).getId();
                return new ModelAndView("redirect301:/topic/"+titleIdTemp);
            }
        }


        if (!isInteger(titleId)) {
            String path = "topic/"+titleId;
            List<Title> zhuanTiByTitlePath = titleService.findZhuanTiByTitlePath(path);
            if (zhuanTiByTitlePath!=null&&!zhuanTiByTitlePath.isEmpty()){
                Integer titleIdTemp = zhuanTiByTitlePath.get(0).getId();
                return new ModelAndView("redirect301:/topic/"+titleIdTemp+"/"+condition+".html");
            }
        }

        if (StringUtils.isNullOrBlank(condition)){
            return new ModelAndView("redirect301:/topic/"+titleId+"/");
        }

        if (isInteger(titleId)){
            String requestURI = request.getRequestURI();
            if(!requestURI.endsWith(".html")) {
                int indexOf = requestURI.indexOf("mtopic");
                String uri = requestURI.substring(indexOf-1);
                return new ModelAndView("redirect301:"+uri+".html");
            }
            Title title = titleService.getTitleById(Integer.valueOf(titleId));
            if(title == null){
                return new ModelAndView("redirect301:/topic/");
            }
            ModelAndView modelAndView ;
            if (condition.equals("content")){
                modelAndView = new ModelAndView("vm_topic_wap/vm-productDetail");
                Map<String, Object> productDetail = null;
                Map<String,Object> mainInfo = null;
                if (cache.exists("seotopic_productDetail/"+titleId)){
                    try {
                        String productDetailString = cache.get("seotopic_productDetail/"+titleId);
                        productDetail = (Map<String, Object>) JSON.parse(productDetailString);
                    } catch (Exception e) {
                        productDetail = getProductDetail(Integer.valueOf(titleId));
                        String productDetailString = JSON.toJSONString(productDetail);
                        cache.set("seotopic_productDetail/"+titleId,productDetailString);
                    }
                }else {
                    productDetail = getProductDetail(Integer.valueOf(titleId));
                    String productDetailString = JSON.toJSONString(productDetail);
                    cache.set("seotopic_productDetail/"+titleId,productDetailString);
                }
                if (cache.exists("seotopic_mainInfo/"+titleId)){
                    try {
                        String mainInfoString = cache.get("seotopic_mainInfo/"+titleId);
                        mainInfo = (Map<String, Object>) JSON.parse(mainInfoString);
                    } catch (Exception e) {
                        mainInfo = getMainInfo(Integer.valueOf(titleId));
                        String mainInfoString = JSON.toJSONString(mainInfo);
                        cache.set("seotopic_mainInfo/"+titleId,mainInfoString);
                    }
                }else {
                    mainInfo = getMainInfo(Integer.valueOf(titleId));
                    String mainInfoString = JSON.toJSONString(mainInfo);
                    cache.set("seotopic_mainInfo/"+titleId,mainInfoString);
                }
                modelAndView.addAllObjects(productDetail);
                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
                modelAndView.addObject("mainInfo",mainInfo);
            } else if (condition.equals("price")){
                modelAndView = new ModelAndView("vm_topic_wap/vm-productPrice");
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);
                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                List<Map<String, Object>> productRelation = getProductRelationFromRedis(titleId);
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("productRelation",productRelation);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
            } else if (condition.equals("images")){
                modelAndView = new ModelAndView("vm_topic_wap/vm-productImages");
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);

//                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoods(Integer.valueOf(titleId));
//                List<Map<String, Object>> littleGoodsImages = getZhuanTiGoods(Integer.valueOf(titleId));
//                List<Map<String, Object>> everyOneSerach = getEveryOneSerach(Integer.valueOf(titleId));
//                List<Map<String, Object>> brandZoneList = getBrandZoneList(Integer.valueOf(titleId));
                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("littleGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
            } else if (condition.equals("comments")){
                modelAndView = new ModelAndView("vm_topic_wap/vm-productComments");
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);
                List<Map<String, Object>> productComments = null;
                if (cache.exists("seotopic_commentsProduct/"+titleId)){
                    try {
                        String productCommentsString = cache.get("seotopic_commentsProduct/"+titleId);
                        productComments = (List<Map<String, Object>>) JSON.parse(productCommentsString);
                    } catch (Exception e) {
                        productComments = getProductComments(Integer.valueOf(titleId), "product_comment");
                        String commentsProductString = JSON.toJSONString(productComments);
                        cache.set("seotopic_commentsProduct/"+titleId,commentsProductString);
                    }
                }else {
                    productComments = getProductComments(Integer.valueOf(titleId), "product_comment");
                    String commentsProductString = JSON.toJSONString(productComments);
                    cache.set("seotopic_commentsProduct/"+titleId,commentsProductString);
                }
                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                modelAndView.addObject("productComments",productComments);
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
            } else if (condition.equals("consulting")){
                modelAndView = new ModelAndView("vm_topic_wap/vm-productConsult");
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);
                List<Map<String, Object>> productAdvisory = null;
                if (cache.exists("seotopic_advisoryProduct/"+titleId)){
                    try {
                        String advisoryProductString = cache.get("seotopic_advisoryProduct/"+titleId);
                        productAdvisory = (List<Map<String, Object>>) JSON.parse(advisoryProductString);
                    } catch (Exception e) {
                        productAdvisory = getProductAdvisory(Integer.valueOf(titleId),20);
                        String advisoryProductString = JSON.toJSONString(productAdvisory);
                        cache.set("seotopic_advisoryProduct/"+titleId,advisoryProductString);
                    }
                }else {
                    productAdvisory = getProductAdvisory(Integer.valueOf(titleId),20);
                    String advisoryProductString = JSON.toJSONString(productAdvisory);
                    cache.set("seotopic_advisoryProduct/"+titleId,advisoryProductString);
                }

                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                modelAndView.addObject("productAdvisory",productAdvisory);
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
            } else if (condition.equals("product")){
                modelAndView = new ModelAndView("vm_topic_wap/vm-productRelation");
                List<Map<String, Object>> everyOneSerach = getEveryOneSerachFromRedis(titleId);
                List<Map<String, Object>> brandZoneList = getBrandZoneListFromRedis(titleId);
                List<Map<String, Object>> zhuanTiGoodsImages = getZhuanTiGoodsImagesFromRedis(titleId);
                List<Map<String, Object>> productRelation = getProductRelationFromRedis(titleId);
                List<Map<String, Object>> tecentList = getTecentList(Integer.valueOf(titleId));
                modelAndView.addObject("productRelation",productRelation);
                modelAndView.addObject("zhuanTiGoodsImages",zhuanTiGoodsImages);
                modelAndView.addObject("everyOneSerach",everyOneSerach);
                modelAndView.addObject("brandZoneList",brandZoneList);
                modelAndView.addObject("tecentList",tecentList);
            } else {
                return new ModelAndView("redirect301:/topic/"+titleId+"/");
            }

            int newsCount ;
            newsCount = newService.getNewsCountByTid(title.getId());//获得专题中文章数量
            title.setNewsCount(newsCount);
            title.setKeywords(keyWordService.getKeywordByTitleId(title.getId()));
            modelAndView.addObject("newsCount", newsCount);
            List<KeyWord> tagList = keyWordService.findTagsByTitleId(title.getId(), 2);   //标签 ： type =2;
            modelAndView.addObject("tag_list", tagList);
            modelAndView.addObject(TITLEKEY, title);

            String goodsId = title.getGoodsId();
            logger.info("QueryItemByIdDataPreInterceptor_doIntercept_goodsId=" + goodsId);

            Map<String, Object> item = null;
            if (cache.exists("seotopic_item/"+titleId)){
                String itemString = cache.get("seotopic_item/" + titleId);
                try {
                    item = (Map<String, Object>) JSON.parse(itemString);
                } catch (Exception e) {
                    item  = HttpClientUtil.getATGProductInfoByID(goodsId,gomeProductService,goodsAppraiseQueryService,prodDetailService,priceService);
                }
            }else {
                item  = HttpClientUtil.getATGProductInfoByID(goodsId,gomeProductService,goodsAppraiseQueryService,prodDetailService,priceService);
            }

            if (null != item) {
                String newsGoodsImagesUrl = null;
                if (cache.exists("seotopic_imgUrl/"+titleId)){
                    try {
                        newsGoodsImagesUrl = cache.get("seotopic_imgUrl/"+titleId);
                    } catch (Exception e) {
                        newsGoodsImagesUrl = getNewsGoods(goodsId);
                        cache.set("seotopic_imgUrl/"+titleId,newsGoodsImagesUrl);
                    }
                }else {
                    newsGoodsImagesUrl = getNewsGoods(goodsId);
                    cache.set("seotopic_imgUrl/"+titleId,newsGoodsImagesUrl);
                }
                modelAndView.addAllObjects(item);
                //获取第一张专题商品图片
                modelAndView.addObject("imgUrl", newsGoodsImagesUrl);
            }

            String brandName = null;
            String facetId = null;
            ProductItem product = null;

            if (cache.exists("seotopic_brandname/"+titleId)){
                try {
                    brandName = cache.get("seotopic_brandname/"+titleId);
                } catch (Exception e) {
                    product = productInfoService.getProdDetailByProductId(goodsId);
                    ProductBrandBean brandinfo = productInfoService.getBrandByBrandId(product.getBrandId());
                    brandName = brandinfo.getcName();
                }
            }else {
                product = productInfoService.getProdDetailByProductId(goodsId);
                ProductBrandBean brandinfo = productInfoService.getBrandByBrandId(product.getBrandId());
                brandName = brandinfo.getcName();
            }

            if (cache.exists("seotopic_facetId/"+titleId)){
                facetId = cache.get("seotopic_facetId/"+titleId);
            }else {
                facetId = prodSpecService.getFacetIdByBrandId( Long.parseLong(product.getBrandId()));
            }
            modelAndView.addObject("brandname", brandName);
            modelAndView.addObject("facetId", facetId);
            String skuIdTemp = null;
            try {
                if(title!=null && null !=title.getSkuId()){
                    skuIdTemp = title.getSkuId();
                }else {
                    //注意，有大萝卜坑，一定要加非空判断
                    if (product!=null && product.getSkuIds()!=null){
                        String[] skuIds = product.getSkuIds().split(":");
                        if (skuIds!=null && skuIds.length!=0){
                            skuIdTemp = skuIds[0];
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            modelAndView.addObject("skuId", skuIdTemp);
            return modelAndView;
        }
        return new ModelAndView("redirect301:/topic/");
    }

    @RequestMapping(value = "/{titleId}/{path}/{condition}", method = RequestMethod.GET)
    public ModelAndView getTitleCondition(@PathVariable(value = "titleId",required = true)String titleId,@PathVariable(value = "path",required = true)String path, @PathVariable(value = "condition",required = true)String condition, HttpServletRequest request, HttpServletResponse response){
        String readPath = "topic/"+titleId+"/"+path;
        List<Title> zhuanTiByTitlePath = titleService.findZhuanTiByTitlePath(readPath);
        if (zhuanTiByTitlePath!=null && !zhuanTiByTitlePath.isEmpty()){
            Integer titleIdTemp = zhuanTiByTitlePath.get(0).getId();
            return new ModelAndView("redirect301:/topic/"+titleIdTemp+"/"+condition+".html");
        }

        return new ModelAndView("redirect301:/topic/");
    }


        /**
         * 分页处理
         * @return  pageMap
         */
    private Map<String, Object> processPageNum(Map<String,Object> pageMap) {
        if (null == pageMap || pageMap.isEmpty()) {
            return null;
        }
        int pageNumber = (Integer) pageMap.get("pageNumber");
        int totalCount = (Integer) pageMap.get("totalCount");
        List<Integer> pageList = new ArrayList<Integer>();
        int start = 1;
        int end = 6;
        if (totalCount > 5) {
            if (pageNumber < 3) {
                calculatePageNum(start, end, pageList);
            } else if (pageNumber > (totalCount - 3)) {
                start = totalCount - 5;
                end = totalCount + 1;
                calculatePageNum(start, end, pageList);
            } else {
                start = pageNumber - 2;
                end = pageNumber + 3;
                calculatePageNum(start, end, pageList);
            }
        } else {
            end = totalCount + 1;
            calculatePageNum(start, end, pageList);
        }
        /*
         * 下一页、上一页 pageList preNo nextNo
         */
        int preNo = pageNumber - 1;
        int nextNo = pageNumber + 1;
        pageMap.put("pageList", pageList);
        pageMap.put("preNo", preNo);
        pageMap.put("nextNo", nextNo);
        return pageMap;

    }

    private void calculatePageNum(int start, int end, List<Integer> pageList) {
        if (null == pageList) {
            pageList = new ArrayList<Integer>();
        }
        for (int i = start; i < end; i++) {
            pageList.add(i);
        }
    }

    /**
     *获取知识详情页商品信息
     */
    private String getNewsGoods(String tgoodId) {
        String News_imgUrl = "";
        logger.info(" getZhuanTiGoods 获取专题图片参数 tgoodId:" + tgoodId );

        String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
        // tgoodId = "A0005825249";
        Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(tgoodId, properties);
        FSGoods fSGoods = null;
        String skuId;
        if (categoryInfoMap != null) {
            skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));
            if (skuId != null && !"".equals(skuId)) {
                String[] skuIds = skuId.split(":");
                if (skuIds.length!=0){
                    skuId = skuIds[0];
                }
            }
            try {
                fSGoods = itemProductInfoService.getFSGoods(tgoodId, skuId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (fSGoods != null) {
            List<FSImgBase> fsImaList = fSGoods.getGoodsImgs();
            String productId = fSGoods.getProductId();
            String sku = fSGoods.getSkuId();
            logger.info(" getZhuanTiGoods 专题图片返回数据 productId:" + productId + ",skuId:" + sku);
            // FSLinkImg

            if (fsImaList != null && !fsImaList.isEmpty()) {
                FSLinkImg fSLinkImg = (FSLinkImg) fsImaList.get(0);
                News_imgUrl = fSLinkImg.getSrc();
            }
        }
        return News_imgUrl;
    }

    private List<Map<String,Object>> getProductRelation(Integer titleId) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Title title = null;
        try {
            title = titleService.getTitleById(titleId);
            resultList = getSuitableRelationProduct(title.getTitle(),title.getGoodsId());
            if (resultList.size()<6){
                List<Title> titlesList = new ArrayList<Title>();
                List<KeyWord> tagList = keyWordService.findTagsByTitleId(titleId, 2);   //标签 ： type =2;
                for (KeyWord keyWord : tagList) {
                    resultList = getSuitableRelationProduct(keyWord.getNames(),title.getGoodsId());
                    if (resultList.size()>=6){
                        break;
                    }
                }
            }
            if (resultList.size()<6){
                resultList = getSuitableRelationProduct("手机",title.getGoodsId());
            }

            if (resultList.size()>6){
                resultList = resultList.subList(0,6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;

    }

    private List<Map<String,Object>> getSuitableRelationProduct(String name,String goodId){
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        try {
            JSONObject jsonObject = null;
            try {
                jsonObject = dubboSearchService.productSearch(getDefaultSearchJSONObject(name));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject content = jsonObject.getJSONObject("content");
            JSONObject pageBar = content.getJSONObject("pageBar");
            Integer totalCount = pageBar.getInteger("totalCount");
            logger.info("搜索名："+name+" 专题下商品总数为 ："+totalCount);
            if (totalCount!=0){
                JSONObject prodInfo = content.getJSONObject("prodInfo");
                JSONArray productArray = prodInfo.getJSONArray("products");
                for (int i = 0; i < productArray.size(); i++) {
                    try {
                        Map<String, Object> map = new HashMap<String, Object>();
                        JSONObject js = (JSONObject) productArray.get(i);
                        String newSkuId = js.getString("skuId");
                        String newProductId = js.getString("pId");
                        if (newProductId.equals(goodId)){
                            continue;
                        }
                        String imageUrl = js.getString("sImg")+"_180.jpg";
                        String newProductName = js.getString("alt");
                        GoodsInfo goodsInfo = itemProductInfoService.getGoodsInfo(newProductId, newSkuId, "180");
                        PrdSpecBean spec = prodSpecService.getSpec(newProductId, newSkuId);
                        Map<String, List<String[]>> resultMap = spec.getResult();
                        List<String[]> mainList = resultMap.get("returnList");
                        StringBuilder maininfo = new StringBuilder();
                        if (mainList != null && !mainList.isEmpty()) {
                            for (String[] strs : mainList) {
                                maininfo.append(strs[0]).append(":").append(strs[1]).append(";");
                            }
                        }
                        if (goodsInfo!=null){
                            map.put("detailHref", goodsInfo.getDetailHref());
                            map.put("price", goodsInfo.getListPrice());
                        }
                        map.put("imgUrl", imageUrl);
                        map.put("productname", newProductName);
                        map.put("id", newProductId);
                        map.put("skuId", newSkuId);
                        map.put("maininfo", maininfo.toString());
                        resultList.add(map);
                        if (resultList.size()>=5){
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            logger.info("getSuitableRelationProduct出现异常",e);
        }
        return resultList;
    }





    private Map<String,Object> getMainInfo(Integer titleId){
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Title title = titleService.getTitleById(Integer.valueOf(titleId));
            String goodId = title.getGoodsId();
            String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
            Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(goodId, properties);
            if (categoryInfoMap != null) {
                String skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));
                if (skuId != null && !"".equals(skuId)) {
                    String[] skuIds = skuId.split(":");
                    if (skuIds.length!=0){
                        skuId = skuIds[0];
                    }
                }
                if (goodId != null && goodId.length() > 0) {
                    if (skuId != null && skuId.length() > 0) {
                        GoodsInfo goodsInfo = null;
                        PrdSpecBean spec = null;
                        try {
                            goodsInfo = itemProductInfoService.getGoodsInfo(goodId, skuId, "180");
                            spec = prodSpecService.getSpec(goodId, skuId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Map<String, List<String[]>> resultMap = spec.getResult();
                        List<String[]> mainList = resultMap.get("returnList");
                        StringBuilder maininfo = new StringBuilder();
                        if (mainList != null && !mainList.isEmpty()) {
                            for (String[] strs : mainList) {
                                maininfo.append(strs[0]).append(":").append(strs[1]).append(";");
                            }
                        }
                        if (goodsInfo!=null){
                            map.put("imgUrl", goodsInfo.getGoodsImgUrl());
                            map.put("productname", goodsInfo.getGoodsName());
                            map.put("detailHref", goodsInfo.getDetailHref());
                            map.put("price", goodsInfo.getListPrice());
                        }
                        map.put("id", goodId);
                        map.put("skuId", skuId);
                        map.put("maininfo", maininfo.toString());

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }



    private List<Map<String,Object>> getNewsReleated(Integer titleId){
        //等文章标签开发好了后，需要存入文章标签的相关内容 以及文章头像的相关字段
        Map<String,Object> map  = new HashMap<String,Object>();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            map.put("titleId", titleId);
            List<News> newList = newService.findNewsList(map);
            if(newList != null  && !newList.isEmpty()){
                if(newList.size()>5){
                    newList = newList.subList(0, 5);
                }
                for(News news :newList){
                    Map<String, Object> search = new HashMap<String, Object>();
                    search.put("topic", news.getTopic());
                    search.put("path", news.getPaths());
                    search.put("picUrl", news.getPicUrl());
                    //标签
                    List<Label> labelList = labelService.getNewsRelatedLabelByNewsID(news.getId());
                    if(labelList.size() > 3){
                        labelList = labelList.subList(0, 3);
                    }
                    search.put("labelList", labelList);
                    String content = news.getContents();
                    content = StringUtils.removeHtmlTag(content);
                    content = content.trim().replaceAll("\\s*", "");
                    //如果要限制长度 StringUtils.substringForNews(orignal, 15)； 其中第二个参数为字符长度
                    search.put("contents", content);

                    search.put("createtime", news.getCreateTime());
                    search.put("id", news.getId());
                    result.add(search);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Map<String,Object>> getProductComments(Integer titleId,String condition){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Title title = titleService.getTitleById(titleId);
            String tgoodId = title.getGoodsId();
            int number = 1;
            if("product_comment".equals(condition)){
                number = 20;
            }
            List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getLatestAppraiseSEO(tgoodId, number);
            logger.info("商品专题-商品评论页-ATG商品评论数据，传入的商品id为productid=" + tgoodId);
            // 返回结果的新建

            // 根据目前模板的信息放入到结果列表中，为了模板暂时不修改，此处做字段对应
            if (appraiseSEOList != null && !appraiseSEOList.isEmpty()) {
                for (AppraiseSEO appraiseSEO : appraiseSEOList) {
                    if (appraiseSEO != null) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        // 评论用户头像
                        map.put("accountimgurl", appraiseSEO.getUserImgUrl());
                        // 评论用户id
                        map.put("accountid", appraiseSEO.getUserId());
                        // 评论时间
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (appraiseSEO.getPostTime() != null) {
                            map.put("commitdate", sdf.format(appraiseSEO.getPostTime()));
                        }
                        // 使用心得
                        map.put("tuijiandian", appraiseSEO.getRecommend());
                        // 好评度
                        map.put("prdPraise", appraiseSEO.getGoodCommentPercent());
                        // 评论用户等级
                        map.put("accountlevel", appraiseSEO.getUserLevel());
                        map.put("shiyongxinde", appraiseSEO.getContent());
                        list.add(map);
                    }
                }
            }
            logger.info("商品专题-商品评论页-ATG商品评论数据，评价数量=" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Map<String,Object>> getProductAdvisory(Integer titleId,Integer limitNumber){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Title title = titleService.getTitleById(titleId);
            String tgoodId = title.getGoodsId();
            List<GoodsQuestionSEO> goodsQuestionSEOList = goodsQuestionSEOService.getLatestGoodsQuestionSEO(tgoodId, limitNumber);
            if(goodsQuestionSEOList!=null){
                if (goodsQuestionSEOList.size()>limitNumber){
                    goodsQuestionSEOList = goodsQuestionSEOList.subList(0,limitNumber);
                }
            }
            if (goodsQuestionSEOList != null && !goodsQuestionSEOList.isEmpty()) {
                for (GoodsQuestionSEO goodsQuestionSEO : goodsQuestionSEOList) {
                    if (goodsQuestionSEO != null) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        // 评论用户id
                        map.put("accountid", goodsQuestionSEO.getUserId());
                        // 评论时间
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (goodsQuestionSEO.getPostTime() != null) {
                            map.put("commitdate", sdf.format(goodsQuestionSEO.getPostTime()));
                        }
                        // 使用心得
                        map.put("zixun", goodsQuestionSEO.getQuestion());
                        // 评论用户等级
                        map.put("accountlevel", goodsQuestionSEO.getUserLevel());
                        // 推荐点
                        map.put("huifu", goodsQuestionSEO.getAnswer());
                        map.put("path", title.getPaths());
                        map.put("titleName", title.getTitle());
                        list.add(map);
                    }
                }
            }
            logger.info("商品专题-商品咨询页-ATG商品咨询数据，评价数量=" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private List<Map<String,Object>> getEveryOneSerach(Integer titleId){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try{
            Title title = titleService.getTitleById(titleId);
            String tgoodId = title.getGoodsId();
            String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
            logger.info("调用dubbo接口prodDetailService.getProductMultiProperties传入参数:tgoodId="+tgoodId+",properties="+Arrays.toString(properties));
            Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(tgoodId, properties);
            logger.info("调用dubbo接口prodDetailService.getProductMultiProperties返回数据:"+ JSONObject.toJSONString(categoryInfoMap));
            if (categoryInfoMap != null) {
                String skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));
                if (skuId != null && !"".equals(skuId)) {
                    String[] skuIds = skuId.split(":");
                    if (skuIds.length!=0){
                        skuId = skuIds[0];
                    }
                }
                String thcategoryId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
                String brandItemId = String.valueOf(categoryInfoMap.get("brandItemId"));
                //根据三级分类id取得二级分类ID信息
                String[] propertiest=new String[]{"parentCatId"};
                Map<String,String> secondCateMap = categoryDetailService.getCategoryMultiProperties(thcategoryId,propertiest);
                String secondCategoryId = secondCateMap.get("parentCatId");
                logger.info("调用dubbo接口prodDetailService.getRankings传入参数:skuId="+skuId+",tgoodId="+tgoodId+",thcategoryId="+thcategoryId);
                String httpUrl = "http://bigd.gome.com.cn/gome/rec?imagesize=130&boxid=box82&sid="+skuId+"&callback=rec&pid="+tgoodId+"&c2id="+secondCategoryId+"&c3id="+thcategoryId+"&brid"+brandItemId;
                logger.info("大家都在读http接口访问链接："+httpUrl);
                String productStr = singleHttpClientUtil.getJsonValue(httpUrl);
                if(productStr != null && productStr != "" && productStr.indexOf('[')!=-1 && productStr.indexOf(']')!=-1){
                    list = HttpClientUtil.parseJson(productStr.substring(productStr.indexOf('['), productStr.indexOf(']')+1));
                }

                for (Map<String, Object> stringObjectMap : list) {
                    String purl = toWapGoodsUrl((String) stringObjectMap.get("purl"));
                    stringObjectMap.put("purl",purl);
                }
            }
        }catch (Exception e) {
            logger.info("生成模块 category_hot_sales 错误："+e.getMessage());
        }
        if(list.size() > 6){
            return list.subList(0, 6);
        }else{
            return list;
        }
    }



    public List<Map<String, Object>> queryDJDZMData(Map<String, Object> data, Title title, int maxnum) {
        // 返回集合
        List<Map<String, Object>> infoMapList = new ArrayList<Map<String, Object>>();
        if (title != null && title.getGoodsId() != null) {
            // 查询销量榜
            RankReturnValue rankings = queryMainRankings(data, title.getGoodsId());
            if (rankings != null) {
                Map<RankingTypeEnum, List<RankingPro>> rankmap = rankings.getMapRanking();
                // 同品类热销榜
                List<RankingPro> categorylist = rankmap.get(RankingTypeEnum.CATEGORYRANKING);
                // 同品牌热销榜
                List<RankingPro> brandlist = rankmap.get(RankingTypeEnum.BRANDRANKING);
                // 同品类填充
                for (RankingPro info : categorylist) {
                    if (infoMapList.size() >= maxnum) {
                        return infoMapList;
                    }
                    if (!isDuplicateGoods(data, title.getId(), info.getPid(), info.getSid())) {
                        Map<String, Object> infomap = new HashMap<String, Object>();
                        infomap.put("imgUrl", info.getIurl());
                        infomap.put("productname", info.getPn());
                        infomap.put("detailHref", toWapGoodsUrl(info.getPurl()));
                        infomap.put("price", info.getGprice());
                        infoMapList.add(infomap);
                    }
                }
                // 同品牌填充
                for (RankingPro info : brandlist) {
                    if (infoMapList.size() >= maxnum) {
                        return infoMapList;
                    }
                    if (!isDuplicateGoods(data, title.getId(), info.getPid(), info.getSid())) {
                        Map<String, Object> infomap = new HashMap<String, Object>();
                        infomap.put("imgUrl", info.getIurl());
                        infomap.put("productname", info.getPn());
                        infomap.put("detailHref", toWapGoodsUrl(info.getPurl()));
                        infomap.put("price", info.getGprice());
                        infoMapList.add(infomap);
                    }
                }
            }
        }
        return infoMapList;
    }


    private List<Map<String, Object>> getBrandZoneList(Integer titleId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Title title = titleService.getTitleById(titleId);
            String tgoodId = title.getGoodsId();
            logger.info("商品专题-商品综述页-ATG商品品牌大全数据获取开始");
            String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
            Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(tgoodId, properties);
            if (categoryInfoMap != null) {
                String catId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
                List<Relevant> relevantList = prodDetailService.getRelevantBrand(catId);
                if (relevantList != null && !relevantList.isEmpty()) {
                    if (relevantList.size() > 6) {
                        relevantList = relevantList.subList(0, 6);
                    }
                    for (Relevant rel : relevantList) {
                        if (rel != null) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("brandname", rel.getDisplayName());
                            String bandurl = "https://m.gome.com.cn/category.html?from=1&scat=2&key_word=" + rel.getDisplayName();
                            map.put("bandurl", bandurl);
                            list.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Map<String,Object>> getTecentList (Integer titleId){
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            List<Title> titlesList = new ArrayList<Title>();
            List<KeyWord> tagList = keyWordService.findTagsByTitleId(titleId, 2);   //标签 ： type =2;
            for (KeyWord keyWord : tagList) {
                try {
                    List<Title> titleListTemp = titleService.findZhuanTiByTagId(keyWord.getId());
                    titlesList.addAll(titleListTemp);
                    if (!titlesList.isEmpty()&&titlesList.size()>10){
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!titlesList.isEmpty()&&titlesList.size()>10){
                titlesList = titlesList.subList(0,10);
            }else {
                int lackSize = 10-titlesList.size();
                List<Title> recentTitles = titleService.findRecentZhuanti();
                recentTitles = recentTitles.subList(0,lackSize);
                titlesList.addAll(recentTitles);
            }

            if(titlesList != null && !titlesList.isEmpty()){
                for(Title title:titlesList){
                    Map<String, Object> search = new HashMap<String, Object>();
                    search.put("name", title.getTitle());
                    search.put("url", title.getPaths());
                    result.add(search);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;


    }

    private Map<String,Object> getProductDetail(Integer titleId){
        Map<String,Object> map = new HashMap<String, Object>();
        try {
            // 获得商品描述内容
            Title title = titleService.getTitleById(titleId);
            String tgoodId = title.getGoodsId();

            String desc = singleHttpClientUtil.getAtgProductDescByID(tgoodId);
            if (null != desc && !"".equals(desc)) {
                map.put(DESC_KEY, desc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }




    private List<Map<String, Object>> getZhuanTiGoods(Integer titleId) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        try {
            Title title = titleService.getTitleById(titleId);
            String tgoodId = title.getGoodsId();

            logger.info(" getZhuanTiGoods 获取专题图片参数 tgoodId:" + tgoodId + ",imageSize:" + 160);


            String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
            // tgoodId = "A0005825249";
            Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(tgoodId, properties);
            FSGoods fSGoods = null;
            String skuId = null;
            if (categoryInfoMap != null) {
                skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));
                if (skuId != null && !"".equals(skuId)) {
                    String[] skuIds = skuId.split(":");
                    if (skuIds.length!=0){
                        skuId = skuIds[0];
                    }
                }
                try {
                    fSGoods = itemProductInfoService.getFSGoods(tgoodId, skuId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (fSGoods != null) {
                List<FSImgBase> fsImaList = fSGoods.getGoodsImgs();
                String productname = fSGoods.getName();
                String description = fSGoods.getDescription();
                String productId = fSGoods.getProductId();
                String sku = fSGoods.getSkuId();
                logger.info(" getZhuanTiGoods 专题图片返回数据 productId:" + productId + ",skuId:" + sku);
                // FSLinkImg
                if (fsImaList != null && !fsImaList.isEmpty()) {
                    for (FSImgBase fsImgBase : fsImaList) {
                        Map<String, Object> params = new HashMap<String, Object>();
                        FSLinkImg fSLinkImg = (FSLinkImg) fsImgBase;
                        String imgUrl = fSLinkImg.getSrc();
                        String alt = fSLinkImg.getAlt();
                        String href = fSLinkImg.getHref();
                        params.put("imgUrl", imgUrl);
                        params.put("imgUrlSize", imgUrl + "_" + 160 + ".jpg");
                        params.put("productname", productname);
                        params.put("id", tgoodId);
                        params.put("skuId", skuId);
                        params.put("href", href);
                        params.put("alt", alt);
                        params.put("description", description);
                        resultList.add(params);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;

    }


    private List<Map<String,Object>> getRecentNews(){
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            List<News> newList = newService.findRecentNewsList();
            if(newList != null  && !newList.isEmpty()){
                for(News news :newList){
                    Map<String, Object> search = new HashMap<String, Object>();
                    search.put("topic", news.getTopic());
                    search.put("path", news.getPaths());
                    search.put("picUrl", news.getPicUrl());
                    //标签
                    List<Label> labelList = labelService.getNewsRelatedLabelByNewsID(news.getId());
                    if(labelList.size() > 3){
                        labelList = labelList.subList(0, 3);
                    }
                    search.put("labelList", labelList);
                    String content = news.getContents();
                    content = StringUtils.removeHtmlTag(content);
                    content = content.trim().replaceAll("\\s*", "");
                    //如果要限制长度 StringUtils.substringForNews(orignal, 15)； 其中第二个参数为字符长度
                    search.put("contents", content);
                    search.put("createtime", news.getCreateTime());
                    search.put("id", news.getId());
                    result.add(search);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }



    private List<Map<String,Object>> getEveryOneSerachFromRedis(String titleId){
        List<Map<String,Object>> everyOneSerach = null;
        if (cache.exists("seowaptopic_everyOneSerach/"+titleId)){
            try {
                String everyOneSerachString = cache.get("seowaptopic_everyOneSerach/"+titleId);
                everyOneSerach = (List<Map<String, Object>>) JSON.parse(everyOneSerachString);
            } catch (Exception e) {
                everyOneSerach = getEveryOneSerach(Integer.valueOf(titleId));
                String everyOneSerachString = JSON.toJSONString(everyOneSerach);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(everyOneSerachString)){
                    cache.set("seowaptopic_everyOneSerach/"+titleId,everyOneSerachString);
                }
            }
        }else {
            everyOneSerach = getEveryOneSerach(Integer.valueOf(titleId));
            String everyOneSerachString = JSON.toJSONString(everyOneSerach);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(everyOneSerachString)){
                cache.set("seowaptopic_everyOneSerach/"+titleId,everyOneSerachString);
            }

        }
        return everyOneSerach;
    }

    private List<Map<String,Object>> getBrandZoneListFromRedis(String titleId){
        List<Map<String,Object>> brandZoneList = null;
        if (cache.exists("seowaptopic_brandZoneList/"+titleId)){
            try {
                String brandZoneListString = cache.get("seowaptopic_brandZoneList/"+titleId);
                brandZoneList = (List<Map<String, Object>>) JSON.parse(brandZoneListString);
            } catch (Exception e) {
                brandZoneList = getBrandZoneList(Integer.valueOf(titleId));
                String brandZoneListString = JSON.toJSONString(brandZoneList);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(brandZoneListString)){
                    cache.set("seowaptopic_brandZoneList/"+titleId,brandZoneListString);
                }
            }
        }else {
            brandZoneList = getBrandZoneList(Integer.valueOf(titleId));
            String brandZoneListString = JSON.toJSONString(brandZoneList);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(brandZoneListString)){
                cache.set("seowaptopic_brandZoneList/"+titleId,brandZoneListString);
            }

        }
        return brandZoneList;
    }

    private List<Map<String,Object>> getZhuanTiGoodsImagesFromRedis(String titleId){
        List<Map<String,Object>> zhuanTiGoodsImages = null;
        if (cache.exists("seotopic_zhuanTiGoodsImages/"+titleId)){
            try {
                String zhuanTiGoodsImagesString = cache.get("seotopic_zhuanTiGoodsImages/"+titleId);
                zhuanTiGoodsImages = (List<Map<String, Object>>) JSON.parse(zhuanTiGoodsImagesString);
            } catch (Exception e) {
                zhuanTiGoodsImages = getZhuanTiGoods(Integer.valueOf(titleId));
                String zhuanTiGoodsImagesString = JSON.toJSONString(zhuanTiGoodsImages);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(zhuanTiGoodsImagesString)){
                    cache.set("seotopic_zhuanTiGoodsImages/"+titleId,zhuanTiGoodsImagesString);
                }
            }
        }else {
            zhuanTiGoodsImages = getZhuanTiGoods(Integer.valueOf(titleId));
            String zhuanTiGoodsImagesString = JSON.toJSONString(zhuanTiGoodsImages);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(zhuanTiGoodsImagesString)){
                cache.set("seotopic_zhuanTiGoodsImages/"+titleId,zhuanTiGoodsImagesString);
            }
        }
        return zhuanTiGoodsImages;
    }



    private List<Map<String,Object>> getProductRelationFromRedis(String titleId){
        List<Map<String,Object>> productRelation = null;
        if (cache.exists("seotopic_productRelation/" + titleId)){
            try {
                String productRelationString = cache.get("seotopic_productRelation/" + titleId);
                productRelation = (List<Map<String, Object>>) JSON.parse(productRelationString);
            } catch (Exception e) {
                productRelation = getProductRelation(Integer.valueOf(titleId));
                String productRelationString = JSON.toJSONString(productRelation);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(productRelationString)){
                    cache.set("seotopic_productRelation/"+titleId,productRelationString);
                }
            }
        }else {
            productRelation = getProductRelation(Integer.valueOf(titleId));
            String productRelationString = JSON.toJSONString(productRelation);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(productRelationString)){
                cache.set("seotopic_productRelation/"+titleId,productRelationString);
            }
        }
        return productRelation;
    }


    public JSONObject getDefaultSearchJSONObject(String titleName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from","robot");
        jsonObject.put("question",titleName);
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


    private boolean isDuplicateGoods(Map<String, Object> data, Integer titleid, String proid, String skuid) {
        String key = CACHE_KEY_SHOW_GOODS + titleid;
        if (!data.containsKey(key)) {
            data.put(key, new HashSet<String>());
        }
        String goodskey = proid + "_" + skuid;
        @SuppressWarnings("unchecked")
        Set<String> goodskeys = (Set<String>) data.get(key);
        if (goodskeys.contains(goodskey)) {
            return true;
        } else {
            goodskeys.add(goodskey);
            data.put(key, goodskeys);
            return false;
        }
    }

    /**
     * 链接转换
     *
     * @param pcurl
     *            PC端商品详情页链接
     * @return wap端商品详情页链接
     */
    private String toWapGoodsUrl(String pcurl) {
        try {
            String path = pcurl.substring(pcurl.indexOf("gome.com.cn/") + 12, pcurl.length());
            return "//item.m.gome.com.cn/product-" + path;
        } catch (Exception e) {
            return pcurl;
        }
    }

    /**
     * 查询该商品对应排行榜
     *
     * @param data
     *            公共部分数据
     * @param goodsid
     *            商品ID
     * @return 排行榜信息
     */
    private RankReturnValue queryMainRankings(Map<String, Object> data, String goodsid) {
        String key = CACHE_KEY_MAIN_RANKINGS + goodsid;
        if (data.containsKey(key)) {
            return (RankReturnValue) data.get(key);
        } else {
            Map<String, String> goodsinfo = queryMainGoodsInfo(data, goodsid);
            if (goodsinfo != null) {
                // 查询销量榜
                RankReturnValue rankings = prodDetailService.getRankings(goodsinfo.get("skuid"), goodsid, goodsinfo.get("thridcatid"));
                if (rankings != null) {
                    data.put(key, rankings);
                }
                return rankings;
            }
            return null;
        }
    }

    /**
     * 根据标题查询主商品相关信息
     * 如：
     * brandid 品牌ID
     * thridcatid 三级分类
     * skuid 商品sku
     *
     * @param data
     * @param goodsid
     *            主商品ID
     * @return 商品相关信息
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> queryMainGoodsInfo(Map<String, Object> data, String goodsid) {
        String key = CACHE_KEY_MAIN_GOODS + goodsid;
        if (data.containsKey(key)) {
            return (Map<String, String>) data.get(key);
        } else {
            // 根据主商品ID查询商品相关信息
            String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
            Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(goodsid, properties);
            if (categoryInfoMap != null) {
                String brandid = String.valueOf(categoryInfoMap.get("brandItemId"));
                String skuItemIds = String.valueOf(categoryInfoMap.get("skuItemIds"));
                // skuid
                String skuid = null;
                if (skuItemIds != null && !"".equals(skuItemIds)) {
                    skuid = skuItemIds.split(":")[0];
                }
                // 三级分类
                String thridcatid = String.valueOf(categoryInfoMap.get("thridCatItemId"));
                Map<String, String> infomap = new HashMap<String, String>();
                infomap.put("brandid", brandid);
                infomap.put("skuid", skuid);
                infomap.put("thridcatid", thridcatid);
                data.put(key, infomap);
                return infomap;
            } else {
                return null;
            }
        }
    }









}





