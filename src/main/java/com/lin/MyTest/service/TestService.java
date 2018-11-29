package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.Test.TestInfo;
import com.lin.MyTest.model.entity.TestEntity;
import com.lin.MyTest.model.request.TestProcedureNotifyRequest;
import com.lin.MyTest.model.request.QCloudLiveNotifyRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TestService {

    /**创建xx*/
    Long create(long hostId, String title, String imagePath, Integer cityId, Byte type, Date scheduledTime,
                Integer tagId, String buildingId, byte buildType);

    /**基于视频创建xxxx*/
    Long createPlayBack(long hostId, String title, String imagePath, Integer cityId, Byte type,
                        Integer tagId, String buildingId, byte buildType, String fileId);

    /**更新基本信息*/
    void update(long TestId, String title, String imagePath, Integer cityId, Long scheduledTime, List<Integer> tagIds,
                String buildingId, Byte buildType, List<String> countryCodes);

    /**掐头去尾*/
    void breakOffBothEnds(long TestId, Integer startTimeOffset, Integer endTimeOffset);

    /**开始xx*/
    void start(long TestId);

    /**结束xx*/
    void close(long TestId);

    /**删除xx*/
    void delete(long TestId);

    /**封禁xx*/
    void ban(long TestId, String reason);

    /**警告xx*/
    void warn(long TestId, String warnMsg);

    /**恢复xx：针对误操作删除封禁的xx，只能恢复到回放状态，主播统计数据无法恢复*/
    void resume(long TestId);

    void passUpload(long TestId);

    /**预告过期*/
    void expire(long TestId);

    /**xx回调*/
    void handleQCloudLiveNotify(QCloudLiveNotifyRequest request);

    /**点播回调：xx视频拼接完成*/
    void handelConcatCompleteEvent(String fileId, String fileUrl);

    /**点播回调：转码等任务流完成*/
    void handelProcedureStateChangedEvent(String fileId, List<TestProcedureNotifyRequest.DataBean.ProcessTaskListBean> processTaskListBeans);

    /**点播回调：视频裁剪完成*/
    void handelClipCompleteEvent(String vodTaskId, Integer status, String fileId, String fileUrl);

    /**playback_concat入库，执行拼接检查*/
    void concatTest(long TestId);

    /**获取xxxx分享地址*/
    String getShareUrl(long TestId, int cityId);

    /**获取xxxx播放地址*/
    String getPlayUrl(long TestId);

    /**播放时长**/
    int getTestDuration(long TestId);


    /**查询TestInfo*/
    TestInfo getTestInfo(long TestId);

    /**查询查询TestInfo*/
    Map<Long, TestInfo> getTestsInfo(List<Long> TestIds);

    /**查询查询TestInfo*/
    List<TestInfo> getTestsInfoList(List<Long> TestIds);

    /**更新TestInfo缓存*/
    void refreshTestInfoCache(long TestId);

    /**批量更新TestInfo缓存*/
    void batchRefreshTestInfoCache(List<Long> TestIds);

    /**查询TestEntity*/
    TestEntity getById(long TestId);

    /**批量查询TestEntity*/
    List<TestEntity> getByIds(List<Long> TestIds);

    /**根据聊天室ID查找xxxxID*/
    Long getIdByChatroom(String chatroomId);

    /**论坛id*/
    TestInfo getIdByTopicId(long topicId);

    /**获取主播id*/
    Long getHostIdByTest(long TestId);

    /**worker 定时关闭超时预告*/
    void closeExpireScheduledTests();

    /**worker 定时校准xx当前在线人数以及最高在线人数*/
    void refreshCurAudienceCount();

    /**worker 定时关闭断流超时xx*/
    void closeDisconnectTests();

    /**worker 定时拼接xx回放*/
    void checkFixTestConcat();

}
