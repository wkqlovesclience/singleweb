/*
 * 文件名： PaginatedArrayList.java
 *
 * 创建日期： Apr 28, 2010
 *
 * Copyright(C) 2010, by caowei.
 *
 * 原始作者: <a href="mailto:caowei@staff.chinabyte.com">caowei</a>
 *
 */
package com.gome.stage.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import org.apache.log4j.Logger;

/**
 * 公共的列表，主要适用于查询数据库时，返回结果要分页处理
 *
 *
 * @version $Revision$
 *
 * @since Apr 28, 2010
 */
@SuppressWarnings({"serial", "rawtypes"})
public final class PaginatedArrayList<V> extends ArrayList<V> implements
		PaginatedList<V> {

	private static final Logger logger = Logger.getLogger(PaginatedArrayList.class);

	protected static final EmptyList EMPTY_LIST= new EmptyList();

	/**
	 * 当前页数
	 */
	private int pageIndex = 1;

	/**
	 * 每页显示记录数，当其值小于0时。应该是返回所有记录
	 */
	private int pageSize = 20;

	/**
	 * 总页数
	 */
	private int totalPage;

	/**
	 * 总纪录数
	 */
	private int totalRec;

	/**
	 * 开始记录数
	 */
	private int startPos;

	/**
	 * 结束记录数
	 */
	private int endPos;

	/**
	 * 显示页的第一页
	 */
	private int begin;
	/**
	 * 显示页的最后一页
	 */
	private int end;

	/**
	 * 通过总记录数、每页数目、页大小来构造实例，同时，还计算其它的值。<br>
	 * ------------------------------------------------------------------------
	 * --<br>
	 * 创建日期：2003-1-9<br>
	 * <br>
	 * 修改者：<br>
	 * 修改日期：<br>
	 * 修改原因：<br>
	 * ------------------------------------------------------------------------
	 * --
	 *
	 * @param pageIndex
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param totalRec
	 *            总纪录数
	 * @roseuid 40B2F47B0219
	 */
	public PaginatedArrayList(int totalRec, int pageIndex, int pageSize) {
		super(pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE);
		calculate(totalRec, pageIndex, pageSize, DEFAULT_DISPALY_PAGE_COUNT);
	}

	/**
	 *
	 * @param pageIndex
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param totalRec
	 *            总纪录数
	 * @roseuid 40B2F47B0219
	 * @param displayPageSize
	 *            共显示多少页
	 */
	public PaginatedArrayList(int totalRec, int pageIndex, int pageSize,
							  int displayPageSize) {
		super(pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE);
		if (displayPageSize < 0) {
			displayPageSize = DEFAULT_DISPALY_PAGE_COUNT;
		}
		calculate(totalRec, pageIndex, pageSize, displayPageSize);
	}

	public void setPageInfo(int totalRec, int pageIndex, int pageSize) {
		calculate(totalRec, pageIndex, pageSize, DEFAULT_DISPALY_PAGE_COUNT);
	}

	/**
	 * 通过总记录数、当前页、每页记录数，计算其它属性<br>
	 * ------------------------------------------------------------------------
	 * --<br>
	 * 创建日期：2003-1-9<br>
	 * <br>
	 * 修改者：<br>
	 * 修改日期：<br>
	 * 修改原因：<br>
	 * ------------------------------------------------------------------------
	 * --
	 *
	 * @param recNum
	 *            总纪录数
	 * @param pageIndex
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @roseuid 40B2F47B0238
	 */
	private void calculate(int recNum, int pageIndex, int pageSize,
						   int displayPageSize) {
		// ---------------------------------
		// 总记录数小于1没有任何任何计算意义。
		// ---------------------------------
		if (recNum < 1) {
			this.begin = 1;
			this.end = 1;
			return;
		}

		// ---------------------------------
		// 页大小为0的时候，无意义
		// ---------------------------------
		if (pageSize == 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		// ---------------------------------
		// 当前页小于1的时候。默认为第1页
		// ---------------------------------
		if (pageIndex < 1)
			pageIndex = 1;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.totalRec = recNum;

		if (pageSize > 0) {
			// ---------------------------------
			// 总页数=总记录/页大小，不能整除就要进一位
			// ---------------------------------
			if (recNum % pageSize > 0)
				totalPage = recNum / pageSize + 1;
			else
				totalPage = recNum / pageSize;

			// ---------------------------------
			// 当前页不能大于总页数。
			// ---------------------------------
			if (pageIndex > totalPage)
				this.pageIndex = totalPage;
			if (this.pageIndex < 1)
				this.pageIndex = 1;
			// --------------------------------------
			// 计算该显示的第一页最后一页的值
			// --------------------------------------
			boolean flag = displayPageSize % 2 == 0;
			int mid = flag ? displayPageSize / 2 : displayPageSize / 2 + 1;
			if (this.pageIndex <= mid) {
				this.begin = 1;
				this.end = displayPageSize >= this.totalPage ? this.totalPage
						: displayPageSize;
				this.end=this.end<=this.totalPage?this.end:this.totalPage;
			} else if (this.pageIndex >= this.totalPage - (mid - 1)) {
				this.end = this.totalPage;
				this.begin = 1 > this.totalPage - (mid - 1) ? 1
						: this.totalPage - (displayPageSize - 1);
				this.begin=this.begin>0?this.begin:1;
			} else {
				if (flag) {
					this.begin = this.pageIndex - (mid - 1);
					this.end = this.pageIndex + mid;
				} else {
					this.begin = this.pageIndex - (mid - 1);
					this.end = this.pageIndex + (mid - 1);
				}
			}

			// ---------------------------------
			// 计算数据库记录的开始处和结束处。
			// ---------------------------------
			startPos = (this.pageIndex - 1) * pageSize;
			endPos = this.pageIndex * pageSize;
		} else {
			// ---------------------------------
			// 页大小小于0的时候，应该是所有记录数
			// ---------------------------------
			startPos = 0;
			endPos = recNum;
			this.begin = 1;
			this.end = 1;
		}

	}

	/**
	 * 取得每页的记录数
	 *
	 * @return 第页记录数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 取得共有多少页
	 *
	 * @return 总页数
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * 取得总记录数
	 *
	 * @return 总记录数
	 */
	public int getTotalRec() {
		return totalRec;
	}

	/**
	 * 取得当前页
	 *
	 * @return 当前页
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * 判断当前页是不是第一页
	 *
	 * @return 结果
	 */
	public boolean isFirstPage() {
		return pageIndex == 1;
	}

	/**
	 * 判断当前页是不是最后一页
	 *
	 * @return 结果
	 */
	public boolean isLastPage() {
		return pageIndex == totalPage;
	}

	/**
	 * 符合记录范围的开始位置
	 *
	 * @return 位置
	 */
	public int getStartPos() {
		return startPos;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.coo8.btoc.util.pages.PaginatedList#getBegin()
	 */
	@Override
	public int getBegin() {
		return this.begin;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.coo8.btoc.util.pages.PaginatedList#getEnd()
	 */
	@Override
	public int getEnd() {
		return this.end;
	}

	/**
	 * 符合记录范围的结束位置
	 *
	 * @return 结束位置
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * 显示分页标志<br>
	 * 请参照： getPage(CommonList,int,String)方法<br>
	 * ------------------------------------------------------------------------
	 * --<br>
	 * 创建日期：2003-1-9<br>
	 * <br>
	 * 修改者：<br>
	 * 修改日期：<br>
	 * 修改原因：<br>
	 * ------------------------------------------------------------------------
	 * --
	 *
	 * @param cl
	 *            列表
	 * @return 结果
	 * @roseuid 40B2F47B0249
	 */
	public static String getPage(PaginatedList cl) {
		return getPage(cl, 10, null);
	}

	/**
	 * 显示分页标志<br>
	 * 请参照： getPage(CommonList,int,String)方法<br>
	 * ------------------------------------------------------------------------
	 * --<br>
	 * 创建日期：2003-1-9<br>
	 * <br>
	 * 修改者：<br>
	 * 修改日期：<br>
	 * 修改原因：<br>
	 * ------------------------------------------------------------------------
	 * --
	 *
	 * @param cl
	 *            列表
	 * @param pageNum
	 *            显示的数目
	 * @return 结果
	 * @roseuid 40B2F47B0267
	 */
	public static String getPage(PaginatedList cl, int pageNum) {
		return getPage(cl, pageNum, null);
	}

	/**
	 * 显示分页标志<br>
	 * 显示分页标志，此方法产生的结果要与f.js配合使用。<br>
	 * 返回结果看起来是这样：<br>
	 * &lt;a href="JavaScript:tunePage(0,'pageNo')">|&lt;&lt;/a> &lt;a
	 * href="JavaScript:tunePage(90,'pageNo')">&lt;&lt;/a> &lt;a
	 * href="JavaScript:tunePage(0,'pageNo')">...&lt;/a> &lt;a
	 * href="JavaScript:tunePage(100,'pageNo')">&lt;font
	 * color='#FF0000'>&lt;b>11&lt;/b>&lt;/font>&lt;/a> &lt;a
	 * href="JavaScript:tunePage(110,'pageNo')">12&lt;/a> .......... &lt;a
	 * href="JavaScript:tunePage(190,'pageNo')">20&lt;/a> &lt;a
	 * href="JavaScript:tunePage(200,'pageNo')">...&lt;/a> &lt;a
	 * href="JavaScript:tunePage(110,'pageNo')">&gt;&lt;/a> &lt;a
	 * href="JavaScript:tunePage(290,'pageNo')">&gt;|&lt;/a><br>
	 * 显示最终结果应该是这样：<br>
	 * |&lt; &lt; ... 11 -- 20. ... > >|<br>
	 * ------------------------------------------------------------------------
	 * --<br>
	 * 创建日期：2003-1-9<br>
	 * <br>
	 * 修改者：<br>
	 * 修改日期：<br>
	 * 修改原因：<br>
	 * ------------------------------------------------------------------------
	 * --
	 *
	 * @param cl
	 *            列表
	 * @param pageNum
	 *            每页中可以翻页的数目
	 * @param strPage
	 *            分页参数
	 * @return 结果
	 * @roseuid 40B2F47B0286
	 */
	public static String getPage(PaginatedList cl, int pageNum, String strPage) {
		// ---------------------------------
		// 定义返回结果变量
		// ---------------------------------
		String rValue = null;
		try {
			if (cl != null) {
				// ---------------------------------
				// 当列表不为null才有效
				// ---------------------------------
				StringBuilder buffer = new StringBuilder();

				// ---------------------------------
				// strPage可以为空。js中默认为pageNo
				// ---------------------------------
				strPage = strPage != null && strPage.length() > 0 ? ",\""
						+ strPage + "\"" : "";

				// ---------------------------------
				// 默认为每页显示10个可翻页的数字。
				// ---------------------------------
				if (pageNum < 1)
					pageNum = 10;
				// ---------------------------------
				// 加入总记录数、当前页、总页数标签
				// ---------------------------------
				buffer.append("共有[<font color='#FF0000'>" + cl.getTotalRec()
						+ "</font>]条记录，" + cl.getPageIndex() + "/"
						+ cl.getTotalPage() + "页。");
				if (cl.getPageIndex() > 1) {
					// ---------------------------------
					// 当前页不是第一页，
					// 还要加入第一页和上一页标签
					// ---------------------------------
					buffer
							.append(" <a href='JavaScript:tunePage(1"
									+ strPage
									+ ")'>|&lt;</a> <a href='JavaScript:tunePage("
									+ (cl.getPageIndex() - 1) + strPage + ")'>&lt;</a>");
				}

				// ---------------------------------
				// 如果当前页大于每页显示页码数。
				// 则要显示快速向上翻的标签(标签是：...)
				// ---------------------------------
				int currentNum = (cl.getPageIndex() % pageNum == 0 ? (cl
						.getPageIndex() / pageNum) - 1 : (int) (cl
						.getPageIndex() / pageNum))
						* pageNum;
				if (currentNum < 0)
					currentNum = 0;
				if (cl.getPageIndex() > pageNum) {
					buffer
							.append(" <a href='JavaScript:tunePage("
									+ (currentNum - pageNum + 1) + strPage + ")'>...</a>");
				}

				// ---------------------------------
				// 显示中间可用的翻页码
				// ---------------------------------
				for (int i = 0; i < pageNum; i++) {
					if ((currentNum + i + 1) > cl.getTotalPage()
							|| cl.getTotalPage() < 2)
						break;
					buffer
							.append(" <a href='JavaScript:tunePage("
									+ (currentNum + i + 1)
									+ strPage
									+ ")'>"
									+ (currentNum + i + 1 == cl.getPageIndex() ? "<font color='#FF0000'><b>"
									+ (currentNum + i + 1)
									+ "</b></font>"
									: (currentNum + i + 1) + "") + "</a>");
				}

				// ---------------------------------
				// 如果还未到达最后一版，
				// 则还要加入快速向下翻的标签(标签是：...)
				// ---------------------------------
				if (cl.getTotalPage() > (currentNum + pageNum))
					buffer
							.append(" <a href='JavaScript:tunePage("
									+ (currentNum + 1 + pageNum) + strPage + ")'>...</a>");

				// ---------------------------------
				// 如果当前页不是最后一页，
				// 则要加入下一页和最后一页标筌
				// ---------------------------------
				if (cl.getPageIndex() < cl.getTotalPage()) {
					buffer.append(" <a href='JavaScript:tunePage("
							+ (cl.getPageIndex() + 1) + strPage
							+ ")'>&gt;</a> <a href='JavaScript:tunePage("
							+ cl.getTotalPage() + strPage + ")'>&gt;|</a>");
				}
				rValue = buffer.toString();
				// ---------------------------------
				// 显式释放资源
				// ---------------------------------
				buffer.setLength(0);
				buffer = null;
			} else {
				rValue = "";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rValue = "";
			logger.error(e.getMessage(), e);
		}
		cl = null;
		return rValue;
	}

	public static String getAjaxPage(PaginatedList cl) {
		return getAjaxPage(cl, 10, null, null);
	}

	public static String getAjaxPage(PaginatedList cl, String funcName) {
		if (funcName == null || funcName.length() == 0)
			return getAjaxPage(cl, cl.getPageSize(), null, null);
		else
			return getAjaxPage(cl, cl.getPageSize(), null, funcName);
	}

	public static String getAjaxPage(PaginatedList cl, int pageNum,
									 String strPage, String funcName) {
		// ---------------------------------
		// 定义返回结果变量
		// ---------------------------------

		String rValue = null;

		if (funcName == null || funcName.length() == 0)
			funcName = "tuneAjaxPage";

		try {
			if (cl != null) {
				// ---------------------------------
				// 当列表不为null才有效
				// ---------------------------------
				StringBuilder buffer = new StringBuilder();

				// ---------------------------------
				// strPage可以为空。js中默认为pageNo
				// ---------------------------------
				strPage = strPage != null && strPage.length() > 0 ? ",\""
						+ strPage + "\"" : "";

				// ---------------------------------
				// 默认为每页显示10个可翻页的数字。
				// ---------------------------------
				if (pageNum < 1)
					pageNum = 10;
				// ---------------------------------
				// 加入总记录数、当前页、总页数标签
				// ---------------------------------
				buffer.append("共有[<font color='#FF0000'>" + cl.getTotalRec()
						+ "</font>]条记录，" + cl.getPageIndex() + "/"
						+ cl.getTotalPage() + "页。");
				if (cl.getPageIndex() > 1) {
					// ---------------------------------
					// 当前页不是第一页，
					// 还要加入第一页和上一页标签
					// ---------------------------------
					buffer
							.append(" <a href='JavaScript:"
									+ funcName
									+ "(1"
									+ strPage
									+ ","
									+ pageNum
									+ ")'><font face='Webdings'>9</font></a> <a href='JavaScript:"
									+ funcName + "(" + (cl.getPageIndex() - 1)
									+ strPage + "," + pageNum + ")'><font face='Webdings'>7</font></a>");
				}

				// ---------------------------------
				// 如果当前页大于每页显示页码数。
				// 则要显示快速向上翻的标签(标签是：...)
				// ---------------------------------
				int currentNum = (cl.getPageIndex() % pageNum == 0 ? (cl
						.getPageIndex() / pageNum) - 1 : (int) (cl
						.getPageIndex() / pageNum))
						* pageNum;
				if (currentNum < 0)
					currentNum = 0;
				if (cl.getPageIndex() > pageNum) {
					buffer.append(" <a href='JavaScript:" + funcName + "("
							+ (currentNum - pageNum + 1) + strPage + ","
							+ pageNum + ")'>...</a>");
				}

				// ---------------------------------
				// 显示中间可用的翻页码
				// ---------------------------------
				for (int i = 0; i < pageNum; i++) {
					if ((currentNum + i + 1) > cl.getTotalPage()
							|| cl.getTotalPage() < 2)
						break;
					buffer
							.append(" <a href='JavaScript:"
									+ funcName
									+ "("
									+ (currentNum + i + 1)
									+ strPage
									+ ","
									+ pageNum
									+ ")'>"
									+ (currentNum + i + 1 == cl.getPageIndex() ? "<font color='#FF0000'><b>"
									+ (currentNum + i + 1)
									+ "</b></font>"
									: (currentNum + i + 1) + "") + "</a>");
				}

				// ---------------------------------
				// 如果还未到达最后一版，
				// 则还要加入快速向下翻的标签(标签是：...)
				// ---------------------------------
				if (cl.getTotalPage() > (currentNum + pageNum))
					buffer.append(" <a href='JavaScript:" + funcName + "("
							+ (currentNum + 1 + pageNum) + strPage + ","
							+ pageNum + ")'>...</a>");

				// ---------------------------------
				// 如果当前页不是最后一页，
				// 则要加入下一页和最后一页标筌
				// ---------------------------------
				if (cl.getPageIndex() < cl.getTotalPage()) {
					buffer
							.append(" <a href='JavaScript:"
									+ funcName
									+ "("
									+ (cl.getPageIndex() + 1)
									+ strPage
									+ ","
									+ pageNum
									+ ")'><font face='Webdings'>8</font></a> <a href='JavaScript:"
									+ funcName + "(" + cl.getTotalPage()
									+ strPage + "," + pageNum + ")'><font face='Webdings'>:</font></a>");
				}
				rValue = buffer.toString();
				// ---------------------------------
				// 显式释放资源
				// ---------------------------------
				buffer.setLength(0);
				buffer = null;
			} else {
				rValue = "";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rValue = "";
			logger.error(e.getMessage(), e);
		}
		cl = null;
		return rValue;
	}

	public static String getAjaxPage(PaginatedList cl, int pageNum,
									 String strPage) {
		// ---------------------------------
		// 定义返回结果变量
		// ---------------------------------

		String rValue = null;

		try {
			if (cl != null) {
				// ---------------------------------
				// 当列表不为null才有效
				// ---------------------------------
				StringBuilder buffer = new StringBuilder();

				// ---------------------------------
				// strPage可以为空。js中默认为pageNo
				// ---------------------------------
				strPage = strPage != null && strPage.length() > 0 ? ",\""
						+ strPage + "\"" : "";

				// ---------------------------------
				// 默认为每页显示10个可翻页的数字。
				// ---------------------------------
				if (pageNum < 1)
					pageNum = 10;
				// ---------------------------------
				// 加入总记录数、当前页、总页数标签
				// ---------------------------------
				buffer.append("共有[<font color='#FF0000'>" + cl.getTotalRec()
						+ "</font>]条记录，" + cl.getPageIndex() + "/"
						+ cl.getTotalPage() + "页。");
				if (cl.getPageIndex() > 1) {
					// ---------------------------------
					// 当前页不是第一页，
					// 还要加入第一页和上一页标签
					// ---------------------------------
					buffer
							.append(" <a href='JavaScript:tuneAjaxPage(1"
									+ strPage
									+ ")'><font face='Webdings'>9</font></a> <a href='JavaScript:tuneAjaxPage("
									+ (cl.getPageIndex() - 1) + strPage + ")'><font face='Webdings'>7</font></a>");
				}

				// ---------------------------------
				// 如果当前页大于每页显示页码数。
				// 则要显示快速向上翻的标签(标签是：...)
				// ---------------------------------
				int currentNum = (cl.getPageIndex() % pageNum == 0 ? (cl
						.getPageIndex() / pageNum) - 1 : (int) (cl
						.getPageIndex() / pageNum))
						* pageNum;
				if (currentNum < 0)
					currentNum = 0;
				if (cl.getPageIndex() > pageNum) {
					buffer
							.append(" <a href='JavaScript:tuneAjaxPage("
									+ (currentNum - pageNum + 1) + strPage + ")'>...</a>");
				}

				// ---------------------------------
				// 显示中间可用的翻页码
				// ---------------------------------
				for (int i = 0; i < pageNum; i++) {
					if ((currentNum + i + 1) > cl.getTotalPage()
							|| cl.getTotalPage() < 2)
						break;
					buffer
							.append(" <a href='JavaScript:tuneAjaxPage("
									+ (currentNum + i + 1)
									+ strPage
									+ ")'>"
									+ (currentNum + i + 1 == cl.getPageIndex() ? "<font color='#FF0000'><b>"
									+ (currentNum + i + 1)
									+ "</b></font>"
									: (currentNum + i + 1) + "") + "</a>");
				}

				// ---------------------------------
				// 如果还未到达最后一版，
				// 则还要加入快速向下翻的标签(标签是：...)
				// ---------------------------------
				if (cl.getTotalPage() > (currentNum + pageNum))
					buffer
							.append(" <a href='JavaScript:tuneAjaxPage("
									+ (currentNum + 1 + pageNum) + strPage + ")'>...</a>");

				// ---------------------------------
				// 如果当前页不是最后一页，
				// 则要加入下一页和最后一页标筌
				// ---------------------------------
				if (cl.getPageIndex() < cl.getTotalPage()) {
					buffer
							.append(" <a href='JavaScript:tuneAjaxPage("
									+ (cl.getPageIndex() + 1)
									+ strPage
									+ ")'><font face='Webdings'>8</font></a> <a href='JavaScript:tuneAjaxPage("
									+ cl.getTotalPage() + strPage + ")'><font face='Webdings'>:</font></a>");
				}
				rValue = buffer.toString();
				// ---------------------------------
				// 显式释放资源
				// ---------------------------------
				buffer.setLength(0);
				buffer = null;
			} else {
				rValue = "";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rValue = "";
			logger.error(e.getMessage(), e);
		}
		cl = null;
		return rValue;
	}

	@SuppressWarnings("unchecked")
	public static final <T> List<T> emptyList() {
		return (List<T>) EMPTY_LIST;
	}

	private static class EmptyList extends AbstractList<Object>
			implements RandomAccess, Serializable,PaginatedList<Object> {

		private static final long serialVersionUID = 8842843931221139166L;

		public int size() {return 0;}

		public boolean contains(Object obj) {return false;}

		public Object get(int index) {
			throw new IndexOutOfBoundsException("Index: "+index);
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_LIST;
		}

		@Override
		public int getPageSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getTotalPage() {
			return 0;
		}

		@Override
		public int getTotalRec() {
			return 0;
		}

		@Override
		public int getPageIndex() {
			return 0;
		}

		@Override
		public boolean isFirstPage() {
			return false;
		}

		@Override
		public boolean isLastPage() {
			return false;
		}

		@Override
		public int getStartPos() {
			return 0;
		}

		@Override
		public int getEndPos() {
			return 0;
		}

		@Override
		public int getBegin() {
			return 0;
		}

		@Override
		public int getEnd() {
			return 0;
		}

	}

	/**
	 * 请保留下面的注释
	 *
	 *
	 * js 翻页代码 function tunePage(toPageNo,pageNo) { try { var topage = 1;
	 * if(typeof(toPageNo) != "number" || toPageNo < 1) topage = 1; else topage
	 * = toPageNo; var olds = window.location.searchSubject; if(typeof(pageNo)
	 * == "undefined" || pageNo == "") pageNo = "pageNo"; var news = "";
	 * if(olds.length > 1) { olds = olds.substring(1,olds.length); var arrays =
	 * olds.split("&"); for (var i = 0; i < arrays.length ; i++) {
	 * if(arrays[i].indexOf(pageNo + "=") < 0 && arrays[i].length > 1) { news +=
	 * "&" + arrays[i]; } } if(news.length > 1) { news = "?" +
	 * news.substring(1,news.length) + "&" + pageNo + "=" + topage; } else {
	 * news = "?" + pageNo + "=" + topage; } } else { news = "?" + pageNo + "="
	 * + topage; } window.location = window.location.pathname + news; } catch(e)
	 * { window.location = window.location.pathname +
	 * window.location.searchSubject; } } function sAll(thisObj,dObj) { try {
	 * var l = eval(dObj + ".length"); if(typeof(l) == "undefined") {
	 * eval(dObj).checked = thisObj.checked; } else { for(var i = 0; i < l; i++)
	 * { if(!eval(dObj + "[" + i + "]").disabled) eval(dObj + "[" + i +
	 * "]").checked = thisObj.checked; } } } catch(e){} }
	 */

}
