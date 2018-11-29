package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.liveroom.StatLiveroom;
import com.lin.MyTest.model.entity.StatLiveroomEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StatLiveroomService {

    /**
     * 设置当前在线人数
     */
    void updateCurAudienceCount(long liveroomId, int count);

    /**
     * 新增当前在线人数
     */
    void incrCurAudienceCount(long liveroomId, int count);

    /**
     * 新增点赞
     */
    void incrLikeCount(long liveroomId, int count);

    /**
     * 新增评论
     */
    void incrCommentCount(long liveroomId, int count);

    /**
     * 新增灌水评论
     */
    void incrRobotCommentCount(long liveroomId, int count);

    /**
     * 新增灌水观看
     */
    void incrLiveroomRobotVV(long liveroomId, int count);

    /**
     * 新增观看
     */

    int getTotalVv(long liveroomId);

    void incrActivitySubscribeCount(long liveroomId, int activitySubscribeVerify);

    void addLiveroomIdToDurationSet(long liveroomId);

    void setDurationActiveExpireTime();

    boolean checkExistDurationActiveRedis();

    void updateStatLiveroomFromRedis(long liveroomId);

    StatLiveroomEntity getByLiveroomId(long liveroomId);

    /**
     * 对外统计接口
     */
    StatLiveroom getStatById(long liveroomId);


    Map<Long, StatLiveroom> getStatByIds(List<Long> liveroomIds);

    /**
     * 根据时间key获取10分钟内活跃直播间id列表
     */
    Set<String> getDurationActiveLiveroomIdSetByTimeKey(String timeKey);

    void deleteDurationActiveLiveroomIdByDateKey(String dateKey);

    /**
     * 获取当前在线人数（当前在线人数只保存在缓存中，没有保存在数据库中）
     */
    int getCurrentAudienceCount(long liveroomId);

    List<StatLiveroomEntity> getList(String liveroomIds);


    /**
     * worker每10min调用
     */
    void statLiveroomPer10Min();

    void increasePressCount(long liveroomId, int type, int count);

    void hostInfoPress(long liveroomId, int type, Long userId);

    /**
     * 缓存迁移
     */

    void refreshOldToNew(List<Long> ids);

    void refreshOldDurationKey();
}
