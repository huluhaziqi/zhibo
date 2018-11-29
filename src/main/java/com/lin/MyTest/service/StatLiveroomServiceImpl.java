package com.lin.MyTest.service;


import com.lin.MyTest.dao.StatLiveroomDao;
import com.lin.MyTest.model.biz.liveroom.StatLiveroom;
import com.lin.MyTest.model.entity.StatLiveroomEntity;
import com.lin.MyTest.redisdao.StatLiveroomRedisDao;
import com.lin.MyTest.util.BeanUtil;
import com.lin.MyTest.util.TimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StatLiveroomServiceImpl implements StatLiveroomService {

    @Autowired
    private LiveroomSearchService liveroomSearchService;

    @Autowired
    private StatLiveroomRedisDao statLiveroomRedisDao;

    @Autowired
    private LiveroomService liveroomService;


    @Autowired
    private StatLiveroomDao statLiveroomDao;

    private Date next10minute;
    private String currentTimeKey;
    private ExecutorService threadPool;

    @PostConstruct
    public void init() {
        Date currentTime = TimeUtils.getCurrentTenMinute().getTime();
        next10minute = TimeUtils.getNextTenMinute().getTime();
        currentTimeKey = TimeUtils.getMinuteKey(currentTime);
        threadPool = Executors.newFixedThreadPool(20);
    }

    @Override
    public void incrCurAudienceCount(long liveroomId, int count) {
        int curAudienceCount = statLiveroomRedisDao.incrCurrentAudienceCount(liveroomId, count);
        //  更新最高在线人数
        updateTopOnlineCount(liveroomId, curAudienceCount);
    }

    @Override
    public void incrLikeCount(long liveroomId, int likeCountVerify) {
        statLiveroomRedisDao.incrLikeCount(liveroomId, likeCountVerify);
    }

    @Override
    public void incrCommentCount(long liveroomId, int commentNumberVerify) {
        statLiveroomRedisDao.incrCommentCount(liveroomId, commentNumberVerify);
    }

    @Override
    public void incrRobotCommentCount(long liveroomId, int commentNumberVerify) {
        statLiveroomRedisDao.incrRobotCommentCount(liveroomId, commentNumberVerify);
    }

    @Override
    public void incrActivitySubscribeCount(long liveroomId, int activitySubscribeVerify) {
        statLiveroomRedisDao.incrActivitySubscribeCount(liveroomId, activitySubscribeVerify);

    }


    @Override
    public int getTotalVv(long liveroomId) {
        return statLiveroomRedisDao.getTotalVv(liveroomId);
    }

    @Override
    public void updateStatLiveroomFromRedis(long liveroomId) {
        //查缓存
        StatLiveroomEntity redisEntity = statLiveroomRedisDao.getStatLiveroom(liveroomId);
        if (redisEntity == null) {
            return;
        }
        StatLiveroomEntity dbEntity = statLiveroomDao.getByLiveroomId(liveroomId);
        if (dbEntity == null) {
            dbEntity = new StatLiveroomEntity(liveroomId);
        }
        dbEntity.setLiveAppVv(Math.max(dbEntity.getLiveAppVv(), redisEntity.getLiveAppVv()));
        dbEntity.setLiveWapVv(Math.max(dbEntity.getLiveWapVv(), redisEntity.getLiveWapVv()));
        dbEntity.setLivePcVv(Math.max(dbEntity.getLivePcVv(), redisEntity.getLivePcVv()));
        dbEntity.setPlaybackAppVv(Math.max(dbEntity.getPlaybackAppVv(), redisEntity.getPlaybackAppVv()));
        dbEntity.setPlaybackWapVv(Math.max(dbEntity.getPlaybackWapVv(), redisEntity.getPlaybackWapVv()));
        dbEntity.setPlaybackPcVv(Math.max(dbEntity.getPlaybackPcVv(), redisEntity.getPlaybackPcVv()));
        dbEntity.setRobotVv(Math.max(dbEntity.getRobotVv(), redisEntity.getRobotVv()));
        dbEntity.setTotalVv(Math.max(dbEntity.getTotalVv(), redisEntity.getTotalVv()));
        dbEntity.setCommentCount(Math.max(dbEntity.getCommentCount(), redisEntity.getCommentCount()));
        dbEntity.setLikeCount(Math.max(dbEntity.getLikeCount(), redisEntity.getLikeCount()));
        dbEntity.setRobotCommentCount(Math.max(dbEntity.getRobotCommentCount(), redisEntity.getRobotCommentCount()));
        dbEntity.setWechatPressCount(Math.max(dbEntity.getWechatPressCount(), redisEntity.getWechatPressCount()));
        dbEntity.setPhonePressCount(Math.max(dbEntity.getPhonePressCount(), redisEntity.getPhonePressCount()));
        dbEntity.setLiveTopAudienceCount(Math.max(dbEntity.getLiveTopAudienceCount(), redisEntity.getLiveTopAudienceCount()));
        dbEntity.setActivitySubscribeCount(Math.max(dbEntity.getActivitySubscribeCount(), redisEntity.getActivitySubscribeCount()));
        statLiveroomDao.insertOrUpdate(dbEntity);
    }

    @Override
    public StatLiveroomEntity getByLiveroomId(long liveroomId) {
        //读缓存
        StatLiveroomEntity statLiveroomEntity = statLiveroomRedisDao.getStatLiveroom(liveroomId);
        //缓存存在，直接返回
        if (statLiveroomEntity != null) {
            return statLiveroomEntity;
        } else {
            //缓存不存在，读数据库
            statLiveroomEntity = statLiveroomDao.getByLiveroomId(liveroomId);
            if (statLiveroomEntity != null) {
                statLiveroomRedisDao.setStatLiveroom(statLiveroomEntity);
            } else {
                statLiveroomEntity = new StatLiveroomEntity(liveroomId);
            }
            return statLiveroomEntity;
        }
    }

    /**
     * 对外的统计统一接口
     *
     * @param liveroomId
     * @return
     */
    @Override
    public StatLiveroom getStatById(long liveroomId) {
        StatLiveroom statLiveroom = new StatLiveroom();
        StatLiveroomEntity statLiveroomEntity = getByLiveroomId(liveroomId);
        BeanUtil.copyPropertiesIgnoreNull(statLiveroomEntity, statLiveroom);
        statLiveroom.setCurAudienceCount(getCurrentAudienceCount(liveroomId));
        return statLiveroom;
    }

    @Override
    public Map<Long, StatLiveroom> getStatByIds(List<Long> liveroomIds) {
        Map<Long, StatLiveroom> map = new HashMap<>();
        liveroomIds.forEach(o -> {
            map.put(o, getStatById(o));
        });
        return map;
    }

    @Override
    public void addLiveroomIdToDurationSet(long liveroomId) {
        checkoutIime();
        statLiveroomRedisDao.addDurationActiveLiveroomId(currentTimeKey, liveroomId);
    }

    @Override
    public void setDurationActiveExpireTime() {
        checkoutIime();
        statLiveroomRedisDao.setDurationActiveExpireTime(currentTimeKey);
    }

    @Override
    public boolean checkExistDurationActiveRedis() {
        return  statLiveroomRedisDao.checkExistDurationActiveRedis(currentTimeKey);
    }

    @Override
    public Set<String> getDurationActiveLiveroomIdSetByTimeKey(String timeKey) {
        return statLiveroomRedisDao.getDurationActiveLiveroomIdSetByTimeKey(timeKey);
    }

    @Override
    public void deleteDurationActiveLiveroomIdByDateKey(String dateKey) {
        String keyPatten = String.format("statliveroom:duration_active_liveroom:%s*", dateKey);
        Set<String> keys = statLiveroomRedisDao.getRedisByKeys(keyPatten);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        String[] keysArray = keys.toArray(new String[keys.size()]);
        statLiveroomRedisDao.deleKeys(keysArray);
    }

    private void checkoutIime() {
        // 增加十分钟内直播间访问记录
        Date now = new Date();
        if (now.getTime() > next10minute.getTime()) {
            currentTimeKey = TimeUtils.getMinuteKey(TimeUtils.getCurrentTenMinute().getTime());
            next10minute = TimeUtils.getNextTenMinute().getTime();
        }
    }

    @Override
    public void incrLiveroomRobotVV(long liveroomId, int robotVVrVerify) {
        statLiveroomRedisDao.incrRobotVv(liveroomId, robotVVrVerify);
        //更新totalVV
        statLiveroomRedisDao.sumResetTotalVv(liveroomId);
    }

    @Override
    public int getCurrentAudienceCount(long liveroomId) {
        int currentAudienceCount = statLiveroomRedisDao.getCurrentAudienceCount(liveroomId);
        return (currentAudienceCount < 0) ? 0 : currentAudienceCount;
    }

    @Override
    public void updateCurAudienceCount(long liveroomId, int count) {
        statLiveroomRedisDao.setCurrentAudienceCount(liveroomId, count);
        //  更新最高在线人数
        updateTopOnlineCount(liveroomId, getCurrentAudienceCount(liveroomId));
    }

    //批量获取statLiveroom信息
    @Override
    public List<StatLiveroomEntity> getList(String liveroomIds) {
        if (StringUtils.isEmpty(liveroomIds)) {
            return new ArrayList<>();
        }
        return statLiveroomDao.getListByLiveroomIds(liveroomIds);
    }

    @Override
    public void statLiveroomPer10Min() {
        Calendar cal = TimeUtils.getLastTenMinute();
        Date today = cal.getTime();
        String minuteKey = TimeUtils.getMinuteKey(today);
        Set<String> durationActiveLiveroomIdSet = getDurationActiveLiveroomIdSetByTimeKey(minuteKey);
        for (String liveroomIdStr : durationActiveLiveroomIdSet) {
            threadPool.execute(() -> {
                long liveroomId = NumberUtils.toLong(liveroomIdStr, 0);
                //  直播间统计：缓存刷到数据库
                try {
                    updateStatLiveroomFromRedis(liveroomId);
                } catch (Exception ignore) {
                }
                //  主播统计：缓存刷到数据库
                try {
                    long userId = liveroomService.getHostIdByLiveroom(liveroomId);
                } catch (Exception ignore) {
                }
                //  直播间ES更新：更新totalVv
                liveroomSearchService.refreshLiveroom(liveroomId);
            });
        }
    }

    @Override
    public void increasePressCount(long liveroomId, int type, int count) {
    }

    @Override
    public void hostInfoPress(long liveroomId, int type, Long userId) {
    }

    @Override
    public void refreshOldToNew(List<Long> ids) {
        statLiveroomRedisDao.refreshOldToNew(ids);
    }

    @Override
    public void refreshOldDurationKey() {
        String keyPatten = String.format("DURATION_ACTIVE_LIVEROOM_*");
        Set<String> keys = statLiveroomRedisDao.getRedisByKeys(keyPatten);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

    }

    private void updateTopOnlineCount(long liveroomId, int count) {
        int top = statLiveroomRedisDao.getLiveTopAudienceCount(liveroomId);
        if (count > top) {
            statLiveroomRedisDao.setLiveTopAudienceCount(liveroomId, count);
        }
    }
}
