package com.lin.MyTest.dao.impl.redis;

import com.lin.MyTest.dao.TestDao;
import com.lin.MyTest.model.entity.TestEntity;
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
public class TestRedisDao implements TestDao {

	@Resource
	private SimpleRedisDao redisDao;

	private static final Integer HOT_CITY_LIST_EXPIRES_TIME = 5;
	private static final Integer HOT_USER_LIST_EXPIRES_TIME = 5;

	@Override
	public long insert(TestEntity TestEntity) {
		redisDao.storeObject(String.format(Test_KEY, TestEntity.getId()), TestEntity);
		return TestEntity.getId();
	}

	@Override
	public TestEntity get(long id) {
		return redisDao.queryForObject(String.format(Test_KEY, id), TestEntity.class);
	}

	@Override
	public List<TestEntity> gets(List<Long> TestIds) {
		List<String> keys = new ArrayList<>();
		for (Long id : TestIds) {
			keys.add(String.format(Test_KEY, id));
		}
		return redisDao.queryForListOfObjects(keys, TestEntity.class);
	}

	public List<TestEntity> getCityHotTestsFromRedis(int cityId) {
		String key = String.format(HOT_CITY_TestS_KEY, cityId);
		return redisDao.queryForListOfObjectsByKey(key, TestEntity.class);
	}

	public void setRedisCityHotTests(int cityId, List<TestEntity> TestEntities) {
		String key = String.format(HOT_CITY_TestS_KEY, cityId);
		redisDao.storeListOfObjects(key, TestEntities);
		redisDao.expireKey(key, HOT_CITY_LIST_EXPIRES_TIME);
	}

	public List<TestEntity> getUserHotTestsFromRedis(long userId) {
		String key = String.format(HOT_USER_TestS_KEY, userId);
		return redisDao.queryForListOfObjectsByKey(key, TestEntity.class);
	}

	public void setRedisUserHotTests(long userId, List<TestEntity> TestEntityResultList) {
		String key = String.format(HOT_USER_TestS_KEY, userId);
		redisDao.storeListOfObjects(key, TestEntityResultList);
		redisDao.expireKey(key, HOT_USER_LIST_EXPIRES_TIME);
	}
	@Value("zhibo:hot_Tests:user_id:%s")
	private String HOT_USER_TestS_KEY;

	@Value("zhibo:hot_Tests:city_id:%s")
	private String HOT_CITY_TestS_KEY;

	@Value("zhibo:Tests:%d")
	private String Test_KEY;

	@Override
	public int updateByPrimaryKey(TestEntity record) {
		return 0;
	}

	@Override
	public List<TestEntity> getAllRecord() {
		return null;
	}

	@Override
	public List<TestEntity> getByHost(long hostId, int offset, int limit) {
		return null;
	}

	@Override
	public TestEntity getByChatroomId(String chatroomId) {
		return null;
	}

	@Override
	public List<TestEntity> getHostShowByHost(Byte videoType, long hostId, int offset, int limit) {
		return null;
	}

	@Override
	public List<TestEntity> getPublishList(long hostId, int offset, int limit) {
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
	public List<TestEntity> getRelatedByBuildingId(long buildingId, int offset, int limit) {
		return null;
	}

	@Override
	public List<TestEntity> getOnShowByBuildingId(long buildingId) {
		return null;
	}

	@Override
	public int getRelatedCountByBuildingId(long buildingId) {
		return 0;
	}

	@Override
	public List<TestEntity> getScheduledByBuildingId(long buildingId) {
		return null;
	}

	@Override
	public List<TestEntity> getAllUpcomingByCityId(int cityId) {
		return null;
	}

	@Override
	public List<TestEntity> getGlobalUpcoming(String liveTagIds) {
		return null;
	}

	@Override
	public List<TestEntity> getScheduleTimeoutTests() {
		return null;
	}

	@Override
	public List<TestEntity> searchTestForAdmin(Byte videoType, Byte status, String cityString, Long hostId, String hostName, Long id, String title, Integer tagId, Integer offset, Integer limit, Date startTime, Date endTime) {
		return null;
	}

	@Override
	public int searchTestForAdminCount(Byte videoType, Byte status, String cityString, Long hostId, String hostName, Long id, String title, Integer tagId, Date startTime, Date endTime) {
		return 0;
	}

	@Override
	public List<TestEntity> searchTestList(Byte videoType, Long hostId, Byte status, String q, Integer offset, Integer limit) {
		return null;
	}

	@Override
	public int searchTestCount(Byte videoType, Long hostId, Byte status, String q) {
		return 0;
	}

	@Override
	public void resetTestInterruptTime(long TestId) {

	}

	@Override
	public List<TestEntity> getByStatusByPaging(int status, int offset, int limit) {
		return null;
	}

	@Override
	public int getTestCreateCountByStartTime(Date startTime) {
		return 0;
	}

	@Override
	public int getTestCount() {
		return 0;
	}

	@Override
	public List<TestEntity> getOnLiveByCity(int cityId) {
		return null;
	}

	@Override
	public List<TestEntity> getOnLiveByCityAndTag(String liveTagIds, int cityId) {
		return null;
	}

	@Override
	public List<TestEntity> getOnLiveByTag(String liveTagIds) {
		return null;
	}

	@Override
	public List<TestEntity> getPlayBackByCity(int cityId, Date startTime) {
		return null;
	}

	@Override
	public List<TestEntity> getPlayBackByCityAndTagIds(String tagIds, int cityId, Date startTime) {
		return null;
	}

	@Override
	public List<TestEntity> getPlayBackByTag(String tagIds, Date startTime) {
		return null;
	}

	@Override
	public List<TestEntity> getPlayBackByCityOrderByCreateTimeAndTag(String tagIds, int cityId) {
		return null;
	}

	@Override
	public List<TestEntity> getPlayBackByCreateTimeAndTag(String tagIds) {
		return null;
	}

	@Override
	public List<TestEntity> getPlayBackByCityOrderByCreateTime(int cityId) {
		return null;
	}

	@Override
	public List<TestEntity> getOnLiveAndCloseByHostIdAndStartTime(long hostId, Date startTime) {
		return null;
	}

	@Override
	public int getOnLiveAndCloseCountByHostIdAndStartTime(long hostId, Date startTime) {
		return 0;
	}

	@Override
	public List<TestEntity> getByHostIdOrderByStatus(String statusList, long uid, int offset, int limit) {
		return null;
	}

	@Override
	public int getCountByHostIdAndStatus(String statusList, long uid) {
		return 0;
	}

	@Override
	public List<TestEntity> getHottestOnShowByBuildingId(int buildingId) {
		return null;
	}

	@Override
	public Long getHottestPlayBackByBuildingId(int buildingId) {
		return null;
	}

	@Override
	public Long getTestIdByTopicId(long topicId) {
		return null;
	}

	@Override
	public List<TestEntity> getByCityAndStatusOrderByAudience(int status, Integer cityId) {
		return null;
	}

	@Override
	public List<TestEntity> getByCityAndStatusAndTime(int status, Integer cityId, String time) {
		return null;
	}

	@Override
	public Map<BigInteger, Map<String, Object>> getUserValidTestCount(String userIds) {
		return null;
	}

	@Override
	public void modifyTestByCityId(int cityId, long TestId) {

	}

	@Override
	public int countForStat(Integer cityId, Date startTime, Date endTime) {
		return 0;
	}
}
