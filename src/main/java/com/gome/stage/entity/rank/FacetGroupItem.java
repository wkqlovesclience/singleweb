package com.gome.stage.entity.rank;

public class FacetGroupItem extends ValueObject
{
	private static final long serialVersionUID = -6886662086757236839L;

	private Integer id;
	private String catId;
	private String value;
	private String code;
	private Integer index;
	private String parentId;
	private String facetId;
	private String insertDate;
	private String updateDate;
	private Integer isshow;
	private String creator;
	private String updator;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getCatId()
	{
		return catId;
	}

	public void setCatId(String catId)
	{
		this.catId = catId;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public Integer getIndex()
	{
		return index;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
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

	public Integer getIsshow()
	{
		return isshow;
	}

	public void setIsshow(Integer isshow)
	{
		this.isshow = isshow;
	}

	public String getFacetId()
	{
		return facetId;
	}

	public void setFacetId(String facetId)
	{
		this.facetId = facetId;
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
}
