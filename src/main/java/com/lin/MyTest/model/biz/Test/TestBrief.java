
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lin.MyTest.model.entity.TagEntity;
import com.lin.MyTest.model.entity.TestEntity;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestBrief {

	@JsonProperty("id")
	private long id;

	@JsonProperty("hostId")
	private long hostId;

	@JsonProperty("chatroomId")
	private String chatroomId;

	@JsonProperty("topicId")
	private Long topicId;

	@JsonProperty("status")
	private byte status;

	@JsonProperty("type")
	private byte type;

	@JsonProperty("title")
	private String title;

	@JsonProperty("info")
	private String info;

	@JsonProperty("cityId")
	private Integer cityId;

	@JsonProperty("likeCount")
	private int likeCount;
	
	@JsonProperty("curAudienceCount")
    private Integer curAudienceCount;

	@JsonProperty("currentPartialUserCount")
	private int currentPartialUserCount;
	
	@JsonProperty("totalAudienceCount")
    private Integer totalAudienceCount;

	@JsonProperty("commentCount")
	private int commentCount;

	@JsonProperty("createTime")
	private Timestamp createTime;

	@JsonProperty("scheduledTime")
    private Timestamp scheduledTime;

	@JsonProperty("liveStartTime")
    private Timestamp liveStartTime;

	@JsonProperty("liveEndTime")
    private Timestamp liveEndTime;

    @JsonProperty("imageUrl")
	private String imageUrl; // 封面大图url

    @JsonProperty("imagePartialUrl")
	private String imagePartialUrl; // 封面小图url

	@JsonProperty("pcUrl")
	private String pcUrl;

	@JsonProperty("duration")
	private Integer duration;

	@JsonProperty("tagEntities")
	private List<TagEntity> tagEntities;

	public TestBrief() {
	}

	public TestBrief(TestEntity TestEntity) {
		BeanUtils.copyProperties(TestEntity, this);
	}

	public TestBrief(com.lin.MyTest.model.biz.Test.TestInfo TestInfo) {
		BeanUtils.copyProperties(TestInfo, this);
	}

	public String getPcUrl() {
		return pcUrl;
	}

	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
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

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
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

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
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

	public Integer getCurAudienceCount() {
		return curAudienceCount;
	}

	public void setCurAudienceCount(Integer curAudienceCount) {
		this.curAudienceCount = curAudienceCount;
	}

	public Integer getTotalAudienceCount() {
		return totalAudienceCount;
	}

	public void setTotalAudienceCount(Integer totalAudienceCount) {
		this.totalAudienceCount = totalAudienceCount;
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

	public int getCurrentPartialUserCount() {
		return currentPartialUserCount;
	}

	public void setCurrentPartialUserCount(int currentPartialUserCount) {
		this.currentPartialUserCount = currentPartialUserCount;
	}

	public List<TagEntity> getTagEntities() {
		return tagEntities;
	}

	public void setTagEntities(List<TagEntity> tagEntities) {
		this.tagEntities = tagEntities;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
