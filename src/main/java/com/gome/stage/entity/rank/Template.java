package com.gome.stage.entity.rank;

import com.gome.stage.bean.vip.common.BaseModel;

import java.util.Date;


public class Template extends BaseModel {

	private static final long serialVersionUID = -7475962811909023579L;
	
	public static final int ENABLED = 0;//����
	public static final int DISABLED = 1;//ͣ��
	
	public static final int SMALL_CATALOG_TYPE = 1;//С��ģ��
	public static final int BRAND_TYPE = 2;//Ʒ��ģ��
	public static final int PRODUCT_TYPE = 3;//��Ʒģ��
	public static final int BIG_CATALOG_TYPE = 4;//����ģ��
	
	private Integer id;
	private Integer status;
	
	private String name;
	private String description;
	private String content;
	private String creator;
	private String updator;
	private String path;
	
	private Date createdTime;
	private Date updatedTime;
	
	//for search
	private Integer pageIndex;
	private Integer pageSize;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
