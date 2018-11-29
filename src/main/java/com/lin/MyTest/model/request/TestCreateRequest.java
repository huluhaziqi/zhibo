package com.lin.MyTest.model.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class TestCreateRequest {

	@NotNull(message = "用户ID不能为空")
	private Long userId;

	@NotNull(message = "创建类型，1创建xxxx，2上传xxxx，不能为空")
	private Byte videoType;

	@NotNull(message = "xxxx封面不可为空")
	private String imagePath;

	private Byte type;

	@NotNull(message = "标题不能为空")
	@Size(min = 4, max = 40, message = "标题长度应在4-40字之内")
	private String title;

	@NotNull(message = "城市不能为空")
	private Integer cityId;

	@NotNull(message = "标签不能为空")
	private Integer tagId;

	private String fileId;

	private String buildingId;

	private Byte buildingType;

	private Date scheduledTime = new Date();

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
		this.title = title;
	}

	public Byte getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(Byte buildingType) {
		this.buildingType = buildingType;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

	public Byte getVideoType() {
		return videoType;
	}

	public void setVideoType(Byte videoType) {
		this.videoType = videoType;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
