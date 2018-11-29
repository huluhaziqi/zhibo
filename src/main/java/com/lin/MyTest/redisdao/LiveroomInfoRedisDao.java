package com.lin.MyTest.redisdao;

import com.lin.MyTest.datasource.JedisService;
import com.lin.MyTest.model.biz.liveroom.LiveroomInfo;
import com.lin.MyTest.util.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class LiveroomInfoRedisDao {

    @Resource
    private JedisService jedisService;

    private static final String LIVEROOM_INFO_HASHKEY = "liveroom:info:%s";

    public void setLiveroomInfo(LiveroomInfo liveroomInfo) {
        jedisService.executePersist(jedis -> jedis.set(String.format(LIVEROOM_INFO_HASHKEY, liveroomInfo.getId()),
                JsonUtils.obj2JsonStr(liveroomInfo)));
    }

    public LiveroomInfo getLiveroomInfo(long liveroomId) {
        String value = jedisService.executePersist(jedis -> jedis.get(String.format(LIVEROOM_INFO_HASHKEY, liveroomId)));
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            return JsonUtils.jsonStr2Obj(value, LiveroomInfo.class);
        }
    }

    //批量读取
    public Set<LiveroomInfo> getLiveroomInfos(List<Long> liveroomIds) {
        Set<LiveroomInfo> result = new HashSet<>();
        String[] keys = liveroomIds.stream().distinct().map(o -> String.format(LIVEROOM_INFO_HASHKEY, o)).toArray(String[]::new);
        List<String> values = jedisService.executePersist(jedis -> jedis.mget(keys));
        values.forEach(o -> {
            if (StringUtils.isNotEmpty(o)) {
                LiveroomInfo liveroomInfo = JsonUtils.jsonStr2Obj(o, LiveroomInfo.class);
                result.add(liveroomInfo);
            }
        });
        return result;
    }

    public void setLiveroomInfos(List<LiveroomInfo> liveroomInfos){
        List<String> keyValueList = new ArrayList<>();
        liveroomInfos.forEach(o -> {
            keyValueList.add(String.format(LIVEROOM_INFO_HASHKEY, o.getId()));
            keyValueList.add(JsonUtils.obj2JsonStr(o));
        });
        String[] strings = keyValueList.toArray(new String[0]);
        jedisService.executePersist(jedis -> jedis.mset(strings));
    }

    public void clearAll(){
        String pattern = "liveroom:info:*";
        Set<String> set = jedisService.executePersist(jedis -> jedis.keys(pattern));
        jedisService.executePersist(jedis -> jedis.del(set.stream().map(String::valueOf).toArray(String[]::new)));
    }

}
