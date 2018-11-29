package com.lin.MyTest.dao.impl.cached;

import com.lin.MyTest.dao.TestDao;
import com.lin.MyTest.dao.impl.jdbc.TestJdbcDao;
import com.lin.MyTest.dao.impl.redis.TestRedisDao;
import com.lin.MyTest.model.entity.TestEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class TestCachedDao implements TestDao {

	@Resource
	private TestJdbcDao TestJdbcDao;

	@Resource
	private TestRedisDao TestRedisDao;

	@Override
	public long insert(TestEntity TestEntity) {
		return 0;
	}

	@Override
	public TestEntity get(long id) {
		TestEntity TestEntity = TestRedisDao.get(id);
		if (TestEntity == null) {
			TestEntity = TestJdbcDao.get(id);
			if (TestEntity != null) {
				TestRedisDao.insert(TestEntity);
			}
		}
		return TestEntity;
	}

	@Override
	public List<TestEntity> gets(List<Long> TestIdList) {
		List<Long> missedIds = new ArrayList<>();
		List<Integer> missedIndex = new ArrayList<>();
		List<TestEntity> TestEntities = TestRedisDao.gets(TestIdList);
		int idListSize = TestIdList.size();
		for (int i = 0; i < idListSize; ++i) {
			if (TestEntities.get(i) == null) {
				missedIds.add(TestIdList.get(i));
				missedIndex.add(i);
			}
		}
		List<TestEntity> missedTests = TestJdbcDao.gets(missedIds);
		int missedTestsSize = missedTests.size();
		for (int i = 0; i < missedTestsSize; ++i) {
			TestEntity TestEntity = missedTests.get(i);
			if (TestEntity != null) {
				TestEntities.set(missedIndex.get(i), TestEntity);
				TestRedisDao.insert(TestEntity);
			}
		}
		return TestEntities;
	}


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
