package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.liveroom.LiveroomSearch;
import com.lin.MyTest.model.entity.LiveroomEntity;

import java.util.List;

public interface LiveroomSearchService {

    void refreshLiveroom(long liveroomId);

    void save(LiveroomEntity entity);

    void bulkSave(List<Long> liveroomIds);

    List<LiveroomSearch> findByPinyin(String key, Integer cityId);

    List<LiveroomSearch> findByChineseWithOthers(String key, Integer cityId);

    List<LiveroomSearch> findByChinese(String key, Integer cityId);

    List<LiveroomSearch> search(String key, Integer cityId, int size);

    List<LiveroomEntity> suggest(String key, Integer cityId, int size);

    void delete(long id);
}
