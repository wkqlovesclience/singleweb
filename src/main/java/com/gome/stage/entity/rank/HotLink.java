package com.gome.stage.entity.rank;

import java.util.Date;

public class HotLink implements java.io.Serializable{

	/**
	 * @author zhanghan-ds3
	 * 次级导航热门链接
	 *
	 */
	private static final long serialVersionUID = -6072947363719953402L;

	//编号
	private  Integer id;
	//热门链接名称
	private String hotName;
	//热门链接PC端url
	private String pcUrl;
	//热门链接WAP端url
	private String wapUrl;
	//排序
	private Integer sort;
	//模块类型
	private Integer moduleType;
	//模块Id
	private Integer moduleId;
	//创建时间
	private Date createDate;
	//创建者
	private String createUser;
	//修改者
	private String updateUser;
	//修改时间
	private Date updateDate;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the hotName
	 */
	public String getHotName() {
		return hotName;
	}
	/**
	 * @param hotName the hotName to set
	 */
	public void setHotName(String hotName) {
		this.hotName = hotName;
	}
	/**
	 * @return the pcUrl
	 */
	public String getPcUrl() {
		return pcUrl;
	}
	/**
	 * @param pcUrl the pcUrl to set
	 */
	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
	}
	/**
	 * @return the wapUrl
	 */
	public String getWapUrl() {
		return wapUrl;
	}
	/**
	 * @param wapUrl the wapUrl to set
	 */
	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}
	/**
	 * @return the sort
	 */
	public Integer getSort() {
		return sort;
	}
	/**
	 * @param sort the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	/**
	 * @return the moduleType
	 */
	public Integer getModuleType() {
		return moduleType;
	}
	/**
	 * @param moduleType the moduleType to set
	 */
	public void setModuleType(Integer moduleType) {
		this.moduleType = moduleType;
	}
	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}
	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return the createUser
	 */
	public String getCreateUser() {
		return createUser;
	}
	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	/**
	 * @return the updateUser
	 */
	public String getUpdateUser() {
		return updateUser;
	}
	/**
	 * @param updateUser the updateUser to set
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
