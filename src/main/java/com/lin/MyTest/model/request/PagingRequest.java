package com.lin.MyTest.model.request;

import javax.validation.constraints.Min;

public class PagingRequest implements PagingInterface {
	// 页数
	@Min(value = 1, message = "pageNumber应为正整数")
	private Integer pageNumber;
	// 每页的个数
	private Integer pageSize;

	public PagingRequest() {
	}

	public PagingRequest(Integer pageNumber, Integer pageSize) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public PagingInterface getPaging() {
		return this;
	}

	@Override
	public Integer getPageNumber() {
		return pageNumber == null ? 1 : pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public Integer getPageSize() {
		return pageSize == null ? 10 : pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
