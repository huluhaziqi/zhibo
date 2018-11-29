package com.lin.MyTest.model.entity;

import java.sql.Timestamp;

public class LiveroomEntity {

    private Long id;

    private Long hostId;

    private String chatroomId;

    private Long topicId;

    private Byte status;

    private Byte videoType;

    private Byte type;

    private String title;

    private String imgPath;

    private Integer cityId;

    private String hostRtmpUrl;

    private String liveRtmpUrl;

    private String liveHlsUrl;

    private String liveFlvUrl;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Timestamp scheduledTime;

    private Timestamp liveStartTime;

    private Timestamp liveEndTime;

    private Timestamp interruptTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId == null ? null : chatroomId.trim();
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath == null ? null : imgPath.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getHostRtmpUrl() {
        return hostRtmpUrl;
    }

    public void setHostRtmpUrl(String hostRtmpUrl) {
        this.hostRtmpUrl = hostRtmpUrl == null ? null : hostRtmpUrl.trim();
    }

    public String getLiveRtmpUrl() {
        return liveRtmpUrl;
    }

    public void setLiveRtmpUrl(String liveRtmpUrl) {
        this.liveRtmpUrl = liveRtmpUrl == null ? null : liveRtmpUrl.trim();
    }

    public String getLiveHlsUrl() {
        return liveHlsUrl;
    }

    public void setLiveHlsUrl(String liveHlsUrl) {
        this.liveHlsUrl = liveHlsUrl == null ? null : liveHlsUrl.trim();
    }

    public String getLiveFlvUrl() {
        return liveFlvUrl;
    }

    public void setLiveFlvUrl(String liveFlvUrl) {
        this.liveFlvUrl = liveFlvUrl == null ? null : liveFlvUrl.trim();
    }

    public void setLiveStartTime(Timestamp liveStartTime) {
        this.liveStartTime = liveStartTime;
    }

    public Timestamp getLiveEndTime() {
        return liveEndTime;
    }

    public void setLiveEndTime(Timestamp liveEndTime) {
        this.liveEndTime = liveEndTime;
    }

    public Timestamp getInterruptTime() {
        return interruptTime;
    }

    public void setInterruptTime(Timestamp interruptTime) {
        this.interruptTime = interruptTime;
    }

    public Byte getVideoType() {
        return videoType;
    }

    public void setVideoType(Byte videoType) {
        this.videoType = videoType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Timestamp getLiveStartTime() {
        return liveStartTime;
    }
}