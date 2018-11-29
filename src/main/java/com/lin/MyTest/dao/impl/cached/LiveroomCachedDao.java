package com.lin.MyTest.dao.impl.cached;

import com.lin.MyTest.dao.LiveroomDao;
import com.lin.MyTest.dao.impl.jdbc.LiveroomJdbcDao;
import com.lin.MyTest.dao.impl.redis.LiveroomRedisDao;
import com.lin.MyTest.model.entity.LiveroomEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class LiveroomCachedDao implements LiveroomDao {

	@Resource
	private LiveroomJdbcDao liveroomJdbcDao;

	@Resource
	private LiveroomRedisDao liveroomRedisDao;

	@Override
	public long insert(LiveroomEntity liveroomEntity) {
		return 0;
	}

	@Override
	public LiveroomEntity get(long id) {
		LiveroomEntity liveroomEntity = liveroomRedisDao.get(id);
		if (liveroomEntity == null) {
			liveroomEntity = liveroomJdbcDao.get(id);
			if (liveroomEntity != null) {
				liveroomRedisDao.insert(liveroomEntity);
			}
		}
		return liveroomEntity;
	}

	@Override
	public List<LiveroomEntity> gets(List<Long> liveroomIdList) {
		List<Long> missedIds = new ArrayList<>();
		List<Integer> missedIndex = new ArrayList<>();
		List<LiveroomEntity> liveroomEntities = liveroomRedisDao.gets(liveroomIdList);
		int idListSize = liveroomIdList.size();
		for (int i = 0; i < idListSize; ++i) {
			if (liveroomEntities.get(i) == null) {
				missedIds.add(liveroomIdList.get(i));
				missedIndex.add(i);
			}
		}
		List<LiveroomEntity> missedLiverooms = liveroomJdbcDao.gets(missedIds);
		int missedLiveroomsSize = missedLiverooms.size();
		for (int i = 0; i < missedLiveroomsSize; ++i) {
			LiveroomEntity liveroomEntity = missedLiverooms.get(i);
			if (liveroomEntity != null) {
				liveroomEntities.set(missedIndex.get(i), liveroomEntity);
				liveroomRedisDao.insert(liveroomEntity);
			}
		}
		return liveroomEntities;
	}


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
