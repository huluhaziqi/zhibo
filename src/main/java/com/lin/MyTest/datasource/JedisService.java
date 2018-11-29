package com.lin.MyTest.datasource;

import com.lin.MyTest.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Calendar;

@Service
public class JedisService {

    @Value("${redis.persistDb}")
    private  int PERSIST_DB_INDEX;
    
    @Value("${redis.cacheDb}")
    private  int CACHE_DB_INDEX;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    
    private static final Logger logger = LoggerFactory.getLogger(JedisService.class);

    private Jedis getPersistJedis() {
        RedisConnection jedisConnection = jedisConnectionFactory.getConnection();
        jedisConnection.select(PERSIST_DB_INDEX);
        return (Jedis) jedisConnection.getNativeConnection();
    }

    private Jedis getCacheJedis() {
        RedisConnection jedisConnection = jedisConnectionFactory.getConnection();
        jedisConnection.select(CACHE_DB_INDEX);
        return (Jedis) jedisConnection.getNativeConnection();
    }

    public String getStr(String key) {
        Jedis jedis = null;
        try {
            jedis = getPersistJedis();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
//            throw new GeneralErrorException(GeneralExceptionEnum.KVSTORE_ERROR);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public int getIntKey(final String key) {
        return executePersist(jedis -> {
            String result = jedis.get(key);
            return result == null ? 0 : Integer.parseInt(result);
        });
    }


    public void increaseKeyAsync(final String key, final int cnt, final Integer expire) {
        executePersistAsync(jedis -> {
            jedis.incrBy(key, cnt);
            if (expire != null) {
                jedis.expire(key, expire);
            }
            return null;
        });
    }

    public void putKeyAsync(final String key, final int value, final Integer expire) {
        executePersistAsync(jedis -> {
            jedis.set(key, value + "");
            if (expire != null) {
                jedis.expire(key, expire);
            }
            return null;
        });
    }

    public void delKey(String key) {
        Jedis jedis = null;
        try {
            jedis = getPersistJedis();
            Calendar now = TimeUtils.getNow();
            jedis.pexpireAt(key, now.getTimeInMillis());
        } catch (Exception e) {
            logger.error("redis异常  , {} ",e);
//            throw new GeneralErrorException(GeneralExceptionEnum.KVSTORE_ERROR);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> T executePersist(BusinessLogic<T> logic) {
        Jedis jedis = null;
        try {
            jedis = getPersistJedis();
            return logic.doBusiness(jedis);
        } catch (Exception e) {
            logger.error("redis异常  , {} ",e);
            return null;
//            throw new GeneralErrorException(GeneralExceptionEnum.KVSTORE_ERROR);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> T executeCache(BusinessLogic<T> logic) {
        Jedis jedis = null;
        try {
            jedis = getCacheJedis();
            return logic.doBusiness(jedis);
        } catch (Exception e) {
            logger.error("redis异常  , {} ",e);
            return null;
//            throw new GeneralErrorException(GeneralExceptionEnum.KVSTORE_ERROR);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

	public void executePersistAsync(final BusinessLogic<?> logic) {
        new Thread(() -> executePersist(logic)).start();
    }

	public void executeCacheAsync(final BusinessLogic<?> logic) {
        new Thread(() -> executeCache(logic)).start();
    }

    public interface BusinessLogic<T> {
        T doBusiness(Jedis jedis);
    }
}
