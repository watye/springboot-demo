package com.talelife.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * mybatis分页辅助类
 * date 2018-06-08
 * 
 * @author lwy
 * @param <E>
 */
public final class Page<E> implements Serializable{
	private static final long serialVersionUID = -2775453835720800655L;
	/**
	 * 默认每页记录数
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	private int pageNum;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 开始行
     */
    private int startRow;
    /**
     * 结束行
     */
    private int endRow;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 总页数
     */
    private int pages;
    
    private transient List<E> data = new ArrayList<>();

    
	public Page() {
		super();
	}
	
	public Page(com.github.pagehelper.Page<E> page) {
		super();
		this.data = page.getResult();
		this.pageSize = page.getPageSize();
		this.endRow = page.getEndRow();
		this.pageNum = page.getPageNum();
		this.pages = page.getPages();
		this.startRow = page.getStartRow();
		this.total = page.getTotal();
	}
	
	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public List<E> getData() {
		return data;
	}

	public void setData(List<E> data) {
		this.data = data;
	}
	
    
}
