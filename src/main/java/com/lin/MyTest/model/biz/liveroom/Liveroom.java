package com.lin.MyTest.model.biz.liveroom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lin.MyTest.model.entity.TagEntity;

import java.sql.Timestamp;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Liveroom {

    @JsonProperty("id")
	private long id;

    @JsonProperty("hostId")
	private long hostId;

    @JsonProperty("chatroomId")
	private String chatroomId;

    @JsonProperty("topicId")
	private Long topicId;

    @JsonProperty("status")
	private Byte status;

    @JsonProperty("type")
	private Byte type;

    @JsonProperty("title")
	private String title;

    @JsonProperty("info")
	private String info;

    @JsonProperty("imgPath")
	private String imgPath;

    @JsonProperty("imgPartialPath")
	private String imgPartialPath;

    @JsonProperty("cityId")
	private Integer cityId;

    @JsonProperty("liveRtmpUrl")
	private String liveRtmpUrl;

    @JsonProperty("liveHlsUrl")
	private String liveHlsUrl;

    @JsonProperty("liveFlvUrl")
	private String liveFlvUrl;

    @JsonProperty("likeCount")
	private int likeCount;

    @JsonProperty("curAudienceCount")
	private int curAudienceCount;

    @JsonProperty("totalAudienceCount")
	private int totalAudienceCount;

    @JsonProperty("activitySubscribeCount")
	private int activitySubscribeCount;

    @JsonProperty("commentCount")
	private int commentCount;

    @JsonProperty("commentUserCount")
	private int commentUserCount;

    @JsonProperty("createTime")
	private Timestamp createTime;

    @JsonProperty("updateTime")
	private Timestamp updateTime;

    @JsonProperty("scheduledTime")
	private Timestamp scheduledTime;

    @JsonProperty("liveStartTime")
	private Timestamp liveStartTime;

    @JsonProperty("liveEndTime")
	private Timestamp liveEndTime;

    @JsonProperty("interruptTime")
	private Timestamp interruptTime;

    @JsonProperty("imageUrl")
	private String imageUrl; // 封面大图url

    @JsonProperty("imagePartialUrl")
	private String imagePartialUrl; // 封面小图url

    @JsonProperty("scheduleCountDown")
	private Integer scheduleCountDown; // 从现在到预告开始剩余的秒数

    @JsonProperty("currentDurationSeconds")
	private Integer currentDurationSeconds;

    @JsonProperty("qrCodeStr")
	private String qrCodeStr; // 用于生成二维码的字符串

    @JsonProperty("bbsUrl")
	private String bbsUrl;
    
    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("videoType")
	private Byte videoType;

    @JsonProperty("tagEntities")
	private List<TagEntity> tagEntities;

	@JsonProperty("denyReason")
    private String denyReason;	//驳回原因

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

	public long getHostId() {
		return hostId;
	}

	public void setHostId(long hostId) {
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getImgPartialPath() {
		return imgPartialPath;
	}

	public void setImgPartialPath(String imgPartialPath) {
		this.imgPartialPath = imgPartialPath;
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

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getCurAudienceCount() {
		return curAudienceCount;
	}

	public void setCurAudienceCount(int curAudienceCount) {
		this.curAudienceCount = curAudienceCount;
	}

	public int getTotalAudienceCount() {
		return totalAudienceCount;
	}

	public void setTotalAudienceCount(int totalAudienceCount) {
		this.totalAudienceCount = totalAudienceCount;
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

	public String getImagePartialUrl() {
		return imagePartialUrl;
	}

	public void setImagePartialUrl(String imagePartialUrl) {
		this.imagePartialUrl = imagePartialUrl;
	}

	public Integer getScheduleCountDown() {
		return scheduleCountDown;
	}

	public void setScheduleCountDown(Integer scheduleCountDown) {
		this.scheduleCountDown = scheduleCountDown;
	}

	public String getQrCodeStr() {
		return qrCodeStr;
	}

	public void setQrCodeStr(String qrCodeStr) {
		this.qrCodeStr = qrCodeStr;
	}

	public Integer getCurrentDurationSeconds() {
		return currentDurationSeconds;
	}

	public void setCurrentDurationSeconds(Integer currentDurationSeconds) {
		this.currentDurationSeconds = currentDurationSeconds;
	}

	public int getActivitySubscribeCount() {
		return activitySubscribeCount;
	}

	public void setActivitySubscribeCount(int activitySubscribeCount) {
		this.activitySubscribeCount = activitySubscribeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getCommentUserCount() {
		return commentUserCount;
	}

	public void setCommentUserCount(int commentUserCount) {
		this.commentUserCount = commentUserCount;
	}

	public String getBbsUrl() {
		return bbsUrl;
	}

	public void setBbsUrl(String bbsUrl) {
		this.bbsUrl = bbsUrl;
	}

	public List<TagEntity> getTagEntities() {
		return tagEntities;
	}

	public void setTagEntities(List<TagEntity> tagEntities) {
		this.tagEntities = tagEntities;
	}

	public Byte getVideoType() {
		return videoType;
	}

	public void setVideoType(Byte videoType) {
		this.videoType = videoType;
	}

	public String getDenyReason() {
		return denyReason;
	}

	public void setDenyReason(String denyReason) {
		this.denyReason = denyReason;
	}
}
