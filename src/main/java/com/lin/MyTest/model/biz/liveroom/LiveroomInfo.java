package com.lin.MyTest.model.biz.liveroom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveroomInfo {

    private long id;
    private Long hostId;
    private String chatroomId;
    private Long topicId;
    private Byte status;
    private Byte type;
    private Byte videoType;
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

    //回放信息
    private Integer duration;
    private String playUrl;

    //填充信息
    private String imageUrl; // 封面大图url
    private String shareUrl; // 分享地址
    private String bbsUrl;  //论坛地址

    private String denyReason;
    private String cityStr;

    private List<String> countryCodes;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        this.chatroomId = chatroomId;
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

    public byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getLiveRtmpUrl() {
        return liveRtmpUrl;
    }

    public void setLiveRtmpUrl(String liveRtmpUrl) {
        this.liveRtmpUrl = liveRtmpUrl;
    }

    public String getLiveHlsUrl() {
        return liveHlsUrl;
    }

    public void setLiveHlsUrl(String liveHlsUrl) {
        this.liveHlsUrl = liveHlsUrl;
    }

    public String getLiveFlvUrl() {
        return liveFlvUrl;
    }

    public void setLiveFlvUrl(String liveFlvUrl) {
        this.liveFlvUrl = liveFlvUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBbsUrl() {
        return bbsUrl;
    }

    public void setBbsUrl(String bbsUrl) {
        this.bbsUrl = bbsUrl;
    }

    public Byte getVideoType() {
        return videoType;
    }

    public void setVideoType(Byte videoType) {
        this.videoType = videoType;
    }

    public String getHostRtmpUrl() {
        return hostRtmpUrl;
    }

    public void setHostRtmpUrl(String hostRtmpUrl) {
        this.hostRtmpUrl = hostRtmpUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getDenyReason() {
        return denyReason;
    }

    public void setDenyReason(String denyReason) {
        this.denyReason = denyReason;
    }


    public String getCityStr() {
        return cityStr;
    }

    public void setCityStr(String cityStr) {
        this.cityStr = cityStr;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = countryCodes;
    }
}
