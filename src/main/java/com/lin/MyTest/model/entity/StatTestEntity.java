package com.lin.MyTest.model.entity;

//20180205校对 15个字段
public class StatTestEntity {

    private long TestId;

    private int liveAppVv;

    private int liveWapVv;

    private int livePcVv;

    private int playbackAppVv;

    private int playbackWapVv;

    private int playbackPcVv;

    private int robotVv;

    private int totalVv;

    private int likeCount;

    private int wechatPressCount;

    private int phonePressCount;

    private int commentCount;

    private int robotCommentCount;

    private int activitySubscribeCount;

    private int liveTopAudienceCount;

    public StatTestEntity() {}

    public StatTestEntity(Long TestId) {
        this.TestId = TestId;
    }

    public long getTestId() {
        return TestId;
    }

    public void setTestId(long TestId) {
        this.TestId = TestId;
    }

    public int getTotalVv() {
        return totalVv;
    }

    public void setTotalVv(int totalVv) {
        this.totalVv = totalVv;
    }

    public int getLiveAppVv() {
        return liveAppVv;
    }

    public void setLiveAppVv(int liveAppVv) {
        this.liveAppVv = liveAppVv;
    }

    public int getLiveWapVv() {
        return liveWapVv;
    }

    public void setLiveWapVv(int liveWapVv) {
        this.liveWapVv = liveWapVv;
    }

    public int getLivePcVv() {
        return livePcVv;
    }

    public void setLivePcVv(int livePcVv) {
        this.livePcVv = livePcVv;
    }

    public int getLiveTopAudienceCount() {
        return liveTopAudienceCount;
    }

    public void setLiveTopAudienceCount(int liveTopAudienceCount) {
        this.liveTopAudienceCount = liveTopAudienceCount;
    }

    public int getPlaybackAppVv() {
        return playbackAppVv;
    }

    public void setPlaybackAppVv(int playbackAppVv) {
        this.playbackAppVv = playbackAppVv;
    }

    public int getPlaybackWapVv() {
        return playbackWapVv;
    }

    public void setPlaybackWapVv(int playbackWapVv) {
        this.playbackWapVv = playbackWapVv;
    }

    public int getPlaybackPcVv() {
        return playbackPcVv;
    }

    public void setPlaybackPcVv(int playbackPcVv) {
        this.playbackPcVv = playbackPcVv;
    }

    public int getRobotVv() {
        return robotVv;
    }

    public void setRobotVv(int robotVv) {
        this.robotVv = robotVv;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getWechatPressCount() {
        return wechatPressCount;
    }

    public void setWechatPressCount(int wechatPressCount) {
        this.wechatPressCount = wechatPressCount;
    }

    public int getPhonePressCount() {
        return phonePressCount;
    }

    public void setPhonePressCount(int phonePressCount) {
        this.phonePressCount = phonePressCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getRobotCommentCount() {
        return robotCommentCount;
    }

    public void setRobotCommentCount(int robotCommentCount) {
        this.robotCommentCount = robotCommentCount;
    }

    public int getActivitySubscribeCount() {
        return activitySubscribeCount;
    }

    public void setActivitySubscribeCount(int activitySubscribeCount) {
        this.activitySubscribeCount = activitySubscribeCount;
    }
}