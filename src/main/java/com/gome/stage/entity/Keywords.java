/* 
 *   WWW.COO8.COM  
 */

package com.gome.stage.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author  JIANGCHENG
 * @version 1.0
 * @since 1.0
 */


public class Keywords implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;
	
	//alias
	public static final String TABLE_ALIAS = "Keywords";
	public static final String ALIAS_ID = "id";
	public static final String ALIAS_TYPES = "types";
	public static final String ALIAS_NAMES = "names";
	public static final String ALIAS_URL = "url";
	public static final String ALIAS_CREATE_TIME = "createTime";
	public static final String ALIAS_UPDATE_TIME = "updateTime";
	public static final String ALIAS_CREATOR = "creator";
	
	 
	 
	//columns START
	private Integer id;
	private String types;
	private String names;
	private String url;
	private java.util.Date createTime;
	private java.util.Date updateTime;
	private String creator;
	//columns END

	public Keywords(){
	}

	public Keywords(
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

	public void setTypes(String value) {
		this.types = value;
	}

	public String getTypes() {
		return this.types;
	}

	public void setNames(String value) {
		this.names = value;
	}

	public String getNames() {
		return this.names;
	}

	public void setUrl(String value) {
		this.url = value;
	}

	public String getUrl() {
		return this.url;
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

	public void setCreator(String value) {
		this.creator = value;
	}

	public String getCreator() {
		return this.creator;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Types",getTypes())
			.append("Names",getNames())
			.append("Url",getUrl())
			.append("CreateTime",getCreateTime())
			.append("UpdateTime",getUpdateTime())
			.append("Creator",getCreator())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Keywords == false) return false;
		if(this == obj) return true;
		Keywords other = (Keywords)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

