package com.lin.MyTest.model.request;

public interface PagingInterface {

	Integer getPageNumber();

	Integer getPageSize();

	default int getOffset() {
		return (getPageNumber() - 1) * getPageSize();
	}

	default int getLimit() {
		return getPageSize();
	}
}
