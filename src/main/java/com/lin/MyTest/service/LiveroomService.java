package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.liveroom.LiveroomInfo;
import com.lin.MyTest.model.entity.LiveroomEntity;
import com.lin.MyTest.model.request.LiveroomProcedureNotifyRequest;
import com.lin.MyTest.model.request.QCloudLiveNotifyRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LiveroomService {

    /**创建直播*/
    Long create(long hostId, String title, String imagePath, Integer cityId, Byte type, Date scheduledTime,
                Integer tagId, String buildingId, byte buildType);

    /**基于视频创建直播间*/
    Long createPlayBack(long hostId, String title, String imagePath, Integer cityId, Byte type,
                        Integer tagId, String buildingId, byte buildType, String fileId);

    /**更新基本信息*/
    void update(long liveroomId, String title, String imagePath, Integer cityId, Long scheduledTime, List<Integer> tagIds,
                String buildingId, Byte buildType, List<String> countryCodes);

    /**掐头去尾*/
    void breakOffBothEnds(long liveroomId, Integer startTimeOffset, Integer endTimeOffset);

    /**开始直播*/
    void start(long liveroomId);

    /**结束直播*/
    void close(long liveroomId);

    /**删除直播*/
    void delete(long liveroomId);

    /**封禁直播*/
    void ban(long liveroomId, String reason);

    /**警告直播*/
    void warn(long liveroomId, String warnMsg);

    /**恢复直播：针对误操作删除封禁的直播，只能恢复到回放状态，主播统计数据无法恢复*/
    void resume(long liveroomId);

    void passUpload(long liveroomId);

    /**预告过期*/
    void expire(long liveroomId);

    /**直播回调*/
    void handleQCloudLiveNotify(QCloudLiveNotifyRequest request);

    /**点播回调：直播视频拼接完成*/
    void handelConcatCompleteEvent(String fileId, String fileUrl);

    /**点播回调：转码等任务流完成*/
    void handelProcedureStateChangedEvent(String fileId, List<LiveroomProcedureNotifyRequest.DataBean.ProcessTaskListBean> processTaskListBeans);

    /**点播回调：视频裁剪完成*/
    void handelClipCompleteEvent(String vodTaskId, Integer status, String fileId, String fileUrl);

    /**playback_concat入库，执行拼接检查*/
    void concatLiveroom(long liveroomId);

    /**获取直播间分享地址*/
    String getShareUrl(long liveroomId, int cityId);

    /**获取直播间播放地址*/
    String getPlayUrl(long liveroomId);

    /**播放时长**/
    int getLiveroomDuration(long liveroomId);


    /**查询LiveroomInfo*/
    LiveroomInfo getLiveroomInfo(long liveroomId);

    /**查询查询LiveroomInfo*/
    Map<Long, LiveroomInfo> getLiveroomsInfo(List<Long> liveroomIds);

    /**查询查询LiveroomInfo*/
    List<LiveroomInfo> getLiveroomsInfoList(List<Long> liveroomIds);

    /**更新LiveroomInfo缓存*/
    void refreshLiveroomInfoCache(long liveroomId);

    /**批量更新LiveroomInfo缓存*/
    void batchRefreshLiveroomInfoCache(List<Long> liveroomIds);

    /**查询LiveroomEntity*/
    LiveroomEntity getById(long liveroomId);

    /**批量查询LiveroomEntity*/
    List<LiveroomEntity> getByIds(List<Long> liveroomIds);

    /**根据聊天室ID查找直播间ID*/
    Long getIdByChatroom(String chatroomId);

    /**论坛id*/
    LiveroomInfo getIdByTopicId(long topicId);

    /**获取主播id*/
    Long getHostIdByLiveroom(long liveroomId);

    /**worker 定时关闭超时预告*/
    void closeExpireScheduledLiverooms();

    /**worker 定时校准直播当前在线人数以及最高在线人数*/
    void refreshCurAudienceCount();

    /**worker 定时关闭断流超时直播*/
    void closeDisconnectLiverooms();

    /**worker 定时拼接直播回放*/
    void checkFixLiveroomConcat();

}
