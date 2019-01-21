/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc ��Ʒ��������
 */
package com.gome.stage.entity.rank;

public class Category extends ValueObject
{
	private static final long serialVersionUID = -7821859663144753029L;

	private Integer cid;
	private String id;
	private String parentId;
	private String categoryName;
	private Integer sort;
	private String createTime;
	private String updateTime;
	private String creator;
	private String updator;
	private Integer isShow;
	private Integer level;
	private Integer ispublish;

	public Integer getCid()
	{
		return cid;
	}

	public void setCid(Integer cid)
	{
		this.cid = cid;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public Integer getSort()
	{
		return sort;
	}

	public void setSort(Integer sort)
	{
		this.sort = sort;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		if(createTime != null && createTime.endsWith(".0"))
		{
			this.createTime = createTime.replace(".0", "");
		}
		else
		{
			this.createTime = createTime;
		}
	}

	public String getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(String updateTime)
	{
		if(updateTime != null && updateTime.endsWith(".0"))
		{
			this.updateTime = updateTime.replace(".0", "");
		}
		else
		{
			this.updateTime = updateTime;
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

	public Integer getIsShow()
	{
		return isShow;
	}

	public void setIsShow(Integer isShow)
	{
		this.isShow = isShow;
	}

	public Integer getLevel()
	{
		return level;
	}

	public void setLevel(Integer level)
	{
		this.level = level;
	}

	public Integer getIspublish()
	{
		return ispublish;
	}

	public void setIspublish(Integer ispublish)
	{
		this.ispublish = ispublish;
	}
}
