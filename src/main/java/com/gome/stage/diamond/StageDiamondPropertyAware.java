package com.gome.stage.diamond;

/**
 * 供需要Diamond属性的bean使用.
 * 
 * @author liuyangming
 */
public interface StageDiamondPropertyAware {

	public String FIELD_DIAMOND_PROPERTIES = "diamondProperties";

	public void setDiamondProperties(java.util.Properties properties);

}
