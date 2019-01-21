/**
 * @author tianyu-ds
 * @updateDate 2015-7-15
 * @project_name trunk
 * @company 国美在线有限公司
 * @desc 将打印日志的信息重新更新整理，使日志信息更加规范，更有目的性的查找问题。
 */
package com.gome.stage.utils;

import com.gome.frontSe.comm.FSImgBase;
import com.gome.frontSe.interfaces.IProdDetailService;
import com.gome.qiantai.model.AppraiseSEO;
import com.gome.qiantai.model.GoodsAppraiseInfoVO;
import com.gome.qiantai.service.IAppraiseSEOService;
import com.gome.qiantai.service.IGoodsAppraiseQueryService;
import com.gome.stage.interfaces.item.IGomeProductService;
import com.gome.stage.interfaces.item.IPriceService;
import com.gome.stage.interfaces.item.IProductInfoService;
import com.gome.stage.item.GomePrice;
import com.gome.stage.item.SkuItem;
import com.gome.tapho.bean.spec.PrdSpecBean;
import com.gome.tapho.interfaces.spec.IProdSpecService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	private static final int CONNECTION_TIMEOUT = 50 * 1000;
	private static final int REQUEST_TIMEOUT = 50 * 1000;

	/**
	 *
	 * 读取 http页面并返回 http页面内容
	 *
	 * @param url
	 * @return
	 */
	public static String readHttpPage(String url) {
		String retStr = "{}";

		HttpClient httpClient = new HttpClient();
		// 设置连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		// 设置读取超时时间
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(REQUEST_TIMEOUT);
		GetMethod getMethod = new GetMethod(url);
		getMethod.setRequestHeader("Connection", "close");
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet()));
				StringBuilder sb = new StringBuilder();
				String resTemp = "";
				while ((resTemp = in.readLine()) != null) {
					sb.append(resTemp);
				}
				retStr = sb.toString();
				logger.info("HttpClientUtil_readHttpPage: requestUrl=" + url + ", statusCode=" + statusCode
						+ ", result_length=" + retStr.length());
			} else {
				int retryStatusCode = 0;
				for (int i = 3; i > 0; i--) {
					retryStatusCode = httpClient.executeMethod(getMethod);
					if (retryStatusCode != HttpStatus.SC_OK) {
						continue;
					}
					BufferedReader in = new BufferedReader(
							new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet()));
					StringBuilder sb = new StringBuilder();
					String resTemp = "";
					while ((resTemp = in.readLine()) != null) {
						sb.append(resTemp);
					}
					retStr = sb.toString();
					break;
				}
				logger.info("HttpClientUtil_readHttpPage: url=" + url + ", statusCode=" + retryStatusCode
						+ ", result_length=" + retStr.length());
			}
		} catch (HttpException e) {
			logger.error("HttpClientUtil_error_readHttpPage_http异常:" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("HttpClientUtil_error_readHttpPage_io异常:" + e.getMessage(), e);
		} finally {
			getMethod.releaseConnection();
		}
		return retStr;
	}

	public static String getJsonValue(String urlStr) {

		HttpClient httpClient = new HttpClient();
		// 设置连接超时时间
		// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		// 设置读取超时时间
		// httpClient.getHttpConnectionManager().getParams().setSoTimeout(REQUEST_TIMEOUT);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(REQUEST_TIMEOUT);
		GetMethod get = new GetMethod(urlStr);

		// PostMethod post=new PostMethod(urlStr);
		get.setRequestHeader("Connection", "close");
		// 使用系统提供的默认的恢复策略
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

		String resultJson = "{}";
		try {
			// 执行get
			int statusCode = httpClient.executeMethod(get);
			if (statusCode == HttpStatus.SC_OK) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(get.getResponseBodyAsStream(), get.getResponseCharSet()));
				StringBuilder sb = new StringBuilder();
				String resTemp = "";
				while ((resTemp = in.readLine()) != null) {
					sb.append(resTemp);
				}
				resultJson = sb.toString();
				logger.info("HttpClientUtil_getJsonValue: requestUrl=" + urlStr + ", statusCode=" + statusCode
						+ ", result_length=" + resultJson.length());
			} else {
				int retryStatusCode = 0;
				for (int i = 3; i > 0; i--) {
					retryStatusCode = httpClient.executeMethod(get);
					if (retryStatusCode != HttpStatus.SC_OK) {
						continue;
					}
					BufferedReader in = new BufferedReader(
							new InputStreamReader(get.getResponseBodyAsStream(), get.getResponseCharSet()));
					StringBuilder sb = new StringBuilder();
					String resTemp = "";
					while ((resTemp = in.readLine()) != null) {
						sb.append(resTemp);
					}
					resultJson = sb.toString();
					break;
				}
				logger.info("HttpClientUtil_getJsonValue: URL=" + urlStr + ", statusCode=" + retryStatusCode
						+ ", result_length=" + resultJson.length());
			}
		} catch (IOException e) {
			/*
			 * String errStr = "与接口通讯出现异常，通讯失败，请稍后重试。URL:" + urlStr; throw new
			 * HttpException(errStr, e);
			 */
			logger.error(urlStr+"    与接口通讯出现异常，通讯失败，请稍后重试。URL异常:" + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			/*
			 * String errStr = "接口返回文档中某个值不符合格式，例如：CUSTID应为整数时，为空。URL:" +
			 * urlStr; throw new IllegalArgumentException(errStr, e);
			 */
			logger.error(urlStr+"    接口返回文档中某个值不符合格式，例如：CUSTID应为整数时，为空。URL异常:" + e.getMessage(), e);
		} finally {
			get.releaseConnection();
		}

		return resultJson;
	}

	public static List<Map<String, Object>> parseJson(String jsonString) {
		if (StringUtils.isNullOrBlank(jsonString) || "{}".equals(jsonString)) {
			return PaginatedArrayList.emptyList();
		}
		List<Map<String, Object>> result = null;
		try {
			JSONArray productArr = JSONArray.fromObject(jsonString);
			result = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < productArr.size(); i++) {
				JSONObject product = productArr.getJSONObject(i);
				Map<String, Object> nodemap = new HashMap<String, Object>();
				for (Iterator<?> iter = product.keys(); iter.hasNext();) {
					String key = (String) iter.next();
					nodemap.put(key, product.get(key));
				}
				result.add(nodemap);
			}
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_parseJson异常:" + e.getMessage(), e);
			return PaginatedArrayList.emptyList();
		} finally {
		}
		return result;
	}

	/**
	 * 封装排行榜首页最新商品评价
	 *
	 * @param appraiseSEOList
	 * @return
	 */
	public static List<Map<String, Object>> parseAppraiseSEO(List<AppraiseSEO> appraiseSEOList) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if (appraiseSEOList != null && !appraiseSEOList.isEmpty()) {
			for (AppraiseSEO appraiseSEO : appraiseSEOList) {
				Map<String, Object> reMap = new HashMap<String, Object>();
				String href = appraiseSEO.getHref();
				String title = appraiseSEO.getContent();

				reMap.put("href", href);
				reMap.put("title", title);

				resultList.add(reMap);

			}
		}

		return resultList;
	}

	/**
	 * 转换Json数组
	 *
	 * @param jsonString
	 *            JSONArray 字符串
	 * @param totalCount
	 *            需要的数据条数
	 * @return
	 */
	public static List<Map<String, Object>> parseJsonArr(String jsonString, int totalCount) {
		if (StringUtils.isNullOrBlank(jsonString) || "{}".equals(jsonString) || !isRightFormatJsonArray(jsonString)) {
			return PaginatedArrayList.emptyList();
		}
		JSONArray productArr = JSONArray.fromObject(jsonString);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < productArr.size(); i++) {
				JSONObject product = productArr.getJSONObject(i);
				Map<String, Object> nodemap = new HashMap<String, Object>();
				for (Iterator<?> iter = product.keys(); iter.hasNext();) {
					String key = (String) iter.next();
					nodemap.put(key, product.get(key));
				}
				result.add(nodemap);
			}
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_parseJsonArr异常:" + e.getMessage(), e);
		} finally {
		}
		if (totalCount > 0 && result.size() > totalCount) {
			return result.subList(0, totalCount);
		}
		return result;
	}

	/**
	 *
	 * @param paramArr
	 * @param totalCount
	 * @return
	 */
	public static List<Map<String, Object>> parseJsonArrToList(JSONArray paramArr, int totalCount) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (paramArr == null) {
			return result;
		}
		try {
			for (int i = 0; i < paramArr.size(); i++) {
				JSONObject product = paramArr.getJSONObject(i);
				Map<String, Object> nodemap = new HashMap<String, Object>();
				for (Iterator<?> iter = product.keys(); iter.hasNext();) {
					String key = (String) iter.next();
					nodemap.put(key, product.get(key));
				}
				result.add(nodemap);
			}
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_parseJsonArrToList异常:" + e.getMessage(), e);
		} finally {
		}
		if (totalCount > 0 && result.size() > totalCount) {
			return result.subList(0, totalCount);
		}
		return result;
	}

	/**
	 * 转换Json数组，根据商品Id区分，不重复
	 *
	 *            JSONArray 字符串
	 * @param totalCount
	 *            需要的数据条数
	 * @return
	 */
	public static List<Map<String, Object>> parseJsonArrNotRepeat(JSONArray productArr, int totalCount,
																  String excludeProductId) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String resultStr = "";
		try {
			for (int i = 0; i < productArr.size(); i++) {
				JSONObject product = productArr.getJSONObject(i);
				String productId = product.getString("id");
				if (productId.equals(excludeProductId) || resultStr.contains(productId)) {
					continue;
				}
				resultStr += productId + ",";
				Map<String, Object> nodemap = new HashMap<String, Object>();
				for (Iterator<?> iter = product.keys(); iter.hasNext();) {
					String key = (String) iter.next();
					nodemap.put(key, product.get(key));
				}
				result.add(nodemap);
			}
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_parseJsonArrNotRepeat异常:" + e.getMessage(), e);
			return result;
		} finally {
		}
		if (totalCount > 0 && result.size() > totalCount) {
			return result.subList(0, totalCount);
		}
		return result;
	}

	/**
	 * 获得商品描述信息
	 *
	 * @param productId
	 *            ， 商品ID
	 * @return
	 */
	public static String getAtgProductDescByID(String productId, IProdDetailService prodDetailService,
											   IProductInfoService itemProductInfoService) {
		String retStr = "";
		String url = "";

		try {

			String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
			// productId = "A0004973378";
			Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
			if (categoryInfoMap != null) {
				String skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));
				if (skuId != null && !"".equals(skuId)) {
					String[] skuIds = skuId.split(":");
					skuId = skuIds[0];
				}
				logger.info("HttpClientUtil_getAtgProductDescByID productId:" + productId + ",skuId:" + skuId);
				// skuId = "sku26350012";
				Map<String, String> decMap = itemProductInfoService.getSkuLongDesc(productId, skuId);

				logger.info("HttpClientUtil_getAtgProductDescByID decMap:" + decMap);

				/*url = SEOPropertiesUtils.getStringValueByKey("desc_pre_url", "http://desc.gome.com.cn/html/")
						+ decMap.get(skuId);*/
				url = "http://desc.gome.com.cn/html/" + decMap.get(skuId);



			}

			// String atgJspPageUrl =
			// PropertiesUtils.getStringValueByKey("atgJspPageUrl") + productId;
			//
			// logger.info("HttpClientUtil_getAtgProductDescByID:
			// atgJspPageUrl=" + atgJspPageUrl);
			//
			// String jsonStr = HttpClientUtil.readHttpPage(atgJspPageUrl);
			//
			// JSONObject demoJson = getMyJsonObject(atgJspPageUrl, jsonStr);
			// if (demoJson == null) {
			// return "";
			// }
			//
			// url = demoJson.get("descritpionUrl").toString();

			String desc = "";

			if (null != url && !"".equals(url)) {
				desc = HttpClientUtil.readHttpPage(url);
			}

			retStr = desc.replaceAll("^jianjie\\(\"", "").replaceAll("\"\\)$", "");

			// 双协议去掉http:
			retStr = retStr.replaceAll("http:", "");

		} catch (Exception e) {
			logger.error("HttpClientUtil_error_getAtgProductDescByID异常:" + e.getMessage(), e);
			retStr = "";
			return retStr;
		}

		return retStr;

	}

	/**
	 * 获得商品信息 不包括上面描述信息
	 *
	 * @param productId
	 * @return
	 */
	public static Map<String, Object> getATGProductInfoByID(String productId, IGomeProductService gomeProductService, IGoodsAppraiseQueryService goodsAppraiseQueryService,
															IProdDetailService prodDetailService, IPriceService priceService) {

		Map<String, Object> retMap = new HashMap<String, Object>();

		// 修改为diamond的获取方式

		/*String atgJspPageUrl = PropertiesUtils.getStringValueByKey("atgJspProductCategoryUrl") + productId;
		logger.info("HttpClientUtil_getAtgProductInfoByID: atgJspPageUrl=" + atgJspPageUrl);
*/
		try {
			//三级分类id
			String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
			Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
			if (categoryInfoMap != null) {
				String thridCatItemId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
				retMap.put("thridCatItemId", thridCatItemId);

				String skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));


				if (skuId != null && !"".equals(skuId)) {
					String[] skuIds = skuId.split(":");
					skuId = skuIds[0];
					retMap.put("skuId", skuId);
				}

				//价格:

				GomePrice gomePrice = priceService.getGomePrice(skuId);
				if(gomePrice != null){
					Double goods_price = gomePrice.getFinalPrice();
					retMap.put("goodPrice", goods_price);
				}

			}

			//获取评论数以及三级分类id

			GoodsAppraiseInfoVO goodAppraiseInfo = goodsAppraiseQueryService.queryInfoVO(productId);
			//String haoping = "100%";
			int haopingInt = 0;
			int totalNum = 0;
			if(goodAppraiseInfo != null && !"null".equals(goodAppraiseInfo)){
				haopingInt = goodAppraiseInfo.getHaoping();
				totalNum = goodAppraiseInfo.getTotalNum();
			}

			retMap.put("haoping", haopingInt);
			retMap.put("commentNum",totalNum);

			//商品专题改版之 获取商品上下架状态
			List<String> productIds = new ArrayList<String>();
			productIds.add(productId);
			List<Integer> statusList = gomeProductService.getProductStates(productIds);

			Integer status = null;
			if(statusList != null && !statusList.isEmpty()){
				status = statusList.get(0);
			}

			//结果集中存储商品上下架状态 用于在模板中判断商品是上架还是下架   4为上价状态 不为4则显示商品下架页面部分的展示
			retMap.put("goodsStatus", status);



		} catch (Exception e) {
			logger.error("HttpClientUtil_error_getATGProductInfoByID异常:" + e.getMessage(), e);
			retMap = null;
			return retMap;
		}
		return retMap;
	}
	/**
	 * 获取热搜第一个商品的信息
	 */
	private static Map<String, Object> getHotKeywordsFirstNode(Map<String, Object> catInfoMap) {
		String productDomainPath = SEOPropertiesUtils.getStringValueByKey("productDomainPath","");
		Map<String, Object> firstMapNode = new HashMap<String, Object>();
		firstMapNode.put("productName", catInfoMap.get("productname"));
		firstMapNode.put("productId", catInfoMap.get("id"));
		firstMapNode.put("skuId", catInfoMap.get("skuId"));
		firstMapNode.put("productUrl", productDomainPath + "/" + catInfoMap.get("id") + ".html");
		String imageURL = catInfoMap.get("imgUrl") + "_160.jpg";
		firstMapNode.put("imgUrl", imageURL);
		firstMapNode.put("price", catInfoMap.get("price"));

		return firstMapNode;
	}

	/**
	 * 根据热搜商品Id，获取热搜详情页相关商品推荐信息
	 */
	public static List<Map<String, Object>> getHotKeywodsProductRelated(String productId, String httpUrl,
																		IProdSpecService prodSpecService, IProdDetailService prodDetailService, IPriceService priceService) {
		logger.info("getHotKeywodsProductRelated start!");

		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> categoryInfoMapPut = new HashMap<String, Object>();
		String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
		Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
		if (categoryInfoMap != null) {
			String catId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
			String brandId = String.valueOf(categoryInfoMap.get("brandItemId"));
			String skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));

			logger.info("getHotKeywodsProductRelated productId:" + productId + ",catId:" + catId + ",brandId:" + brandId
					+ ",skuId:" + skuId);

			if (skuId != null && !"".equals(skuId)) {
				String[] skuIds = skuId.split(":");
				skuId = skuIds[0];
			}

			logger.info("getHotKeywodsProductRelated skuId:" + skuId);

			categoryInfoMapPut.put("skuId", skuId);
			categoryInfoMapPut.put("id", productId);
			SkuItem skuItem = prodDetailService.getSku(productId, skuId, null);

			List<FSImgBase> fSImgBaseList = null;
			String productname = null;
			Double price = null;

			if (skuItem != null) {
				fSImgBaseList = skuItem.getSkuImgs();
				productname = skuItem.getDisplayName();
				//价格不再通过这个获取，而是走专门的价格接口获取
				//price = skuItem.getSalePrice().toString();
			}

			if(skuId !=null){
				GomePrice gomePrice = priceService.getGomePrice(skuId);
				if(gomePrice != null){
					price = gomePrice.getFinalPrice();
				}
			}
			String imgUrl = null;
			if (fSImgBaseList != null && !fSImgBaseList.isEmpty()) {
				FSImgBase fSImgBase = fSImgBaseList.get(0);
				imgUrl = fSImgBase.getSrc();
			}
			categoryInfoMapPut.put("productname", productname);
			categoryInfoMapPut.put("imgUrl", imgUrl);
			categoryInfoMapPut.put("price", price);

			String faceId = "";
			if (brandId != null && !"".equals(brandId) && !"null".equals(brandId)) {
				faceId = prodSpecService.getFacetIdByBrandId(Long.valueOf(brandId));
			}
			logger.info("getHotKeywodsProductRelated faceId:" + faceId);
			try {
				// add the first product info
				resultMap.add(getHotKeywordsFirstNode(categoryInfoMapPut));
				// String params = "{\"pageSize\":" + 8 + "\"catId\":" +"\"" +
				// catId + "\""+",\"facets\":"+"\"" + brandId + "\"}";
				/*
				 * String params = "{\"pageSize\":" + 8 + ",\"catId\":" + "\"" +
				 * catId + "\"" + ",\"facets\":" + "\"" + faceId + "\"}";
				 */
				String requestUrl = httpUrl + "8/1/10/0/" + catId + "/" + faceId + "/0/0/0/0?from=brand";
				// String paramJson = URLEncoder.encode(params, "UTF-8");
				// String requestUrl = httpUrl + "&catId=" + catId + "&brandId="
				// + brandId;
				// String requestUrl = httpUrl + paramJson;
				logger.info("HttpClientUtil_getHotKeywodsProductRelated: requestUrl=" + requestUrl);
				String searchInfo = getJsonValue(requestUrl);
				JSONObject productsInfoObject = getMyJsonObject(requestUrl, searchInfo);
				if (productsInfoObject == null || productsInfoObject.isEmpty()) {
					return resultMap;
				}

				JSONObject contentJson = productsInfoObject.getJSONObject("content");
				if (contentJson == null || contentJson.isNullObject()) {
					return resultMap;
				}
				JSONObject prodInfoJson = contentJson.getJSONObject("prodInfo");

				if (prodInfoJson == null || prodInfoJson.isNullObject()) {
					return resultMap;
				}
				JSONArray productArr = prodInfoJson.getJSONArray("products");
				if (productArr == null || productArr.isEmpty()) {
					return resultMap;
				}

				randomSwap(productArr);
				resultMap.addAll(getSearchDataByInterface(productArr, 7, 160));

			} catch (Exception e) {
				logger.error("HttpClientUtil_error_getHotKeywodsProductRelated异常:" + e.getMessage(), e);
				resultMap = null;
				return resultMap;
			}
		} else {
			resultMap = null;
		}
		logger.info("getHotKeywodsProductRelated end!");
		return resultMap;
	}

	/**
	 * 根据热搜商品Id，获取热搜详情页相关商品推荐信息
	 */
	public static List<Map<String, Object>> getWapHotKeywodsProductRelated(String productId, String httpUrl,
																		   IProdSpecService prodSpecService, IProdDetailService prodDetailService, IPriceService priceService) {
		logger.info("getWapHotKeywodsProductRelated start!");
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();

		String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
		Map<String, Object> categoryInfoMapPut = new HashMap<String, Object>();
		Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
		if (categoryInfoMap != null) {
			String catId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
			String brandId = String.valueOf(categoryInfoMap.get("brandItemId"));
			String skuId = String.valueOf(categoryInfoMap.get("skuItemIds"));

			logger.info("getWapHotKeywodsProductRelated productId:" + productId + ",catId:" + catId + ",brandId:"
					+ ",skuId:" + skuId);

			if (skuId != null && !"".equals(skuId)) {
				String[] skuIds = skuId.split(":");
				skuId = skuIds[0];
			}
			categoryInfoMapPut.put("skuId", skuId);
			categoryInfoMapPut.put("id", productId);
			SkuItem skuItem = prodDetailService.getSku(productId, skuId, null);

			List<FSImgBase> fSImgBaseList = null;
			String productname = null;
			Double price = null;
			if (skuItem != null) {
				fSImgBaseList = skuItem.getSkuImgs();
				productname = skuItem.getDisplayName();
				//price = skuItem.getSalePrice().toString();
			}

			if(skuId !=null){
				GomePrice gomePrice = priceService.getGomePrice(skuId);
				if(gomePrice != null){
					price = gomePrice.getFinalPrice();
				}
			}

			String imgUrl = null;
			if (fSImgBaseList != null && !fSImgBaseList.isEmpty()) {
				FSImgBase fSImgBase = fSImgBaseList.get(0);
				imgUrl = fSImgBase.getSrc();
			}

			categoryInfoMapPut.put("productname", productname);
			categoryInfoMapPut.put("imgUrl", imgUrl);
			categoryInfoMapPut.put("price", price);

			String faceId = "";
			if (brandId != null && !"".equals(brandId) && !"null".equals(brandId)) {
				faceId = prodSpecService.getFacetIdByBrandId(Long.valueOf(brandId));
			}
			try {
				// add the first product info
				resultMap.add(getHotKeywordsFirstNode(categoryInfoMapPut));

				// String params = "{\"pageSize\":" + 8 + "\"catId\":" +"\"" +
				// catId
				// + "\""+",\"facets\":"+"\"" + brandId + "\"}";
				/*
				 * String params = "{\"pageSize\":" + 8 + ",\"catId\":" + "\"" +
				 * catId + "\"" + ",\"facets\":" + "\"" + faceId + "\"}"; String
				 * paramJson = URLEncoder.encode(params, "UTF-8");
				 */
				// String requestUrl = httpUrl + "&catId=" + catId + "&brandId="
				// +
				// brandId;
				// String requestUrl = httpUrl + paramJson;
				// String requestUrl = httpUrl + "&catId=" + catId + "&brandId="
				// +
				// brandId;
				String requestUrl = httpUrl + "8/1/10/0/" + catId + "/" + faceId + "/0/0/0/0?from=brand";
				logger.info("HttpClientUtil_getWapHotKeywodsProductRelated: requestUrl=" + requestUrl);

				String searchInfo = getJsonValue(requestUrl);
				JSONObject productsInfoObject = getMyJsonObject(requestUrl, searchInfo);
				if (productsInfoObject == null || productsInfoObject.isEmpty()) {
					return resultMap;
				}

				JSONObject contentJson = productsInfoObject.getJSONObject("content");
				if (contentJson == null || contentJson.isNullObject()) {
					return resultMap;
				}
				JSONObject prodInfoJson = contentJson.getJSONObject("prodInfo");

				if (prodInfoJson == null || prodInfoJson.isNullObject()) {
					return resultMap;
				}
				JSONArray productArr = prodInfoJson.getJSONArray("products");
				if (productArr == null || productArr.isEmpty()) {
					return resultMap;
				}
				randomSwap(productArr);
				resultMap.addAll(getWapSearchDataByInterface(productArr, 160));

			} catch (Exception e) {
				logger.error("HttpClientUtil_error_getHotKeywodsProductRelated异常:" + e.getMessage(), e);
				return resultMap;
			}
		} else {
			resultMap = null;
		}
		logger.info("getWapHotKeywodsProductRelated end!");
		return resultMap;
	}

	// 随机打乱搜索组的接口数据，避免页面重复
	public static void randomSwap(JSONArray arr) {
		if (arr == null || arr.isEmpty()) {
			return;
		}
		int i, j;
		JSONObject temp = null;
		Random rand = new Random();
		for (i = 0; i < arr.size(); i++) {
			j = rand.nextInt(1001) % (arr.size() - i) + i;
			temp = arr.getJSONObject(i);
			arr.set(i, arr.getJSONObject(j));
			arr.set(j, temp);
		}
	}

	/**
	 * 获取热词搜索数据
	 *
	 * @param productArr
	 * @param imgSize
	 * @return
	 */
	public static List<Map<String, Object>> getHotTitleSearchDataByInterface(IPriceService priceService, JSONArray productArr, int imgSize) {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();


		if (productArr == null || productArr.isEmpty()) {
			return resultList;
		}


		for (int i = 0; i < productArr.size(); i++) {
			JSONObject obj = productArr.getJSONObject(i);
			String productId = obj.getString("pId");
			String skuId = obj.getString("skuId");
			Integer evaluateCount = obj.getInt("evaluateCount");

			// String skus = obj.getString("skus");
			// JSONObject skusJson = JSONObject.fromObject(skus);

			String productName = obj.getString("name");
			String altName = obj.getString("alt");

			// 获取商品价格
			Double price = null;
			if (skuId != null) {
				GomePrice gomePrice = priceService.getGomePrice(skuId);
				if (gomePrice != null) {
					price = gomePrice.getFinalPrice();
				}
			}

			String imgUrl = obj.getString("sImg");
			String sUrl = obj.getString("sUrl");
			// String pUrl = obj.getString("pUrl");

			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("productId", productId);
			itemMap.put("skuId", skuId);
			itemMap.put("productName", productName);
			itemMap.put("price", price);
			itemMap.put("imgUrl", imgUrl + "_" + imgSize + ".jpg");
			itemMap.put("sUrl", sUrl);
			// itemMap.put("pUrl", pUrl);
			itemMap.put("evaluateCount", evaluateCount);
			itemMap.put("altName", altName);

			resultList.add(itemMap);
		}

		return resultList;

	}

	// 获取搜索组接口数据
	@SuppressWarnings("unused")
	public static List<Map<String, Object>> getSearchDataByInterface(JSONArray productArr, int totalCount,
																	 int imgSize) {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (productArr == null || productArr.isEmpty()) {
			return resultList;
		}


		String currentEnv = SEOPropertiesUtils.getStringValueByKey("currentEnv","");

		String productDomainPath = SEOPropertiesUtils.getStringValueByKey("productDomainPath","");
		for (int i = 0; i < productArr.size(); i++) {
			JSONObject obj = productArr.getJSONObject(i);
			String productId = obj.getString("pId");
			String skuId = obj.getString("skuId");
			String productUrl = productDomainPath + "/" + productId + ".html";

			// JSONObject skuObj = obj.getJSONObject("skus");

			String productName = obj.getString("name");

			Double price = obj.getDouble("price");
			if (price == null) {
				price = 0.0;
			}

			String imgUrl = "";
			if (obj.containsKey("sImg")) {
				imgUrl = obj.getString("sImg") + "_" + imgSize + ".jpg";
			}

			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("productId", productId);
			itemMap.put("skuId", skuId);
			itemMap.put("productName", productName);
			itemMap.put("productUrl", productUrl);
			itemMap.put("price", price);
			itemMap.put("imgUrl", imgUrl);
			itemMap.put("currentEnv", currentEnv);
			itemMap.put("productDomainPath", productDomainPath);

			resultList.add(itemMap);
		}
		if (totalCount > 0 && resultList.size() > totalCount) {
			return resultList.subList(0, totalCount);
		}
		return resultList;
	}

	// 手机端获取搜索组接口数据
	@SuppressWarnings("unused")
	public static List<Map<String, Object>> getWapSearchDataByInterface(JSONArray productArr, int imgSize) {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (productArr == null || productArr.isEmpty()) {
			return resultList;
		}


		String currentEnv = SEOPropertiesUtils.getStringValueByKey("currentEnv","");
		String productDomainPath = SEOPropertiesUtils.getStringValueByKey("productDomainPath","");
		for (int i = 0; i < productArr.size(); i++) {
			JSONObject obj = productArr.getJSONObject(i);
			String productId = obj.getString("pId");
			String skuId = obj.getString("skuId");
			String productUrl = productDomainPath + "/" + productId + ".html";

			// JSONObject skuObj = obj.getJSONObject("skus");

			String productName = obj.getString("name");

			Double price = obj.getDouble("price");
			if (price == null) {
				price = 0.0;
			}

			String imgUrl = "";
			if (obj.containsKey("sImg")) {
				imgUrl = obj.getString("sImg") + "_" + imgSize + ".jpg";
			}

			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("productId", productId);
			itemMap.put("skuId", skuId);
			itemMap.put("productName", productName);
			itemMap.put("productUrl", productUrl);
			itemMap.put("price", price);
			itemMap.put("imgUrl", imgUrl);
			itemMap.put("currentEnv", currentEnv);
			itemMap.put("productDomainPath", productDomainPath);

			resultList.add(itemMap);
		}
		return resultList;
	}

	/**
	 * 根据商品Id，获取热搜详情页的相关商品评论
	 */
	public static List<Map<String, Object>> getHotwordsCommentsMapData(String productId,
																	   IAppraiseSEOService appraiseSEOService, IProdSpecService prodSpecService,
																	   IProdDetailService prodDetailService, IPriceService priceService) {
		//productId = "A0006110501";
		logger.info("getHotwordsCommentsMapData start!");

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
		Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
		String firstProductId = String.valueOf(productId);
		String catId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
		String brandId = String.valueOf(categoryInfoMap.get("brandItemId"));
		String firstSkuId = String.valueOf(categoryInfoMap.get("skuItemIds"));

		logger.info("getHotwordsCommentsMapData productId:" + productId + ",catId:" + catId + ",brandId:" + brandId
				+ ",firstSkuId:" + firstSkuId);

		if (firstSkuId != null && !"".equals(firstSkuId) && !"null".equals(firstSkuId)) {
			String[] skuIds = firstSkuId.split(":");
			firstSkuId = skuIds[0];
		}
		logger.info("getHotwordsCommentsMapData firstSkuId:" + firstSkuId);

		String faceId = "";
		if (brandId != null && !"".equals(brandId) && !"null".equals(brandId)) {
			faceId = prodSpecService.getFacetIdByBrandId(Long.valueOf(brandId));
		}
		logger.info("getHotwordsCommentsMapData faceId:" + faceId);
		try {

			/*
			 * String params = "{\"pageSize\":" + 8 + ",\"catId\":" + "\"" +
			 * catId + "\"" + ",\"facets\":" + "\"" + faceId + "\"}"; String
			 * paramJson = URLEncoder.encode(params, "UTF-8");
			 */

			// http://data.search.api/p/product/8/1/10/0/catId/facets/0/0/0/0
			/*
			 * String requestUrl =
			 * PropertiesUtils.getStringValueByKey("atgJspHotKeyWordUrl",
			 * "http://data.search.api/p/asynSearch?module=infMC&from=infMC&sort=10")
			 * + paramJson;
			 */
			String requestUrl = SEOPropertiesUtils.getStringValueByKey("atgJspHotKeyWordUrl","") + "8/1/10/0/" + catId + "/"
					+ faceId + "/0/0/0/0?from=brand";
			logger.info("获取热搜详情页的相关商品评论HttpClientUtil_getHotwordsCommentsMapData:requestUrl=" + requestUrl);
			List<String> appraiseParms = null;
			String searchInfo = getJsonValue(requestUrl);
			if (searchInfo == null || "{}".equals(searchInfo)) {
				appraiseParms = new ArrayList<String>();
				appraiseParms.add(firstProductId + "-" + firstSkuId);

				List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

				resultList = getMapDataFromList(appraiseSEOList,priceService);
				logger.info("requestUrl,getJsonValue后获取的字符串为null或者为{}");
				return resultList;
			}

			JSONArray productArr = null;
			JSONObject productsInfoObject = getMyJsonObject(requestUrl, searchInfo);

			if (productsInfoObject != null) {
				JSONObject contentJson = productsInfoObject.getJSONObject("content");
				if (contentJson != null && !contentJson.isNullObject()) {
					JSONObject pageBarJson = contentJson.getJSONObject("pageBar");
					int total = pageBarJson.getInt("totalCount");
					if (total == 0) {
						appraiseParms = new ArrayList<String>();
						appraiseParms.add(firstProductId + "-" + firstSkuId);

						List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

						resultList = getMapDataFromList(appraiseSEOList,priceService);
						logger.info("requestUrl转换为JSONObject后不包含products");
						return resultList;
					}
					JSONObject prodInfoJson = contentJson.getJSONObject("prodInfo");
					if (prodInfoJson != null && !prodInfoJson.isNullObject()) {
						productArr = prodInfoJson.getJSONArray("products");
					}
				}
			}

			if (productArr == null || productArr.isEmpty()) {
				appraiseParms = new ArrayList<String>();
				appraiseParms.add(firstProductId + "-" + firstSkuId);

				List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

				resultList = getMapDataFromList(appraiseSEOList,priceService);
				logger.info("products转换为json数组后为null或者size=0");
				return resultList;
			}

			randomSwap(productArr);

			int loopNum = 1;
			appraiseParms = new ArrayList<String>();
			for (int i = 0; i < productArr.size(); i++) {
				if (loopNum > 7) {
					break;
				}
				JSONObject product = productArr.getJSONObject(i);
				String prodId = product.getString("pId");
				String prodSkuId = product.getString("skuId");

				String productIdSkuIds = prodId + "-" + prodSkuId;

				appraiseParms.add(productIdSkuIds);
				logger.info("生成的productIdSkuIds:" + productIdSkuIds);
				loopNum++;
			}

			List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

			resultList = getMapDataFromList(appraiseSEOList,priceService);
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_getHotwordsCommentsMapData异常:" + e.getMessage(), e);
			return resultList;
		}
		logger.info("getHotwordsCommentsMapData end!");
		return resultList;
	}

	/**
	 * 根据商品Id，获取热搜详情页的相关商品评论
	 */
	public static List<Map<String, Object>> getWapHotwordsCommentsMapData(String productId,
																		  IAppraiseSEOService appraiseSEOService, IProdSpecService prodSpecService,
																		  IProdDetailService prodDetailService, IPriceService priceService) {
		logger.info("getWapHotwordsCommentsMapData start!");
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
		Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
		String firstProductId = String.valueOf(productId);
		String catId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
		String brandId = String.valueOf(categoryInfoMap.get("brandItemId"));
		String firstSkuId = String.valueOf(categoryInfoMap.get("skuItemIds"));

		logger.info("getWapHotwordsCommentsMapData productId:" + productId + ",catId:" + catId + ",brandId:" + brandId
				+ ",firstSkuId:" + firstSkuId);
		if (firstSkuId != null && !"".equals(firstSkuId)) {
			String[] skuIds = firstSkuId.split(":");
			firstSkuId = skuIds[0];
		}
		logger.info("getWapHotwordsCommentsMapData firstSkuId:" + firstSkuId);
		String faceId = "";
		if (brandId != null && !"".equals(brandId) && !"null".equals(brandId)) {
			faceId = prodSpecService.getFacetIdByBrandId(Long.valueOf(brandId));
		}
		try {

			String requestUrl = SEOPropertiesUtils.getStringValueByKey("atgJspHotKeyWordUrl","") + "8/1/10/0/" + catId + "/"
					+ faceId + "/0/0/0/0?from=brand";
			// String requestUrl
			// ="http://apis.atguat.com.cn/p/product/8/1/10/0/"+catId+"/"+faceId+"/0/0/0/0";
			logger.info("获取热搜详情页的相关商品评论HttpClientUtil_getWapHotwordsCommentsMapData:requestUrl=" + requestUrl);
			List<String> appraiseParms = null;
			String searchInfo = getJsonValue(requestUrl);
			if (searchInfo == null || "{}".equals(searchInfo)) {
				appraiseParms = new ArrayList<String>();
				appraiseParms.add(firstProductId + "-" + firstSkuId);

				List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

				resultList = getMapDataFromList(appraiseSEOList,priceService);
				logger.info("requestUrl,getJsonValue后获取的字符串为null或者为{}");
				return resultList;
			}

			/*
			 * JSONObject productsInfoObject = getMyJsonObject(requestUrl,
			 * searchInfo);
			 *
			 * JSONObject contentJson =
			 * productsInfoObject.getJSONObject("content"); JSONObject
			 * prodInfoJson = contentJson.getJSONObject("prodInfo"); JSONArray
			 * productArr = prodInfoJson.getJSONArray("products");
			 *
			 * if (!productsInfoObject.containsKey("content") ||
			 * !contentJson.containsKey("prodInfo") ||
			 * !prodInfoJson.containsKey("products")) { appraiseParms = new
			 * ArrayList<String>(); appraiseParms.add(firstProductId + "-" +
			 * firstSkuId);
			 *
			 * List<AppraiseSEO> appraiseSEOList =
			 * appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);
			 *
			 * resultList = getMapDataFromList(appraiseSEOList);
			 * logger.info("requestUrl转换为JSONObject后不包含products"); return
			 * resultList; }
			 */

			JSONArray productArr = null;
			JSONObject productsInfoObject = getMyJsonObject(requestUrl, searchInfo);
			if (productsInfoObject != null) {
				JSONObject contentJson = productsInfoObject.getJSONObject("content");
				if (contentJson != null && !contentJson.isNullObject()) {
					JSONObject pageBarJson = contentJson.getJSONObject("pageBar");
					int total = pageBarJson.getInt("totalCount");
					if (total == 0) {
						appraiseParms = new ArrayList<String>();
						appraiseParms.add(firstProductId + "-" + firstSkuId);

						List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

						resultList = getMapDataFromList(appraiseSEOList,priceService);
						logger.info("requestUrl转换为JSONObject后不包含products");
						return resultList;
					}
					JSONObject prodInfoJson = contentJson.getJSONObject("prodInfo");
					if (prodInfoJson != null && !prodInfoJson.isNullObject()) {
						productArr = prodInfoJson.getJSONArray("products");
					}
				}
			}

			if (productArr == null || productArr.isEmpty()) {
				appraiseParms = new ArrayList<String>();
				appraiseParms.add(firstProductId + "-" + firstSkuId);

				List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);

				resultList = getMapDataFromList(appraiseSEOList,priceService);
				logger.info("products转换为json数组后为null或者size=0");
				return resultList;
			}

			randomSwap(productArr);

			int loopNum = 1;
			appraiseParms = new ArrayList<String>();
			for (int i = 0; i < productArr.size(); i++) {
				if (loopNum > 7) {
					break;
				}
				JSONObject product = productArr.getJSONObject(i);
				String prodId = product.getString("pId");
				String prodSkuId = product.getString("skuId");

				String productIdSkuIds = prodId + "-" + prodSkuId;

				appraiseParms.add(productIdSkuIds);
				logger.info("生成的productIdSkuIds:" + productIdSkuIds);
				loopNum++;
			}

			List<AppraiseSEO> appraiseSEOList = appraiseSEOService.getRelatedAppraiseSEO(appraiseParms);
			resultList = getMapDataFromList(appraiseSEOList,priceService);
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_getWapHotwordsCommentsMapData异常:" + e.getMessage(), e);
			return resultList;
		}
		logger.info("getWapHotwordsCommentsMapData end!");
		return resultList;
	}

	/**
	 * 封装返回的相关商品评价
	 *
	 * @param appraiseSEOList
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getMapDataFromList(List<AppraiseSEO> appraiseSEOList, IPriceService priceService)  {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if (appraiseSEOList != null && !appraiseSEOList.isEmpty()) {
			for (AppraiseSEO appraiseSEO : appraiseSEOList) {
				if (appraiseSEO != null) {
					Map<String, Object> reMap = new HashMap<String, Object>();
					/**
					 * 获取参数
					 */
					String prdUrl = appraiseSEO.getPrdUrl();
					String imageUrl = appraiseSEO.getImageUrl();
					String productname = appraiseSEO.getProductName();
					Double goods_price = null;
					if(appraiseSEO.getSkuId() !=null){
						GomePrice gomePrice = priceService.getGomePrice(appraiseSEO.getSkuId());
						if(gomePrice != null){
							goods_price = gomePrice.getFinalPrice();
						}
					}
					//double goods_price = appraiseSEO.getGoodsPrice();
					Integer goodCommentPercent = appraiseSEO.getGoodCommentPercent();
					Integer totalCount = appraiseSEO.getTotalCount();
					String prdAdviceURL = null;
					String title = null;
					// 推荐点
					String recommends = appraiseSEO.getRecommend();
					// 使用心得
					String summary = appraiseSEO.getContent();

					List<String> recocontentlist = new ArrayList<String>();
					String[] recommendsArr = recommends.split(",");
					if (recommendsArr != null && recommendsArr.length > 0) {
						for (int i = 0; i < recommendsArr.length; i++) {
							recocontentlist.add(recommendsArr[i]);
						}
					}

					reMap.put("prdUrl", prdUrl);
					reMap.put("imageUrl", imageUrl);
					reMap.put("productname", productname);
					reMap.put("goods_price", goods_price);
					reMap.put("goodCommentPercent", goodCommentPercent);
					reMap.put("totalCount", totalCount);
					reMap.put("prdAdviceURL", prdAdviceURL);
					reMap.put("title", title);
					reMap.put("recocontentlist", recocontentlist);
					reMap.put("summary", summary);

					resultList.add(reMap);
				}
			}

		}

		return resultList;
	}

	/**
	 * 对象接口数据获得
	 */
	public static List<Map<String, Object>> getMapDataFromJsonArray(JSONArray valArr) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Object obj : valArr) {
			JSONObject jsonObject = JSONObject.fromObject(obj);
			if (null != jsonObject) {
				Map<String, Object> map = new HashMap<String, Object>();
				Iterator<?> iter = jsonObject.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					String value = jsonObject.getString(key);
					map.put(key, value);
				}
				resultList.add(map);
			}
		}
		return resultList;
	}

	/**
	 * 对象接口数据获得
	 *
	 * @param url
	 * @return
	 */
	public static Map<String, Object> getMapDataFormJsonUrl(String url) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == url || "".equals(url)) {
			return map;
		}
		try {
			logger.info("HttpClientUtil_getMapDataFormJsonUrl: url=" + url);

			String jsonString = getJsonValue(url);
			if (null != jsonString && jsonString.length() > 1) {
				jsonString = jsonString.trim();
				JSONObject json = null;
				if (jsonString.length() == 0) {
					return map;
				}
				String startChar = "" + jsonString.charAt(0);
				String endChar = "" + jsonString.charAt(jsonString.length() - 1);

				if ("{".equals(startChar) && "}".equals(endChar)) {
					json = JSONObject.fromObject(jsonString);
				} else if ("[".equals(startChar) && "]".equals(endChar)) {
					JSONArray jsonArr = JSONArray.fromObject(jsonString);
					json = (JSONObject) jsonArr.get(0);
				}
				if (null != json) {
					Iterator<?> iter = json.keys();
					while (iter.hasNext()) {
						String key = (String) iter.next();
						String value = json.getString(key);
						map.put(key, value);
					}
				}
			}
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_getMapDataFormJsonUrl异常:" + e.getMessage(), e);
			return map;
		}
		return map;
	}

	/**
	 * 通过分类URL获得商品排行
	 *
	 * @param productId
	 * @param url
	 * @return
	 */
	public static String getProductRankJsonString(String productId, String url) {
		// productId;
		String atgJspPageUrl = SEOPropertiesUtils.getStringValueByKey("atgJspProductCategoryUrl","") + productId;
		logger.info("HttpClientUtil_getProductRankJsonString: atgJspPageUrl=" + atgJspPageUrl);

		Map<String, Object> mapData = getMapDataFormJsonUrl(atgJspPageUrl);
		if (null != mapData) {
			String categoryId = String.valueOf(mapData.get("catalogid"));
			if (null != categoryId && !"".equals(categoryId)) {
				String curl = url + "categoryId=" + categoryId;

				logger.info("HttpClientUtil_getProductRankJsonString: curl=" + curl);

				return readHttpPage(curl);
			}

		}
		return "[]";
	}

	/**
	 * 通过Key 获取配置文件值
	 *
	 * @param key
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getProperties(String key) {
		String retVal = "";
		if (null != key && !"".equals(key)) {

			InputStream inputStream = HttpClientUtil.class.getClass().getClassLoader()
					.getSystemResourceAsStream("config/publish.properties");
			Properties p = new Properties();
			try {
				p.load(inputStream);
				retVal = p.getProperty(key.trim());
			} catch (IOException e1) {
				logger.error("HttpClientUtil_error_getProperties异常:" + e1.getMessage(), e1);
				retVal = "";
				return retVal;
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("HttpClientUtil_error_getProperties异常:" + e.getMessage(), e);
				}
			}
		}

		return retVal;
	}

	/**
	 * 捕捉JSONObject.fromObject异常
	 *
	 */
	public static JSONObject getMyJsonObject(String httpURL, String strJson) {
		JSONObject resultJson = null;
		try {
			resultJson = JSONObject.fromObject(strJson);
		} catch (Exception e) {
			logger.error("HttpClientUtil_error_getMyJsonObject异常:httpURL=" + httpURL + ", errorInfo=" + e.getMessage(), e);
			// MailHelper.sendHtmlMail("SEO Interface Error", httpURL);
			return null;
		}
		return resultJson;
	}

	/**
	 * 正则表达式匹配JSONObject是否正确，只是粗略的判断
	 *
	 */
	public static boolean isRightFormatJsonObject(String strJson) {
		if (strJson == null || "".equals(strJson)) {
			return false;
		}
		strJson = strJson.trim();
		String regex = "^\\{(\\\"[\\s\\S]+\\\"\\:[\\s\\S]*)+\\}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(strJson);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 正则表达式匹配JSONOArray是否正确，只是粗略的判断
	 *
	 */
	public static boolean isRightFormatJsonArray(String strJson) {
		if (strJson == null || "".equals(strJson)) {
			return false;
		}
		strJson = strJson.trim();
		String regex = "^\\[\\s*(\\{(\\\"[\\s\\S]+\\\"\\:[\\s\\S]+)+\\}\\s*,?)+\\s*\\]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(strJson);
		if (matcher.find()) {
			return true;
		}
		return false;
	}


	/**
	 * 主商品productID的三级分类ID去获取最新上架的商品top5

	 *
	 * @param productId
	 * @return
	 */
	public static List<Map<String, Object>> getNewsCommentsMapData(String productId, IProdDetailService prodDetailService, IPriceService priceService, IProdSpecService prodSpecService){
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		JSONArray productArr = null;
		//三级分类id
		String[] properties = new String[] { "brandItemId", "thridCatItemId", "skuItemIds" };
		Map<String, String> categoryInfoMap = prodDetailService.getProductMultiProperties(productId, properties);
		if (categoryInfoMap != null) {
			String thridCatItemId = String.valueOf(categoryInfoMap.get("thridCatItemId"));
			// 修改为diamond的获取方式
			String atgJspPageUrl = SEOPropertiesUtils.getStringValueByKey("atgJspNewsProductCategoryUrl","") + "5/1/10/0/"+thridCatItemId+"/0/0/10/0/0x1000/0?from=seo_search";
			logger.info("HttpClientUtil_getAtgProductInfoByID: atgJspPageUrl=" + atgJspPageUrl);
			try {

				String jsonString = getJsonValue(atgJspPageUrl);
				JSONObject productsInfoObject = HttpClientUtil.getMyJsonObject(atgJspPageUrl, jsonString);
				if (productsInfoObject != null && !productsInfoObject.isEmpty()) {
					JSONObject contentJson = productsInfoObject.getJSONObject("content");
					JSONObject productsJson = contentJson.getJSONObject("prodInfo");
					if (productsJson == null || productsJson.isNullObject() || !productsJson.containsKey("products")) {
						return resultMap;
					}
					productArr = productsJson.getJSONArray("products");
					if (productArr != null && !productArr.isEmpty()) {
						randomSwap(productArr);
						resultMap.addAll(getSearchNewsDataByInterface(productArr,priceService,prodSpecService));
					}
				}

			} catch (Exception e) {
				logger.error("HttpClientUtil_error_getNewsCommentsMapData异常:" + e.getMessage(), e);
			}
		}
		return resultMap;
	}

	// 获取搜索组接口数据,添加商品价格信息。
	public static List<Map<String, Object>> getSearchNewsDataByInterface(JSONArray productArr, IPriceService priceService, IProdSpecService prodSpecService) {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (productArr == null || productArr.isEmpty()) {
			return resultList;
		}

		String currentEnv = SEOPropertiesUtils.getStringValueByKey("currentEnv","");

		String productDomainPath = SEOPropertiesUtils.getStringValueByKey("productDomainPath","");
		for (int i = 0; i < productArr.size(); i++) {
			JSONObject obj = productArr.getJSONObject(i);
			String productId = obj.getString("pId");
			String skuId = obj.getString("skuId");
			String productUrl = productDomainPath + "/" + productId + ".html";
			String productName = obj.getString("name");
			Double goods_price = null ;
			//价格:
			GomePrice gomePrice = priceService.getGomePrice(skuId);
			if(gomePrice != null){
				goods_price = gomePrice.getFinalPrice();
			}

			if (goods_price == null) {
				goods_price = 0.0;
			}

			String imgUrl = "";
			if (obj.containsKey("sImg")) {
				imgUrl = obj.getString("sImg");
			}
			PrdSpecBean spec = prodSpecService.getSpec(productId, skuId);
			Map<String, List<String[]>> resultMap = spec.getResult();
			List<String[]> mainList = resultMap.get("returnList");
			StringBuilder maininfo = new StringBuilder();
			if (mainList != null && !mainList.isEmpty()) {
				for (String[] strs : mainList) {
					maininfo.append(strs[0] + ":" + strs[1] + ";");
				}
			}

			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("id", productId);
			itemMap.put("skuId", skuId);
			itemMap.put("productName", productName);
			itemMap.put("productUrl", productUrl);
			itemMap.put("price", goods_price);
			itemMap.put("imgUrl", imgUrl);
			itemMap.put("currentEnv", currentEnv);
			itemMap.put("productDomainPath", productDomainPath);
			itemMap.put("maininfo", maininfo.toString());
			resultList.add(itemMap);
		}
		return resultList;
	}
}