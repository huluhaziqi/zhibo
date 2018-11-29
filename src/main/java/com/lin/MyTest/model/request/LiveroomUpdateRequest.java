package com.lin.MyTest.model.request;

import javax.validation.constraints.Size;
import java.util.List;

public class LiveroomUpdateRequest {

    private Integer cityId;

    @Size(min = 4, max = 40, message = "标题长度应在4-40字之内")
    private String title;

    private String imagePath;

    private Long scheduledTime;

    /**
     *如果为null就默认不修改
     */
    private List<Integer> tagIds;

    /**
     * 如果为null 默认不修改
     */
    private String buildingId;

    /**
     * 如果是null默认不修改
     */
    private Byte buildingType;

    /**
     *如果为null就默认不修改
     */
    private List<String> countryCodes;

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public Byte getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(Byte buildingType) {
        this.buildingType = buildingType;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = countryCodes;
    }
}
