package com.lin.MyTest.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QCloudVODVideoTransInfoV2Response {

	@JsonProperty("code")
	private int status;

	@JsonProperty("message")
	private String errorMessage;

	@JsonProperty("transcodeInfo")
	private TranscodeInfo transcodeInfo;


	@JsonProperty("basicInfo")
	private BasicInfo basicInfo;

	public BasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInfo basicInfo) {
		this.basicInfo = basicInfo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public TranscodeInfo getTranscodeInfo() {
		return transcodeInfo;
	}

	public void setTranscodeInfo(TranscodeInfo transcodeInfo) {
		this.transcodeInfo = transcodeInfo;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class TranscodeInfo {
		private List<TransCode> transcodeList;

		public List<TransCode> getTranscodeList() {
			return transcodeList;
		}

		public void setTranscodeList(List<TransCode> transcodeList) {
			this.transcodeList = transcodeList;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BasicInfo {
		private String name; // 视频名称
		private Integer size; // 视频大小。单位：字节
		private Integer duration; // 视频时长。单位：秒
		private String description; // 视频描述
		private String status; // 视频状态， normal：正常
		private Integer createTime; // 视频创建时xx，距离1970-01-01 00:00:00的秒数
		private Integer updateTime; // 视频信息最近更新时xx，距离1970-01-01 00:00:00的秒数
		private Integer expireTime; // 视频过期时xx，距离1970-01-01 00:00:00的秒数
		private Integer classificationId; // 视频分类Id
		private String classificationName; // 视频分类名称
		private Integer playerId; // 播放器Id
		private String coverUrl; // 视频封面图片地址
		private String type; // 视频封装格式
		private String sourceVideoUrl; // 视频源文件地址

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public Integer getDuration() {
			return duration;
		}

		public void setDuration(Integer duration) {
			this.duration = duration;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Integer getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Integer createTime) {
			this.createTime = createTime;
		}

		public Integer getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Integer updateTime) {
			this.updateTime = updateTime;
		}

		public Integer getExpireTime() {
			return expireTime;
		}

		public void setExpireTime(Integer expireTime) {
			this.expireTime = expireTime;
		}

		public Integer getClassificationId() {
			return classificationId;
		}

		public void setClassificationId(Integer classificationId) {
			this.classificationId = classificationId;
		}

		public String getClassificationName() {
			return classificationName;
		}

		public void setClassificationName(String classificationName) {
			this.classificationName = classificationName;
		}

		public Integer getPlayerId() {
			return playerId;
		}

		public void setPlayerId(Integer playerId) {
			this.playerId = playerId;
		}

		public String getCoverUrl() {
			return coverUrl;
		}

		public void setCoverUrl(String coverUrl) {
			this.coverUrl = coverUrl;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSourceVideoUrl() {
			return sourceVideoUrl;
		}

		public void setSourceVideoUrl(String sourceVideoUrl) {
			this.sourceVideoUrl = sourceVideoUrl;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TransCode {
		private String url;

		private Integer definition;

		private Integer bitrate;

		private Integer height;

		private Integer width;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Integer getDefinition() {
			return definition;
		}

		public void setDefinition(Integer definition) {
			this.definition = definition;
		}

		public Integer getBitrate() {
			return bitrate;
		}

		public void setBitrate(Integer bitrate) {
			this.bitrate = bitrate;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}
	}
}
