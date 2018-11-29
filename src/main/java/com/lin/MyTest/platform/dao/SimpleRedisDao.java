package com.lin.MyTest.platform.dao;

import com.lin.MyTest.platform.HashMapperFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class SimpleRedisDao {

	private RedisTemplate<String, ?> template;

	private HashOperations<String, String, Object> hashOps;

	private ZSetOperations<String, Number> zsetOps;

	private ListOperations<String, Object> listOps;

	private SetOperations<String, Object> setOps;

	@SuppressWarnings("unchecked")
	public SimpleRedisDao(RedisTemplate<String, ?> template) {
		this.template = template;
		this.hashOps = template.opsForHash();
		this.zsetOps = (ZSetOperations<String, Number>) template.opsForZSet();
		this.listOps = (ListOperations<String, Object>) template.opsForList();
		this.setOps = (SetOperations<String, Object>) template.opsForSet();
	}

	/**
	 * Delete the key from redis.
	 *
	 * @param key
	 */
	public void deleteKey(String key) {
		template.delete(key);
	}

	/**
	 * Delete the key from redis.
	 *
	 * @param keys
	 */
	public void deleteKeys(List<String> keys) {
		template.delete(keys);
	}

	/**
	 * Set the key an expire timeout.
	 *
	 * @param key
	 * @param timeout
	 */
	public void expireKey(String key, long timeout) {
		template.expire(key, timeout, TimeUnit.SECONDS);
	}

	/* ------------------------------------------------- */
	/* All methods below are used for Object operations. */

	/**
	 * Query an object by a key and return it as a map.
	 *
	 * @param key
	 * @return a map corresponding to the key, or null if the key doesn't exist
	 */
	public Map<String, Object> queryForMap(final String key) {
		Map<String, Object> result = hashOps.entries(key);
		return result.isEmpty() ? null : result;
	}

	/**
	 * Query an object by a key and return it as an object.
	 *
	 * @param key
	 * @param cls
	 * @return an object corresponding to the key, or null if the key doesn't
	 *         exist
	 */
	public <T> T queryForObject(String key, Class<T> cls) {
		return HashMapperFactory.getHashMapper(cls).fromHash(queryForMap(key));
	}

	/**
	 * Query a list of maps by a list of keys.
	 *
	 * @param keys
	 * @return a list of maps
	 */
	public List<Map<String, Object>> queryForListOfMaps(final List<String> keys) {
		final List<Object> results = new ArrayList<Object>();
		template.executePipelined(new RedisCallback<List<Object>>() {
			@SuppressWarnings("rawtypes")
			RedisSerializer keySerializer = template.getKeySerializer();

			@Override
			public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();
				for (String key : keys) {
					@SuppressWarnings("unchecked")
					byte[] rawKey = keySerializer.serialize(key);
					connection.hGetAll(rawKey);
				}
				results.addAll(connection.closePipeline());
				return null;
			}
		});

		@SuppressWarnings("rawtypes")
		RedisSerializer hashKeySerializer = template.getHashKeySerializer();
		@SuppressWarnings("rawtypes")
		RedisSerializer hashValueSerializer = template.getHashValueSerializer();

		List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>(results.size());
		for (Object result : results) {
			@SuppressWarnings("unchecked")
			Map<byte[], byte[]> rawResult = (Map<byte[], byte[]>) result;
			if (rawResult.isEmpty()) {
				resultMaps.add(null);
			} else {
				Map<String, Object> sResult = new HashMap<String, Object>();
				for (Entry<byte[], byte[]> entry : rawResult.entrySet()) {
					String key = (String) hashKeySerializer.deserialize(entry.getKey());
					Object value = hashValueSerializer.deserialize(entry.getValue());
					sResult.put(key, value);
				}
				resultMaps.add(sResult);
			}
		}
		return resultMaps;
	}

	/**
	 * Query a list of objects by a list of keys.
	 *
	 * @param keys
	 * @param cls
	 * @return a list of objects
	 */
	public <T> List<T> queryForListOfObjects(List<String> keys, Class<T> cls) {
		List<Map<String, Object>> resultMaps = queryForListOfMaps(keys);
		List<T> resultObjs = new ArrayList<T>(resultMaps.size());
		HashMapper<T, String, Object> mapper = HashMapperFactory.getHashMapper(cls);
		for (Map<String, Object> result : resultMaps) {
			resultObjs.add(mapper.fromHash(result));
		}
		return resultObjs;
	}

	/**
	 * Store the object with the key.
	 *
	 * @param key
	 * @param obj
	 */
	public <T> void storeObject(String key, T obj) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) obj.getClass();
		Map<String, Object> hash = HashMapperFactory.getHashMapper(cls).toHash(obj);
		hashOps.putAll(key, hash);
	}

	/* All methods above are used for Object operations. */
	/* ------------------------------------------------- */

	/* ------------------------------------------------- */
	/* All methods below are used for ID List operations. */

	public <T> void storeListOfObjects(String key, List<T> list) {
		for(T value : list) {
			listOps.rightPush(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Long> queryForListOfLongByKey(String key) {
		List<Object> range = listOps.range(key, 0, listOps.size(key));
		List<Long> resultObjs = new ArrayList<>(range.size());
		for(Object object : range) {
			resultObjs.add(((Number)object).longValue());
		}
		return resultObjs;
	}

	@SuppressWarnings("unchecked")
	public List<String> queryForListOfStringByKey(String key) {
		List<Object> range = listOps.range(key, 0, listOps.size(key));
		List<String> resultObjs = new ArrayList<>(range.size());
		for(Object object : range) {
			resultObjs.add((String) object);
		}
		return resultObjs;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> queryForListOfObjectsRangeByKey(String key, Class<T> cls, long start, long end) {
		List<Object> range = listOps.range(key, start, end);
		List<T> resultObjs = new ArrayList<>(range.size());
		for(Object object : range) {
			Map<String, Object> result = (Map<String, Object>) object;
			HashMapper<T, String, Object> mapper = HashMapperFactory.getHashMapper(cls);
			resultObjs.add(mapper.fromHash(result));
		}

		return resultObjs;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> queryForListOfObjectsByKey(String key, Class<T> cls) {
		return queryForListOfObjectsRangeByKey(key, cls, 0, listOps.size(key));
	}

	/* All methods above are used for ID List operations. */
	/* ------------------------------------------------- */

    public void storeHV(String key, String hk, Object hv) {
        hashOps.put(key, hk, hv);
    }

    public Object queryHV(String key, String hk) {
        return hashOps.get(key, hk);
    }

    public void deleteHV(String key, String hk) {
        hashOps.delete(key, hk);
    }

    public long incrementHV(String key,String hk,long count){
    	return hashOps.increment(key,hk,count);
	}

	public long addSet(String key,Object values){
    	return setOps.add(key,values);
	}

	public Set<Object> membersSet(String key){
		return setOps.members(key);
	}

}