package com.lin.MyTest.model.biz.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ListWithTotalCount<T> implements Serializable {

    private static final long serialVersionUID = 24379827243423L;

    private List<T> list;
    private int totalCount;

    public ListWithTotalCount() {
    }

    public ListWithTotalCount(List<T> list, int totalCount) {
        this.list = list;
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
