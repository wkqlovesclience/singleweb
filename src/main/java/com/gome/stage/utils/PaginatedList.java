/*
 * 文件名： PaginatedList.java
 * 
 * 创建日期： Apr 28, 2010
 *
 * Copyright(C) 2010, by caowei.
 *
 * 原始作者: <a href="mailto:caowei@staff.chinabyte.com">caowei</a>
 *
 */
package com.gome.stage.utils;

import java.util.List;

/**
 * 
 * 公共的列表，主要适用于查询数据库时，返回结果要分页处理
 * 
 * @version $Revision$
 * 
 * @since Apr 28, 2010
 */
public interface PaginatedList<V> extends List<V> {
	/**
	 * 取得每页的记录数
	 * 
	 * @return 第页记录数
	 */
	int getPageSize();

	/**
	 * 取得共有多少页
	 * 
	 * @return 总页数
	 */
	int getTotalPage();

	/**
	 * 取得总记录数
	 * 
	 * @return 总记录数
	 */
	int getTotalRec();

	/**
	 * 取得当前页
	 * 
	 * @return 当前页
	 */
	int getPageIndex();

	/**
	 * 判断当前页是不是第一页
	 * 
	 * @return 结果
	 */
	boolean isFirstPage();

	/**
	 * 判断当前页是不是最后一页
	 * 
	 * @return 结果
	 */
	boolean isLastPage();

	/**
	 * 符合记录范围的开始位置
	 * 
	 * @return 位置
	 */
	int getStartPos();

	/**
	 * 符合记录范围的结束位置
	 * 
	 * @return 结束位置
	 */
	int getEndPos();
	
	/**
	 * @return 页面显示页的第一页
	 */
	int getBegin();
	/**
	 * 
	 * @return 页面显示页的最后一页
	 */
	int getEnd();
	
	/**
	 * 默认页大小
	 */
	public static final int DEFAULT_PAGE_SIZE = 40;
	
	
	/**
	 * 该属性仅供基于Struts框架使用
	 */
	public static final String COMMONLIST_TAG_KEY = "paginatedlist_tag_key";
	
	/**
	 * 每个页面只显示5 例如 1 2 3 4 5 
	 */
	public static final int DEFAULT_DISPALY_PAGE_COUNT=5;
}
