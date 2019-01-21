package com.gome.stage.utils;

import java.text.DecimalFormat;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.gome.frontSe.comm.FSGoods;
import com.gome.frontSe.comm.FSImgBase;
import com.gome.frontSe.comm.FSRushGoods;
import com.gome.stage.bean.home.FloorData;
import com.gome.stage.bean.home.GomeFloor;
import com.gome.stage.page.HomePage;

public class HomePageUtils {

	private static DecimalFormat myformat = new DecimalFormat("#.0");

	/**
	 * 获得首页楼层的json格式数据
	 * 
	 * @param bean
	 *            首页对象
	 * @param tab
	 *            tab名称
	 * @return
	 */
	public static JSONObject getIndexFloorJson(HomePage bean, String tab) {
		JSONObject homeJson = new JSONObject();
		tab = tab.replace("F", "");
		int tabNum = Integer.parseInt(tab);
		int index = tabNum - 1;

		if (bean.getGomeFloors() == null || bean.getGomeFloors().size() < 1)
			return homeJson;

		if (index < 0 || index >= bean.getGomeFloors().size() || bean.getGomeFloors().size() == 1) {
			index = 0;
		}
		GomeFloor gomeFloor = bean.getGomeFloors().get(index);

		if (gomeFloor != null && gomeFloor.getFloorData() != null && gomeFloor.getFloorData().size() > 0) {
			homeJson.put("boxMore", gomeFloor.getFloorDataMore());

			int dataIndex = 0;
			int dataSize = gomeFloor.getFloorData().size();
			JSONArray jsonList = new JSONArray();
			for (FloorData data : gomeFloor.getFloorData()) {
				int line = data.getLine();
				int count = data.getIndex();
				String account = "?intcmp=sy-H-" + tabNum + "-" + line + "-" + count + "-";
				
				JSONObject jsonData = new JSONObject();
				jsonData.put("boxType", data.getType() + "");
				
				String style = "";
				int next = 2;
				if (data.getType() == 2) {
					style = "floorWid_max";
				} else if (data.getType() == 3) {
					style = "floorBox_scare";
				} else if (data.getType() == 4) {
					style = "floorBnd_story";
				} else if (data.getType() == 5) {
					style = "floorHig_max";
					next = 1;
				}
				
				jsonData.put("boxStyle", style);
				if(dataSize > dataIndex+next && gomeFloor.getFloorData().get(dataIndex+next) != null && gomeFloor.getFloorData().get(dataIndex+next).getLine() != line) {
					if(data.getType() != 5 && gomeFloor.getFloorData().get(dataIndex+1).getLine() == line) {
						jsonData.put("boxStyle", style + " floorBox_hide");						
					}
				} else if(dataSize == dataIndex+2 && data.getType() != 5) {
						jsonData.put("boxStyle", style + " floorBox_hide");	
				}					
				dataIndex++;

				String imageSrc = "";
				String title = "";
				String href = "";
				String prdId = "";
				String prdSku = "";
				String prdRealHref = "";
				String prdRealName = "";
				double prdRealPrice = 0.0;
				String is3d = "0";

				JSONObject box = new JSONObject();
				if (data.getType() == 3 && data.getRushGoods() != null) {
					FSRushGoods good = data.getRushGoods();

					// 抢购图片
					if (StringUtils.isNotBlank(good.getManualImg())) {
						imageSrc = good.getManualImg();
					} else if (good.getGoodsImgs() != null && good.getGoodsImgs().size() > 0) {
						imageSrc = good.getGoodsImgs().get(0).getSrc() + "_160.jpg";
					}
					if(good.getName() != null)
						title += good.getName();
					if(good.getSalesPromotionTitle() != null)
						title += good.getSalesPromotionTitle();
					href = good.getDetailHref();
					prdId = good.getProductId();
					prdSku = good.getSku();
					prdRealHref = good.getDetailHref();
					prdRealName = good.getName();
					prdRealPrice = good.getRushPrice();
					box.put("rushPrice", good.getRushPrice() + "");
					box.put("gomePrice", good.getGomePrice() + "");
					box.put("prdName", good.getName() == null ? "" : good.getName());
					box.put("prdTitle", good.getSalesPromotionTitle() == null ? "" : good.getSalesPromotionTitle());
					box.put("prdText", good.getDescription() == null ? "" : good.getDescription());
					box.put("prdHref", href == null ? "" : href);
					box.put("startTime", good.getStartTime() + "");
					box.put("endTime", good.getEndTime() + "");
					box.put("limit", good.getLimitQuantity() + "");
					box.put("last", good.getLastQuantity() + "");

					jsonData.put("boxRush", box);

				} else if (data.getType() == 4 && data.getBrandStory() != null) {
					jsonData.put("boxBrand", box);// 品牌故事
					title = data.getBrandStory().getTitle();
					href = data.getBrandStory().getHref();
					imageSrc = data.getBrandStory().getSrc();

				} else if (data.getCommonPrds() != null) {
					FSGoods good = data.getCommonPrds();

					prdId = good.getProductId();
					prdSku = good.getSku();
					prdRealHref = good.getDetailHref();
					prdRealName = good.getName();
					prdRealPrice = good.getPrice();
					if(good.isThreeDflag()){
						is3d = "1";
					}
					
					// 商品图片
					if (StringUtils.isNotBlank(good.getManualImg())) {
						imageSrc = good.getManualImg();
					} else if (good.getGoodsImgs() != null && good.getGoodsImgs().size() > 0) {
						imageSrc = good.getGoodsImgs().get(0).getSrc() + "_400.jpg";
					}
					if(good.getName() != null)
						title += good.getName();
					if(good.getSalesPromotionTitle() != null)
						title += good.getSalesPromotionTitle();
					if(good.getDetailHref() != null)
						href = good.getDetailHref() + account + prdId;
					
					box.put("prdName", good.getName() == null ? "" : good.getName());
					box.put("prdTitle", good.getSalesPromotionTitle() == null ? "" : good.getSalesPromotionTitle());
					box.put("prdText", good.getDescription() == null ? "" : good.getDescription());
					box.put("prdHref", href == null ? "" : href);
					box.put("price", good.getPrice() + "");
					box.put("gomePrice", good.getGomePrice() + "");
					box.put("save", "");
					if (good.getGomePrice() > good.getPrice()) {
						double save = good.getGomePrice() - good.getPrice();
						box.put("save", "省" + myformat.format(save) + "元");
					}
					box.put("bztType", good.getBztType() + "");

					jsonData.put("boxPrd", box);

					// 二维码
					if (good.getGoodsModel() != null && good.getGoodsModel().size() > 0) {
						FSImgBase img = good.getGoodsModel().get(0);

						JSONObject prdCode = new JSONObject();
						prdCode.put("src", img.getSrc());
						if (img.getImgOtherAttr() != null) {
							prdCode.put("des", img.getImgOtherAttr().get("desc"));
							prdCode.put("prm", img.getImgOtherAttr().get("promoDesc"));
						}
						jsonData.put("prdCode", prdCode);
					}
					
					//爆炸贴
					Map<String, String> map = good.getAffixAttr();
					for (String key : map.keySet()) {
						if(key.startsWith("label")) {
							jsonData.put(key, map.get(key));
						}
					}
				}

				jsonData.put("prdImage", imageSrc == null ? "" : imageSrc);
				jsonData.put("prdAlt", title == null ? "" : title);
				jsonData.put("prdHref", href == null ? "" : href);
				jsonData.put("prdId", prdId == null ? "" : prdId);
				jsonData.put("prdSku", prdSku == null ? "" : prdSku);
				jsonData.put("prdRealHref", prdRealHref == null ? "" : prdRealHref);
				jsonData.put("prdRealName", prdRealName == null ? "" : prdRealName);
				jsonData.put("prdRealPrice", prdRealPrice + "");
				
				jsonData.put("tdflag", is3d);

				jsonList.add(jsonData);
			}

			homeJson.put("list", jsonList);
		}
		return homeJson;
	}

}
