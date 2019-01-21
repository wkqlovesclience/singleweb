/*
 *   WWW.COO8.COM
 */
package com.gome.stage.entity.rank;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;


/**
 * @author  JIANGCHENG
 * @version 1.0
 * @since 1.0
 */


public class Titles  implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	//alias
	public static final String TABLE_ALIAS = "Titles";
	public static final String ALIAS_ID = "id";
	public static final String ALIAS_PATHS = "paths";
	public static final String ALIAS_TITLE = "title";
	public static final String ALIAS_SOURCES = "sources";
	public static final String ALIAS_PER_SOURCE = "goodsId";
	public static final String ALIAS_END_SOURCE = "goodsName";
	public static final String ALIAS_CREATOR = "creator";
	public static final String ALIAS_CREATE_TIME = "createTime";
	public static final String ALIAS_UPDATE_TIME = "updateTime";
	public static final String ALIAS_PUBLIC_STAT = "publicStat";
	public static final String ALIAS_CHECK_STAT = "checkStat";



	//columns START
	private Integer id;
	private String paths;
	private String title;
	private String sources;
	private String goodsId;
	private String goodsName;
	private String creator;
	private java.util.Date createTime;
	private java.util.Date updateTime;
	private String publicStat;
	private String checkStat;
	private String tags;
	private String keywords;
	private String tagName;
	private Integer newsCount;
	private String indexStat;
	private String site;
	private String skuId;

	private List<HotLink> hotlinkList;
	//columns END

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Titles(){
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Titles(
			Integer id
	){
		this.id = id;
	}

	public void setId(Integer value) {
		this.id = value;
	}

	public Integer getId() {
		return this.id;
	}

	public void setPaths(String value) {
		this.paths = value;
	}

	public String getPaths() {
		return this.paths;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public String getTitle() {
		return this.title;
	}

	public void setSources(String value) {
		this.sources = value;
	}

	public String getSources() {
		return this.sources;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public void setCreator(String value) {
		this.creator = value;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public void setUpdateTime(java.util.Date value) {
		this.updateTime = value;
	}

	public java.util.Date getUpdateTime() {
		return this.updateTime;
	}

	public void setPublicStat(String value) {
		this.publicStat = value;
	}

	public String getPublicStat() {
		return this.publicStat;
	}

	public void setCheckStat(String value) {
		this.checkStat = value;
	}

	public String getCheckStat() {
		return this.checkStat;
	}

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("Id",getId())
				.append("Paths",getPaths())
				.append("Title",getTitle())
				.append("Sources",getSources())
				.append("GoodsId",getGoodsId())
				.append("GoodsName",getGoodsName())
				.append("Creator",getCreator())
				.append("CreateTime",getCreateTime())
				.append("UpdateTime",getUpdateTime())
				.append("PublicStat",getPublicStat())
				.append("CheckStat",getCheckStat())
				.append("IndexStat",getIndexStat())
				.append("Site",getSite())
				.toString();
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int hashCode() {
		return new HashCodeBuilder()
				.append(getId())
				.toHashCode();
	}

	public boolean equals(Object obj) {
		if(obj instanceof Titles == false) return false;
		if(this == obj) return true;
		Titles other = (Titles)obj;
		return new EqualsBuilder()
				.append(getId(),other.getId())
				.isEquals();
	}

	public Integer getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(Integer newsCount) {
		this.newsCount = newsCount;
	}

	public String getIndexStat() {
		return indexStat;
	}

	public void setIndexStat(String indexStat) {
		this.indexStat = indexStat;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	/**
	 * @return the hotlinkList
	 */
	public List<HotLink> getHotlinkList() {
		return hotlinkList;
	}

	/**
	 * @param hotlinkList the hotlinkList to set
	 */
	public void setHotlinkList(List<HotLink> hotlinkList) {
		this.hotlinkList = hotlinkList;
	}

}

