package com.lin.MyTest.model.biz.liveroom;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.completion.Completion;

import java.io.Serializable;

@Document(indexName = "test-live-liveroom")
public class LiveroomSearch implements Serializable{

    @Id
    private long id;

    @Field
    private String title;

    @Field
    private String pinyin;

    @Field
    private String firstCharacter;

    @Field
    private String iks;

    @Field
    private Integer totalVv;

    @Field
    private Integer cityId;

    @Field
    private Integer status;

    @Field
    private Integer tag;

    @Field
    private Completion titleSuggest;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getFirstCharacter() {
        return firstCharacter;
    }

    public void setFirstCharacter(String firstCharacter) {
        this.firstCharacter = firstCharacter;
    }

    public Integer getTotalVv() {
        return totalVv;
    }

    public void setTotalVv(Integer totalVv) {
        this.totalVv = totalVv;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIks() {
        return iks;
    }

    public void setIks(String iks) {
        this.iks = iks;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Completion getTitleSuggest() {
        return titleSuggest;
    }

    public void setTitleSuggest(Completion titleSuggest) {
        this.titleSuggest = titleSuggest;
    }
}
