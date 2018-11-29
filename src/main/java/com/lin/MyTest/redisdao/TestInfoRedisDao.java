package com.lin.MyTest.redisdao;

import com.lin.MyTest.datasource.JedisService;
import com.lin.MyTest.model.biz.Test.TestInfo;
import com.lin.MyTest.util.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class TestInfoRedisDao {

    @Resource
    private JedisService jedisService;

    private static final String Test_INFO_HASHKEY = "Test:info:%s";

    public void setTestInfo(TestInfo TestInfo) {
        jedisService.executePersist(jedis -> jedis.set(String.format(Test_INFO_HASHKEY, TestInfo.getId()),
                JsonUtils.obj2JsonStr(TestInfo)));
    }

    public TestInfo getTestInfo(long TestId) {
        String value = jedisService.executePersist(jedis -> jedis.get(String.format(Test_INFO_HASHKEY, TestId)));
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            return JsonUtils.jsonStr2Obj(value, TestInfo.class);
        }
    }

    //批量读取
    public Set<TestInfo> getTestInfos(List<Long> TestIds) {
        Set<TestInfo> result = new HashSet<>();
        String[] keys = TestIds.stream().distinct().map(o -> String.format(Test_INFO_HASHKEY, o)).toArray(String[]::new);
        List<String> values = jedisService.executePersist(jedis -> jedis.mget(keys));
        values.forEach(o -> {
            if (StringUtils.isNotEmpty(o)) {
                TestInfo TestInfo = JsonUtils.jsonStr2Obj(o, TestInfo.class);
                result.add(TestInfo);
            }
        });
        return result;
    }

    public void setTestInfos(List<TestInfo> TestInfos){
        List<String> keyValueList = new ArrayList<>();
        TestInfos.forEach(o -> {
            keyValueList.add(String.format(Test_INFO_HASHKEY, o.getId()));
            keyValueList.add(JsonUtils.obj2JsonStr(o));
        });
        String[] strings = keyValueList.toArray(new String[0]);
        jedisService.executePersist(jedis -> jedis.mset(strings));
    }

    public void clearAll(){
        String pattern = "Test:info:*";
        Set<String> set = jedisService.executePersist(jedis -> jedis.keys(pattern));
        jedisService.executePersist(jedis -> jedis.del(set.stream().map(String::valueOf).toArray(String[]::new)));
    }

}
