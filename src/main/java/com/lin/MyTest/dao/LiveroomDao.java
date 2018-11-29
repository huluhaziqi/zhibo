package com.lin.MyTest.dao;


import com.lin.MyTest.model.entity.LiveroomEntity;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LiveroomDao {

	LiveroomEntity get(long id);

	List<LiveroomEntity> gets(List<Long> liveroomIds);


	int updateByPrimaryKey(LiveroomEntity record);

//	int updateStatus(@Param("id")Long id, @Param("status")Byte status, @Param("updateTime")Date updateTime,
//					 @Param("liveStartTime")Date liveStartTime, @Param("liveEndTime")Date liveEndTime,
//					 @Param("interruptTime")Date interruptTime);

	List<LiveroomEntity> getAllRecord();

	List<LiveroomEntity> getByHost(@Param("hostId") long hostId, @Param("offset") int offset,
																  @Param("limit") int limit);

	LiveroomEntity getByChatroomId(@Param("chatroomId") String chatroomId);

	List<LiveroomEntity> getHostShowByHost(@Param("videoType") Byte videoType, @Param("hostId") long hostId,
																		  @Param("offset") int offset, @Param("limit") int limit);

	List<LiveroomEntity> getPublishList(@Param("hostId") long hostId, @Param("offset") int offset,
																	   @Param("limit") int limit);

	int getPublishListCount(@Param("hostId") long hostId);

	int getCountByHost(@Param("hostId") long hostId);

//	List<LiveroomEntity> getHostShowByHostAndStatusList(@Param("videoType") Byte videoType, @Param("hostId") long hostId,
//														@Param("statusList") String statusList, @Param("offset") int offset,
//														@Param("limit") int limit);

	int getHostShowCountByHost(@Param("videoType") Byte videoType, @Param("hostId") long hostId);

	int getHostUploadCountByHost(@Param("videoType") Byte videoType, @Param("hostId") long hostId,
								 @Param("statusList") String statusList);

	Long getOnLiveIdByHostId(@Param("hostId") long hostId);

//	Long getScheduledExpiredByHostId(@Param("hostId") long hostId);

	List<LiveroomEntity> getRelatedByBuildingId(@Param("buildingId") long buildingId, @Param("offset") int offset,
																			   @Param("limit") int limit);

	List<LiveroomEntity> getOnShowByBuildingId(@Param("buildingId") long buildingId);

	int getRelatedCountByBuildingId(@Param("buildingId") long buildingId);

	List<LiveroomEntity> getScheduledByBuildingId(@Param("buildingId") long buildingId);

	List<LiveroomEntity> getAllUpcomingByCityId(@Param("cityId") int cityId);

	List<LiveroomEntity> getGlobalUpcoming(@Param("liveTagIds") String liveTagIds);

	List<LiveroomEntity> getScheduleTimeoutLiverooms();

	List<LiveroomEntity> searchLiveroomForAdmin(@Param("videoType") Byte videoType,
																			   @Param("status") Byte status, @Param("cityString") String cityString,
																			   @Param("hostId") Long hostId, @Param("hostName") String hostName, @Param("id") Long id,
																			   @Param("title") String title, @Param("tagId") Integer tagId,
																			   @Param("offset") Integer offset, @Param("limit") Integer limit,
																			   @Param("startTime") Date startTime, @Param("endTime") Date endTime);


	int searchLiveroomForAdminCount(@Param("videoType") Byte videoType,
									@Param("status") Byte status, @Param("cityString") String cityString,
									@Param("hostId") Long hostId, @Param("hostName") String hostName, @Param("id") Long id,
									@Param("title") String title, @Param("tagId") Integer tagId,
									@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	List<LiveroomEntity> searchLiveroomList(@Param("videoType") Byte videoType, @Param("hostId") Long hostId,
																		   @Param("status") Byte status, @Param("title") String q,
																		   @Param("offset") Integer offset, @Param("limit") Integer limit);

	int searchLiveroomCount(@Param("videoType") Byte videoType, @Param("hostId") Long hostId, @Param("status") Byte status,
							@Param("title") String q);


	void resetLiveroomInterruptTime(@Param("liveroomId") long liveroomId);

	List<LiveroomEntity> getByStatusByPaging(@Param("status") int status, @Param("offset") int offset,
																			@Param("limit") int limit);

	int getLiveroomCreateCountByStartTime(@Param("startTime") Date startTime);

	int getLiveroomCount();

	List<LiveroomEntity> getOnLiveByCity(@Param("cityId") int cityId);

	List<LiveroomEntity> getOnLiveByCityAndTag(@Param("liveTagIds") String liveTagIds, @Param("cityId") int cityId);

	List<LiveroomEntity> getOnLiveByTag(@Param("liveTagIds") String liveTagIds);

	List<LiveroomEntity> getPlayBackByCity(@Param("cityId") int cityId, @Param("createTime") Date startTime);

	List<LiveroomEntity> getPlayBackByCityAndTagIds(@Param("tagIds") String tagIds, @Param("cityId") int cityId, @Param("createTime") Date startTime);


	List<LiveroomEntity> getPlayBackByTag(@Param("tagIds") String tagIds, @Param("createTime") Date startTime);

	List<LiveroomEntity> getPlayBackByCityOrderByCreateTimeAndTag(@Param("tagIds") String tagIds, @Param("cityId") int cityId);

	List<LiveroomEntity> getPlayBackByCreateTimeAndTag(@Param("tagIds") String tagIds);

	List<LiveroomEntity> getPlayBackByCityOrderByCreateTime(@Param("cityId") int cityId);

	List<LiveroomEntity> getOnLiveAndCloseByHostIdAndStartTime(@Param("hostId") long hostId,
																							  @Param("createTime") Date startTime);

	int getOnLiveAndCloseCountByHostIdAndStartTime(@Param("hostId") long hostId, @Param("createTime") Date startTime);

	List<LiveroomEntity> getByHostIdOrderByStatus(@Param("statusList") String statusList, @Param("hostId") long uid, @Param("offset") int offset,
																				 @Param("limit") int limit);

	int getCountByHostIdAndStatus(@Param("statusList") String statusList, @Param("hostId") long uid);

	List<LiveroomEntity> getHottestOnShowByBuildingId(@Param("buildingId") int buildingId);

	Long getHottestPlayBackByBuildingId(@Param("buildingId") int buildingId);

//	List<Long> getScheduledLiveByHostId(@Param("hostId") long hostId);

	Long getLiveroomIdByTopicId(@Param("topicId") long topicId);

	List<LiveroomEntity> getByCityAndStatusOrderByAudience(@Param("status") int status, @Param("cityId") Integer cityId);

	List<LiveroomEntity> getByCityAndStatusAndTime(@Param("status") int status, @Param("cityId") Integer cityId, @Param("time") String time);

	Map<BigInteger, Map<String, Object>> getUserValidLiveroomCount(@Param("userIds") String userIds);

	void modifyLiveroomByCityId(@Param("cityId") int cityId, @Param("id") long liveroomId);

//	List<LiveroomEntity> getByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
//											   @Param("endTime") Date endTime, @Param("offset") Integer offset,
//											   @Param("limit") Integer limit);
//	int getCountByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
//								   @Param("endTime") Date endTime);
	int countForStat(@Param("cityId") Integer cityId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

	long insert(LiveroomEntity liveroomEntity);

}
