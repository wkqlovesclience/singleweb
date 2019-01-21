package com.gome.stage.entity;

import java.io.Serializable;

/**
 * 热词搜索
 * 
 * @author fanqingxia
 *
 */
public class HotSearchword implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1176291859250198379L;

	/**
	 * id
	 */
	private Integer id;
	/**
	 * 热词
	 */
	private String title;
	/**
	 * 热词分词
	 */
	private String tag;

	/**
	 * 关键词是否能搜到结果 1是 0否
	 */
	private Integer isSearched;

	private String createTime;

	private String createTimeYMD;

	private String updateTime;

	private String modifier;

	private String creator;

	private Integer checked;

	private String serverUrl;

	private String onlineUrl;

	private String site;

	private String indexState;

	private String hotwordsUrl;

	public String getHotwordsUrl() {
		return hotwordsUrl;
	}

	public void setHotwordsUrl(String hotwordsUrl) {
		this.hotwordsUrl = hotwordsUrl;
	}

	public Integer getIsSearched() {
		return isSearched;
	}

	public void setIsSearched(Integer isSearched) {
		this.isSearched = isSearched;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCreateTimeYMD() {
		return createTimeYMD;
	}

	public void setCreateTimeYMD(String createTimeYMD) {
		this.createTimeYMD = createTimeYMD;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getOnlineUrl() {
		return onlineUrl;
	}

	public void setOnlineUrl(String onlineUrl) {
		this.onlineUrl = onlineUrl;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getIndexState() {
		return indexState;
	}

	public void setIndexState(String indexState) {
		this.indexState = indexState;
	}

}
