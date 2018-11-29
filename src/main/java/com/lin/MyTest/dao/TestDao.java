package com.lin.MyTest.dao;


import com.lin.MyTest.model.entity.TestEntity;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TestDao {

	TestEntity get(long id);

	List<TestEntity> gets(List<Long> TestIds);


	int updateByPrimaryKey(TestEntity record);

//	int updateStatus(@Param("id")Long id, @Param("status")Byte status, @Param("updateTime")Date updateTime,
//					 @Param("liveStartTime")Date liveStartTime, @Param("liveEndTime")Date liveEndTime,
//					 @Param("interruptTime")Date interruptTime);

	List<TestEntity> getAllRecord();

	List<TestEntity> getByHost(@Param("hostId") long hostId, @Param("offset") int offset,
																  @Param("limit") int limit);

	TestEntity getByChatroomId(@Param("chatroomId") String chatroomId);

	List<TestEntity> getHostShowByHost(@Param("videoType") Byte videoType, @Param("hostId") long hostId,
																		  @Param("offset") int offset, @Param("limit") int limit);

	List<TestEntity> getPublishList(@Param("hostId") long hostId, @Param("offset") int offset,
																	   @Param("limit") int limit);

	int getPublishListCount(@Param("hostId") long hostId);

	int getCountByHost(@Param("hostId") long hostId);

//	List<TestEntity> getHostShowByHostAndStatusList(@Param("videoType") Byte videoType, @Param("hostId") long hostId,
//														@Param("statusList") String statusList, @Param("offset") int offset,
//														@Param("limit") int limit);

	int getHostShowCountByHost(@Param("videoType") Byte videoType, @Param("hostId") long hostId);

	int getHostUploadCountByHost(@Param("videoType") Byte videoType, @Param("hostId") long hostId,
								 @Param("statusList") String statusList);

	Long getOnLiveIdByHostId(@Param("hostId") long hostId);

//	Long getScheduledExpiredByHostId(@Param("hostId") long hostId);

	List<TestEntity> getRelatedByBuildingId(@Param("buildingId") long buildingId, @Param("offset") int offset,
																			   @Param("limit") int limit);

	List<TestEntity> getOnShowByBuildingId(@Param("buildingId") long buildingId);

	int getRelatedCountByBuildingId(@Param("buildingId") long buildingId);

	List<TestEntity> getScheduledByBuildingId(@Param("buildingId") long buildingId);

	List<TestEntity> getAllUpcomingByCityId(@Param("cityId") int cityId);

	List<TestEntity> getGlobalUpcoming(@Param("liveTagIds") String liveTagIds);

	List<TestEntity> getScheduleTimeoutTests();

	List<TestEntity> searchTestForAdmin(@Param("videoType") Byte videoType,
																			   @Param("status") Byte status, @Param("cityString") String cityString,
																			   @Param("hostId") Long hostId, @Param("hostName") String hostName, @Param("id") Long id,
																			   @Param("title") String title, @Param("tagId") Integer tagId,
																			   @Param("offset") Integer offset, @Param("limit") Integer limit,
																			   @Param("startTime") Date startTime, @Param("endTime") Date endTime);


	int searchTestForAdminCount(@Param("videoType") Byte videoType,
									@Param("status") Byte status, @Param("cityString") String cityString,
									@Param("hostId") Long hostId, @Param("hostName") String hostName, @Param("id") Long id,
									@Param("title") String title, @Param("tagId") Integer tagId,
									@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	List<TestEntity> searchTestList(@Param("videoType") Byte videoType, @Param("hostId") Long hostId,
																		   @Param("status") Byte status, @Param("title") String q,
																		   @Param("offset") Integer offset, @Param("limit") Integer limit);

	int searchTestCount(@Param("videoType") Byte videoType, @Param("hostId") Long hostId, @Param("status") Byte status,
							@Param("title") String q);


	void resetTestInterruptTime(@Param("TestId") long TestId);

	List<TestEntity> getByStatusByPaging(@Param("status") int status, @Param("offset") int offset,
																			@Param("limit") int limit);

	int getTestCreateCountByStartTime(@Param("startTime") Date startTime);

	int getTestCount();

	List<TestEntity> getOnLiveByCity(@Param("cityId") int cityId);

	List<TestEntity> getOnLiveByCityAndTag(@Param("liveTagIds") String liveTagIds, @Param("cityId") int cityId);

	List<TestEntity> getOnLiveByTag(@Param("liveTagIds") String liveTagIds);

	List<TestEntity> getPlayBackByCity(@Param("cityId") int cityId, @Param("createTime") Date startTime);

	List<TestEntity> getPlayBackByCityAndTagIds(@Param("tagIds") String tagIds, @Param("cityId") int cityId, @Param("createTime") Date startTime);


	List<TestEntity> getPlayBackByTag(@Param("tagIds") String tagIds, @Param("createTime") Date startTime);

	List<TestEntity> getPlayBackByCityOrderByCreateTimeAndTag(@Param("tagIds") String tagIds, @Param("cityId") int cityId);

	List<TestEntity> getPlayBackByCreateTimeAndTag(@Param("tagIds") String tagIds);

	List<TestEntity> getPlayBackByCityOrderByCreateTime(@Param("cityId") int cityId);

	List<TestEntity> getOnLiveAndCloseByHostIdAndStartTime(@Param("hostId") long hostId,
																							  @Param("createTime") Date startTime);

	int getOnLiveAndCloseCountByHostIdAndStartTime(@Param("hostId") long hostId, @Param("createTime") Date startTime);

	List<TestEntity> getByHostIdOrderByStatus(@Param("statusList") String statusList, @Param("hostId") long uid, @Param("offset") int offset,
																				 @Param("limit") int limit);

	int getCountByHostIdAndStatus(@Param("statusList") String statusList, @Param("hostId") long uid);

	List<TestEntity> getHottestOnShowByBuildingId(@Param("buildingId") int buildingId);

	Long getHottestPlayBackByBuildingId(@Param("buildingId") int buildingId);

//	List<Long> getScheduledLiveByHostId(@Param("hostId") long hostId);

	Long getTestIdByTopicId(@Param("topicId") long topicId);

	List<TestEntity> getByCityAndStatusOrderByAudience(@Param("status") int status, @Param("cityId") Integer cityId);

	List<TestEntity> getByCityAndStatusAndTime(@Param("status") int status, @Param("cityId") Integer cityId, @Param("time") String time);

	Map<BigInteger, Map<String, Object>> getUserValidTestCount(@Param("userIds") String userIds);

	void modifyTestByCityId(@Param("cityId") int cityId, @Param("id") long TestId);

//	List<TestEntity> getByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
//											   @Param("endTime") Date endTime, @Param("offset") Integer offset,
//											   @Param("limit") Integer limit);
//	int getCountByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
//								   @Param("endTime") Date endTime);
	int countForStat(@Param("cityId") Integer cityId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

	long insert(TestEntity TestEntity);

}
