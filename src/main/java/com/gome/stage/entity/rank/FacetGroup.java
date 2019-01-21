package com.gome.stage.entity.rank;
public class FacetGroup extends ValueObject
{
	private static final long serialVersionUID = -3463839689737737461L;

	private Integer id;
	private String groupId;
	private String categoryId;
	private String displayName;
	private Integer weight;
	private Integer type;
	private String facetid;
	private String insertDate;
	private String updateDate;
	private String creator;
	private String updator;
	private Integer isshow;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getGroupId()
	{
		return groupId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	public String getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getWeight()
	{
		return weight;
	}

	public void setWeight(Integer weight)
	{
		this.weight = weight;
	}

	public Integer getType()
	{
		return type;
	}

	public void setType(Integer type)
	{
		this.type = type;
	}

	public String getFacetid()
	{
		return facetid;
	}

	public void setFacetid(String facetid)
	{
		this.facetid = facetid;
	}

	public String getInsertDate()
	{
		return insertDate;
	}

	public void setInsertDate(String insertDate)
	{
		if (insertDate != null && insertDate.endsWith(".0"))
		{
			this.insertDate = insertDate.replace(".0", "");
		}
		else
		{
			this.insertDate = insertDate;
		}
	}

	public String getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(String updateDate)
	{
		if (updateDate != null && updateDate.endsWith(".0"))
		{
			this.updateDate = updateDate.replace(".0", "");
		}
		else
		{
			this.updateDate = updateDate;
		}
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getUpdator()
	{
		return updator;
	}

	public void setUpdator(String updator)
	{
		this.updator = updator;
	}

	public Integer getIsshow()
	{
		return isshow;
	}

	public void setIsshow(Integer isshow)
	{
		this.isshow = isshow;
	}
}
