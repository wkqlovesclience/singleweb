package com.gome.stage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gome.search.dubbo.idl.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.seo.productword.interfaces.IProductWordDubboService;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.tools.AppConfigTools;

import redis.Gcache;

@Controller
@RequestMapping(value = "/chanpin")
public class ProductWordsController {

	private GomeLogger logger = GomeLoggerFactory
			.getLogger(ProductWordsController.class);
	
	@Autowired
	private DubboService dubboSearchService;
	
	@Autowired
	@Qualifier("gcache_productCard")
	private Gcache cache;
	
	@Autowired
	private IProductWordDubboService dubboProductWordService;
	
	private Set<String> keywords;
	
	private final Integer PAGE_SIZE = 120;
	
	
	{
		String[] keys = new String[]{"A", "B", "C","D", "E",
				                     "F","G", "H", "I","J", 
				                     "K", "L","M", "N", "O",
				                     "P", "Q", "R","S","T",
				                     "U", "V", "W","X","Y",
				                     "Z", "0-9"};
		keywords = new HashSet<String>(Arrays.asList(keys));
	}

	/*商品词字母页全部首页         
	 *  /chanpin        全部   没有分页  总共展示业务最新发布的270条商品词
	 *  除了分页的商品词列表数据  字母关键词  配置的友情链接暂时不用
     */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getproductWordsList1(HttpServletRequest request,HttpServletResponse response
			) {
		ModelAndView modle = new ModelAndView("productwords/index");
		try {
			String requestURI = request.getRequestURI();
			System.out.println("请求1"+requestURI);
            if(!requestURI.endsWith("/")) {
        		int indexOf = requestURI.indexOf("chanpin");
        		String uri = requestURI.substring(indexOf-1);
        		System.out.println("重定向"+uri);
        		return new ModelAndView("redirect301:"+uri+"/");
			}
            System.out.println("请求2"+requestURI);
			
			String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
	        modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
			
			//获取全部页  字母列表
			JSONArray productWordsAllList = dubboProductWordService.getproductWordsAllList(270);
			if(productWordsAllList!=null && !productWordsAllList.isEmpty()){
				modle.addObject("total_page", 1);
				modle.addObject("page", 1);
				modle.addObject("page_size", 270);
				modle.addObject("data_num", productWordsAllList.size());
				modle.addObject("productdata", productWordsAllList);
			}else {
				modle.addObject("total_page", 1);
				modle.addObject("page", 1);
				modle.addObject("page_size", 270);
				modle.addObject("data_num", 0);
			}
			
			JSONArray friendlyLinkList = new JSONArray();
			JSONObject friendlyLink1 = new JSONObject();
			friendlyLink1.put("name", "国美首页");
			friendlyLink1.put("url", "www.gome.com.cn");
			friendlyLinkList.add(friendlyLink1);
			
			//友情链接
			//JSONArray friendlyLinkList = dubboProductWordService.getFriendlyLinkList();
			if(friendlyLinkList!=null && !friendlyLinkList.isEmpty()){
				modle.addObject("friendlylink", friendlyLinkList);
			}
			
			//关键词
			modle.addObject("keyWord", "all");
		} catch (Exception e) {
			logger.error("商品词全部选项页出错 ",e);
			return new ModelAndView(new RedirectView("https://ep.gome.com.cn/")); 
		}
		return modle;
	}
	
	/*商品词字母页字母首页         
	 *  /chanpin/{keyWord}     
	 *  除了分页的商品词列表数据  字母关键词  配置的友情链接暂时不用
	默认每页120条数据*/
	@RequestMapping(value = "/{keyWord}", method = RequestMethod.GET)
	public ModelAndView getproductWordsList2(HttpServletRequest request,HttpServletResponse response,
			@PathVariable(value = "keyWord",required = true) String keyWord
			) {
		
		ModelAndView modle = new ModelAndView("productwords/index");
		try {
			String requestURI = request.getRequestURI();
			System.out.println("请求1"+requestURI);
            if(!requestURI.endsWith("/")) {
        		int indexOf = requestURI.indexOf("chanpin");
        		String uri = requestURI.substring(indexOf-1);
        		System.out.println("重定向"+uri);
        		return new ModelAndView("redirect301:"+uri+"/");
			}
            System.out.println("请求2"+requestURI);
			/*如果关键字为空字符串就直接重定向到全部页
			如果关键字是商品词的id数字的话 就转发到搜索结果页
			如果关键字还不包含字母A-Z和0-9,就直接重定向到全部页*/
			if(StringUtils.isBlank(keyWord)) {
				return new ModelAndView("redirect301:/chanpin/");
			}else if(isNumeric(keyWord)){
				return new ModelAndView("forward:/chanpin/search/content/"+keyWord);
			}else if(! keywords.contains(keyWord)) {
				return new ModelAndView("redirect301:/chanpin/");
			}
			
			//字母页首页参数构造
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("page", 1);
			jsonObject.put("page_size", PAGE_SIZE);
			jsonObject.put("keyword", keyWord);
			
			String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
	        modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
			
			//查询字母页首页列表
			JSONObject productWordsList = dubboProductWordService.getproductWordsList(jsonObject);
			if(productWordsList !=null){
				modle.addObject("total_page", productWordsList.getString("total_page"));
				modle.addObject("page", productWordsList.getString("page"));
				modle.addObject("page_size", PAGE_SIZE);
				modle.addObject("data_num", productWordsList.getString("data_num"));
				modle.addObject("keyWord", keyWord);
				modle.addObject("productdata", productWordsList.getJSONArray("productdata"));
			}else {
				modle.addObject("total_page", 1);
				modle.addObject("page", 1);
				modle.addObject("page_size", PAGE_SIZE);
				modle.addObject("data_num", 0);
				modle.addObject("keyWord", keyWord);
			}
			
			JSONArray friendlyLinkList = new JSONArray();
			JSONObject friendlyLink1 = new JSONObject();
			friendlyLink1.put("name", "国美首页");
			friendlyLink1.put("url", "www.gome.com.cn");
			friendlyLinkList.add(friendlyLink1);
			
			
			//友情链接
			//JSONArray friendlyLinkList = dubboProductWordService.getFriendlyLinkList();
			if(friendlyLinkList!=null && !friendlyLinkList.isEmpty()){
				modle.addObject("friendlylink", friendlyLinkList);
			}
		} catch (Exception e) {
			logger.error("商品词字母页首页出错 ",e);
			return new ModelAndView(new RedirectView("https://ep.gome.com.cn/")); 
		}
		return modle;
	}
	
	
	/*商品词字母页         
	 *  /chanpin/A/2    A字母第二页  超过页数默认跳到最后一页
	 *  除了分页的商品词列表数据  字母关键词  还有配置的友情链接
	默认每页100条数据*/
	@RequestMapping(value = {"/{keyWord}/{page}"}, method = RequestMethod.GET)
	public ModelAndView getproductWordsList3(HttpServletRequest request,HttpServletResponse response,
			@PathVariable(value = "keyWord",required = true) String keyWord,
			@PathVariable(value = "page",required = true) String page
			) {
		ModelAndView modle = new ModelAndView("productwords/index");
		try {
			String requestURI = request.getRequestURI();
			System.out.println("请求1"+requestURI);
            if(!requestURI.endsWith("/")) {
        		int indexOf = requestURI.indexOf("chanpin");
        		String uri = requestURI.substring(indexOf-1);
        		System.out.println("重定向"+uri);
        		return new ModelAndView("redirect301:"+uri+"/");
			}
            System.out.println("请求2"+requestURI);
			
			if(StringUtils.isBlank(keyWord) || ! keywords.contains(keyWord)){
				return new ModelAndView("redirect301:/chanpin/");
			}else if(StringUtils.isBlank(page) || !isNumeric(page)) {
				return new ModelAndView("redirect301:/chanpin/"+keyWord+"/");
			}
			
			Integer data_num = dubboProductWordService.getCharacterDataNum(keyWord);
			data_num = data_num == null ? 0 : data_num;
			
			Integer total_page  = data_num/PAGE_SIZE + data_num%PAGE_SIZE;
			total_page = total_page == 0 ? 1 : total_page;
			
			if(Integer.valueOf(page) > total_page) 
				return new ModelAndView("redirect301:/chanpin/"+keyWord+"/"+total_page+"/");
			else if(Integer.valueOf(page) < 1) 
				return new ModelAndView("redirect301:/chanpin/"+keyWord+"/1/");
			
			
			//字母页首页参数构造
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("page", page);
			jsonObject.put("page_size", PAGE_SIZE);
			jsonObject.put("keyword", keyWord);
			
			String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
	        modle.addObject("storeConfiguration", JSON.parseObject(appConfig));
			
			//查询字母页首页列表
			JSONObject productWordsList = dubboProductWordService.getproductWordsList(jsonObject);
			if(productWordsList !=null){
				modle.addObject("total_page", productWordsList.getString("total_page"));
				modle.addObject("page", productWordsList.getString("page"));
				modle.addObject("page_size", PAGE_SIZE);
				modle.addObject("data_num", productWordsList.getString("data_num"));
				modle.addObject("keyWord", keyWord);
				modle.addObject("productdata", productWordsList.getJSONArray("productdata"));
			}else {
				modle.addObject("total_page", 1);
				modle.addObject("page", 1);
				modle.addObject("page_size", 120);
				modle.addObject("data_num", 0);
				modle.addObject("keyWord", keyWord);
			}
			
			JSONArray friendlyLinkList = new JSONArray();
			JSONObject friendlyLink1 = new JSONObject();
			friendlyLink1.put("name", "国美首页");
			friendlyLink1.put("url", "www.gome.com.cn");
			friendlyLinkList.add(friendlyLink1);
			
			
			//友情链接
			//JSONArray friendlyLinkList = dubboProductWordService.getFriendlyLinkList();
			if(friendlyLinkList!=null && !friendlyLinkList.isEmpty()){
				modle.addObject("friendlylink", friendlyLinkList);
			}
		} catch (Exception e) {
			logger.error("商品词字母页分页出错 ",e);
			return new ModelAndView(new RedirectView("https://ep.gome.com.cn/")); 
		}
		return modle;
	}

	
	/*
	 * 商品词搜索结果页  通过路径上的商品词id值去查找商品词
	 *   1.商品词关键字
	 *   2.相关商品词数据
	 *   3.搜索结果页初始化数据
	 * */
	@RequestMapping(value = "/search/content/{productWordId}", method = RequestMethod.GET)
	public ModelAndView getCouponDetail(HttpServletRequest request,HttpServletResponse response,
			@PathVariable(value = "productWordId",required = true) Integer productWordId
			) {
		ModelAndView mav = new ModelAndView("productwords/search");
		
		/*ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-gcache.xml");
		Gcache cache = (Gcache) applicationContext.getBean("gcache_productCard");*/
		
		try {
			
			String productWordName = dubboProductWordService.getProductWordNameByID(productWordId+"");
			if(productWordName == null) {
				return new ModelAndView("redirect301:/chanpin/");
			}
			//获取相关商品词信息
			JSONArray likeProductWord = dubboProductWordService.getLikeProductWordByID(productWordId+"");
			
			
	        //获取商品词搜索结果首页信息
			JSONObject jsonObject = getDefaultSearchJSONObject(productWordName);
			JSONObject productSearch = dubboSearchService.productSearch(jsonObject);
//			logger.info("qrx的测试数据");
//			System.out.println(productSearch.toJSONString());
//			logger.info(productSearch.toJSONString());
			
			//获取分页信息
			JSONObject content = productSearch.getJSONObject("content");
			JSONObject header = productSearch.getJSONObject("header");
			String rawquestion = header.getString("rawquestion");
			JSONObject pageBar = content.getJSONObject("pageBar");
			String pageNumber = pageBar.getString("pageNumber");
			Integer totalpage = pageBar.getInteger("totalPage");
			String pageSize = pageBar.getString("pageSize");
			Integer totalCount = pageBar.getInteger("totalCount");
			System.out.println("补足前"+totalCount);
			System.out.println("原词"+productWordName+"查词"+rawquestion);
			if(productWordName.equals(rawquestion)){
				if(totalCount < 48) {
					String friendProductWordName = null; 
					String key = "seo_productwords_id:"+productWordId;
					if(cache.exists(key)) {
						friendProductWordName = cache.get(key);
						System.out.println("缓存中取出");
					}else {
						System.out.println("缓存中没有,进行补词");
						friendProductWordName = dubboProductWordService.getFriendProductWordByID(productWordId+"");
						if(StringUtils.isNotBlank(friendProductWordName)) {
							cache.setex(key, 86400, friendProductWordName);
						}else{
							System.out.println("没有同词根下其他词，补词失败");
						}
					}
					System.out.println("补足词为"+friendProductWordName);
					if(friendProductWordName != null) {
						JSONObject friendJsonObject = getDefaultSearchJSONObject(friendProductWordName);
						JSONObject friendProductSearch = dubboSearchService.productSearch(friendJsonObject);
						System.out.println("++++====");
						JSONArray friendProductSearchInfoArray = friendProductSearch.getJSONObject("content").getJSONObject("prodInfo").getJSONArray("products");
						System.out.println("====++++");
						JSONObject prodSearchInfo = content.getJSONObject("prodInfo");
						JSONArray productSearchInfoArray = prodSearchInfo.getJSONArray("products");
						System.out.println("补足词条数"+friendProductSearchInfoArray.size());
						Integer size = friendProductSearchInfoArray.size() <= 48-totalCount ? friendProductSearchInfoArray.size() : 48-totalCount;
						System.out.println("增加条数"+size);
						for (int i = 0; i < size ; i++) {
							productSearchInfoArray.add(friendProductSearchInfoArray.getJSONObject(i));
						}
						prodSearchInfo.put("products", productSearchInfoArray);
						pageBar.put("totalCount", productSearchInfoArray.size());
						
						content.put("prodInfo", prodSearchInfo);
						content.put("pageBar", pageBar);
						productSearch.put("content", content);
						
						System.out.println("补足后"+(productSearchInfoArray.size()));
						
						mav.addObject("page", 1);
						mav.addObject("total_page", 1);
						mav.addObject("page_size", 48);
						mav.addObject("data_num", productSearchInfoArray.size());
					}else {
						mav.addObject("page", pageNumber);
						mav.addObject("total_page", totalpage);
						mav.addObject("page_size", pageSize);
						mav.addObject("data_num", totalCount);
					}
				}else {
					mav.addObject("page", pageNumber);
					mav.addObject("total_page", totalpage);
					mav.addObject("page_size", pageSize);
					mav.addObject("data_num", totalCount);
				}
			}else {
				String key = "seo_productwords_id_random:"+productWordId;
				String sixRandomString = new String();
				Integer[] tenRandom = null;
				if(cache.exists(key)) {
					sixRandomString = cache.get(key);
					System.out.println("补足随机数缓存中有了"+sixRandomString);
					tenRandom =getIntegerArray(sixRandomString);
				}else {
					tenRandom = getTenRandom(30);
					sixRandomString = getString(tenRandom);
					System.out.println("补足随机数缓存中没有"+sixRandomString);
					cache.setex(key, 86400, sixRandomString);
				}
				JSONObject prodSearchInfo = content.getJSONObject("prodInfo");
				JSONArray productSearchInfoArray = prodSearchInfo.getJSONArray("products");
				JSONArray randomJsonArray = new JSONArray();
				for (Integer num : tenRandom) {
					randomJsonArray.add(productSearchInfoArray.getJSONObject(num));
				}
				prodSearchInfo.put("products", randomJsonArray);
				pageBar.put("totalCount", randomJsonArray.size());
				pageBar.put("totalPage", 1);
				
				content.put("prodInfo", prodSearchInfo);
				content.put("pageBar", pageBar);
				productSearch.put("content", content);
				
				mav.addObject("page", 1);
				mav.addObject("total_page", 1);
				mav.addObject("page_size", randomJsonArray.size());
				mav.addObject("data_num", randomJsonArray.size());
			}
			//填充数据
			String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
			mav.addObject("storeConfiguration", JSON.parseObject(appConfig));
	        
			mav.addObject("like_data", likeProductWord);
			mav.addObject("search_data", productSearch);
			mav.addObject("product_word", productWordName);
			mav.addObject("data", productSearch.toJSONString());
		} catch (Exception e) {
			logger.error("商品词搜索结果页出错 ",e);
			System.out.println(e);
			return new ModelAndView("redirect301:/chanpin/");
		}
		return mav;
	}
	
	
	
	/*
	 * 商品词搜索结果页  通过路径上的商品词id值去查找商品词
	 *   1.商品词关键字
	 *   2.相关商品词数据
	 *   3.搜索结果页初始化数据
	 * */
	@RequestMapping(value = "/search/jsondata/{productWordId}", method = RequestMethod.GET)
	@ResponseBody
	public String getCouponDetailJSON(HttpServletRequest request,HttpServletResponse response,
			@PathVariable(value = "productWordId",required = true) Integer productWordId
			) {
		
		JSONObject data = new JSONObject();
		try {
			String productWordName = dubboProductWordService.getProductWordNameByID(productWordId+"");
			if(productWordName == null) {
				return null;
			}
			//获取相关商品词信息
			JSONArray likeProductWord = dubboProductWordService.getLikeProductWordByID(productWordId+"");
			
			
	        //获取商品词搜索结果首页信息
			JSONObject jsonObject = getDefaultSearchJSONObject(productWordName);
			JSONObject productSearch = dubboSearchService.productSearch(jsonObject);
			logger.info("qrx的测试数据");
			System.out.println(productSearch.toJSONString());
			logger.info(productSearch.toJSONString());
			
			//获取分页信息
			JSONObject content = productSearch.getJSONObject("content");
			JSONObject pageBar = content.getJSONObject("pageBar");
			String pageNumber = pageBar.getString("pageNumber");
			Integer totalpage = pageBar.getInteger("totalPage");
			String pageSize = pageBar.getString("pageSize");
			String totalCount = pageBar.getString("totalCount");
			
			
			String appConfig=  JSON.toJSONString(AppConfigTools.getAppConfig()) ;
			data.put("storeConfiguration", JSON.parseObject(appConfig));
			data.put("page", pageNumber);
			data.put("total_page", totalpage);
			data.put("page_size", pageSize);
			data.put("data_num", totalCount);
			data.put("like_data", likeProductWord);
			data.put("search_data", productSearch);
			data.put("product_word", productWordName);
		} catch (Exception e) {
			logger.error("商品词搜索结果页出错 ",e);
			return null;
		}
		return data.toJSONString();
	}
	
	
	/*
	 * 多重路径错误处理  超过三级路径
	 * */
	@RequestMapping(value = "/**", method = RequestMethod.GET)
	public ModelAndView multiPathErrorDeal() {
			logger.error("多重路径错误处理");
			return new ModelAndView(new RedirectView("https://ep.gome.com.cn/")); 
	}
	
	public JSONObject getDefaultSearchJSONObject(String productWordName) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("from","robot");
		jsonObject.put("question",productWordName);
		jsonObject.put("catId","0");
		jsonObject.put("regionId","11010200");
		jsonObject.put("market","10");
		jsonObject.put("facets","0");
		jsonObject.put("productTag","0");
		jsonObject.put("sort","0");
		jsonObject.put("priceTag","0");
		jsonObject.put("pageSize","48");
		jsonObject.put("pageNumber","1");
		jsonObject.put("instock","0");
		jsonObject.put("sale","0");
		return jsonObject;
	}
	
	public void getAllData(List<Map<String, String>> list) {
		for (int i = 1; i <= 270; i++) {
                 Map<String,String> map = new HashMap<String,String>();
                 map.put("id",i+"");
                 map.put("name", "西瓜"+i+"号");
                 list.add(map);
		}
	}
	
	public void getData(List<Map<String, String>> list, Integer page) {
		for (int i = (page-1)*120+1; i <= (page*120 <= 832 ? page*120 :832); i++) {
                 Map<String,String> map = new HashMap<String,String>();
                 map.put("id",i+"");
                 map.put("name", "西瓜"+i+"号");
                 list.add(map);
		}
	}
	
	
	public void getLikeData(List<Map<String, String>> list) {
		for (int i = 1; i <= 15; i++) {
                 Map<String,String> map = new HashMap<String,String>();
                 map.put("id",i+"");
                 map.put("name", "西瓜"+i+"号");
                 list.add(map);
		}
	}
	
	public void getFriendlyData(List<Map<String, String>> list) {
		for (int i = 1; i < 22; i++) {
                 Map<String,String> map = new HashMap<String,String>();
                 map.put("name","国美首页");
                 map.put("url", "www.gome.com.cn");
                 list.add(map);
		}
	}
	
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public DubboService getDubboSearchService() {
		return dubboSearchService;
	}

	public void setDubboSearchService(DubboService dubboSearchService) {
		this.dubboSearchService = dubboSearchService;
	}

	public IProductWordDubboService getDubboProductWordService() {
		return dubboProductWordService;
	}

	public void setDubboProductWordService(IProductWordDubboService dubboProductWordService) {
		this.dubboProductWordService = dubboProductWordService;
	}	
	
	public static Integer[] getTenRandom(Integer totalCount){
		  Random rand = new Random();
		  HashSet<Integer> hashSet = new HashSet<Integer>();
		  while(true){
		   int random = rand.nextInt(totalCount) + 1;
		    hashSet.add(random);
		    if(hashSet.size() == 10)
		    	return (Integer[]) hashSet.toArray(new Integer[hashSet.size()]);
		   }
		}
	
	public static String getString(Integer[] sixRandom){
		   String string ="";
		  for (int i = 0; i < sixRandom.length; i++) {
			  string+=sixRandom[i]+",";
		  }
		  return string.substring(0, string.length()-1);
		}
	
	public static Integer[] getIntegerArray(String sixNumString){
		String[] split = sixNumString.split(",");
		Integer[] sixNum = new Integer[split.length];
		  for (int i = 0; i < split.length; i++) {
			  sixNum[i]=Integer.valueOf(split[i]);
		  }
		  return sixNum;
	}
	public static void main(String[] args) {
		Integer[] sixRandom = getTenRandom(30);
		String string = getString(sixRandom);
		Integer[] integerArray = getIntegerArray(string);
		for (int i = 0; i < integerArray.length; i++) {
			System.out.println(integerArray[i]);
		}
	}
}
