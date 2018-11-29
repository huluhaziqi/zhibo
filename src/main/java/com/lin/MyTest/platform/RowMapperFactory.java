package com.lin.MyTest.platform;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RowMapperFactory {

	private static Map<Class<?>, RowMapper<?>> rowMappers = new ConcurrentHashMap<Class<?>, RowMapper<?>>();
	private static Object lock = new Object();

	@SuppressWarnings("unchecked")
	public static <T> RowMapper<T> getRowMapper(Class<T> cls) {
		if (cls == null) {
			throw new IllegalArgumentException();
		}

		RowMapper<T> rowMapper = null;
		if ((rowMapper = (RowMapper<T>) rowMappers.get(cls)) == null) {
			synchronized (lock) {
				if ((rowMapper = (RowMapper<T>) rowMappers.get(cls)) == null) {
					rowMapper = BeanPropertyRowMapper.newInstance(cls);
					rowMappers.put(cls, rowMapper);
				}
			}
		}
		return rowMapper;
	}

	public static void putRowMapper(Class<?> cls, RowMapper<?> rowMapper) {
		if (cls == null || rowMapper == null) {
			throw new IllegalArgumentException();
		}
		rowMappers.put(cls, rowMapper);
	}

	private static <T> RowMapper<T> primitivesRowMapper(final Class<T> primitiveClass) {
		return new RowMapper<T>() {
			@Override
			@SuppressWarnings("unchecked")
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Assert.state(columnCount == 1, "More than one columns need to be mapped.");
				return (T) JdbcUtils.getResultSetValue(rs, 1, primitiveClass);
			}
		};
	}

	static {
		Class<?>[] supportedPrimitives = new Class[] { String.class, boolean.class, Boolean.class,
				byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class,
				Long.class, float.class, Float.class, double.class, Double.class, byte[].class,
				java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
				java.math.BigDecimal.class, java.sql.Blob.class, java.sql.Clob.class };

		for (Class<?> clazz : supportedPrimitives) {
			putRowMapper(clazz, primitivesRowMapper(clazz));
		}
	}

}