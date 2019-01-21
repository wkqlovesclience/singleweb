/**
 *
 */
package com.gome.stage.entity.rank;

/**
 * @author wangfufu
 *  分类下前六的品牌
 */
public class BrandClassify {

	public String categoryId;
	public Integer type;
	public String catId;
	public String value;
	public Integer code;
	public Integer index;
	public Integer parentId;
	public String facetId;
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getCatId() {
		return catId;
	}
	public void setCatId(String catId) {
		this.catId = catId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getFacetId() {
		return facetId;
	}
	public void setFacetId(String facetId) {
		this.facetId = facetId;
	}


}
