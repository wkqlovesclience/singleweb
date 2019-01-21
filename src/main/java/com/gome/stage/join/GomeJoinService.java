package com.gome.stage.join;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.frontSe.comm.FSLinkImg;
import com.gome.frontSe.data.model.MainTemplate;
import com.gome.stage.interfaces.whale.ITemplateService;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;

public class GomeJoinService {
	protected GomeLogger logger = GomeLoggerFactory
			.getLogger(GomeJoinService.class);
//	private static final String KEYWORD = "/gome-join";
	
	private ITemplateService templateService;
	
	public ITemplateService getTemplateService() {
		return templateService;
	}

	public void setTemplateService(ITemplateService templateService) {
		this.templateService = templateService;
	}

	public JSONObject getModelInfoBykw(String keyword){
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(keyword)){
			logger.warn("GomeJoinService getModelInfoBykw is error !! the keyword is null");
			return result;
		}
		try {
			//商家入驻根模版
			MainTemplate mainTemp = templateService.getTemplateByKeyword(keyword);
			
			//banner
			MainTemplate banner = templateService.getTemplateByKeyword(keyword+"/banner");
			if(banner != null && StringUtils.isNotBlank(banner.getKeyword())){
				//轮播图
				List<FSLinkImg> imgs = templateService.findFSLinkImgs(banner.getKeyword(), mainTemp.getModelId(), 2);
				if(CollectionUtils.isNotEmpty(imgs)){
					JSONArray jsImgs = new JSONArray();
					for (int i = 0; i < imgs.size(); i++) {
						FSLinkImg img = imgs.get(i);
						JSONObject jsimg = new JSONObject();
						jsimg.put("alt", img.getElemenlTitle());
						jsimg.put("imgUrl", img.getImgUrl());
						jsimg.put("href", img.getTolink());
						jsImgs.add(jsimg);
					}
					result.put("banner", jsImgs);
				}
			}
			
			//content
			MainTemplate content = templateService.getTemplateByKeyword(keyword+"/content");
			if(content != null && StringUtils.isNotBlank(content.getKeyword())){
				//childTemplate
				List<MainTemplate> childTems = templateService.findTemplatesLikeKeyword(content.getKeyword(), content.getModelId());
				if(CollectionUtils.isNotEmpty(childTems)){
					JSONArray jscontents = new JSONArray();
					for (int i = 0; i < childTems.size(); i++) {
						MainTemplate childTem = childTems.get(i);
						JSONObject jscon = new JSONObject();
						jscon.put("name", childTem.getModelName());
						jscon.put("url", childTem.getUrl());
						
						//图片链
						List<FSLinkImg> childImgs = templateService.findFSLinkImgs(childTem.getKeyword(), content.getModelId(), 2);
						if(CollectionUtils.isNotEmpty(childImgs)){
							FSLinkImg img = childImgs.get(0);
							JSONObject jsimg = new JSONObject();
							jsimg.put("alt", img.getElemenlTitle());
							jsimg.put("imgUrl", img.getImgUrl());
							jsimg.put("href", img.getTolink());
							jscon.put("img", jsimg);
						}
						
						//文字链
						List<FSLinkImg> childTexts = templateService.findFSLinkImgs(childTem.getKeyword(), content.getModelId(), 1);
						if(CollectionUtils.isNotEmpty(childTexts)){
							JSONArray conTexts = new JSONArray();
							for (int j = 0; j < childTexts.size(); j++) {
								FSLinkImg img = childTexts.get(j);
								JSONObject jsText = new JSONObject();
								jsText.put("alt", img.getContent());
								jsText.put("href", img.getTolink());
								conTexts.add(jsText);
							}
							jscon.put("texts", conTexts);
						}
						jscontents.add(jscon);
					}
					result.put("content", jscontents);
				}
			}
			
			//入住流程
			MainTemplate process = templateService.getTemplateByKeyword(keyword+"/process");
			if(process != null && StringUtils.isNotBlank(process.getKeyword())){
				//入住流程图片
				List<FSLinkImg> processImg = templateService.findFSLinkImgs(process.getKeyword(), mainTemp.getModelId(), 2);
				JSONObject jsProcess = new JSONObject();
				jsProcess.put("name", process.getModelName());
				jsProcess.put("url", process.getUrl());
				if(CollectionUtils.isNotEmpty(processImg)){
					FSLinkImg img = processImg.get(0);
					JSONObject jsimg = new JSONObject();
					jsimg.put("alt", img.getElemenlTitle());
					jsimg.put("imgUrl", img.getImgUrl());
					jsimg.put("href", img.getTolink());
					jsProcess.put("img", jsimg);
				}
				result.put("process", jsProcess);
			}
			
			//热门品牌
			MainTemplate hotBrand = templateService.getTemplateByKeyword(keyword+"/brand");
			if(hotBrand != null && StringUtils.isNotBlank(hotBrand.getKeyword())){
				//childTemplate
				List<MainTemplate> childBrands = templateService.findTemplatesLikeKeyword(hotBrand.getKeyword(), hotBrand.getModelId());
				if(CollectionUtils.isNotEmpty(childBrands)){
					JSONArray brands = new JSONArray();
					for (int i = 0; i < childBrands.size(); i++) {
						JSONObject jsBrand = new JSONObject();
						MainTemplate childBrand = childBrands.get(i);
						jsBrand.put("name", childBrand.getModelName());
						jsBrand.put("url", childBrand.getUrl());
						//图片链
						List<FSLinkImg> childImgs = templateService.findFSLinkImgs(childBrand.getKeyword(), hotBrand.getModelId(), 2);
						if(CollectionUtils.isNotEmpty(childImgs)){
							JSONArray brandImgs = new JSONArray();
							for (int j = 0; j < childImgs.size(); j++) {
								FSLinkImg img = childImgs.get(j);
								JSONObject jsImg = new JSONObject();
								jsImg.put("alt", img.getElemenlTitle());
								jsImg.put("imgUrl", img.getImgUrl());
								jsImg.put("href", img.getTolink());
								brandImgs.add(jsImg);
							}
							jsBrand.put("imgs", brandImgs);
						}
						brands.add(jsBrand);
					}
					result.put("hotBrand", brands);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		return result;
	}
}
