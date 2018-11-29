package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.Test.StatTest;
import com.lin.MyTest.model.entity.StatTestEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StatTestService {

    /**
     * 设置当前在线人数
     */
    void updateCurAudienceCount(long TestId, int count);

    /**
     * 新增当前在线人数
     */
    void incrCurAudienceCount(long TestId, int count);

    /**
     * 新增点赞
     */
    void incrLikeCount(long TestId, int count);

    /**
     * 新增评论
     */
    void incrCommentCount(long TestId, int count);

    /**
     * 新增灌水评论
     */
    void incrRobotCommentCount(long TestId, int count);

    /**
     * 新增灌水观看
     */
    void incrTestRobotVV(long TestId, int count);

    /**
     * 新增观看
     */

    int getTotalVv(long TestId);

    void incrActivitySubscribeCount(long TestId, int activitySubscribeVerify);

    void addTestIdToDurationSet(long TestId);

    void setDurationActiveExpireTime();

    boolean checkExistDurationActiveRedis();

    void updateStatTestFromRedis(long TestId);

    StatTestEntity getByTestId(long TestId);

    /**
     * 对外统计接口
     */
    StatTest getStatById(long TestId);


    Map<Long, StatTest> getStatByIds(List<Long> TestIds);

    /**
     * 根据时xxkey获取10分钟内活跃xxxxid列表
     */
    Set<String> getDurationActiveTestIdSetByTimeKey(String timeKey);

    void deleteDurationActiveTestIdByDateKey(String dateKey);

    /**
     * 获取当前在线人数（当前在线人数只保存在缓存中，没有保存在数据库中）
     */
    int getCurrentAudienceCount(long TestId);

    List<StatTestEntity> getList(String TestIds);


    /**
     * worker每10min调用
     */
    void statTestPer10Min();

    void increasePressCount(long TestId, int type, int count);

    void hostInfoPress(long TestId, int type, Long userId);

    /**
     * 缓存迁移
     */

    void refreshOldToNew(List<Long> ids);

    void refreshOldDurationKey();
}
