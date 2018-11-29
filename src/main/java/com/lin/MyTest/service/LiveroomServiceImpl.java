package com.lin.MyTest.service;


import com.lin.MyTest.enums.LiveroomStatusEnum;
import com.lin.MyTest.enums.VideoTypeEnum;
import com.lin.MyTest.exception.LiveroomStateException;
import com.lin.MyTest.model.biz.liveroom.LiveroomInfo;
import com.lin.MyTest.model.entity.LiveroomEntity;
import com.lin.MyTest.model.request.LiveroomProcedureNotifyRequest;
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

import static com.lin.MyTest.exception.LiveroomStateException.LiveroomStateExceptionEnum.*;

@Service
public class LiveroomServiceImpl implements LiveroomService {

    @Autowired
    private LiveroomSearchService liveroomSearchService;

    @Autowired
    private LiveroomQueryService liveroomQueryService;

    @Autowired
    private StatLiveroomService statLiveroomService;

    @Autowired
    private com.lin.MyTest.redisdao.LiveroomRedisDao liveroomRedisDao;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private com.lin.MyTest.dao.repository.LiveroomDao liveroomDao;

    @Value("${spring.H5BaseUrl}")
    private String H5BaseUrl;

    @Value("${spring.PCBaseUrl}")
    private String PCBaseUrl;

    @Value("${building.houseWebBaseUrl}")
    private String buildingWebBaseUrl;

    private static final String LIVEROOM_SHARE_URL = "%s%s/zhibo/%s.html";
    private static final String LIVEROOM_SEQUENCE_TABLE_NAME = "liveroom";

    private static final Logger logger = LoggerFactory.getLogger(LiveroomServiceImpl.class);

    private ExecutorService threadPool;

    @PostConstruct
    void init() {
        threadPool = Executors.newFixedThreadPool(20);
    }

    /**
     * 创建直播间
     */
    @Override
    public Long create(long hostId, String title, String imagePath, Integer cityId, Byte type, Date scheduledTime,
                       Integer tagId, String buildingId, byte buildType) {
        liveroomQueryService.checkUserCreateLimit(hostId);
        LiveroomEntity liveroomEntity = new LiveroomEntity();
        //开始创建，设置时间限制
        liveroomQueryService.setUserCreateLimit(hostId);
        long liveroomId = sequenceService.getNexIdByTableName(LIVEROOM_SEQUENCE_TABLE_NAME);
        liveroomEntity.setId(liveroomId);
        liveroomEntity.setHostId(hostId);
        liveroomEntity.setTitle(title);
        liveroomEntity.setImgPath(imagePath);
        liveroomEntity.setStatus(LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue());
        liveroomEntity.setCityId(cityId);
        liveroomEntity.setType(type);
        liveroomEntity.setScheduledTime(new Timestamp(scheduledTime.getTime()));
        liveroomEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        liveroomEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//        liveroomEntity.setHostRtmpUrl(qCloudVideoService.getHostRtmpUrl(liveroomId));
//        liveroomEntity.setLiveRtmpUrl(qCloudVideoService.getLiveRtmpUrl(liveroomId));
//        liveroomEntity.setLiveFlvUrl(qCloudVideoService.getLiveFlvUrl(liveroomId));
//        liveroomEntity.setLiveHlsUrl(qCloudVideoService.getLiveHlsUrl(liveroomId));
        //添加视频类型
        liveroomEntity.setVideoType(VideoTypeEnum.LIVEROOM.getValue());
        //创建聊天室
//        String chatRoomId = qCloudIMService.createChatRoom(String.valueOf(hostId), title);
//        liveroomEntity.setChatroomId(chatRoomId);
        //保存直播间-聊天室关系
//        liveroomRedisDao.setLiveroomChatroomBind(liveroomId, chatRoomId);
        //保存直播间-直播关系
        liveroomRedisDao.setLiveroomHostBind(liveroomId, hostId);
        //帖子
        //插入数据库
        liveroomDao.insertWithPrimaryId(liveroomEntity);
        //标签
        if (tagId != null) {
        }
        try {
            threadPool.execute(() -> {
                //kafka消息
                //刷新直播间缓存
                refreshLiveroomInfoCache(liveroomId);
                //预告：刷新ES
                if (scheduledTime.after(new Date())) {
                    //刷新ES
                    liveroomSearchService.refreshLiveroom(liveroomId);
                }
                //统计
                //刷新楼盘缓存
                if (StringUtils.isNotEmpty(buildingId) &&
                        StringUtils.isNumeric(buildingId)) {
                    long buildId = Long.parseLong(buildingId);
                    if (scheduledTime.after(new Date())) {
                        //楼盘预告列表
                        liveroomQueryService.refreshBuildingPreviewsCache(buildId, null);
                    } else {
                        //楼盘直播间列表
                        liveroomQueryService.refreshBuildingLiveAndPlaybackCache(buildId, null);
                    }
                    //刷新楼盘直播个数缓存
                }
                //更新城市预告缓存
                if (scheduledTime.after(new Date())) {
                    liveroomQueryService.refreshCityPreviewsCache(cityId, null);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
            liveroomQueryService.removeUserCreateLimit(hostId);
        }
        return liveroomId;
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
        long liveroomId = sequenceService.getNexIdByTableName(LIVEROOM_SEQUENCE_TABLE_NAME);

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
        //插入直播间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        LiveroomEntity liveroomEntity = new LiveroomEntity();
        liveroomEntity.setId(liveroomId);
        liveroomEntity.setStatus(LiveroomStatusEnum.STATUS_UPLOAD_READY.getValue());
        liveroomEntity.setTitle(title);
        liveroomEntity.setHostId(hostId);
        liveroomEntity.setLiveStartTime(timestamp);
        liveroomEntity.setImgPath(imagePath);
        liveroomEntity.setScheduledTime(timestamp);
        liveroomEntity.setType(type);
        liveroomEntity.setCreateTime(timestamp);
        liveroomEntity.setUpdateTime(timestamp);
        liveroomEntity.setCityId(cityId);
        liveroomEntity.setVideoType(VideoTypeEnum.UPLOAD.getValue());
        //保存直播间-直播关系
        liveroomRedisDao.setLiveroomHostBind(liveroomId, hostId);
        //创建帖子
        //标签
        if (tagId != null) {
        }
        //楼盘
        //更新直播结束时间
        if (duration > 0) {
            long liveEndTime = liveroomEntity.getLiveStartTime().getTime() + duration * 1000;
            liveroomEntity.setLiveEndTime(new Timestamp(liveEndTime));
        }
        //如果已经存在.m3u8格式视频，视为转码完毕
        if (url.endsWith(".m3u8")) {
            liveroomEntity.setStatus(LiveroomStatusEnum.STATUS_UPLOAD_TRANSCODED.getValue());
        }
        liveroomDao.insertWithPrimaryId(liveroomEntity);

        //统计与消息
        try {
            threadPool.execute(() -> {
                // 刷新缓存
                refreshLiveroomInfoCache(liveroomId);
                //统计
                //添加upload数目
                // 刷新ES
                liveroomSearchService.refreshLiveroom(liveroomId);
                // 发送kafka
                // 刷新HotTag列表
                liveroomQueryService.refreshHotAndTagCache(liveroomId);
                // 刷新楼盘直播列表
                liveroomQueryService.refreshBuildingLiveAndPlaybackCache(Long.parseLong(buildingId), null);
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return liveroomId;
    }

    @Override
    public void update(long liveroomId, String title, String imagePath, Integer cityId, Long scheduledTime,
                       List<Integer> tagIds, String buildingId, Byte buildType, List<String> countryCodes) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (liveroomInfo == null) {
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
                    throw new LiveroomStateException(LIVEROOM_IMAGE_ILLEGAL);
                }
            }
            if (StringUtils.isNotEmpty(title)) {
                if (title.toLowerCase().contains("script")) {
                    throw new LiveroomStateException(LIVEROOM_TITLE_FORBIDDEN);
                }
            }
            liveroomDao.updateInfo(liveroomId, title, imagePath, scheduledDate, cityId);
        }
        // 更新标签
        if (!CollectionUtils.isEmpty(tagIds)) {
        }

        // 更新缓存
        refreshLiveroomInfoCache(liveroomId);

        // 刷新hot和tag缓存
        threadPool.execute(() -> {

            // 更新hot和tag缓存
            if (!CollectionUtils.isEmpty(tagIds) || cityId != null) {

                //更新新的
                liveroomQueryService.refreshHotAndTagCache(liveroomId);
            }
            // 更新楼盘缓存
            if (StringUtils.isNotEmpty(buildingId) &&
                    StringUtils.isNumeric(buildingId)) {
                //更新旧楼盘缓存
                    long oldBuildId = 0L;
                    if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                        liveroomQueryService.refreshBuildingPreviewsCache(oldBuildId, null);
                    } else {
                        liveroomQueryService.refreshBuildingLiveAndPlaybackCache(oldBuildId, null);
                    }
                long newBuildId = Long.parseLong(buildingId);
                if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                    liveroomQueryService.refreshBuildingPreviewsCache(newBuildId, null);
                } else {
                    liveroomQueryService.refreshBuildingLiveAndPlaybackCache(newBuildId, null);
                }
            }
            // 刷新ES
            liveroomSearchService.refreshLiveroom(liveroomId);
            //更新城市预告
            if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                liveroomQueryService.refreshCityPreviewsCache(liveroomInfo.getCityId(), null);
            }
        });
    }

    @Override
    public void breakOffBothEnds(long liveroomId, Integer startTimeOffset, Integer endTimeOffset) {
        //检查直播间状态
        if (startTimeOffset == null && endTimeOffset == null) {
            return;
        }
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (liveroomInfo == null || !liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_CLOSED.getValue())) {
            throw new LiveroomStateException(LIVEROOM_NOT_EXIST);
        }

    }

    @Override
    public void start(long liveroomId) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        // 更新状态
        liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_ONSHOW.getValue(), new Date(), new Date(), null, null, null);
        // 推送
        // 刷新缓存
        refreshLiveroomInfoCache(liveroomId);
        threadPool.execute(() -> {
            // 自动灌水
            // 刷新ES
            liveroomSearchService.refreshLiveroom(liveroomId);
            // 刷新hot和tag缓存
            liveroomQueryService.refreshHotAndTagCache(liveroomId);
            //更新预告缓存
            liveroomQueryService.refreshCityPreviewsCache(liveroomInfo.getCityId(), null);
            // 刷新楼盘预告、直播缓存

        });
    }

    @Override
    public void close(long liveroomId) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        // 更新状态
        liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_CLOSED.getValue(), new Date(), null, new Date(), null, null);
        try {
            // 停止灌水
            // 禁止推流
            // 发送直播间关闭IM消息
            // 解散聊天室
            // 发送kafka消息
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        // 更新缓存
        refreshLiveroomInfoCache(liveroomId);
        threadPool.execute(() -> {
            // 更新ES
            liveroomSearchService.refreshLiveroom(liveroomId);
            // 更新HotTag缓存
            liveroomQueryService.refreshHotAndTagCache(liveroomId);
            // 更新楼盘缓存
        });
    }

    @Override
    public void delete(long liveroomId) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_ONSHOW.getValue())) {
            throw new LiveroomStateException(LiveroomStateException.LiveroomStateExceptionEnum.LIVEROOM_ON_SHOW_CAN_NOT_BE_DELETE);
        }
        if (liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue())) {
            try {
                // 禁止推流
                // 发送直播间关闭IM消息
                // 解散聊天室
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        //修改状态
        liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_DELETED.getValue(), new Date(), null, null, null, null);
        try {
            //删除帖子
            //kafka
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        // 更新缓存
        refreshLiveroomInfoCache(liveroomId);
        threadPool.execute(() -> {
            //删除ES
            liveroomSearchService.delete(liveroomId);
            //刷新HotTag缓存
            if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_CLOSED.getValue()) {
                liveroomQueryService.refreshHotAndTagCache(liveroomId);
            }
            //城市预告
            if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                liveroomQueryService.refreshCityPreviewsCache(liveroomInfo.getCityId(), null);
            }
            //楼盘直播列表
        });
    }

    @Override
    public void ban(long liveroomId, String reason) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_ONSHOW.getValue()) ||
                liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue())) {
            try {
                // 停止灌水
                // 禁止推流
                // 发送直播间关闭IM消息
                // 解散聊天室
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        // 更新状态
        liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_BANNED.getValue(), null, null, null, null, null);
        try {
            // 删除帖子
            // kafka
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        // 更新缓存
        refreshLiveroomInfoCache(liveroomId);
        threadPool.execute(() -> {
            // 删除ES
            liveroomSearchService.delete(liveroomId);
            //更新城市预告
            if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_READY_TO_PLAY.getValue()) {
                liveroomQueryService.refreshCityPreviewsCache(getLiveroomInfo(liveroomId).getCityId(), null);
            }
            // 刷新HotTag缓存
            if (liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_ONSHOW.getValue() ||
                    liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_CLOSED.getValue()) {
                liveroomQueryService.refreshHotAndTagCache(liveroomId);
            }
            //刷新楼盘缓存
        });
    }

    @Override
    public void warn(long liveroomId, String warnMsg) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (liveroomInfo.getStatus() != LiveroomStatusEnum.STATUS_ONSHOW.getValue()) {
            return;
        }
    }

    @Override
    public void resume(long liveroomId) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (!liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_DELETED.getValue()) &&
                !liveroomInfo.getStatus().equals(LiveroomStatusEnum.STATUS_BANNED.getValue())) {
            return;
        }
        // 更新状态
        liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_CLOSED.getValue(), new Date(), null, null, null, null);
        try {
            //恢复帖子
            //恢复Kafka
        } catch (Exception e) {
            e.printStackTrace();
        }
        //更新直播间缓存
        refreshLiveroomInfoCache(liveroomId);
        threadPool.execute(() -> {
            // 更新ES
            liveroomSearchService.refreshLiveroom(liveroomId);
            // 刷新hot和tag缓存
            liveroomQueryService.refreshHotAndTagCache(liveroomId);
            // 刷新楼盘缓存
        });
    }

    @Override
    public void passUpload(long liveroomId) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        if (liveroomInfo == null || liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_DELETED.getValue() ||
                liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_CLOSED.getValue()) {
            return;
        }

        if (liveroomInfo.getVideoType().equals(VideoTypeEnum.UPLOAD.getValue()) &&
                liveroomInfo.getStatus() == LiveroomStatusEnum.STATUS_UPLOAD_TRANSCODED.getValue()) {
            // 更新状态
            liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_CLOSED.getValue(), new Date(), null, null, null, null);
            // 更新缓存
            refreshLiveroomInfoCache(liveroomId);
            threadPool.execute(() -> {
                // 更新ES
                liveroomSearchService.refreshLiveroom(liveroomId);
                // 更新HotTag缓存
                liveroomQueryService.refreshHotAndTagCache(liveroomId);
                // 更新楼盘缓存
                //IM

            });
        }
    }

    @Override
    public void expire(long liveroomId) {
        LiveroomInfo liveroomInfo = getLiveroomInfo(liveroomId);
        // 更新状态
        liveroomDao.updateStatus(liveroomId, LiveroomStatusEnum.STATUS_SCHEDULE_EXPIRED.getValue(), new Date(), null, null, null, null);
        try {
            //关闭推流
            //解散聊天室
            //发kafka
            //删除帖子
            // 更新直播间缓存
            refreshLiveroomInfoCache(liveroomId);
            threadPool.execute(() -> {
                // 更新ES
                liveroomSearchService.delete(liveroomId);
                //更新城市缓存
                liveroomQueryService.refreshCityPreviewsCache(liveroomInfo.getCityId(), null);
                // 刷新楼盘预告列表
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
    public void handelProcedureStateChangedEvent(String fileId, List<LiveroomProcedureNotifyRequest.DataBean.ProcessTaskListBean> processTaskListBeans) {

    }

    @Override
    public void handelClipCompleteEvent(String vodTaskId, Integer status, String fileId, String fileUrl) {

    }

    @Override
    public void concatLiveroom(long liveroomId) {

    }

    @Override
    public String getShareUrl(long liveroomId, int cityId) {
        return null;
    }

    @Override
    public String getPlayUrl(long liveroomId) {
        return null;
    }

    @Override
    public int getLiveroomDuration(long liveroomId) {
        return 0;
    }

    @Override
    public LiveroomInfo getLiveroomInfo(long liveroomId) {
        return null;
    }

    @Override
    public Map<Long, LiveroomInfo> getLiveroomsInfo(List<Long> liveroomIds) {
        return null;
    }

    @Override
    public List<LiveroomInfo> getLiveroomsInfoList(List<Long> liveroomIds) {
        return null;
    }

    @Override
    public void refreshLiveroomInfoCache(long liveroomId) {

    }

    @Override
    public void batchRefreshLiveroomInfoCache(List<Long> liveroomIds) {

    }

    @Override
    public LiveroomEntity getById(long liveroomId) {
        return null;
    }

    @Override
    public List<LiveroomEntity> getByIds(List<Long> liveroomIds) {
        return null;
    }

    @Override
    public Long getIdByChatroom(String chatroomId) {
        return null;
    }

    @Override
    public LiveroomInfo getIdByTopicId(long topicId) {
        return null;
    }

    @Override
    public Long getHostIdByLiveroom(long liveroomId) {
        return null;
    }

    @Override
    public void closeExpireScheduledLiverooms() {

    }

    @Override
    public void refreshCurAudienceCount() {

    }

    @Override
    public void closeDisconnectLiverooms() {

    }

    @Override
    public void checkFixLiveroomConcat() {

    }
}