package com.gome.stage.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gome.frontSe.interfaces.IProdDetailService;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.tools.AppConfigTools;
import com.gome.stage.web.CompareController;



public class GuessLikeUtils {
	//正则规则（匹配?intcmp）
	public static final String REGEX1 = "\\.html\\?intcmp[\\s\\S]+?\""; 
	//用于填充的字符串
	public static final String REPLACESTR1 = "\\.html\"";    
	private static GomeLogger logger = GomeLoggerFactory.getLogger(CompareController.class);
	
	private Map<String,String> idMap;
	
	public  JSONObject getJSONMap(String itemId,IProdDetailService prodDetailService){		
		JSONObject object = null;   	
    	GuessLikeUtils guessLikeUtils = new GuessLikeUtils();
    	
        try {
			String [] skuIDs = itemId.split("-");
			
			
			if(!("0".equals(skuIDs[0]))){
				setIdMap(guessLikeUtils.getIDs(skuIDs[0],prodDetailService));
				this.idMap.put("skuId", skuIDs[0]);
				object = guessLikeUtils.getJSONFromGuessLike(idMap);
				
			}
			
		} catch (Exception e) {
			logger.error("itemId : " + itemId + "error : " + e);
			System.out.println("Error: " + e);
		}
		return object;		
	}
	
	//通过SkuId获得productId,brandId,shopid,firstCatId,thirdCatId
    public  Map<String,String> getIDs(String skuId,IProdDetailService prodDetailService){
    	Map<String, String>map = null;
    	Map<String,String> mapPro = new HashMap<String, String>();
    	Map<String, String>mapSku = new HashMap<String, String>();
    	String[] skuProperties = { "productIds" };
    	String[] proProperties = {"firstCatItemId","thridCatItemId","shopItemId","brandItemId"};
    	
    	try {   		
    		map = prodDetailService.getSkuMultiProperties(skuId, skuProperties);	
			String productId = (String) map.get("productIds");
			String[] productIds = productId.split(":");
			if(productIds[0] != null){
				mapPro =  prodDetailService.getProductMultiProperties(productIds[0],proProperties);
				
				mapSku.put("productIds", productIds[0]);
				mapPro.putAll(mapSku);
				logger.info(mapPro.toString());
			}
		} catch (BeansException e){
			logger.error("skuId : " + skuId);
		}
    	return mapPro;
    }
    
    public JSONObject getJSONFromGuessLike(Map<String, String> map){
    	JSONObject obj = null;
    	String urlString = "";
    	try { 
			String productId  = map.get("productIds");
			String brandId = map.get("brandItemId");
			String shopid = map.get("shopItemId");
			String firstCatId = map.get("firstCatItemId");
			String thirdCatId = map.get("thridCatItemId");
			String skuId = map.get("skuId");
			
			if(StringUtils.isNotBlank(brandId)&&StringUtils.isNotBlank(firstCatId)&&StringUtils.isNotBlank(thirdCatId)){
				
				if(StringUtils.isBlank(shopid)){
					shopid = null;
				}
				
				String site = AppConfigTools.getAppConfig().get("seoTuiJianSite");
				
				urlString = String.format("http://"+site+"/gome/rec?callback=null&boxid=box37&pid=%s&area=11010100&cid=null&uid=null&imagesize=130&brid=%s&shopid=%s&c1id=%s&c3id=%s&sid=%s",productId,brandId,shopid,firstCatId,thirdCatId,skuId);
				logger.info(urlString);
			    
				
				String reString = getEntity(urlString);
				reString=StringUtils.remove(reString, "null(");
				reString = deleteUrlStr(reString, REPLACESTR1, REGEX1);
				reString=reString.substring(0,reString.length()-1);
				obj =JSON.parseObject(reString);
			}
		} catch (Exception e) {
			logger.error("url : " + urlString);
			logger.error("map : " + map.toString());
		}
		return obj;
    }
    
	public String getEntity(String url){
	   	 HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
	        //HttpClient  
	        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
	   	 
	        HttpGet httpGet = new HttpGet(url);  
       
	        HttpEntity entity=null;
	        
	        String result="";
			try {
				// 执行get请求
				HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
				// 获取响应消息实体
				entity= httpResponse.getEntity();
				
				if(entity != null){
					try {
						result=EntityUtils.toString(entity);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} catch (Exception e) {
				return null;
			} finally {

				try {
					closeableHttpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return null;
				}
			}
			return result;
		}
		public String deleteUrlStr(String sourString,String replceStr,String regex){
			String destString = "";
			Pattern p = Pattern.compile(regex);
			Matcher matcher = p.matcher(sourString);
			
			destString = matcher.replaceAll(replceStr);
			return destString;
		}

		public Map<String,String> getIdMap() {
			return idMap;
		}

		public void setIdMap(Map<String,String> idMap) {
			this.idMap = idMap;
		}
}