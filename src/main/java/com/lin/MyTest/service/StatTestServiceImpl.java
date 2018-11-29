package com.lin.MyTest.service;


import com.lin.MyTest.dao.StatTestDao;
import com.lin.MyTest.model.biz.Test.StatTest;
import com.lin.MyTest.model.entity.StatTestEntity;
import com.lin.MyTest.redisdao.StatTestRedisDao;
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
public class StatTestServiceImpl implements StatTestService {

    @Autowired
    private TestSearchService TestSearchService;

    @Autowired
    private StatTestRedisDao statTestRedisDao;

    @Autowired
    private TestService TestService;


    @Autowired
    private StatTestDao statTestDao;

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
    public void incrCurAudienceCount(long TestId, int count) {
        int curAudienceCount = statTestRedisDao.incrCurrentAudienceCount(TestId, count);
        //  更新最高在线人数
        updateTopOnlineCount(TestId, curAudienceCount);
    }

    @Override
    public void incrLikeCount(long TestId, int likeCountVerify) {
        statTestRedisDao.incrLikeCount(TestId, likeCountVerify);
    }

    @Override
    public void incrCommentCount(long TestId, int commentNumberVerify) {
        statTestRedisDao.incrCommentCount(TestId, commentNumberVerify);
    }

    @Override
    public void incrRobotCommentCount(long TestId, int commentNumberVerify) {
        statTestRedisDao.incrRobotCommentCount(TestId, commentNumberVerify);
    }

    @Override
    public void incrActivitySubscribeCount(long TestId, int activitySubscribeVerify) {
        statTestRedisDao.incrActivitySubscribeCount(TestId, activitySubscribeVerify);

    }


    @Override
    public int getTotalVv(long TestId) {
        return statTestRedisDao.getTotalVv(TestId);
    }

    @Override
    public void updateStatTestFromRedis(long TestId) {
        //查缓存
        StatTestEntity redisEntity = statTestRedisDao.getStatTest(TestId);
        if (redisEntity == null) {
            return;
        }
        StatTestEntity dbEntity = statTestDao.getByTestId(TestId);
        if (dbEntity == null) {
            dbEntity = new StatTestEntity(TestId);
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
        statTestDao.insertOrUpdate(dbEntity);
    }

    @Override
    public StatTestEntity getByTestId(long TestId) {
        //读缓存
        StatTestEntity statTestEntity = statTestRedisDao.getStatTest(TestId);
        //缓存存在，直接返回
        if (statTestEntity != null) {
            return statTestEntity;
        } else {
            //缓存不存在，读数据库
            statTestEntity = statTestDao.getByTestId(TestId);
            if (statTestEntity != null) {
                statTestRedisDao.setStatTest(statTestEntity);
            } else {
                statTestEntity = new StatTestEntity(TestId);
            }
            return statTestEntity;
        }
    }

    /**
     * 对外的统计统一接口
     *
     * @param TestId
     * @return
     */
    @Override
    public StatTest getStatById(long TestId) {
        StatTest statTest = new StatTest();
        StatTestEntity statTestEntity = getByTestId(TestId);
        BeanUtil.copyPropertiesIgnoreNull(statTestEntity, statTest);
        statTest.setCurAudienceCount(getCurrentAudienceCount(TestId));
        return statTest;
    }

    @Override
    public Map<Long, StatTest> getStatByIds(List<Long> TestIds) {
        Map<Long, StatTest> map = new HashMap<>();
        TestIds.forEach(o -> {
            map.put(o, getStatById(o));
        });
        return map;
    }

    @Override
    public void addTestIdToDurationSet(long TestId) {
        checkoutIime();
        statTestRedisDao.addDurationActiveTestId(currentTimeKey, TestId);
    }

    @Override
    public void setDurationActiveExpireTime() {
        checkoutIime();
        statTestRedisDao.setDurationActiveExpireTime(currentTimeKey);
    }

    @Override
    public boolean checkExistDurationActiveRedis() {
        return  statTestRedisDao.checkExistDurationActiveRedis(currentTimeKey);
    }

    @Override
    public Set<String> getDurationActiveTestIdSetByTimeKey(String timeKey) {
        return statTestRedisDao.getDurationActiveTestIdSetByTimeKey(timeKey);
    }

    @Override
    public void deleteDurationActiveTestIdByDateKey(String dateKey) {
        String keyPatten = String.format("statTest:duration_active_Test:%s*", dateKey);
        Set<String> keys = statTestRedisDao.getRedisByKeys(keyPatten);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        String[] keysArray = keys.toArray(new String[keys.size()]);
        statTestRedisDao.deleKeys(keysArray);
    }

    private void checkoutIime() {
        // 增加十分钟内xxxx访问记录
        Date now = new Date();
        if (now.getTime() > next10minute.getTime()) {
            currentTimeKey = TimeUtils.getMinuteKey(TimeUtils.getCurrentTenMinute().getTime());
            next10minute = TimeUtils.getNextTenMinute().getTime();
        }
    }

    @Override
    public void incrTestRobotVV(long TestId, int robotVVrVerify) {
        statTestRedisDao.incrRobotVv(TestId, robotVVrVerify);
        //更新totalVV
        statTestRedisDao.sumResetTotalVv(TestId);
    }

    @Override
    public int getCurrentAudienceCount(long TestId) {
        int currentAudienceCount = statTestRedisDao.getCurrentAudienceCount(TestId);
        return (currentAudienceCount < 0) ? 0 : currentAudienceCount;
    }

    @Override
    public void updateCurAudienceCount(long TestId, int count) {
        statTestRedisDao.setCurrentAudienceCount(TestId, count);
        //  更新最高在线人数
        updateTopOnlineCount(TestId, getCurrentAudienceCount(TestId));
    }

    //批量获取statTest信息
    @Override
    public List<StatTestEntity> getList(String TestIds) {
        if (StringUtils.isEmpty(TestIds)) {
            return new ArrayList<>();
        }
        return statTestDao.getListByTestIds(TestIds);
    }

    @Override
    public void statTestPer10Min() {
        Calendar cal = TimeUtils.getLastTenMinute();
        Date today = cal.getTime();
        String minuteKey = TimeUtils.getMinuteKey(today);
        Set<String> durationActiveTestIdSet = getDurationActiveTestIdSetByTimeKey(minuteKey);
        for (String TestIdStr : durationActiveTestIdSet) {
            threadPool.execute(() -> {
                long TestId = NumberUtils.toLong(TestIdStr, 0);
                //  xxxx统计：缓存刷到数据库
                try {
                    updateStatTestFromRedis(TestId);
                } catch (Exception ignore) {
                }
                //  主播统计：缓存刷到数据库
                try {
                    long userId = TestService.getHostIdByTest(TestId);
                } catch (Exception ignore) {
                }
                //  xxxxES更新：更新totalVv
                TestSearchService.refreshTest(TestId);
            });
        }
    }

    @Override
    public void increasePressCount(long TestId, int type, int count) {
    }

    @Override
    public void hostInfoPress(long TestId, int type, Long userId) {
    }

    @Override
    public void refreshOldToNew(List<Long> ids) {
        statTestRedisDao.refreshOldToNew(ids);
    }

    @Override
    public void refreshOldDurationKey() {
        String keyPatten = String.format("DURATION_ACTIVE_Test_*");
        Set<String> keys = statTestRedisDao.getRedisByKeys(keyPatten);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

    }

    private void updateTopOnlineCount(long TestId, int count) {
        int top = statTestRedisDao.getLiveTopAudienceCount(TestId);
        if (count > top) {
            statTestRedisDao.setLiveTopAudienceCount(TestId, count);
        }
    }
}
