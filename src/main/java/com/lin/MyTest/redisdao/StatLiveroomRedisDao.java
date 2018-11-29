package com.lin.MyTest.redisdao;

import com.lin.MyTest.datasource.JedisService;
import com.lin.MyTest.model.entity.StatLiveroomEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class StatLiveroomRedisDao {
    /**
     * 统计直播间所有数据缓存
     */
	@Resource
	private JedisService jedisService;
	
    private static final String STAT_LIVEROOM_HASHKEY = "statliveroom:liveroom:liveroom_%s";
    //缓存迁移
    private static final String OLD_STAT_LIVEROOM_HASHKEY = "STAT_LIVEROOM_%s";

    private static final String STAT_LIVEROOM_CURRENT_AUDIENCE_COUNT_HASHFIELD = "CURRENT_AUDIENCE_COUNT";   //当前在线人数
    private static final String STAT_LIVEROOM_LIKE_COUNT_HASHFIELD = "LIKE_COUNT";   //点赞数
    private static final String STAT_LIVEROOM_COMMENT_COUNT_HASHFIELD = "COMMENT_COUNT";   //总评论数（含机器人）
    private static final String STAT_LIVEROOM_ROBOT_COMMENT_COUNT_HASHFIELD = "ROBOT_COMMENT_COUNT";   //机器人评论数
    private static final String STAT_LIVEROOM_LIVE_TOP_ONLINE_COUNT_HASHFILD = "LIVE_TOP_ONLINE_COUNT";   //直播间最高在线人数
    private static final String STAT_LIVEROOM_TOTAL_VV_HASHFIELD = "TOTAL_VV";//总的观看数
    private static final String STAT_LIVEROOM_WECHAT_PRESS_COUNT = "WECHAT_PRESS_COUNT";//直播间中为微信按钮点击量
    private static final String STAT_LIVEROOM_PHONE_PRESS_COUNT = "PHONE_PRESS_COUNT";//直播间中为手机按钮点击量
    private static final String STAT_LIVEROOM_SUBSCRIBE_ACTIVITY_HASHFIELD = "ACTIVITY_SUBSCRIBE_COUNT";

    private static final String STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD = "LIVE_APP_VV";
    private static final String STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD = "LIVE_WAP_VV";
    private static final String STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD = "LIVE_PC_VV";

    private static final String STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD = "PLAYBACK_APP_VV";
    private static final String STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD = "PLAYBACK_WAP_VV";
    private static final String STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD = "PLAYBACK_PC_VV";
    /**
     * 目前RobotRedisDao中只记录了如下数据：
     * 1.直播间中设置的目标机器人数量
     * 2.直播间中机器人当前在线人数
     * 3.直播间中机器人的评论
     * 
     * 机器人的逻辑是，每增加一个机器人，会随机增加1到2个观看人数
     * 目前缺乏记录机器人的观看人次的统计，因此在StatLiveroomRedisDao记录机器人的统计
     */
    private static final String STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD = "LIVE_ROBOT_VV";

    //duration活跃直播间
    private static final String DURATION_ACTIVE_LIVEROOM_SETKEY = "statliveroom:duration_active_liveroom:%s";

    //缓存迁移
    private static final String OLD_DURATION_ACTIVE_LIVEROOM_SETKEY = "DURATION_ACTIVE_LIVEROOM_%s";

    private static final int DURATION_ACTIVE_LIVEROOM_EXPIRED_SECOND = 60*60*24*10;


    public int getCurrentAudienceCount(long liveroomId){
    	String audienceCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) , 
    			STAT_LIVEROOM_CURRENT_AUDIENCE_COUNT_HASHFIELD));
    	return NumberUtils.toInt(audienceCountStr, 0);
    }

    public int incrCurrentAudienceCount(long liveroomId, int newCount){
    	Long currentCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) , 
    			STAT_LIVEROOM_CURRENT_AUDIENCE_COUNT_HASHFIELD, newCount));
    	return currentCount.intValue();
    }
    
    public void setCurrentAudienceCount(long liveroomId, int count){
    	jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
    			STAT_LIVEROOM_CURRENT_AUDIENCE_COUNT_HASHFIELD, String.valueOf(count)));
    }
    
    public int getLikeCount(long liveroomId){
    	String likeCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) , 
    			STAT_LIVEROOM_LIKE_COUNT_HASHFIELD));
    	return NumberUtils.toInt(likeCountStr, 0);
    }
    
    public Integer incrLikeCount(long liveroomId, int newCount){
    	Long likeCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) , 
    			STAT_LIVEROOM_LIKE_COUNT_HASHFIELD, newCount));
    	return likeCount.intValue();
    }

    public void setLikeCount(long liveroomId, int newCount){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIKE_COUNT_HASHFIELD, String.valueOf(newCount)));
    }
    
    public Integer incrCommentCount(long liveroomId, int newCount){
    	Long commentCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) , 
    			STAT_LIVEROOM_COMMENT_COUNT_HASHFIELD, newCount));
    	return commentCount.intValue();
    }

    public void setCommentCount(long liveroomId, int newCount){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_COMMENT_COUNT_HASHFIELD, String.valueOf(newCount)));
    }

    //robotComment
    public int getRobotCommentCount(long liveroomId){
        String commentCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_ROBOT_COMMENT_COUNT_HASHFIELD));
        return NumberUtils.toInt(commentCountStr, 0);
    }

    public Integer incrRobotCommentCount(long liveroomId, int newCount){
        Long commentCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_ROBOT_COMMENT_COUNT_HASHFIELD, newCount));
        return commentCount.intValue();
    }

    public Integer setRobotCommentCount(long liveroomId, int newCount){
        Long commentCount = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_ROBOT_COMMENT_COUNT_HASHFIELD, String.valueOf(newCount)));
        return commentCount.intValue();
    }

    
    public int getLiveTopAudienceCount(long liveroomId){
        String topOnlineCountStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId), 
                STAT_LIVEROOM_LIVE_TOP_ONLINE_COUNT_HASHFILD));
        return NumberUtils.toInt(topOnlineCountStr, 0);
    }
    
    public void setLiveTopAudienceCount(long liveroomId, int topOnlineCount){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId), 
                STAT_LIVEROOM_LIVE_TOP_ONLINE_COUNT_HASHFILD,  String.valueOf(topOnlineCount)));
    }

    public int getTotalVv(long liveroomId){
        String totalVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_TOTAL_VV_HASHFIELD));
        return NumberUtils.toInt(totalVVStr, 0);
    }

    public Integer setTotalVv(long liveroomId, int newTotalVV){
        Long totalVV = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_TOTAL_VV_HASHFIELD, String.valueOf(newTotalVV)));
        return totalVV.intValue();
    }

    public Integer incrWechatPressCount(long liveroomId, int newAPPVV){
        Long wechatPressCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_WECHAT_PRESS_COUNT, newAPPVV));
        return wechatPressCount.intValue();
    }

    public int setWechatPressCount(long liveroomId, int count){
        Long wechatPressCount = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_WECHAT_PRESS_COUNT, String.valueOf(count)));
        return wechatPressCount.intValue();
    }

    public Integer incrPhonePressCount(long liveroomId, int newAPPVV){
        Long phonePressCount = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PHONE_PRESS_COUNT, newAPPVV));
        return phonePressCount.intValue();
    }

    public int setPhonePressCount(long liveroomId, int count){
        Long phonePressCount = jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PHONE_PRESS_COUNT, String.valueOf(count)));
        return phonePressCount.intValue();
    }

    public int getLiveAppVv(long liveroomId){
        String appLiveVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD));
        return NumberUtils.toInt(appLiveVVStr, 0);
    }

    public Integer incrLiveAppVv(long liveroomId, int newAPPVV){
        Long appLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD, newAPPVV));
        return appLiveVV.intValue();
    }

    public void setLiveAppVv(long liveroomId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    public int getLiveWapVv(long liveroomId){
        String wapLiveVVStr = jedisService.executePersist( jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD));
        return NumberUtils.toInt(wapLiveVVStr, 0);
    }

    public Integer incrLiveWapVv(long liveroomId, int newWAPVV){
        Long wapLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD, newWAPVV));
        return wapLiveVV.intValue();
    }

    public void setLiveWapVv(long liveroomId, int count){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD, String.valueOf(count)));
    }

    public int getLivePcVv(long liveroomId){
        String pcLiveVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD));
        return NumberUtils.toInt(pcLiveVVStr, 0);
    }

    public Integer incrLivePcVv(long liveroomId, int count){
        Long pcLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD, count));
        return pcLiveVV.intValue();
    }

    public void setLivePcVv(long liveroomId, int count){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD, String.valueOf(count)));
    }

    public int getRobotVv(long liveroomId){
        String robotLiveVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD));
        return NumberUtils.toInt(robotLiveVVStr, 0);
    }

    public Integer incrRobotVv(long liveroomId, int newRobotCVV){
        Long robotLiveVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD, newRobotCVV));
        return robotLiveVV.intValue();
    }

    public void setRobotVv(long liveroomId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    //新的合并缓存
    public int getPlaybackAppVv(long liveroomId){
        String appPlaybackVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD));
        return NumberUtils.toInt(appPlaybackVVStr, 0);
    }

    public Integer incrPlaybackAppVv(long liveroomId, int newAPPVV){
        Long appPlaybackVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD, newAPPVV));
        return appPlaybackVV.intValue();
    }

    public void setPlaybackAppVv(long liveroomId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    public int getPlaybackWapVv(long liveroomId){
        String wapPlaybackVVStr = jedisService.executePersist( jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD));
        return NumberUtils.toInt(wapPlaybackVVStr, 0);
    }

    public Integer incrPlaybackWapVv(long liveroomId, int newWAPVV){
        Long wapPlaybackVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD, newWAPVV));
        return wapPlaybackVV.intValue();
    }

    public void setPlaybackWapVv(long liveroomId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    public int getPlaybackPcVv(long liveroomId){
        String pcPlaybackVVStr = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD));
        return NumberUtils.toInt(pcPlaybackVVStr, 0);
    }

    public Integer incrPlaybackPcVv(long liveroomId, int newPCVV){
        Long pcPlaybackVV = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD, newPCVV));
        return pcPlaybackVV.intValue();
    }

    public void setPlaybackPcVv(long liveroomId, int newAppVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD, String.valueOf(newAppVV)));
    }

    //activity
    public int getActivitySubscribeCount(long liveroomId){
        String activity = jedisService.executePersist(jedis -> jedis.hget(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_SUBSCRIBE_ACTIVITY_HASHFIELD));
        return NumberUtils.toInt(activity, 0);
    }

    public Integer incrActivitySubscribeCount(long liveroomId, int newVV){
        Long activity = jedisService.executePersist(jedis -> jedis.hincrBy(String.format(STAT_LIVEROOM_HASHKEY, liveroomId),
                STAT_LIVEROOM_SUBSCRIBE_ACTIVITY_HASHFIELD, newVV));
        return activity.intValue();
    }

    public void setActivitySubscribeCount(long liveroomId, int newVV){
        jedisService.executePersist(jedis -> jedis.hset(String.format(STAT_LIVEROOM_HASHKEY, liveroomId) ,
                STAT_LIVEROOM_SUBSCRIBE_ACTIVITY_HASHFIELD, String.valueOf(newVV)));
    }
    
    public int addDurationActiveLiveroomId(String timeKey ,long liveroomId){
        Long result = jedisService.executePersist(jedis -> jedis.sadd( String.format(DURATION_ACTIVE_LIVEROOM_SETKEY, timeKey), String.valueOf(liveroomId)));
        return result.intValue();
    }

    public void setDurationActiveExpireTime(String timeKey){
        jedisService.executePersist(jedis -> jedis.expire(String.format(DURATION_ACTIVE_LIVEROOM_SETKEY, timeKey),DURATION_ACTIVE_LIVEROOM_EXPIRED_SECOND));
    }

    public boolean checkExistDurationActiveRedis(String timeKey){
       return jedisService.executePersist(jedis -> jedis.exists(String.format(DURATION_ACTIVE_LIVEROOM_SETKEY, timeKey)));
    }
    
    public Set<String> getDurationActiveLiveroomIdSetByTimeKey(String timeKey){
        return jedisService.executePersist(jedis -> jedis.smembers(String.format(DURATION_ACTIVE_LIVEROOM_SETKEY, timeKey)));
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

    public StatLiveroomEntity getStatLiveroom(long liveroomId){
        Map<String, String> liveOverViewMap = jedisService.executePersist(jedis -> jedis.hgetAll(String.format(STAT_LIVEROOM_HASHKEY, liveroomId)));
        if(MapUtils.isEmpty(liveOverViewMap)){
            return null;
        }
        StatLiveroomEntity statLiveroomEntity = new StatLiveroomEntity(liveroomId);
        if(!MapUtils.isEmpty(liveOverViewMap)){
            statLiveroomEntity.setLiveAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD), 0));
            statLiveroomEntity.setLiveWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD), 0));
            statLiveroomEntity.setLivePcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD), 0));
            statLiveroomEntity.setRobotVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD), 0));
            statLiveroomEntity.setPlaybackAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD), 0));
            statLiveroomEntity.setPlaybackWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD), 0));
            statLiveroomEntity.setPlaybackPcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD), 0));
            statLiveroomEntity.setTotalVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_TOTAL_VV_HASHFIELD), 0));
            statLiveroomEntity.setLikeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIKE_COUNT_HASHFIELD), 0));
            statLiveroomEntity.setCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_COMMENT_COUNT_HASHFIELD), 0));
            statLiveroomEntity.setRobotCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_ROBOT_COMMENT_COUNT_HASHFIELD), 0));
            statLiveroomEntity.setWechatPressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_WECHAT_PRESS_COUNT), 0));
            statLiveroomEntity.setPhonePressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PHONE_PRESS_COUNT), 0));
            statLiveroomEntity.setActivitySubscribeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_SUBSCRIBE_ACTIVITY_HASHFIELD), 0));
            statLiveroomEntity.setLiveTopAudienceCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_TOP_ONLINE_COUNT_HASHFILD), 0));
        }
        return statLiveroomEntity;
    }


    public StatLiveroomEntity getOldStatLiveroom(long liveroomId){
        Map<String, String> liveOverViewMap = jedisService.executePersist(jedis -> jedis.hgetAll(String.format(OLD_STAT_LIVEROOM_HASHKEY, liveroomId)));
        if(MapUtils.isEmpty(liveOverViewMap)){
            return null;
        }
        StatLiveroomEntity statLiveroomEntity = new StatLiveroomEntity(liveroomId);
        if(!MapUtils.isEmpty(liveOverViewMap)){
            statLiveroomEntity.setLiveAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD), 0));
            statLiveroomEntity.setLiveWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD), 0));
            statLiveroomEntity.setLivePcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD), 0));
            statLiveroomEntity.setRobotVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD), 0));
            statLiveroomEntity.setPlaybackAppVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD), 0));
            statLiveroomEntity.setPlaybackWapVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD), 0));
            statLiveroomEntity.setPlaybackPcVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD), 0));
            statLiveroomEntity.setTotalVv(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_TOTAL_VV_HASHFIELD), 0));
            statLiveroomEntity.setLikeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIKE_COUNT_HASHFIELD), 0));
            statLiveroomEntity.setCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_COMMENT_COUNT_HASHFIELD), 0));
            statLiveroomEntity.setRobotCommentCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_ROBOT_COMMENT_COUNT_HASHFIELD), 0));
            statLiveroomEntity.setWechatPressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_WECHAT_PRESS_COUNT), 0));
            statLiveroomEntity.setPhonePressCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PHONE_PRESS_COUNT), 0));
            statLiveroomEntity.setActivitySubscribeCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_SUBSCRIBE_ACTIVITY_HASHFIELD), 0));
            statLiveroomEntity.setLiveTopAudienceCount(NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_TOP_ONLINE_COUNT_HASHFILD), 0));
        }
        return statLiveroomEntity;
    }

    //刷新旧的缓存到新的
    public void refreshOldToNew(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        ids.forEach(o->{
            StatLiveroomEntity statLiveroomEntity =  getOldStatLiveroom(o);
            setStatLiveroom(statLiveroomEntity);
        });
    }

    public void setStatLiveroom(StatLiveroomEntity entity) {
        if(entity == null) {
            return;
        }
        setLiveAppVv(entity.getLiveroomId(), entity.getLiveAppVv());
        setLiveWapVv(entity.getLiveroomId(), entity.getLiveWapVv());
        setLivePcVv(entity.getLiveroomId(), entity.getLivePcVv());
        setPlaybackAppVv(entity.getLiveroomId(), entity.getPlaybackAppVv());
        setPlaybackWapVv(entity.getLiveroomId(), entity.getPlaybackWapVv());
        setPlaybackPcVv(entity.getLiveroomId(), entity.getPlaybackPcVv());
        setRobotVv(entity.getLiveroomId(), entity.getRobotVv());
        setTotalVv(entity.getLiveroomId(), entity.getTotalVv());
        setCommentCount(entity.getLiveroomId(), entity.getCommentCount());
        setRobotCommentCount(entity.getLiveroomId(), entity.getRobotCommentCount());
        setLikeCount(entity.getLiveroomId(), entity.getLikeCount());
        setWechatPressCount(entity.getLiveroomId(), entity.getWechatPressCount());
        setPhonePressCount(entity.getLiveroomId(), entity.getPhonePressCount());
        setLiveTopAudienceCount(entity.getLiveroomId(), entity.getLiveTopAudienceCount());
        setActivitySubscribeCount(entity.getLiveroomId(), entity.getActivitySubscribeCount());
    }

    /**
     * 在vv变化时重新计算totalVv
     * */
    public void sumResetTotalVv(long liveroomId) {
        Map<String, String> liveOverViewMap = jedisService.executePersist(jedis -> jedis.hgetAll(String.format(STAT_LIVEROOM_HASHKEY, liveroomId)));
        if(MapUtils.isEmpty(liveOverViewMap)){
            return;
        }
        int totalVv = NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_APP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_WAP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_PC_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_APP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_WAP_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_PLAYBACK_PC_VV_HASHFIELD), 0) +
                NumberUtils.toInt(liveOverViewMap.get(STAT_LIVEROOM_LIVE_ROBOT_VV_HASHFIELD), 0);
        setTotalVv(liveroomId, totalVv);
    }
}
