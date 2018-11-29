package com.lin.MyTest.dao.impl.jdbc;

import com.lin.MyTest.dao.LiveroomDao;
import com.lin.MyTest.model.entity.LiveroomEntity;
import com.lin.MyTest.platform.dao.SimpleJdbcDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class LiveroomJdbcDao implements LiveroomDao {

    @Resource
    private SimpleJdbcDao jdbcDao;


    @Override
    public long insert(LiveroomEntity liveroomEntity) {
        Map<String, Object> paramsMap = getMap(liveroomEntity);
        return jdbcDao.update(INSERT, paramsMap);
    }

    @Override
    public LiveroomEntity get(long id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcDao.queryForObject(GET, paramMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> gets(List<Long> liveroomIds) {
        if (CollectionUtils.isEmpty(liveroomIds)) {
            return new ArrayList<>();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("idList", liveroomIds);
        return jdbcDao.queryForListOfObjects(GETS, paramMap, LiveroomEntity.class);
    }

    @Override
    public int updateByPrimaryKey(LiveroomEntity record) {
        Map<String, Object> paramsMap = getMap(record);
        return jdbcDao.update(UPDATE, paramsMap);
    }

    @Override
    public List<LiveroomEntity> getAllRecord() {
        Map<String, Object> paramsMap = new HashMap<>();
        return jdbcDao.queryForListOfObjects(ALL_RECORD, paramsMap, LiveroomEntity.class);
    }


    @Override
    public List<LiveroomEntity> getByHost(long hostId, int offset, int limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        return jdbcDao.queryForListOfObjects(GET_ENTITIES_BY_HOST, paramsMap, LiveroomEntity.class);
    }

    @Override
    public int getCountByHost(long hostId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        return jdbcDao.queryForObject(GET_COUNT_BY_HOST, paramsMap, Integer.class);

    }

    @Override
    public LiveroomEntity getByChatroomId(String chatroomId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("chatroomId", chatroomId);
        return jdbcDao.queryForObject(GET_BY_CHATROOMID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getHostShowByHost(Byte videoType, long hostId, int offset, int limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("hostId", hostId);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        return jdbcDao.queryForListOfObjects(GET_HOST_SHOW_BY_HOST, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPublishList(long hostId, int offset, int limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        return jdbcDao.queryForListOfObjects(GET_PUBLISH_LIST, paramsMap, LiveroomEntity.class);
    }

    @Override
    public int getPublishListCount(long hostId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        return jdbcDao.queryForObject(GET_PUBLISH_LIST_COUNT, paramsMap, Integer.class);
    }



    @Override
    public int getHostShowCountByHost(Byte videoType, long hostId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("hostId", hostId);
        return jdbcDao.queryForObject(GET_HOST_SHOW_COUNT_BY_HOST, paramsMap, Integer.class);
    }

    @Override
    public int getHostUploadCountByHost(Byte videoType, long hostId, String statusList) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("statusList", statusList);
        paramsMap.put("hostId", hostId);
        return jdbcDao.queryForObject(GET_HOST_UPLOAD_COUNT_BY_HOST, paramsMap, Integer.class);
    }

    @Override
    public Long getOnLiveIdByHostId(long hostId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        return jdbcDao.queryForObject(GET_ON_LIVE_ID_BY_HOSTID, paramsMap, Long.class);
    }


    @Override
    public List<LiveroomEntity> getRelatedByBuildingId(long buildingId, int offset, int limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("buildingId", buildingId);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        return jdbcDao.queryForListOfObjects(GET_RELATED_BY_BUILDING_ID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getOnShowByBuildingId(long buildingId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("buildingId", buildingId);
        return jdbcDao.queryForListOfObjects(GET_ONSHOW_BY_BUILDING_ID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public int getRelatedCountByBuildingId(long buildingId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("buildingId", buildingId);
        return jdbcDao.queryForObject(GET_RELATED_COUNT_BY_BUILDING_ID, paramsMap, Integer.class);
    }

    @Override
    public List<LiveroomEntity> getScheduledByBuildingId(long buildingId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("buildingId", buildingId);
        return jdbcDao.queryForListOfObjects(GET_SCHEDULED_BY_BUILDING_ID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getAllUpcomingByCityId(int cityId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", cityId);
        return jdbcDao.queryForListOfObjects(GET_ALL_UPCOMING_BY_CITY_ID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getGlobalUpcoming(String liveTagIds) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", liveTagIds);
        return jdbcDao.queryForListOfObjects(GET_GLOBAL_UPCOMING, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getScheduleTimeoutLiverooms() {
        Map<String, Object> paramsMap = new HashMap<>();
        return jdbcDao.queryForListOfObjects(GET_SCHEDULED_TIMEOUT_LIVEROOMS, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> searchLiveroomForAdmin(Byte videoType, Byte status, String cityString, Long hostId,
                                                       String hostName, Long id, String title, Integer tagId,
                                                       Integer offset, Integer limit, Date startTime, Date endTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("status", status);
        paramsMap.put("cityString", cityString);
        paramsMap.put("hostId", hostId);
        paramsMap.put("hostName", hostName);
        paramsMap.put("id", id);
        paramsMap.put("title", title);
        paramsMap.put("tagId", tagId);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        return jdbcDao.queryForListOfObjects(getSqlSearchLiveroomForAdmin(paramsMap), new HashMap<>(), LiveroomEntity.class);
    }

    @Override
    public int searchLiveroomForAdminCount(Byte videoType, Byte status, String cityString, Long hostId,
                                           String hostName, Long id, String title, Integer tagId, Date startTime, Date endTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("status", status);
        paramsMap.put("cityString", cityString);
        paramsMap.put("hostId", hostId);
        paramsMap.put("hostName", hostName);
        paramsMap.put("id", id);
        paramsMap.put("title", title);
        paramsMap.put("tagId", tagId);
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        return jdbcDao.queryForObject(getSqlSearchLiveroomForAdminCount(paramsMap), new HashMap<>(), Integer.class);
    }

    @Override
    public List<LiveroomEntity> searchLiveroomList(Byte videoType, Long hostId, Byte status, String q, Integer offset, Integer limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("status", status);
        paramsMap.put("hostId", hostId);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        paramsMap.put("title", q);
        return jdbcDao.queryForListOfObjects(getSqlSearchLiveroomList(paramsMap), new HashMap<>(), LiveroomEntity.class);
    }

    @Override
    public int searchLiveroomCount(Byte videoType, Long hostId, Byte status, String q) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("videoType", videoType);
        paramsMap.put("status", status);
        paramsMap.put("hostId", hostId);
        paramsMap.put("title", q);
        return jdbcDao.queryForObject(getSqlSearchLiveroomList(paramsMap), new HashMap<>(), Integer.class);
    }

    @Override
    public void resetLiveroomInterruptTime(long liveroomId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("liveroomId", liveroomId);
        jdbcDao.update(RESET_LIVEROOM_INTERRUPT_TIME, paramsMap);
    }

    @Override
    public List<LiveroomEntity> getByStatusByPaging(int status, int offset, int limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("status", status);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        return jdbcDao.queryForListOfObjects(GET_BY_STATUS_AND_PAGING, paramsMap, LiveroomEntity.class);
    }

    @Override
    public int getLiveroomCreateCountByStartTime(Date startTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("startTime", startTime);
        return jdbcDao.queryForObject(GET_LIVEROOM_CREATE_COUNT_BY_START_TIME, paramsMap, Integer.class);
    }

    @Override
    public int getLiveroomCount() {
        return jdbcDao.queryForObject(GET_LIVEROOM_COUNT, new HashMap<>(), Integer.class);
    }

    @Override
    public List<LiveroomEntity> getOnLiveByCity(int cityId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", cityId);
        return jdbcDao.queryForListOfObjects(GET_ON_LIVE_BY_CITYID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getOnLiveByCityAndTag(String liveTagIds, int cityId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", cityId);
        paramsMap.put("liveTagIds", liveTagIds);
        return jdbcDao.queryForListOfObjects(GET_ON_LIVE_BY_CITY_AND_TAG, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getOnLiveByTag(String liveTagIds) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("liveTagIds", liveTagIds);
        return jdbcDao.queryForListOfObjects(GET_ON_LIVE_BY_TAG, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPlayBackByCity(int cityId, Date startTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", cityId);
        paramsMap.put("createTime", startTime);
        return jdbcDao.queryForListOfObjects(GET_PLAYBACK_BY_CITY, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPlayBackByCityAndTagIds(String tagIds, int cityId, Date startTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tagIds", tagIds);
        paramsMap.put("cityId", cityId);
        paramsMap.put("createTime", startTime);
        return jdbcDao.queryForListOfObjects(GET_PLAYBACK_BY_CITY_AND_TAG, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPlayBackByTag(String tagIds, Date startTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tagIds", tagIds);
        paramsMap.put("createTime", startTime);
        return jdbcDao.queryForListOfObjects(GET_PLAYBACK_BY_TAG, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPlayBackByCityOrderByCreateTimeAndTag(String tagIds, int cityId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tagIds", tagIds);
        paramsMap.put("cityId", cityId);
        return jdbcDao.queryForListOfObjects(GET_PLAYBACK_BY_CITY_ORDER_BY_CREATE_TIME_AND_TAG, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPlayBackByCreateTimeAndTag(String tagIds) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tagIds", tagIds);
        return jdbcDao.queryForListOfObjects(GET_PLAYBACK_BY_CREATE_TIME_AND_TAG, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getPlayBackByCityOrderByCreateTime(int cityId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", cityId);
        return jdbcDao.queryForListOfObjects(GET_PLAYBACK_BY_CITY_ORDER_BY_CREATE_TIME, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getOnLiveAndCloseByHostIdAndStartTime(long hostId, Date startTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        paramsMap.put("createTime", startTime);
        return jdbcDao.queryForListOfObjects(GET_ONLIVE_AND_CLOSE_BY_HOSTID_AND_STARTTIME, paramsMap, LiveroomEntity.class);
    }

    @Override
    public int getOnLiveAndCloseCountByHostIdAndStartTime(long hostId, Date startTime) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("hostId", hostId);
        paramsMap.put("createTime", startTime);
        return jdbcDao.queryForObject(GET_ONLIVE_AND_CLOSE_COUNT_BY_HOSTID_AND_STARTTIME, paramsMap, Integer.class);
    }

    @Override
    public List<LiveroomEntity> getByHostIdOrderByStatus(String statusList, long uid, int offset, int limit) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("statusList", statusList);
        paramsMap.put("hostId", uid);
        paramsMap.put("offset", offset);
        paramsMap.put("limit", limit);
        return jdbcDao.queryForListOfObjects(GET_BY_HOSTID_ORDER_BY_STATUS, paramsMap, LiveroomEntity.class);
    }

    @Override
    public int getCountByHostIdAndStatus(String statusList, long uid) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("statusList", statusList);
        paramsMap.put("hostId", uid);
        return jdbcDao.queryForObject(GET_COUNT_BY_HOSTID_AND_STATUS, paramsMap, Integer.class);
    }

    @Override
    public List<LiveroomEntity> getHottestOnShowByBuildingId(int buildingId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("buildingId", buildingId);
        return jdbcDao.queryForListOfObjects(GET_HOSTTEST_ONSHOW_BY_BUILDING_ID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public Long getHottestPlayBackByBuildingId(int buildingId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("buildingId", buildingId);
        return jdbcDao.queryForObject(GET_HOSTTEST_PLAYBACK_BY_BUILDING_ID, paramsMap, Long.class);
    }

    @Override
    public Long getLiveroomIdByTopicId(long topicId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("topicId", topicId);
        return jdbcDao.queryForObject(GET_BY_TOPIC_ID, paramsMap, Long.class);
    }

    @Override
    public List<LiveroomEntity> getByCityAndStatusOrderByAudience(int status, Integer cityId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("status", status);
        paramsMap.put("cityId", cityId);
        return jdbcDao.queryForListOfObjects(GET_BY_CITY_AND_STATUS_ORDER_BY_ID, paramsMap, LiveroomEntity.class);
    }

    @Override
    public List<LiveroomEntity> getByCityAndStatusAndTime(int status, Integer cityId, String time) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("status", status);
        paramsMap.put("cityId", cityId);
        paramsMap.put("time", time);
        return jdbcDao.queryForListOfObjects(GET_BY_CITY_AND_STATUS_AND_TIME, paramsMap, LiveroomEntity.class);
    }

    @Override
    public Map<BigInteger, Map<String, Object>> getUserValidLiveroomCount(String userIds) {

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userIds", userIds);
        List<Map<String, Object>> maps = jdbcDao.queryForListOfMaps(GET_USER_VALID_LIVEROOM_COUNT, paramsMap);
        Map<BigInteger, Map<String, Object>> result;
        result = maps.stream().collect(Collectors.toMap(o -> (BigInteger) o.get("userId"), o -> o));
        return result;
    }

    @Override
    public void modifyLiveroomByCityId(int cityId, long liveroomId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("cityId", cityId);
        paramsMap.put("liveroomId", liveroomId);
        jdbcDao.update(MODIFY_LIVEROOM_BY_CITY_ID, paramsMap);
    }

    @Override
    public int countForStat(Integer cityId, Date startTime, Date endTime) {
        return 0;
    }



    private String getSqlSearchLiveroomForAdmin(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select l.* from liveroom as l ");

        if (!CollectionUtils.isEmpty(paramsMap)) {

            Long hostId = (Long) paramsMap.get("hostId");
            String hostName = (String) paramsMap.get("hostName");

            if (hostId != null || hostName != null) {
                stringBuffer.append(" join user as u on l.host_id = u.id ");
            }

            Integer tagId = (Integer) paramsMap.get("tagId");

            if (tagId != null) {
                stringBuffer.append(" join liveroom_tag as lt on l.id = lt.liveroom_id ");
            }
            stringBuffer.append(" where ");

            Long id = (Long) paramsMap.get("id");
            Byte videoType = (Byte) paramsMap.get("videoType");
            Byte status = (Byte) paramsMap.get("status");
            String cityString = (String) paramsMap.get("cityString");
            String title = (String) paramsMap.get("title");
            Date startTime = (Date) paramsMap.get("startTime");
            Date endTime = (Date) paramsMap.get("endTime");

            boolean first = true;
            if (id != null) {
                if (first) {
                    stringBuffer.append("l.id = :id");
                    first = false;
                } else {
                    stringBuffer.append("and l.id = :id");
                }
            }
            if (videoType != null) {
                if (first) {
                    stringBuffer.append("l.video_type = :videoType");
                    first = false;
                } else {
                    stringBuffer.append("and l.video_type = :videoType");
                }
            }
            if (status != null) {
                if (first) {
                    stringBuffer.append("l.status = :status");
                    first = false;
                } else {
                    stringBuffer.append("and l.status = :status");
                }
            }
            if (cityString != null) {
                if (first) {
                    stringBuffer.append("l.city_id = :cityString");
                    first = false;
                } else {
                    stringBuffer.append("and l.city_id = :cityString");
                }
            }
            if (hostId != null) {
                if (first) {
                    stringBuffer.append("u.host_id = :hostId");
                    first = false;
                } else {
                    stringBuffer.append("and u.host_id = :hostId");
                }
            }
            if (hostName != null) {
                if (first) {
                    stringBuffer.append("u.host_name = :hostName");
                    first = false;
                } else {
                    stringBuffer.append("and u.host_id = :hostId");
                }
            }
            if (title != null) {
                if (first) {
                    stringBuffer.append("l.title regexp :title");
                    first = false;
                } else {
                    stringBuffer.append("and l.title regexp :title");
                }
            }
            if (tagId != null) {
                if (first) {
                    stringBuffer.append(" lt.live_tag_id = :tagId and lt.status = 1 ");
                    first = false;
                } else {
                    stringBuffer.append("and lt.live_tag_id = :tagId and lt.status = 1 ");
                }
            }
            if (startTime != null && endTime != null) {
                if (first) {
                    stringBuffer.append("l.create_time between :startTime and :endTime");
                    first = false;
                } else {
                    stringBuffer.append("and l.create_time between :startTime and :endTime");
                }
            }
        }
        stringBuffer.append(" order by l.id desc ");
        Integer offset = (Integer) paramsMap.get("offset");
        Integer limit = (Integer) paramsMap.get("limit");
        if (offset != null && limit != null) {
            stringBuffer.append(" limit :offset, :limit");
        }
        return stringBuffer.toString();
    }

    private String getSqlSearchLiveroomForAdminCount(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select count(1) from liveroom as l ");

        if (!CollectionUtils.isEmpty(paramsMap)) {

            Long hostId = (Long) paramsMap.get("hostId");
            String hostName = (String) paramsMap.get("hostName");

            if (hostId != null || hostName != null) {
                stringBuffer.append(" join user as u on l.host_id = u.id ");
            }

            Integer tagId = (Integer) paramsMap.get("tagId");

            if (tagId != null) {
                stringBuffer.append(" join liveroom_tag as lt on l.id = lt.liveroom_id ");
            }
            stringBuffer.append(" where ");

            Long id = (Long) paramsMap.get("id");
            Byte videoType = (Byte) paramsMap.get("videoType");
            Byte status = (Byte) paramsMap.get("status");
            String cityString = (String) paramsMap.get("cityString");
            String title = (String) paramsMap.get("title");
            Date startTime = (Date) paramsMap.get("startTime");
            Date endTime = (Date) paramsMap.get("endTime");

            boolean first = true;
            if (id != null) {
                if (first) {
                    stringBuffer.append("l.id = :id");
                    first = false;
                } else {
                    stringBuffer.append("and l.id = :id");
                }
            }
            if (videoType != null) {
                if (first) {
                    stringBuffer.append("l.video_type = :videoType");
                    first = false;
                } else {
                    stringBuffer.append("and l.video_type = :videoType");
                }
            }
            if (status != null) {
                if (first) {
                    stringBuffer.append("l.status = :status");
                    first = false;
                } else {
                    stringBuffer.append("and l.status = :status");
                }
            }
            if (cityString != null) {
                if (first) {
                    stringBuffer.append("l.city_id = :cityString");
                    first = false;
                } else {
                    stringBuffer.append("and l.city_id = :cityString");
                }
            }
            if (hostId != null) {
                if (first) {
                    stringBuffer.append("u.host_id = :hostId");
                    first = false;
                } else {
                    stringBuffer.append("and u.host_id = :hostId");
                }
            }
            if (hostName != null) {
                if (first) {
                    stringBuffer.append("u.host_name = :hostName");
                    first = false;
                } else {
                    stringBuffer.append("and u.host_id = :hostId");
                }
            }
            if (title != null) {
                if (first) {
                    stringBuffer.append("l.title regexp :title");
                    first = false;
                } else {
                    stringBuffer.append("and l.title regexp :title");
                }
            }
            if (tagId != null) {
                if (first) {
                    stringBuffer.append(" lt.live_tag_id = :tagId and lt.status = 1 ");
                    first = false;
                } else {
                    stringBuffer.append("and lt.live_tag_id = :tagId and lt.status = 1 ");
                }
            }
            if (startTime != null && endTime != null) {
                if (first) {
                    stringBuffer.append("l.create_time between :startTime and :endTime");
                    first = false;
                } else {
                    stringBuffer.append("and l.create_time between :startTime and :endTime");
                }
            }
        }
        return stringBuffer.toString();
    }

    private String getSqlSearchLiveroomList(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select * from liveroom ");

        if (!CollectionUtils.isEmpty(paramsMap)) {
            stringBuffer.append(" where ");

            Byte videoType = (Byte) paramsMap.get("videoType");
            Byte status = (Byte) paramsMap.get("status");
            String title = (String) paramsMap.get("title");
            Long hostId = (Long) paramsMap.get("hostId");
            Integer offset = (Integer) paramsMap.get("offset");
            Integer limit = (Integer) paramsMap.get("limit");

            boolean first = true;
            if (videoType != null) {
                if (first) {
                    stringBuffer.append("video_type = :videoType");
                    first = false;
                } else {
                    stringBuffer.append("and video_type = :videoType");
                }
            }
            if (status != null) {
                if (first) {
                    stringBuffer.append("status = :status");
                    first = false;
                } else {
                    stringBuffer.append("and status = :status");
                }
            }
            if (hostId != null) {
                if (first) {
                    stringBuffer.append("host_id = :hostId");
                    first = false;
                } else {
                    stringBuffer.append("and host_id = :hostId");
                }
            }
            if (title != null) {
                if (first) {
                    stringBuffer.append("title regexp :title");
                    first = false;
                } else {
                    stringBuffer.append("and title regexp :title");
                }
            }
        }
        stringBuffer.append(" order by id desc ");
        Integer offset = (Integer) paramsMap.get("offset");
        Integer limit = (Integer) paramsMap.get("limit");
        if (offset != null && limit != null) {
            stringBuffer.append(" limit :offset, :limit");
        }
        return stringBuffer.toString();
    }

    private String getSqlSearchLiveroomCount(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select count(1) from liveroom ");

        if (!CollectionUtils.isEmpty(paramsMap)) {
            stringBuffer.append(" where ");

            Byte videoType = (Byte) paramsMap.get("videoType");
            Byte status = (Byte) paramsMap.get("status");
            String title = (String) paramsMap.get("title");
            Long hostId = (Long) paramsMap.get("hostId");
            Integer offset = (Integer) paramsMap.get("offset");
            Integer limit = (Integer) paramsMap.get("limit");

            boolean first = true;
            if (videoType != null) {
                if (first) {
                    stringBuffer.append("video_type = :videoType");
                    first = false;
                } else {
                    stringBuffer.append("and video_type = :videoType");
                }
            }
            if (status != null) {
                if (first) {
                    stringBuffer.append("status = :status");
                    first = false;
                } else {
                    stringBuffer.append("and status = :status");
                }
            }
            if (hostId != null) {
                if (first) {
                    stringBuffer.append("host_id = :hostId");
                    first = false;
                } else {
                    stringBuffer.append("and host_id = :hostId");
                }
            }
            if (title != null) {
                if (first) {
                    stringBuffer.append("title regexp :title");
                    first = false;
                } else {
                    stringBuffer.append("and title regexp :title");
                }
            }
        }
        return stringBuffer.toString();
    }

    private String getSqlGetByCityAndDateToSeo(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select * from liveroom ");

        if (!CollectionUtils.isEmpty(paramsMap)) {
            stringBuffer.append(" where ");

            Integer cityId = (Integer) paramsMap.get("cityId");
            Date startTime = (Date) paramsMap.get("startTime");
            Date endTime = (Date) paramsMap.get("endTime");

            boolean first = true;
            if (cityId != null) {
                if (first) {
                    stringBuffer.append("city_id = :cityId");
                    first = false;
                } else {
                    stringBuffer.append("and city_id = :cityId");
                }
            }
            if (startTime != null && endTime != null) {
                if (first) {
                    stringBuffer.append("create_time between :startTime and :endTime");
                    first = false;
                } else {
                    stringBuffer.append("and create_time between :startTime and :endTime");
                }
            }
            stringBuffer.append(" and status in (1,4)");

        }
        stringBuffer.append(" order by create_time desc ");
        Integer offset = (Integer) paramsMap.get("offset");
        Integer limit = (Integer) paramsMap.get("limit");
        if (offset != null && limit != null) {
            stringBuffer.append(" limit :offset, :limit");
        }
        return stringBuffer.toString();
    }

    private String getSqlGetCountByCityAndDateToSeo(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select count(1) from liveroom ");

        if (!CollectionUtils.isEmpty(paramsMap)) {
            stringBuffer.append(" where ");

            Integer cityId = (Integer) paramsMap.get("cityId");
            Date startTime = (Date) paramsMap.get("startTime");
            Date endTime = (Date) paramsMap.get("endTime");

            boolean first = true;
            if (cityId != null) {
                if (first) {
                    stringBuffer.append("city_id = :cityId");
                    first = false;
                } else {
                    stringBuffer.append("and city_id = :cityId");
                }
            }
            if (startTime != null && endTime != null) {
                if (first) {
                    stringBuffer.append("create_time between :startTime and :endTime");
                    first = false;
                } else {
                    stringBuffer.append("and create_time between :startTime and :endTime");
                }
            }
            stringBuffer.append(" and status in (1,4)");
        }
        return stringBuffer.toString();
    }

    private String getSqlCountForStat(Map<String, Object> paramsMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select count(1) from liveroom ");

        if (!CollectionUtils.isEmpty(paramsMap)) {
            stringBuffer.append(" where ");

            Integer cityId = (Integer) paramsMap.get("cityId");
            Date startTime = (Date) paramsMap.get("startTime");
            Date endTime = (Date) paramsMap.get("endTime");

            boolean first = true;
            if (cityId != null) {
                if (first) {
                    stringBuffer.append("city_id = :cityId");
                    first = false;
                } else {
                    stringBuffer.append("and city_id = :cityId");
                }
            }
            if (startTime != null && endTime != null) {
                if (first) {
                    stringBuffer.append("create_time between :startTime and :endTime");
                    first = false;
                } else {
                    stringBuffer.append("and create_time between :startTime and :endTime");
                }
            }
        }
        return stringBuffer.toString();
    }



    private String getSqlOfGetLiveroomIds(Long userId, String userName, String title, Byte status, List<Integer> cityList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isEmpty(userName)) {
            stringBuilder.append("select id from liveroom ");
            boolean first = true;
            if (userId != null) {
                stringBuilder.append(" where host_id = :host_id ");
                first = false;
            }
            if (!StringUtils.isEmpty(title)) {
                if (first) {
                    stringBuilder.append("where title like :title ");
                    first = false;
                } else {
                    stringBuilder.append("and title like :title ");
                }
            }
            if (status != null) {
                if (first) {
                    stringBuilder.append("where status = :status ");
                    first = false;
                } else {
                    stringBuilder.append("and status = :status ");
                }
            }
            if (cityList != null && !cityList.isEmpty()) {
                if (first) {
                    stringBuilder.append("where city_id in (:cityList) ");
                } else {
                    stringBuilder.append("and city_id in (:cityList) ");
                }
            }
        } else {
            stringBuilder.append("select l.id from liveroom l inner join user u on l.host_id = u.id where " +
                    "u.name like :name ");
            if (userId != null) {
                stringBuilder.append(" and host_id = :host_id ");
            }
            if (!StringUtils.isEmpty(title)) {
                stringBuilder.append("and title like :title ");
            }
            if (status != null) {
                stringBuilder.append("and status = :status ");
            }
            if (cityList != null && !cityList.isEmpty()) {
                stringBuilder.append("and city_id in (:cityList) ");
            }
        }
        stringBuilder.append("order by id desc limit :offset, :limit");
        return stringBuilder.toString();
    }

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` WHERE `id` = :id")
    private String GET;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where `id` in (:idList)")
    private String GETS;

    @Value("insert into liveroom (id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, " +
            "img_partial_path, city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, " +
            "like_count, cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count, " +
            "create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time) " +
            "values (:id, :hostId, :chatroomId, :topicId, :status, :videoType, :type, :title, :info, :imgPath, " +
            ":imgPartialPath, :cityId, :hostRtmpUrl, :liveRtmpUrl, :liveHlsUrl, :liveFlvUrl, :historyFlvUrlsJson, " +
            ":likeCount, :curAudienceCount, :totalAudienceCount, :activitySubscribeCount, :commentCount, :commentUserCount, " +
            ":createTime, :updateTime, :scheduledTime, :liveStartTime, :liveEndTime, :interruptTime)")
    private String INSERT;

    @Value("update liveroom set host_id = :hostId, chatroom_id = :chatroomId, topic_id = :topicId, status = :status, " +
            "type = :type, video_type = :videoType, title = :title, info = :info, img_path = :imgPath, " +
            "img_partial_path = :imgPartialPath, city_id = :cityId, host_rtmp_url = :hostRtmpUrl, live_rtmp_url = :liveRtmpUrl, " +
            "live_hls_url = :liveHlsUrl, live_flv_url = :liveFlvUrl, history_flv_urls_json = :historyFlvUrlsJson, " +
            "like_count = :likeCount, cur_audience_count = :curAudienceCount, total_audience_count = :totalAudienceCount, " +
            "activity_subscribe_count = :activitySubscribeCount, comment_count = :commentCount, comment_user_count = :commentUserCount, " +
            "create_time = :createTime, update_time = :updateTime, scheduled_time = :scheduledTime, live_start_time = :liveStartTime, " +
            "live_end_time = :liveEndTime, interrupt_time = :interruptTime where id = :id")
    private String UPDATE;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom`")
    private String ALL_RECORD;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where host_id = :hostId order by id desc limit :offset, :limit")
    private String GET_ENTITIES_BY_HOST;

    @Value("select count(1) from `liveroom` where host_id = :hostId")
    private String GET_COUNT_BY_HOST;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where chatroom_id = :chatroomId")
    private String GET_BY_CHATROOMID;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where host_id = :hostId and status != - 1 and status < 6 and video_type = :videoType " +
            "order by id desc limit :offset, :limit")
    private String GET_HOST_SHOW_BY_HOST;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where host_id = :hostId and status in (1,4) order by create_time desc limit :offset, :limit")
    private String GET_PUBLISH_LIST;

    @Value("select count(1) from `liveroom` where host_id = :hostId and status in (1,4) ")
    private String GET_PUBLISH_LIST_COUNT;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where host_id = :hostId and status in (:statusList) and video_type = :videoType " +
            " order by id desc limit :offset, :limit")
    private String GET_HOST_SHOW_BY_HOST_AND_STATUS_LIST;

    @Value("select count(1) from `liveroom` where host_id = :hostId and status != -1 and status < 6  and video_type = :videoType")
    private String GET_HOST_SHOW_COUNT_BY_HOST;

    @Value("select count(1) from `liveroom` where host_id = :hostId and status in (:statusList) and video_type = :videoType")
    private String GET_HOST_UPLOAD_COUNT_BY_HOST;

    @Value("select id from liveroom where host_id = :hostId and status = 1 limit 0 , 1")
    private String GET_ON_LIVE_ID_BY_HOSTID;

    @Value("select id from liveroom where host_id = : hostId and status = 2 and scheduled_time < now() order by scheduled_time limit 1 ")
    private String GET_SCHEDULED_EXPIRED_BY_HOSTID;

    @Value("select l.* from liveroom as l join liveroom_building as lb on l.id = lb.liveroom_id join stat_liveroom as sl " +
            "on l.id = sl.liveroom_id where lb.building_id = :buildingId and lb.type = 1 and lb.status = 2 and " +
            "lb.show_status = 1 and (l.status = 1 or l.status = 4) order by l.status, sl.total_vv desc, l.id desc limit :offset, :limit")
    private String GET_RELATED_BY_BUILDING_ID;

    @Value("select l.* from liveroom as l join liveroom_building as lb on l.id = lb.liveroom_id " +
            " where lb.building_id = :buildingId and lb.type = 1 and lb.status = 2 and lb.show_status = 1" +
            " and l.status = 1 order by l.status, l.id desc")
    private String GET_ONSHOW_BY_BUILDING_ID;

    @Value("select count(1) from liveroom as l join liveroom_building as lb on l.id = lb.liveroom_id " +
            " where lb.building_id = :buildingId and lb.type = 1 and lb.status = 2 and lb.show_status = 1" +
            " and (l.status = 1 or l.status = 4)")
    private String GET_RELATED_COUNT_BY_BUILDING_ID;

    @Value("select l.* from liveroom as l inner join liveroom_building as lb on l.id = lb.liveroom_id " +
            " where lb.building_id = :buildingId and lb.type = 1 and lb.status = 2 and lb.show_status = 1" +
            " and l.status = 2 order by scheduled_time")
    private String GET_SCHEDULED_BY_BUILDING_ID;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where city_id = :cityId and scheduled_time > now() and status = 2 " +
            " order by scheduled_time")
    private String GET_ALL_UPCOMING_BY_CITY_ID;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id " +
            "where b.live_tag_id in (:liveTagIds) and a.scheduled_time > now() and a.status = 2 " +
            "order by a.scheduled_time")
    private String GET_GLOBAL_UPCOMING;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status = 2 and scheduled_time < now()")
    private String GET_SCHEDULED_TIMEOUT_LIVEROOMS;


    @Value("update liveroom set interrupt_time = null where id = :liveroomId")
    private String RESET_LIVEROOM_INTERRUPT_TIME;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status = :status order by id desc limit :offset, :limit")
    private String GET_BY_STATUS_AND_PAGING;

    @Value("select count(1) from liveroom where craete_time >= :startTime")
    private String GET_LIVEROOM_CREATE_COUNT_BY_START_TIME;

    @Value("select count(1) from liveroom")
    private String GET_LIVEROOM_COUNT;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status = 1 and city_id = #{cityId}")
    private String GET_ON_LIVE_BY_CITYID;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where b.live_tag_id in " +
            "(:liveTagIds) and a.status = 1 and a.city_id = :cityId order by a.create_time desc")
    private String GET_ON_LIVE_BY_CITY_AND_TAG;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where b.live_tag_id in " +
            "(:liveTagIds) and a.status = 1 order by a.create_time desc")
    private String GET_ON_LIVE_BY_TAG;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status = 4 and video_type = 1 and live_end_time >= :createTime and city_id = :cityId")
    private String GET_PLAYBACK_BY_CITY;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (:tagIds) and  " +
            "a.live_end_time >= :createTime and a.city_id = :cityId ")
    private String GET_PLAYBACK_BY_CITY_AND_TAG;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (:tagIds) and a.live_end_time >= :createTime limit 5")
    private String GET_PLAYBACK_BY_TAG;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (:tagIds) and a.city_id = :cityId order by a.live_end_time desc limit 1000")
    private String GET_PLAYBACK_BY_CITY_ORDER_BY_CREATE_TIME_AND_TAG;

    @Value("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (:tagIds) order by a.live_end_time desc limit 1000")
    private String GET_PLAYBACK_BY_CREATE_TIME_AND_TAG;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status = 4 and video_type = 1 and city_id = :cityId order by live_end_time desc limit 1000")
    private String GET_PLAYBACK_BY_CITY_ORDER_BY_CREATE_TIME;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status in (1,4) and create_time >= :createTime and host_id = :hostId limit 1000")
    private String GET_ONLIVE_AND_CLOSE_BY_HOSTID_AND_STARTTIME;

    @Value("select count(1) from `liveroom` where status in (1,4) and create_time >= :createTime and host_id = :hostId limit 1000")
    private String GET_ONLIVE_AND_CLOSE_COUNT_BY_HOSTID_AND_STARTTIME;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status in (:statusList) and host_id = :hostId order by status, create_time desc limit " +
            ":offset , :limit ")
    private String GET_BY_HOSTID_ORDER_BY_STATUS;

    @Value("select count(1) from `liveroom` where status in (:statusList) and host_id = :hostId ")
    private String GET_COUNT_BY_HOSTID_AND_STATUS;

    @Value("select l.* from liveroom as l join liveroom_building as lb on l.id = lb.liveroom_id " +
            "WHERE lb.building_id = :buildingId AND lb.type = 1 AND l.status = 1")
    private String GET_HOSTTEST_ONSHOW_BY_BUILDING_ID;

    @Value("select l.id from liveroom as l join liveroom_building as lb on l.id = lb.liveroom_id join stat_liveroom as sl " +
            "on l.id = sl.liveroom_id WHERE lb.building_id = :buildingId AND lb.type = 1 AND l.status = 4 " +
            "order by sl.total_vv desc limit 1")
    private String GET_HOSTTEST_PLAYBACK_BY_BUILDING_ID;

    @Value("select id from liveroom where host_id = :hostId and status = 2")
    private String GET_SCHEDULED_LIVE_BY_HOSTID;

    @Value("select id from liveroom where topic_id = :topicId")
    private String GET_BY_TOPIC_ID;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status= :status and city_id = :cityId order by id desc ")
    private String GET_BY_CITY_AND_STATUS_ORDER_BY_ID;

    @Value("select id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, img_partial_path, " +
            "city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, like_count," +
            " cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count," +
            " create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time " +
            "from `liveroom` where status= :status and city_id = :cityId and scheduled_time >= :time " +
            "order by id desc limit 200")
    private String GET_BY_CITY_AND_STATUS_AND_TIME;

    @Value("select host_id as userId , count(1) as count from liveroom where host_id in (:userIds) and `status` " +
            "in (1, 2, 4) GROUP BY host_id")
    private String GET_USER_VALID_LIVEROOM_COUNT;

    @Value("update liveroom set city_id = :cityId where id = :id")
    private String MODIFY_LIVEROOM_BY_CITY_ID;



    private Map<String, Object> getMap(LiveroomEntity liveroomEntity) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id", liveroomEntity.getId());
        paramsMap.put("hostId", liveroomEntity.getHostId());
        paramsMap.put("chatroomId", liveroomEntity.getChatroomId());
        paramsMap.put("topicId", liveroomEntity.getTopicId());
        paramsMap.put("status", liveroomEntity.getStatus());
        paramsMap.put("videoType", liveroomEntity.getVideoType());
        paramsMap.put("type", liveroomEntity.getType());
        paramsMap.put("title", liveroomEntity.getTitle());
        paramsMap.put("imgPath", liveroomEntity.getImgPath());
        paramsMap.put("cityId", liveroomEntity.getCityId());
        paramsMap.put("hostRtmpUrl", liveroomEntity.getHostRtmpUrl());
        paramsMap.put("liveRtmpUrl", liveroomEntity.getLiveRtmpUrl());
        paramsMap.put("liveHlsUrl", liveroomEntity.getLiveHlsUrl());
        paramsMap.put("liveFlvUrl", liveroomEntity.getLiveFlvUrl());
        paramsMap.put("createTime", liveroomEntity.getCreateTime());
        paramsMap.put("updateTime", liveroomEntity.getUpdateTime());
        paramsMap.put("scheduledTime", liveroomEntity.getScheduledTime());
        paramsMap.put("liveStartTime", liveroomEntity.getLiveStartTime());
        paramsMap.put("liveEndTime", liveroomEntity.getLiveEndTime());
        paramsMap.put("interruptTime", liveroomEntity.getInterruptTime());
        return paramsMap;
    }
}
