package com.lin.MyTest.platform;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.hash.HashMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapperFactory {

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private static final Map<Class<?>, HashMapper<?, String, Object>> hashMappers = new ConcurrentHashMap<>();
	private static final Object lock = new Object();

	@SuppressWarnings("unchecked")
	public static <T> HashMapper<T, String, Object> getHashMapper(final Class<T> cls) {
		if (cls == null) {
			throw new IllegalArgumentException();
		}

		HashMapper<T, String, Object> mapper = null;
		if ((mapper = (HashMapper<T, String, Object>) hashMappers.get(cls)) == null) {
			synchronized (lock) {
				if ((mapper = (HashMapper<T, String, Object>) hashMappers.get(cls)) == null) {
					mapper = new HashMapper<T, String, Object>() {
						@Override
						public Map<String, Object> toHash(T obj) {
							return obj == null ? null : objectMapper.convertValue(obj, Map.class);
						}

						@Override
						public T fromHash(Map<String, Object> hash) {
							return hash == null ? null : objectMapper.convertValue(hash, cls);
						}
					};
					hashMappers.put(cls, mapper);
				}
			}
		}
		return mapper;
	}

	public static void putMapper(Class<?> cls, HashMapper<?, String, Object> mapper) {
		if (cls == null || mapper == null) {
			throw new IllegalArgumentException();
		}
		hashMappers.put(cls, mapper);
	}

}
