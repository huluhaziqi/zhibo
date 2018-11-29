package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.Test.TestSearch;
import com.lin.MyTest.model.entity.TestEntity;

import java.util.List;

public interface TestSearchService {

    void refreshTest(long TestId);

    void save(TestEntity entity);

    void bulkSave(List<Long> TestIds);

    List<TestSearch> findByPinyin(String key, Integer cityId);

    List<TestSearch> findByChineseWithOthers(String key, Integer cityId);

    List<TestSearch> findByChinese(String key, Integer cityId);

    List<TestSearch> search(String key, Integer cityId, int size);

    List<TestEntity> suggest(String key, Integer cityId, int size);

    void delete(long id);
}
