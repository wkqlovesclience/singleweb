package com.gome.stage.service.impl.rank;

import com.gome.qiantai.model.GoodsAppraiseInfoVO;
import com.gome.qiantai.service.IGoodsAppraiseQueryService;
import com.gome.stage.bean.Ranking.NewProductRank;
import com.gome.stage.bean.Ranking.SimpleProduct;
import com.gome.stage.entity.rank.RankIndexFloor;
import com.gome.stage.entity.rank.Titles;
import com.gome.stage.interfaces.item.IGomeProductService;
import com.gome.stage.interfaces.item.IPriceService;
import com.gome.stage.interfaces.seo.newrank.IGomeNewProductRankService;
import com.gome.stage.interfaces.seo.newrank.IGomeSEOSaleRank;
import com.gome.stage.item.GomePrice;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.mapper.rank.RankIndexFloorMapper;
import com.gome.stage.service.interfaces.rank.IRankManager;
import com.gome.stage.utils.ConstantsUtil;
import com.gome.stage.utils.HttpClientUtil;
import com.gome.stage.utils.PublishUtil;
import com.gome.stage.utils.SEOPropertiesUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class RankManagerImpl implements IRankManager {

    private GomeLogger logger = GomeLoggerFactory
            .getLogger(RankManagerImpl.class);

    // 接口地址中存放变量名
    private static final String CAT3ID = "{cat3Id}";

    private static final String FACETID = "{facetId}";

    // 商品上架状态
    private final int ON_SALE_STATUS = 4;
    @Autowired
    private RankIndexFloorMapper rankIndexFloorMapper;
    @Autowired
    private IPriceService priceService;
    @Autowired
    private IGomeProductService gomeProductService;
    @Autowired
    private IGomeSEOSaleRank gomeSEOSaleRank;
    @Autowired
    private IGomeNewProductRankService gomeNewProductRankService;
    @Autowired
    private IGoodsAppraiseQueryService goodsAppraiseQueryService;
    private String saleInfo_purl = SEOPropertiesUtils.getStringValueByKey("saleInfo_purl","item.gome.com.cn/");
    private String saleInfo_wapUrl = SEOPropertiesUtils.getStringValueByKey("saleInfo_wapUrl","item.m.gome.com.cn/product-");

    @Override
    public List<RankIndexFloor> queryRankIndexShowFloor() {
        return rankIndexFloorMapper.getShowFloor();
    }

    /**
     * 根据标题创建时间查询此标题创建之前最近N个专题信息
     *
     * @param createtime
     *            创建时间
     * @param topnum
     *            最新前N个
     * @return 专题信息
     */
    @Override
    public List<Titles> findCurrentTitlesByCreateTime(Date createtime, int topnum) {
        return rankIndexFloorMapper.findCurrentTitlesByCreateTime(createtime, topnum);
    }


    @Override
    public List<Map<String, Object>> querySaleInfosByFacetId(String cat3Id, String facetId) {
        try {
            // 请求URL
            String url = ConstantsUtil.RANK_SEARCH_URL_MODEL.replace(CAT3ID, cat3Id);
            url = url.replace(FACETID, facetId);
            // 返回JSON
            logger.info(url);
            String jsonstr = HttpClientUtil.getJsonValue(url);
            JSONArray products = null;
            try {
                // 获取商品数据
                products = JSONObject.fromObject(jsonstr).getJSONObject("content")
                        .getJSONObject("prodInfo").getJSONArray("products");
            } catch (Exception e) {
                logger.info("分类为"+cat3Id+"筛选项"+facetId+"获取商品数据失败");
                e.printStackTrace();
            }
            List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
            if (products == null) {
                return returnList;
            }

            for (int i = 0; i < products.size(); i++) {
                JSONObject obj = products.getJSONObject(i);
                if (obj == null) {
                    continue;
                }
                Map<String, Object> info = new HashMap<String, Object>();
                // 商品ID
                String pid = obj.getString("pId");
                logger.info("排行榜品牌商品信息：" + obj.toString() );
                // 判断上下架状态
                int state = gomeProductService.getProudctState(pid);
                if (state != ON_SALE_STATUS) {
                    continue;
                }
                info.put("pid", pid);
                // SkuID
                String sid = obj.getString("skuId");
                info.put("sid", sid);
                // 商品名称
                info.put("pn", obj.getString("name"));
                // 商品PC地址
                info.put("purl", obj.getString("sUrl"));
                // 商品PC地址
                info.put("wapUrl", obj.getString("sUrl").replaceFirst(saleInfo_purl,saleInfo_wapUrl));
                // 商品 图片
                info.put("iurl", obj.getString("sImg"));
                //评论数
                info.put("evaluateCount", obj.getString("evaluateCount"));
                // 推荐指数
                info.put("evaluateNum", PublishUtil.getRecommendNum(i + 1));
                // 获取商品价格
                Double price = null;
                if (sid != null) {
                    GomePrice gomePrice = priceService.getGomePrice(sid);
                    if (gomePrice != null) {
                        price = gomePrice.getFinalPrice();
                    }
                }
                info.put("price", price);
                returnList.add(info);
            }
            return returnList;
        } catch (Exception e) {
            logger.error("排行榜页根据三级分类ID和筛选项ID获取商品销量信息失败！cat3Id=" + cat3Id + ",facetId=" + facetId, e);
            return null;
        }

    }

    @Override
    public List<Map<String, Object>> queryHotSaleInfosByCat3Id(String cat3Id) {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        List<SimpleProduct> queryList = gomeSEOSaleRank.getProdctThirdCateRank(cat3Id);
        int maxSaleNum = 0;
        for (int i = 0; queryList != null && i < queryList.size(); i++) {
            SimpleProduct product = queryList.get(i);
            //设置最大销量数
            if(i==0){
                maxSaleNum = product.getSaleNum();
            }
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

            //评论数
            info.put("evaluateCount", evaluateCount);
            //推荐指数
            info.put("evaluateNum", PublishUtil.getRecommendNum(i + 1));
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
            info.put("progressNum", PublishUtil.getProgressNum(product.getSaleNum(), maxSaleNum));
            returnList.add(info);
        }
        return returnList;
    }

    @Override
    public List<Map<String, Object>> queryNewSaleInfosByCat3Id(String cat3Id) {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
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
            info.put("evaluateNum", PublishUtil.getRecommendNum(i + 1));
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
            returnList.add(info);
        }
        return returnList;
    }

    /**
     * 查询商品评论信息
     * @param pid 商品productID
     * @return 评论信息
     */
    private GoodsAppraiseInfoVO queryProductEvaluate(String pid) {
        //查询评论
        GoodsAppraiseInfoVO goodsAppraiseInfoVO = goodsAppraiseQueryService.queryInfoVO(pid);
        return goodsAppraiseInfoVO;
    }
    private String product_purl = SEOPropertiesUtils.getStringValueByKey("product_purl","//item.gome.com.cn/");
    private String product_wapUrl = SEOPropertiesUtils.getStringValueByKey("product_wapUrl","//item.m.gome.com.cn/product-");

    private String getProductPageURL(String pid, String sid) {
        return  product_purl + pid + "-" + sid + ".html";
    }

    private String getProductPageWapURL(String pid, String sid) {
        return product_wapUrl + pid + "-" + sid + ".html";
    }

}
