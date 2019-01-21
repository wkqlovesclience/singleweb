package com.gome.stage.entity;

import java.sql.Date;

public class Label {
	
	private Integer id;
	private String type;
	private String names;
	private String url;
	private Date createTime;
	private Date updateTime;
	private String creator;
	private String modifier;
	private String site;
	private String states; //发布状态
	private String everySearch;//是否是大家都在搜包含标签
	
	private Integer totalVo;//计算列表页中 总条数
	private Integer pageSize;//
	private Integer pageNo;//
	
	
	public String getStates() {
		return states;
	}
	public void setStates(String states) {
		this.states = states;
	}
	public String getEverySearch() {
		return everySearch;
	}
	public void setEverySearch(String everySearch) {
		this.everySearch = everySearch;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTotalVo() {
		return totalVo;
	}
	public void setTotalVo(Integer totalVo) {
		this.totalVo = totalVo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	
}
