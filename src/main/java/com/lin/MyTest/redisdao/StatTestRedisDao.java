package com.lin.MyTest.redisdao;

import com.lin.MyTest.datasource.JedisService;
import com.lin.MyTest.model.entity.StatTestEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class StatTestRedisDao {
    /**
     * 统计xxxx所有数据缓存
     */
	@Resource
	private JedisService jedisService;
	
    private static final String STAT_Test_HASHKEY = "statTest:Test:Test_%s";
    //缓存迁移
    private static final String OLD_STAT_Test_HASHKEY = "STAT_Test_%s";

    private static final String STAT_Test_CURRENT_AUDIENCE_COUNT_HASHFIELD = "CURRENT_AUDIENCE_COUNT";   //当前在线人数
    private static final String STAT_Test_LIKE_COUNT_HASHFIELD = "LIKE_COUNT";   //点赞数
    private static final String STAT_Test_COMMENT_COUNT_HASHFIELD = "COMMENT_COUNT";   //总评论数（含机器人）
    private static final String STAT_Test_ROBOT_COMMENT_COUNT_HASHFIELD = "ROBOT_COMMENT_COUNT";   //机器人评论数
    private static final String STAT_Test_LIVE_TOP_ONLINE_COUNT_HASHFILD = "LIVE_TOP_ONLINE_COUNT";   //xxxx最高在线人数
    private static final String STAT_Test_TOTAL_VV_HASHFIELD = "TOTAL_VV";//总的观看数
    private static final String STAT_Test_WECHAT_PRESS_COUNT = "WECHAT_PRESS_COUNT";//xxxx中为微信按钮点击量
    private static final String STAT_Test_PHONE_PRESS_COUNT = "PHONE_PRESS_COUNT";//xxxx中为手机按钮点击量
    private static final String STAT_Test_SUBSCRIBE_ACTIVITY_HASHFIELD = "ACTIVITY_SUBSCRIBE_COUNT";

    private static final String STAT_Test_LIVE_APP_VV_HASHFIELD = "LIVE_APP_VV";
    private static final String STAT_Test_LIVE_WAP_VV_HASHFIELD = "LIVE_WAP_VV";
    private static final String STAT_Test_LIVE_PC_VV_HASHFIELD = "LIVE_PC_VV";

    private static final String STAT_Test_PLAYBACK_APP_VV_HASHFIELD = "PLAYBACK_APP_VV";
    private static final String STAT_Test_PLAYBACK_WAP_VV_HASHFIELD = "PLAYBACK_WAP_VV";
    private static final String STAT_Test_PLAYBACK_PC_VV_HASHFIELD = "PLAYBACK_PC_VV";
    /**
     * 目前RobotRedisDao中只记录了如下数据：
     * 1.xxxx中设置的目标机器人数量
     * 2.xxxx中机器人当前在线人数
     * 3.xxxx中机器人的评论
     * 
     * 机器人的逻辑是，每增加一个机器人，会随机增加1到2个观看人数
     * 目前缺乏记录机器人的观看人次的统计，因此在StatTestRedisDao记录机器人的统计
     */
    private static final String STAT_Test_LIVE_ROBOT_VV_HASHFIELD = "LIVE_ROBOT_VV";

    //duration活跃xxxx
    private static final String DURATION_ACTIVE_Test_SETKEY = "statTest:duration_active_Test:%s";

    //缓存迁移
    private static final String OLD_DURATION_ACTIVE_Test_SETKEY = "DURATION_ACTIVE_Test_%s";

    private static final int DURATION_ACTIVE_Test_EXPIRED_SECOND = 60*60*24*10;


    public int getCurrentAudienceCount(long TestId){
    	String audienceCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId) ,
    			STAT_Test_CURRENT_AUDIENCE_COUNT_HASHFIELD));
    	return NumberUtils.toInt(audienceCountStr, 0);
    }

    public int incrCurrentAudienceCount(long TestId, int newCount){
    	Long currentCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
    			STAT_Test_CURRENT_AUDIENCE_COUNT_HASHFIELD, newCount));
    	return currentCount.intValue();
    }
    
    public void setCurrentAudienceCount(long TestId, int count){
    	jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId),
    			STAT_Test_CURRENT_AUDIENCE_COUNT_HASHFIELD, String.valueOf(count)));
    }
    
    public int getLikeCount(long TestId){
    	String likeCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId) ,
    			STAT_Test_LIKE_COUNT_HASHFIELD));
    	return NumberUtils.toInt(likeCountStr, 0);
    }
    
    public Integer incrLikeCount(long TestId, int newCount){
    	Long likeCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
    			STAT_Test_LIKE_COUNT_HASHFIELD, newCount));
    	return likeCount.intValue();
    }

    public void setLikeCount(long TestId, int newCount){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIKE_COUNT_HASHFIELD, String.valueOf(newCount)));
    }
    
    public Integer incrCommentCount(long TestId, int newCount){
    	Long commentCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
    			STAT_Test_COMMENT_COUNT_HASHFIELD, newCount));
    	return commentCount.intValue();
    }

    public void setCommentCount(long TestId, int newCount){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_COMMENT_COUNT_HASHFIELD, String.valueOf(newCount)));
    }

    //robotComment
    public int getRobotCommentCount(long TestId){
        String commentCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_ROBOT_COMMENT_COUNT_HASHFIELD));
        return NumberUtils.toInt(commentCountStr, 0);
    }

    public Integer incrRobotCommentCount(long TestId, int newCount){
        Long commentCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_ROBOT_COMMENT_COUNT_HASHFIELD, newCount));
        return commentCount.intValue();
    }

    public Integer setRobotCommentCount(long TestId, int newCount){
        Long commentCount = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_ROBOT_COMMENT_COUNT_HASHFIELD, String.valueOf(newCount)));
        return commentCount.intValue();
    }

    
    public int getLiveTopAudienceCount(long TestId){
        String topOnlineCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_TOP_ONLINE_COUNT_HASHFILD));
        return NumberUtils.toInt(topOnlineCountStr, 0);
    }
    
    public void setLiveTopAudienceCount(long TestId, int topOnlineCount){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_TOP_ONLINE_COUNT_HASHFILD,  String.valueOf(topOnlineCount)));
    }

    public int getTotalVv(long TestId){
        String totalVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_TOTAL_VV_HASHFIELD));
        return NumberUtils.toInt(totalVVStr, 0);
    }

    public Integer setTotalVv(long TestId, int newTotalVV){
        Long totalVV = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_TOTAL_VV_HASHFIELD, String.valueOf(newTotalVV)));
        return totalVV.intValue();
    }

    public Integer incrWechatPressCount(long TestId, int newAPPVV){
        Long wechatPressCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_WECHAT_PRESS_COUNT, newAPPVV));
        return wechatPressCount.intValue();
    }

    public int setWechatPressCount(long TestId, int count){
        Long wechatPressCount = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_WECHAT_PRESS_COUNT, String.valueOf(count)));
        return wechatPressCount.intValue();
    }

    public Integer incrPhonePressCount(long TestId, int newAPPVV){
        Long phonePressCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PHONE_PRESS_COUNT, newAPPVV));
        return phonePressCount.intValue();
    }

    public int setPhonePressCount(long TestId, int count){
        Long phonePressCount = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PHONE_PRESS_COUNT, String.valueOf(count)));
        return phonePressCount.intValue();
    }

    public int getLiveAppVv(long TestId){
        String appLiveVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIVE_APP_VV_HASHFIELD));
        return NumberUtils.toInt(appLiveVVStr, 0);
    }

    public Integer incrLiveAppVv(long TestId, int newAPPVV){
        Long appLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIVE_APP_VV_HASHFIELD, newAPPVV));
        return appLiveVV.intValue();
    }

    public void setLiveAppVv(long TestId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIVE_APP_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    public int getLiveWapVv(long TestId){
        String wapLiveVVStr = jedisService.executePersist( jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_WAP_VV_HASHFIELD));
        return NumberUtils.toInt(wapLiveVVStr, 0);
    }

    public Integer incrLiveWapVv(long TestId, int newWAPVV){
        Long wapLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_WAP_VV_HASHFIELD, newWAPVV));
        return wapLiveVV.intValue();
    }

    public void setLiveWapVv(long TestId, int count){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIVE_WAP_VV_HASHFIELD, String.valueOf(count)));
    }

    public int getLivePcVv(long TestId){
        String pcLiveVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_PC_VV_HASHFIELD));
        return NumberUtils.toInt(pcLiveVVStr, 0);
    }

    public Integer incrLivePcVv(long TestId, int count){
        Long pcLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_PC_VV_HASHFIELD, count));
        return pcLiveVV.intValue();
    }

    public void setLivePcVv(long TestId, int count){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIVE_PC_VV_HASHFIELD, String.valueOf(count)));
    }

    public int getRobotVv(long TestId){
        String robotLiveVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_ROBOT_VV_HASHFIELD));
        return NumberUtils.toInt(robotLiveVVStr, 0);
    }

    public Integer incrRobotVv(long TestId, int newRobotCVV){
        Long robotLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_LIVE_ROBOT_VV_HASHFIELD, newRobotCVV));
        return robotLiveVV.intValue();
    }

    public void setRobotVv(long TestId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_LIVE_ROBOT_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    //新的合并缓存
    public int getPlaybackAppVv(long TestId){
        String appPlaybackVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PLAYBACK_APP_VV_HASHFIELD));
        return NumberUtils.toInt(appPlaybackVVStr, 0);
    }

    public Integer incrPlaybackAppVv(long TestId, int newAPPVV){
        Long appPlaybackVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PLAYBACK_APP_VV_HASHFIELD, newAPPVV));
        return appPlaybackVV.intValue();
    }

    public void setPlaybackAppVv(long TestId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PLAYBACK_APP_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    public int getPlaybackWapVv(long TestId){
        String wapPlaybackVVStr = jedisService.executePersist( jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_PLAYBACK_WAP_VV_HASHFIELD));
        return NumberUtils.toInt(wapPlaybackVVStr, 0);
    }

    public Integer incrPlaybackWapVv(long TestId, int newWAPVV){
        Long wapPlaybackVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_PLAYBACK_WAP_VV_HASHFIELD, newWAPVV));
        return wapPlaybackVV.intValue();
    }

    public void setPlaybackWapVv(long TestId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PLAYBACK_WAP_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    public int getPlaybackPcVv(long TestId){
        String pcPlaybackVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_PLAYBACK_PC_VV_HASHFIELD));
        return NumberUtils.toInt(pcPlaybackVVStr, 0);
    }

    public Integer incrPlaybackPcVv(long TestId, int newPCVV){
        Long pcPlaybackVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_PLAYBACK_PC_VV_HASHFIELD, newPCVV));
        return pcPlaybackVV.intValue();
    }

    public void setPlaybackPcVv(long TestId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_PLAYBACK_PC_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    //activity
    public int getActivitySubscribeCount(long TestId){
        String activity = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_SUBSCRIBE_ACTIVITY_HASHFIELD));
        return NumberUtils.toInt(activity, 0);
    }

    public Integer incrActivitySubscribeCount(long TestId, int newVV){
        Long activity = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_Test_HASHKEY, TestId),
                STAT_Test_SUBSCRIBE_ACTIVITY_HASHFIELD, newVV));
        return activity.intValue();
    }

    public void setActivitySubscribeCount(long TestId, int newVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_Test_HASHKEY, TestId) ,
                STAT_Test_SUBSCRIBE_ACTIVITY_HASHFIELD, String.valueOf(newVV)));
    }
    
    public int addDurationActiveTestId(String timeKey ,long TestId){
        Long result = jedisService.executePersist(jedis -> jedis.sadd( String.format(DURATION_ACTIVE_Test_SETKEY, timeKey), String.valueOf(TestId)));
        return result.intValue();
    }

    public void setDurationActiveExpireTime(String timeKey){
        jedisService.executePersist(jedis -> jedis.expire(String.format(DURATION_ACTIVE_Test_SETKEY, timeKey),DURATION_ACTIVE_Test_EXPIRED_SECOND));
    }

    public boolean checkExistDurationActiveRedis(String timeKey){
       return jedisService.executePersist(jedis -> jedis.exists(String.format(DURATION_ACTIVE_Test_SETKEY, timeKey)));
    }
    
    public Set<String> getDurationActiveTestIdSetByTimeKey(String timeKey){
        return jedisService.executePersist(jedis -> jedis.smembers(String.format(DURATION_ACTIVE_Test_SETKEY, timeKey)));
    }

    public Set<String> getRedisByKeys(String keys){
        return jedisService.executePersist(jedis -> jedis.keys(keys));
    }

    public void rename(String oldKey,String newKey){
        jedisService.executePersist(jedis -> jedis.rename(oldKey,newKey));
    }

    public void deleKeys(String... keys){
        jedisService.executePersist(jedis->jedis.del(keys));
    }

    public StatTestEntity getStatTest(long TestId){
        Map<String, String> liveOverViewMap = jedisService.executePersist(jedis -> jedis.hgetAll(String.format(STAT_Test_HASHKEY, TestId)));
        if(MapUtils.isEmpty(liveOverViewMap)){
            return null;
        }
        StatTestEntity statTestEntity = new StatTestEntity(TestId);
        if(!MapUtils.isEmpty(liveOverViewMap)){
            statTestEntity.setLiveAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_APP_VV_HASHFIELD), 0));
            statTestEntity.setLiveWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_WAP_VV_HASHFIELD), 0));
            statTestEntity.setLivePcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_PC_VV_HASHFIELD), 0));
            statTestEntity.setRobotVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_ROBOT_VV_HASHFIELD), 0));
            statTestEntity.setPlaybackAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_APP_VV_HASHFIELD), 0));
            statTestEntity.setPlaybackWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_WAP_VV_HASHFIELD), 0));
            statTestEntity.setPlaybackPcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_PC_VV_HASHFIELD), 0));
            statTestEntity.setTotalVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_TOTAL_VV_HASHFIELD), 0));
            statTestEntity.setLikeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIKE_COUNT_HASHFIELD), 0));
            statTestEntity.setCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_COMMENT_COUNT_HASHFIELD), 0));
            statTestEntity.setRobotCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_ROBOT_COMMENT_COUNT_HASHFIELD), 0));
            statTestEntity.setWechatPressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_WECHAT_PRESS_COUNT), 0));
            statTestEntity.setPhonePressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PHONE_PRESS_COUNT), 0));
            statTestEntity.setActivitySubscribeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_SUBSCRIBE_ACTIVITY_HASHFIELD), 0));
            statTestEntity.setLiveTopAudienceCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_TOP_ONLINE_COUNT_HASHFILD), 0));
        }
        return statTestEntity;
    }


    public StatTestEntity getOldStatTest(long TestId){
        Map<String, String> liveOverViewMap = jedisService.executePersist(jedis -> jedis.hgetAll(String.format(OLD_STAT_Test_HASHKEY, TestId)));
        if(MapUtils.isEmpty(liveOverViewMap)){
            return null;
        }
        StatTestEntity statTestEntity = new StatTestEntity(TestId);
        if(!MapUtils.isEmpty(liveOverViewMap)){
            statTestEntity.setLiveAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_APP_VV_HASHFIELD), 0));
            statTestEntity.setLiveWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_WAP_VV_HASHFIELD), 0));
            statTestEntity.setLivePcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_PC_VV_HASHFIELD), 0));
            statTestEntity.setRobotVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_ROBOT_VV_HASHFIELD), 0));
            statTestEntity.setPlaybackAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_APP_VV_HASHFIELD), 0));
            statTestEntity.setPlaybackWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_WAP_VV_HASHFIELD), 0));
            statTestEntity.setPlaybackPcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_PC_VV_HASHFIELD), 0));
            statTestEntity.setTotalVv(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_TOTAL_VV_HASHFIELD), 0));
            statTestEntity.setLikeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIKE_COUNT_HASHFIELD), 0));
            statTestEntity.setCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_COMMENT_COUNT_HASHFIELD), 0));
            statTestEntity.setRobotCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_ROBOT_COMMENT_COUNT_HASHFIELD), 0));
            statTestEntity.setWechatPressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_WECHAT_PRESS_COUNT), 0));
            statTestEntity.setPhonePressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PHONE_PRESS_COUNT), 0));
            statTestEntity.setActivitySubscribeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_SUBSCRIBE_ACTIVITY_HASHFIELD), 0));
            statTestEntity.setLiveTopAudienceCount(NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_TOP_ONLINE_COUNT_HASHFILD), 0));
        }
        return statTestEntity;
    }

    //刷新旧的缓存到新的
    public void refreshOldToNew(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        ids.forEach(o->{
            StatTestEntity statTestEntity =  getOldStatTest(o);
            setStatTest(statTestEntity);
        });
    }

    public void setStatTest(StatTestEntity entity) {
        if(entity == null) {
            return;
        }
        setLiveAppVv(entity.getTestId(), entity.getLiveAppVv());
        setLiveWapVv(entity.getTestId(), entity.getLiveWapVv());
        setLivePcVv(entity.getTestId(), entity.getLivePcVv());
        setPlaybackAppVv(entity.getTestId(), entity.getPlaybackAppVv());
        setPlaybackWapVv(entity.getTestId(), entity.getPlaybackWapVv());
        setPlaybackPcVv(entity.getTestId(), entity.getPlaybackPcVv());
        setRobotVv(entity.getTestId(), entity.getRobotVv());
        setTotalVv(entity.getTestId(), entity.getTotalVv());
        setCommentCount(entity.getTestId(), entity.getCommentCount());
        setRobotCommentCount(entity.getTestId(), entity.getRobotCommentCount());
        setLikeCount(entity.getTestId(), entity.getLikeCount());
        setWechatPressCount(entity.getTestId(), entity.getWechatPressCount());
        setPhonePressCount(entity.getTestId(), entity.getPhonePressCount());
        setLiveTopAudienceCount(entity.getTestId(), entity.getLiveTopAudienceCount());
        setActivitySubscribeCount(entity.getTestId(), entity.getActivitySubscribeCount());
    }

    /**
     * 在vv变化时重新计算totalVv
     * */
    public void sumResetTotalVv(long TestId) {
        Map<String, String> liveOverViewMap = jedisService.executePersist(jedis -> jedis.hgetAll(String.format(STAT_Test_HASHKEY, TestId)));
        if(MapUtils.isEmpty(liveOverViewMap)){
            return;
        }
        int totalVv = NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_APP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_WAP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_PC_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_APP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_WAP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_Test_PLAYBACK_PC_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_Test_LIVE_ROBOT_VV_HASHFIELD), 0);
        setTotalVv(TestId, totalVv);
    }
}
