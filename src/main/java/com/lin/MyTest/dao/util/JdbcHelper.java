package com.lin.MyTest.dao.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JdbcHelper {

    public static Map<String, Object> generateInsertParamMap(Object object) {
        Map<String, Object> paramMap = new HashMap<>();
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                paramMap.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return paramMap;
    }
}
