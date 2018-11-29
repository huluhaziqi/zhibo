package com.lin.MyTest.service;


import com.lin.MyTest.enums.TestStatusEnum;
import com.lin.MyTest.enums.VideoTypeEnum;
import com.lin.MyTest.exception.TestStateException;
import com.lin.MyTest.model.biz.Test.TestInfo;
import com.lin.MyTest.model.entity.TestEntity;
import com.lin.MyTest.model.request.TestProcedureNotifyRequest;
import com.lin.MyTest.model.request.QCloudLiveNotifyRequest;
import com.lin.MyTest.model.response.QCloudVODVideoTransInfoV2Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lin.MyTest.exception.TestStateException.TestStateExceptionEnum.*;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestSearchService TestSearchService;

    @Autowired
    private TestQueryService TestQueryService;

    @Autowired
    private StatTestService statTestService;

    @Autowired
    private com.lin.MyTest.redisdao.TestRedisDao TestRedisDao;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private com.lin.MyTest.dao.repository.TestDao TestDao;

    @Value("${spring.H5BaseUrl}")
    private String H5BaseUrl;

    @Value("${spring.PCBaseUrl}")
    private String PCBaseUrl;

    @Value("${building.houseWebBaseUrl}")
    private String buildingWebBaseUrl;

    private static final String Test_SHARE_URL = "%s%s/zhibo/%s.html";
    private static final String Test_SEQUENCE_TABLE_NAME = "Test";

    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    private ExecutorService threadPool;

    @PostConstruct
    void init() {
        threadPool = Executors.newFixedThreadPool(20);
    }

    /**
     * 创建xxxx
     */
    @Override
    public Long create(long hostId, String title, String imagePath, Integer cityId, Byte type, Date scheduledTime,
                       Integer tagId, String buildingId, byte buildType) {
        TestQueryService.checkUserCreateLimit(hostId);
        TestEntity TestEntity = new TestEntity();
        //开始创建，设置时xx限制
        TestQueryService.setUserCreateLimit(hostId);
        long TestId = sequenceService.getNexIdByTableName(Test_SEQUENCE_TABLE_NAME);
        TestEntity.setId(TestId);
        TestEntity.setHostId(hostId);
        TestEntity.setTitle(title);
        TestEntity.setImgPath(imagePath);
        TestEntity.setStatus(TestStatusEnum.STATUS_READY_TO_PLAY.getValue());
        TestEntity.setCityId(cityId);
        TestEntity.setType(type);
        TestEntity.setScheduledTime(new Timestamp(scheduledTime.getTime()));
        TestEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        TestEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//        TestEntity.setHostRtmpUrl(qCloudVideoService.getHostRtmpUrl(TestId));
//        TestEntity.setLiveRtmpUrl(qCloudVideoService.getLiveRtmpUrl(TestId));
//        TestEntity.setLiveFlvUrl(qCloudVideoService.getLiveFlvUrl(TestId));
//        TestEntity.setLiveHlsUrl(qCloudVideoService.getLiveHlsUrl(TestId));
        //添加视频类型
        TestEntity.setVideoType(VideoTypeEnum.Test.getValue());
        //创建聊天室
//        String chatRoomId = qCloudIMService.createChatRoom(String.valueOf(hostId), title);
//        TestEntity.setChatroomId(chatRoomId);
        //保存xxxx-聊天室关系
//        TestRedisDao.setTestChatroomBind(TestId, chatRoomId);
        //保存xxxx-xx关系
        TestRedisDao.setTestHostBind(TestId, hostId);
        //帖子
        //插入数据库
        TestDao.insertWithPrimaryId(TestEntity);
        //标签
        if (tagId != null) {
        }
        try {
            threadPool.execute(() -> {
                //kafka消息
                //刷新xxxx缓存
                refreshTestInfoCache(TestId);
                //预告：刷新ES
                if (scheduledTime.after(new Date())) {
                    //刷新ES
                    TestSearchService.refreshTest(TestId);
                }
                //统计
                //刷新xx缓存
                if (StringUtils.isNotEmpty(buildingId) &&
                        StringUtils.isNumeric(buildingId)) {
                    long buildId = Long.parseLong(buildingId);
                    if (scheduledTime.after(new Date())) {
                        //xx预告列表
                        TestQueryService.refreshBuildingPreviewsCache(buildId, null);
                    } else {
                        //xxxxxx列表
                        TestQueryService.refreshBuildingLiveAndPlaybackCache(buildId, null);
                    }
                    //刷新xxxx个数缓存
                }
                //更新城市预告缓存
                if (scheduledTime.after(new Date())) {
                    TestQueryService.refreshCityPreviewsCache(cityId, null);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
            TestQueryService.removeUserCreateLimit(hostId);
        }
        return TestId;
    }

    /**
     * 创建回放
     */
    @Override
    public Long createPlayBack(long hostId, String title, String imagePath, Integer cityId, Byte type,
                               Integer tagId, String buildingId, byte buildType, String fileId) {

        //检查fileId是否已经在playback_concat中存在
        //获取转码视频信息
        QCloudVODVideoTransInfoV2Response infoV2Response = null;
        long TestId = sequenceService.getNexIdByTableName(Test_SEQUENCE_TABLE_NAME);

        //构造PlaybackConcat信息，入库
        int duration = infoV2Response.getBasicInfo().getDuration();
        int definition = 0;
        String url = null;
        String originUrl = null;
        for (QCloudVODVideoTransInfoV2Response.TransCode transCode : infoV2Response.getTranscodeInfo().getTranscodeList()) {
            if (transCode.getUrl().endsWith("m3u8")) {
                if (transCode.getDefinition() > definition) {
                    definition = transCode.getDefinition();
                    url = transCode.getUrl();
                }
            }
        }
        if (infoV2Response.getBasicInfo() != null) {
            originUrl = infoV2Response.getBasicInfo().getSourceVideoUrl();
            if (StringUtils.isEmpty(url)) {
                url = originUrl;
            }
        }
        //插入xxxx
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        TestEntity TestEntity = new TestEntity();
        TestEntity.setId(TestId);
        TestEntity.setStatus(TestStatusEnum.STATUS_UPLOAD_READY.getValue());
        TestEntity.setTitle(title);
        TestEntity.setHostId(hostId);
        TestEntity.setLiveStartTime(timestamp);
        TestEntity.setImgPath(imagePath);
        TestEntity.setScheduledTime(timestamp);
        TestEntity.setType(type);
        TestEntity.setCreateTime(timestamp);
        TestEntity.setUpdateTime(timestamp);
        TestEntity.setCityId(cityId);
        TestEntity.setVideoType(VideoTypeEnum.UPLOAD.getValue());
        //保存xxxx-xx关系
        TestRedisDao.setTestHostBind(TestId, hostId);
        //创建帖子
        //标签
        if (tagId != null) {
        }
        //xx
        //更新xx结束时xx
        if (duration > 0) {
            long liveEndTime = TestEntity.getLiveStartTime().getTime() + duration * 1000;
            TestEntity.setLiveEndTime(new Timestamp(liveEndTime));
        }
        //如果已经存在.m3u8格式视频，视为转码完毕
        if (url.endsWith(".m3u8")) {
            TestEntity.setStatus(TestStatusEnum.STATUS_UPLOAD_TRANSCODED.getValue());
        }
        TestDao.insertWithPrimaryId(TestEntity);

        //统计与消息
        try {
            threadPool.execute(() -> {
                // 刷新缓存
                refreshTestInfoCache(TestId);
                //统计
                //添加upload数目
                // 刷新ES
                TestSearchService.refreshTest(TestId);
                // 发送kafka
                // 刷新HotTag列表
                TestQueryService.refreshHotAndTagCache(TestId);
                // 刷新xxxx列表
                TestQueryService.refreshBuildingLiveAndPlaybackCache(Long.parseLong(buildingId), null);
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return TestId;
    }

    @Override
    public void update(long TestId, String title, String imagePath, Integer cityId, Long scheduledTime,
                       List<Integer> tagIds, String buildingId, Byte buildType, List<String> countryCodes) {
        TestInfo TestInfo = getTestInfo(TestId);
        if (TestInfo == null) {
            return;
        }
        // 更新字段
        if (StringUtils.isNotEmpty(title) || StringUtils.isNotEmpty(imagePath) || cityId != null || scheduledTime != null) {
            Date scheduledDate = null;
            if (scheduledTime != null) {
                scheduledDate = new Date(scheduledTime);
            }

            if (StringUtils.isNotEmpty(imagePath)) {
                if (imagePath.contains("script") || imagePath.contains("<img") || imagePath.contains("php") ||
                        imagePath.contains("<") || imagePath.contains(">")) {
                    throw new TestStateException(Test_IMAGE_ILLEGAL);
                }
            }
            if (StringUtils.isNotEmpty(title)) {
                if (title.toLowerCase().contains("script")) {
                    throw new TestStateException(Test_TITLE_FORBIDDEN);
                }
            }
            TestDao.updateInfo(TestId, title, imagePath, scheduledDate, cityId);
        }
        // 更新标签
        if (!CollectionUtils.isEmpty(tagIds)) {
        }

        // 更新缓存
        refreshTestInfoCache(TestId);

        // 刷新hot和tag缓存
        threadPool.execute(() -> {

            // 更新hot和tag缓存
            if (!CollectionUtils.isEmpty(tagIds) || cityId != null) {

                //更新新的
                TestQueryService.refreshHotAndTagCache(TestId);
            }
            // 更新xx缓存
            if (StringUtils.isNotEmpty(buildingId) &&
                    StringUtils.isNumeric(buildingId)) {
                //更新旧xx缓存
                    long oldBuildId = 0L;
                    if (TestInfo.getStatus() == TestStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                        TestQueryService.refreshBuildingPreviewsCache(oldBuildId, null);
                    } else {
                        TestQueryService.refreshBuildingLiveAndPlaybackCache(oldBuildId, null);
                    }
                long newBuildId = Long.parseLong(buildingId);
                if (TestInfo.getStatus() == TestStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                    TestQueryService.refreshBuildingPreviewsCache(newBuildId, null);
                } else {
                    TestQueryService.refreshBuildingLiveAndPlaybackCache(newBuildId, null);
                }
            }
            // 刷新ES
            TestSearchService.refreshTest(TestId);
            //更新城市预告
            if (TestInfo.getStatus() == TestStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                TestQueryService.refreshCityPreviewsCache(TestInfo.getCityId(), null);
            }
        });
    }

    @Override
    public void breakOffBothEnds(long TestId, Integer startTimeOffset, Integer endTimeOffset) {
        //检查xxxx状态
        if (startTimeOffset == null && endTimeOffset == null) {
            return;
        }
        TestInfo TestInfo = getTestInfo(TestId);
        if (TestInfo == null || !TestInfo.getStatus().equals(TestStatusEnum.STATUS_CLOSED.getValue())) {
            throw new TestStateException(Test_NOT_EXIST);
        }

    }

    @Override
    public void start(long TestId) {
        TestInfo TestInfo = getTestInfo(TestId);
        // 更新状态
        TestDao.updateStatus(TestId, TestStatusEnum.STATUS_ONSHOW.getValue(), new Date(), new Date(), null, null, null);
        // 推送
        // 刷新缓存
        refreshTestInfoCache(TestId);
        threadPool.execute(() -> {
            // 自动灌水
            // 刷新ES
            TestSearchService.refreshTest(TestId);
            // 刷新hot和tag缓存
            TestQueryService.refreshHotAndTagCache(TestId);
            //更新预告缓存
            TestQueryService.refreshCityPreviewsCache(TestInfo.getCityId(), null);
            // 刷新xx预告、xx缓存

        });
    }

    @Override
    public void close(long TestId) {
        TestInfo TestInfo = getTestInfo(TestId);
        // 更新状态
        TestDao.updateStatus(TestId, TestStatusEnum.STATUS_CLOSED.getValue(), new Date(), null, new Date(), null, null);
        try {
            // 停止灌水
            // 禁止推流
            // 发送xxxx关闭IM消息
            // 解散聊天室
            // 发送kafka消息
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        // 更新缓存
        refreshTestInfoCache(TestId);
        threadPool.execute(() -> {
            // 更新ES
            TestSearchService.refreshTest(TestId);
            // 更新HotTag缓存
            TestQueryService.refreshHotAndTagCache(TestId);
            // 更新xx缓存
        });
    }

    @Override
    public void delete(long TestId) {
        TestInfo TestInfo = getTestInfo(TestId);
        if (TestInfo.getStatus().equals(TestStatusEnum.STATUS_ONSHOW.getValue())) {
            throw new TestStateException(TestStateException.TestStateExceptionEnum.Test_ON_SHOW_CAN_NOT_BE_DELETE);
        }
        if (TestInfo.getStatus().equals(TestStatusEnum.STATUS_READY_TO_PLAY.getValue())) {
            try {
                // 禁止推流
                // 发送xxxx关闭IM消息
                // 解散聊天室
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        //修改状态
        TestDao.updateStatus(TestId, TestStatusEnum.STATUS_DELETED.getValue(), new Date(), null, null, null, null);
        try {
            //删除帖子
            //kafka
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        // 更新缓存
        refreshTestInfoCache(TestId);
        threadPool.execute(() -> {
            //删除ES
            TestSearchService.delete(TestId);
            //刷新HotTag缓存
            if (TestInfo.getStatus() == TestStatusEnum.STATUS_CLOSED.getValue()) {
                TestQueryService.refreshHotAndTagCache(TestId);
            }
            //城市预告
            if (TestInfo.getStatus() == TestStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                TestQueryService.refreshCityPreviewsCache(TestInfo.getCityId(), null);
            }
            //xxxx列表
        });
    }

    @Override
    public void ban(long TestId, String reason) {
        TestInfo TestInfo = getTestInfo(TestId);
        if (TestInfo.getStatus().equals(TestStatusEnum.STATUS_ONSHOW.getValue()) ||
                TestInfo.getStatus().equals(TestStatusEnum.STATUS_READY_TO_PLAY.getValue())) {
            try {
                // 停止灌水
                // 禁止推流
                // 发送xxxx关闭IM消息
                // 解散聊天室
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        // 更新状态
        TestDao.updateStatus(TestId, TestStatusEnum.STATUS_BANNED.getValue(), null, null, null, null, null);
        try {
            // 删除帖子
            // kafka
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        // 更新缓存
        refreshTestInfoCache(TestId);
        threadPool.execute(() -> {
            // 删除ES
            TestSearchService.delete(TestId);
            //更新城市预告
            if (TestInfo.getStatus() == TestStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                TestQueryService.refreshCityPreviewsCache(getTestInfo(TestId).getCityId(), null);
            }
            // 刷新HotTag缓存
            if (TestInfo.getStatus() == TestStatusEnum.STATUS_ONSHOW.getValue() ||
                    TestInfo.getStatus() == TestStatusEnum.STATUS_CLOSED.getValue()) {
                TestQueryService.refreshHotAndTagCache(TestId);
            }
            //刷新xx缓存
        });
    }

    @Override
    public void warn(long TestId, String warnMsg) {
        TestInfo TestInfo = getTestInfo(TestId);
        if (TestInfo.getStatus() != TestStatusEnum.STATUS_ONSHOW.getValue()) {
            return;
        }
    }

    @Override
    public void resume(long TestId) {
        TestInfo TestInfo = getTestInfo(TestId);
        if (!TestInfo.getStatus().equals(TestStatusEnum.STATUS_DELETED.getValue()) &&
                !TestInfo.getStatus().equals(TestStatusEnum.STATUS_BANNED.getValue())) {
            return;
        }
        // 更新状态
        TestDao.updateStatus(TestId, TestStatusEnum.STATUS_CLOSED.getValue(), new Date(), null, null, null, null);
        try {
            //恢复帖子
            //恢复Kafka
        } catch (Exception e) {
            e.printStackTrace();
        }
        //更新xxxx缓存
        refreshTestInfoCache(TestId);
        threadPool.execute(() -> {
            // 更新ES
            TestSearchService.refreshTest(TestId);
            // 刷新hot和tag缓存
            TestQueryService.refreshHotAndTagCache(TestId);
            // 刷新xx缓存
        });
    }

    @Override
    public void passUpload(long TestId) {
        TestInfo TestInfo = getTestInfo(TestId);
        if (TestInfo == null || TestInfo.getStatus() == TestStatusEnum.STATUS_DELETED.getValue() ||
                TestInfo.getStatus() == TestStatusEnum.STATUS_CLOSED.getValue()) {
            return;
        }

        if (TestInfo.getVideoType().equals(VideoTypeEnum.UPLOAD.getValue()) &&
                TestInfo.getStatus() == TestStatusEnum.STATUS_UPLOAD_TRANSCODED.getValue()) {
            // 更新状态
            TestDao.updateStatus(TestId, TestStatusEnum.STATUS_CLOSED.getValue(), new Date(), null, null, null, null);
            // 更新缓存
            refreshTestInfoCache(TestId);
            threadPool.execute(() -> {
                // 更新ES
                TestSearchService.refreshTest(TestId);
                // 更新HotTag缓存
                TestQueryService.refreshHotAndTagCache(TestId);
                // 更新xx缓存
                //IM

            });
        }
    }

    @Override
    public void expire(long TestId) {
        TestInfo TestInfo = getTestInfo(TestId);
        // 更新状态
        TestDao.updateStatus(TestId, TestStatusEnum.STATUS_SCHEDULE_EXPIRED.getValue(), new Date(), null, null, null, null);
        try {
            //关闭推流
            //解散聊天室
            //发kafka
            //删除帖子
            // 更新xxxx缓存
            refreshTestInfoCache(TestId);
            threadPool.execute(() -> {
                // 更新ES
                TestSearchService.delete(TestId);
                //更新城市缓存
                TestQueryService.refreshCityPreviewsCache(TestInfo.getCityId(), null);
                // 刷新xx预告列表
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void handleQCloudLiveNotify(QCloudLiveNotifyRequest request) {

    }

    @Override
    public void handelConcatCompleteEvent(String fileId, String fileUrl) {

    }

    @Override
    public void handelProcedureStateChangedEvent(String fileId, List<TestProcedureNotifyRequest.DataBean.ProcessTaskListBean> processTaskListBeans) {

    }

    @Override
    public void handelClipCompleteEvent(String vodTaskId, Integer status, String fileId, String fileUrl) {

    }

    @Override
    public void concatTest(long TestId) {

    }

    @Override
    public String getShareUrl(long TestId, int cityId) {
        return null;
    }

    @Override
    public String getPlayUrl(long TestId) {
        return null;
    }

    @Override
    public int getTestDuration(long TestId) {
        return 0;
    }

    @Override
    public TestInfo getTestInfo(long TestId) {
        return null;
    }

    @Override
    public Map<Long, TestInfo> getTestsInfo(List<Long> TestIds) {
        return null;
    }

    @Override
    public List<TestInfo> getTestsInfoList(List<Long> TestIds) {
        return null;
    }

    @Override
    public void refreshTestInfoCache(long TestId) {

    }

    @Override
    public void batchRefreshTestInfoCache(List<Long> TestIds) {

    }

    @Override
    public TestEntity getById(long TestId) {
        return null;
    }

    @Override
    public List<TestEntity> getByIds(List<Long> TestIds) {
        return null;
    }

    @Override
    public Long getIdByChatroom(String chatroomId) {
        return null;
    }

    @Override
    public TestInfo getIdByTopicId(long topicId) {
        return null;
    }

    @Override
    public Long getHostIdByTest(long TestId) {
        return null;
    }

    @Override
    public void closeExpireScheduledTests() {

    }

    @Override
    public void refreshCurAudienceCount() {

    }

    @Override
    public void closeDisconnectTests() {

    }

    @Override
    public void checkFixTestConcat() {

    }
}