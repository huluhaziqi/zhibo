package com.lin.MyTest.redisdao;

import com.lin.MyTest.datasource.JedisService;
import com.lin.MyTest.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Repository
public class TestRedisDao {

    @Resource
    private JedisService jedisService;

    private static final String Test_CREATE_LIMIT_KEY_PREFIX = "Test:create-limit:%s";
    private static final int Test_CREATE_LIMIT_KEY_EXPIRES_SECONDS = 30;

    private static final String HOT_Test_LIST_OF_CITY = "Test:hot-list:city-%s";
    private static final String LABEL_Test_LIST_OF_CITY = "Test:label-list:city-%s:label-%s";
    private static final String PREVIEW_Test_OF_CITY = "Test:preview-list:city-%s";
    private static final String LABEL_Test_LIST = "Test:label-list:label-%s";
    private static final String HOT_Test_LIST_OF_CITY_LAST_REFRESH_TIME = "Test:hot-list:last-refresh:city-%s";
    private static final String LABEL_Test_LIST_OF_CITY_LAST_REFRESH_TIME = "Test:label-list:last-refresh:city-%s:label-%s";
    private static final String LABEL_Test_LIST_LAST_REFRESH_TIME = "Test:label-list:last-refresh:label-%s";

    private static final String Test_CHATROOM_BIND = "Test:chatroom-bind";
    private static final String Test_HOST_BIND = "Test:host-bind";
    private static final String Test_HERO_BIND = "Test:hero-bind";

    /**
     * 创建xxxx频率限制
     */
    public void setUserCreateLimitRedis(long userId) {
        jedisService.executePersist(jedis -> jedis.setex(String.format(Test_CREATE_LIMIT_KEY_PREFIX, userId), Test_CREATE_LIMIT_KEY_EXPIRES_SECONDS, "1"));
    }

    public String getUserCreateLimitRedis(long userId) {
        return jedisService.executePersist(jedis -> jedis.get(String.format(Test_CREATE_LIMIT_KEY_PREFIX, userId)));
    }

    public void removeUserCreateLimitRedis(long userId) {
        jedisService.delKey(String.format(Test_CREATE_LIMIT_KEY_PREFIX, userId));
    }

    /**
     * 城市推荐列表
     */
    public void setCityHotListRedis(int cityId, String[] Tests) {
        jedisService.executePersist(jedis -> jedis.rpush(String.format(HOT_Test_LIST_OF_CITY, cityId), Tests));
    }

    public List<String> getCityHotListRedis(int cityId, long start, long end) {
        return jedisService.executePersist(jedis -> jedis.lrange(String.format(HOT_Test_LIST_OF_CITY, cityId), start, end));
    }

    public long getCityHotListCountRedis(int cityId) {
        return jedisService.executePersist(jedis -> jedis.llen(String.format(HOT_Test_LIST_OF_CITY, cityId)));
    }

    public Boolean checkCityHotListExists(int cityId) {
        return jedisService.executePersist(jedis -> jedis.exists(String.format(HOT_Test_LIST_OF_CITY, cityId)));
    }

    public void removeCityHotListRedis(int cityId) {
        jedisService.executePersist(jedis -> jedis.del(String.format(HOT_Test_LIST_OF_CITY, cityId)));
    }

    //set缓存最后刷新时xx
    public void setCityHotListRefreshTimeRedis(int cityId, Date date) {
        String key = TimeUtils.getSecondKey(date);
        jedisService.executePersist(jedis -> jedis.set(String.format(HOT_Test_LIST_OF_CITY_LAST_REFRESH_TIME, cityId), key));
    }

    //get缓存最后刷新时xx
    public Date getCityHotListRefreshTimeRedis(int cityId) {
        String value = jedisService.executePersist(jedis -> jedis.get(String.format(HOT_Test_LIST_OF_CITY_LAST_REFRESH_TIME, cityId)));
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            try {
                return TimeUtils.getSimpleDateFormate("yyyy-MM-dd HH:mm:ss").parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                return null;
            }
        }
    }

    /**
     * 城市频道列表
     */
    public void setCityLabelList(Integer channelId, Integer cityId, String[] Tests) {
        if (cityId != null) {
            jedisService.executePersist(jedis -> jedis.rpush(String.format(LABEL_Test_LIST_OF_CITY, cityId, channelId), Tests));
        } else {
            jedisService.executePersist(jedis -> jedis.rpush(String.format(LABEL_Test_LIST, channelId), Tests));
        }
    }

    public List<String> getCityLabelListRedis(Integer channelId, Integer cityId, long start, long end) {
        if (cityId != null) {
            return jedisService.executePersist(jedis -> jedis.lrange(String.format(LABEL_Test_LIST_OF_CITY, cityId, channelId), start, end));
        } else {
            return jedisService.executePersist(jedis -> jedis.lrange(String.format(LABEL_Test_LIST, channelId), start, end));
        }
    }

    public long getCityLabelListCountRedis(Integer channelId, Integer cityId) {
        if (cityId != null) {
            return jedisService.executePersist(jedis -> jedis.llen(String.format(LABEL_Test_LIST_OF_CITY, cityId, channelId)));
        } else {
            return jedisService.executePersist(jedis -> jedis.llen(String.format(LABEL_Test_LIST, channelId)));
        }
    }

    public Boolean checkCityLabelListExists(Integer channelId, Integer cityId) {
        if (cityId != null) {
            return jedisService.executePersist(jedis -> jedis.exists(String.format(LABEL_Test_LIST_OF_CITY, cityId, channelId)));
        } else {
            return jedisService.executePersist(jedis -> jedis.exists(String.format(LABEL_Test_LIST, channelId)));
        }
    }

    public void removeCityLabelList(Integer channelId, Integer cityId) {
        if (cityId != null) {
            jedisService.executePersist(jedis -> jedis.del(String.format(LABEL_Test_LIST_OF_CITY, cityId, channelId)));
        } else {
            jedisService.executePersist(jedis -> jedis.del(String.format(LABEL_Test_LIST, channelId)));
        }
    }

    public void setLabelListRefreshTimeRedis(Integer channelId, Integer cityId, Date date) {
        //全国属性cityId == null
        if (cityId != null) {
            jedisService.executePersist(jedis -> jedis.set(String.format(LABEL_Test_LIST_OF_CITY_LAST_REFRESH_TIME, cityId, channelId),
                    TimeUtils.getSecondKey(date)));
        } else {
            jedisService.executePersist(jedis -> jedis.set(String.format(LABEL_Test_LIST_LAST_REFRESH_TIME, channelId),
                    TimeUtils.getSecondKey(date)));
        }
    }

    public Date getLabelListRefreshTimeRedis(Integer channelId, Integer cityId) {
        //全国属性cityId == null
        String value;
        if (cityId != null) {
            value = jedisService.executePersist(jedis -> jedis.get(String.format(LABEL_Test_LIST_OF_CITY_LAST_REFRESH_TIME, cityId, channelId)));
        } else {
            value = jedisService.executePersist(jedis -> jedis.get(String.format(LABEL_Test_LIST_LAST_REFRESH_TIME, channelId)));
        }

        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            return TimeUtils.parseSecondKey(value);
        }
    }

    /**
     * 城市预告列表
     */
    public void setCityPreviewList(Integer cityId, String[] Tests) {
        jedisService.executePersist(jedis -> jedis.rpush(String.format(PREVIEW_Test_OF_CITY, cityId), Tests));
    }

    public List<String> getCityPreviewListRedis(Integer cityId, long start, long end) {
        return jedisService.executePersist(jedis -> jedis.lrange(String.format(PREVIEW_Test_OF_CITY, cityId), start, end));
    }

    public long getCityPreviewListCountRedis(Integer cityId) {
        return jedisService.executePersist(jedis -> jedis.llen(String.format(PREVIEW_Test_OF_CITY, cityId)));
    }

    public Boolean checkCityPreviewListExists(Integer cityId) {
        return jedisService.executePersist(jedis -> jedis.exists(String.format(PREVIEW_Test_OF_CITY, cityId)));
    }

    public void removeCityPreviewList(Integer cityId) {
        jedisService.executePersist(jedis -> jedis.del(String.format(PREVIEW_Test_OF_CITY, cityId)));
    }

    /**
     * xxxx-聊天室关系
     */
    public void setTestChatroomBind(long TestId, String chatroomId) {
        jedisService.executePersist(jedis -> jedis.hset(Test_CHATROOM_BIND, chatroomId, String.valueOf(TestId)));
    }

    public Long getTestChatroomBind(String chatroomId) {
        String value = jedisService.executePersist(jedis -> jedis.hget(Test_CHATROOM_BIND, chatroomId));
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            return Long.valueOf(value);
        }
    }

    /**
     * xxxx-主播关系
     */
    public void setTestHostBind(long TestId, long hostId) {
        jedisService.executePersist(jedis -> jedis.hset(Test_HOST_BIND, String.valueOf(TestId), String.valueOf(hostId)));
    }

    public Long getTestHostBind(long TestId) {
        String value = jedisService.executePersist(jedis -> jedis.hget(Test_HOST_BIND, String.valueOf(TestId)));
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            return Long.valueOf(value);
        }
    }

}
