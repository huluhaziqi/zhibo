package com.lin.MyTest.dao.impl.redis;

import com.lin.MyTest.dao.LiveroomDao;
import com.lin.MyTest.model.entity.LiveroomEntity;
import com.lin.MyTest.platform.dao.SimpleRedisDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class LiveroomRedisDao implements LiveroomDao {

	@Resource
	private SimpleRedisDao redisDao;

	private static final Integer HOT_CITY_LIST_EXPIRES_TIME = 5;
	private static final Integer HOT_USER_LIST_EXPIRES_TIME = 5;

	@Override
	public long insert(LiveroomEntity liveroomEntity) {
		redisDao.storeObject(String.format(LIVEROOM_KEY, liveroomEntity.getId()), liveroomEntity);
		return liveroomEntity.getId();
	}

	@Override
	public LiveroomEntity get(long id) {
		return redisDao.queryForObject(String.format(LIVEROOM_KEY, id), LiveroomEntity.class);
	}

	@Override
	public List<LiveroomEntity> gets(List<Long> liveroomIds) {
		List<String> keys = new ArrayList<>();
		for (Long id : liveroomIds) {
			keys.add(String.format(LIVEROOM_KEY, id));
		}
		return redisDao.queryForListOfObjects(keys, LiveroomEntity.class);
	}

	public List<LiveroomEntity> getCityHotLiveroomsFromRedis(int cityId) {
		String key = String.format(HOT_CITY_LIVEROOMS_KEY, cityId);
		return redisDao.queryForListOfObjectsByKey(key, LiveroomEntity.class);
	}

	public void setRedisCityHotLiverooms(int cityId, List<LiveroomEntity> liveroomEntities) {
		String key = String.format(HOT_CITY_LIVEROOMS_KEY, cityId);
		redisDao.storeListOfObjects(key, liveroomEntities);
		redisDao.expireKey(key, HOT_CITY_LIST_EXPIRES_TIME);
	}

	public List<LiveroomEntity> getUserHotLiveroomsFromRedis(long userId) {
		String key = String.format(HOT_USER_LIVEROOMS_KEY, userId);
		return redisDao.queryForListOfObjectsByKey(key, LiveroomEntity.class);
	}

	public void setRedisUserHotLiverooms(long userId, List<LiveroomEntity> liveroomEntityResultList) {
		String key = String.format(HOT_USER_LIVEROOMS_KEY, userId);
		redisDao.storeListOfObjects(key, liveroomEntityResultList);
		redisDao.expireKey(key, HOT_USER_LIST_EXPIRES_TIME);
	}
	@Value("zhibo:hot_liverooms:user_id:%s")
	private String HOT_USER_LIVEROOMS_KEY;

	@Value("zhibo:hot_liverooms:city_id:%s")
	private String HOT_CITY_LIVEROOMS_KEY;

	@Value("zhibo:liverooms:%d")
	private String LIVEROOM_KEY;

	@Override
	public int updateByPrimaryKey(LiveroomEntity record) {
		return 0;
	}

	@Override
	public List<LiveroomEntity> getAllRecord() {
		return null;
	}

	@Override
	public List<LiveroomEntity> getByHost(long hostId, int offset, int limit) {
		return null;
	}

	@Override
	public LiveroomEntity getByChatroomId(String chatroomId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getHostShowByHost(Byte videoType, long hostId, int offset, int limit) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPublishList(long hostId, int offset, int limit) {
		return null;
	}

	@Override
	public int getPublishListCount(long hostId) {
		return 0;
	}

	@Override
	public int getCountByHost(long hostId) {
		return 0;
	}

	@Override
	public int getHostShowCountByHost(Byte videoType, long hostId) {
		return 0;
	}

	@Override
	public int getHostUploadCountByHost(Byte videoType, long hostId, String statusList) {
		return 0;
	}

	@Override
	public Long getOnLiveIdByHostId(long hostId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getRelatedByBuildingId(long buildingId, int offset, int limit) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getOnShowByBuildingId(long buildingId) {
		return null;
	}

	@Override
	public int getRelatedCountByBuildingId(long buildingId) {
		return 0;
	}

	@Override
	public List<LiveroomEntity> getScheduledByBuildingId(long buildingId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getAllUpcomingByCityId(int cityId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getGlobalUpcoming(String liveTagIds) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getScheduleTimeoutLiverooms() {
		return null;
	}

	@Override
	public List<LiveroomEntity> searchLiveroomForAdmin(Byte videoType, Byte status, String cityString, Long hostId, String hostName, Long id, String title, Integer tagId, Integer offset, Integer limit, Date startTime, Date endTime) {
		return null;
	}

	@Override
	public int searchLiveroomForAdminCount(Byte videoType, Byte status, String cityString, Long hostId, String hostName, Long id, String title, Integer tagId, Date startTime, Date endTime) {
		return 0;
	}

	@Override
	public List<LiveroomEntity> searchLiveroomList(Byte videoType, Long hostId, Byte status, String q, Integer offset, Integer limit) {
		return null;
	}

	@Override
	public int searchLiveroomCount(Byte videoType, Long hostId, Byte status, String q) {
		return 0;
	}

	@Override
	public void resetLiveroomInterruptTime(long liveroomId) {

	}

	@Override
	public List<LiveroomEntity> getByStatusByPaging(int status, int offset, int limit) {
		return null;
	}

	@Override
	public int getLiveroomCreateCountByStartTime(Date startTime) {
		return 0;
	}

	@Override
	public int getLiveroomCount() {
		return 0;
	}

	@Override
	public List<LiveroomEntity> getOnLiveByCity(int cityId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getOnLiveByCityAndTag(String liveTagIds, int cityId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getOnLiveByTag(String liveTagIds) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPlayBackByCity(int cityId, Date startTime) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPlayBackByCityAndTagIds(String tagIds, int cityId, Date startTime) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPlayBackByTag(String tagIds, Date startTime) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPlayBackByCityOrderByCreateTimeAndTag(String tagIds, int cityId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPlayBackByCreateTimeAndTag(String tagIds) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getPlayBackByCityOrderByCreateTime(int cityId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getOnLiveAndCloseByHostIdAndStartTime(long hostId, Date startTime) {
		return null;
	}

	@Override
	public int getOnLiveAndCloseCountByHostIdAndStartTime(long hostId, Date startTime) {
		return 0;
	}

	@Override
	public List<LiveroomEntity> getByHostIdOrderByStatus(String statusList, long uid, int offset, int limit) {
		return null;
	}

	@Override
	public int getCountByHostIdAndStatus(String statusList, long uid) {
		return 0;
	}

	@Override
	public List<LiveroomEntity> getHottestOnShowByBuildingId(int buildingId) {
		return null;
	}

	@Override
	public Long getHottestPlayBackByBuildingId(int buildingId) {
		return null;
	}

	@Override
	public Long getLiveroomIdByTopicId(long topicId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getByCityAndStatusOrderByAudience(int status, Integer cityId) {
		return null;
	}

	@Override
	public List<LiveroomEntity> getByCityAndStatusAndTime(int status, Integer cityId, String time) {
		return null;
	}

	@Override
	public Map<BigInteger, Map<String, Object>> getUserValidLiveroomCount(String userIds) {
		return null;
	}

	@Override
	public void modifyLiveroomByCityId(int cityId, long liveroomId) {

	}

	@Override
	public int countForStat(Integer cityId, Date startTime, Date endTime) {
		return 0;
	}
}
