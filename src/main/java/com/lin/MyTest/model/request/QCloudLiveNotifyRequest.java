package com.lin.MyTest.model.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QCloudLiveNotifyRequest {

	// 基础
	private Long t;
	private String sign;
	private Integer event_type;
	private String stream_id;
	// 和stream_id一样，使用stream_id;
	private String channel_id;

	// event_type = 0 or 1 推流/断流
	private String appname;
	private String app;
	private Integer update_time;
	private String sequence;
	private String node;
	private String user_ip;
	private Integer errcode;
	private String errmsg;
	private String push_duration;


	// event_type = 100 新录制文件
	private String video_id;
	private String video_url;
	private String file_size;
	private Long start_time;
	private Long end_time;
	private String file_id;
	private String file_format;
	private Integer vod2Flag;
	private String record_file_id;
	private Integer duration;
	private String stream_param;

	//event_type = 200 新的截图图片生成
	private String pic_url;
	private Integer create_time;
	private String pic_full_url;

	public Long getT() {
		return t;
	}

	public void setT(Long t) {
		this.t = t;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Integer getEvent_type() {
		return event_type;
	}

	public void setEvent_type(Integer event_type) {
		this.event_type = event_type;
	}

	public String getStream_id() {
		return stream_id;
	}

	public void setStream_id(String stream_id) {
		this.stream_id = stream_id;
	}

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public Integer getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Integer update_time) {
		this.update_time = update_time;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getUser_ip() {
		return user_ip;
	}

	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
	}

	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getPush_duration() {
		return push_duration;
	}

	public void setPush_duration(String push_duration) {
		this.push_duration = push_duration;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public Long getStart_time() {
		return start_time;
	}

	public void setStart_time(Long start_time) {
		this.start_time = start_time;
	}

	public Long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Long end_time) {
		this.end_time = end_time;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getFile_format() {
		return file_format;
	}

	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}

	public Integer getVod2Flag() {
		return vod2Flag;
	}

	public void setVod2Flag(Integer vod2Flag) {
		this.vod2Flag = vod2Flag;
	}

	public String getRecord_file_id() {
		return record_file_id;
	}

	public void setRecord_file_id(String record_file_id) {
		this.record_file_id = record_file_id;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getStream_param() {
		return stream_param;
	}

	public void setStream_param(String stream_param) {
		this.stream_param = stream_param;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public String getPic_full_url() {
		return pic_full_url;
	}

	public void setPic_full_url(String pic_full_url) {
		this.pic_full_url = pic_full_url;
	}
}
