package com.lin.MyTest.dao.repository;

import com.lin.MyTest.model.entity.LiveroomEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface LiveroomDao {

    @Insert("insert into liveroom (id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, " +
            "img_partial_path, city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, " +
            "like_count, cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count, " +
            "create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time) " +
            "values (#{id}, #{hostId}, #{chatroomId}, #{topicId}, #{status}, #{videoType}, #{type}, #{title}, null, #{imgPath}, " +
            "null, #{cityId}, #{hostRtmpUrl}, #{liveRtmpUrl}, #{liveHlsUrl}, #{liveFlvUrl}, null, " +
            "0, 0, 0, 0, 0, 0, " +
            "#{createTime}, #{updateTime}, #{scheduledTime}, #{liveStartTime}, #{liveEndTime}, #{interruptTime})")
    void insertWithPrimaryId(LiveroomEntity record);

    @Update("<script> " +
            "update `liveroom` " +
            "<trim prefix=\"set\" suffixOverrides=\",\">" +
            "<if test=\"title != null\"> title = #{title}, </if>" +
            "<if test=\"imgPath != null\"> img_path = #{imgPath}, </if>" +
            "<if test=\"cityId != null\"> city_id = #{cityId}, </if>" +
            "<if test=\"scheduledTime != null\"> scheduled_time = #{scheduledTime}, </if>" +
            "update_time = NOW()" +
            "</trim>" +
            "where id = #{id}" +
            "</script>")
    int updateInfo(@Param("id") long id, @Param("title") String title, @Param("imgPath") String imagePath,
                   @Param("scheduledTime") Date scheduledTime, @Param("cityId") Integer cityId);

    @Update("<script> " +
            "update `liveroom` " +
            "<trim prefix=\"set\" suffixOverrides=\",\">" +
            "<if test=\"status != null\"> status = #{status}, </if>" +
            "<if test=\"updateTime != null\"> update_time = #{updateTime}, </if>" +
            "<if test=\"liveStartTime != null\"> live_start_time = #{liveStartTime}, </if>" +
            "<if test=\"liveEndTime != null\"> live_end_time = #{liveEndTime}, </if>" +
            "<if test=\"interruptTime != null\"> interrupt_time = #{interruptTime}, </if>" +
            "<if test=\"scheduledTime != null\"> scheduled_time = #{scheduledTime}, </if>" +
            "</trim>" +
            "where id = #{id}" +
            "</script>")
    int updateStatus(@Param("id") Long id, @Param("status") Byte status, @Param("updateTime") Date updateTime,
                     @Param("liveStartTime") Date liveStartTime, @Param("liveEndTime") Date liveEndTime,
                     @Param("interruptTime") Date interruptTime, @Param("scheduledTime") Date scheduledTime);

    @Update("<script> " +
            "update `liveroom` " +
            "<trim prefix=\"set\" suffixOverrides=\",\">" +
            "<if test=\"type != null\"> type = #{type}, </if>" +
            "<if test=\"status != null\"> status = #{status}, </if>" +
            "<if test=\"updateTime != null\"> update_time = #{updateTime}, </if>" +
            "<if test=\"liveEndTime != null\"> live_end_time = #{liveEndTime}, </if>" +
            "</trim>" +
            "where id = #{id}" +
            "</script>")
    int updateTypeAndStatus(@Param("id") Long id, @Param("type") Byte type, @Param("status") Byte status,
                            @Param("liveEndTime") Date liveEndTime, @Param("updateTime") Date updateTime);

    /**
     * 单个id查询
     */
    @Select("select * from liveroom where id = #{id}")
    LiveroomEntity getById(@Param("id") Long id);

    /**
     * 批量ids查询
     */
    @Select("select * from liveroom WHERE id in (${ids}) order by FIND_IN_SET(id,'${ids}')")
    List<LiveroomEntity> getByIds(@Param("ids") String ids);

    /**
     * 聊天室id查询
     */
    @Select("select id from liveroom WHERE chatroom_id = #{chatroomId}")
    Long getIdByChatroomId(@Param("chatroomId") String chatroomId);

    @Select("select id from liveroom where topic_id = #{topicId}")
    Long getByTopicId(@Param("topicId") long topicId);

    @Select("select id from liveroom where status in (1,2,4) order by id")
    List<Long> getAllRecord();

    /**
     * 根据status批量查询直播间id
     */
    @Select("select id from liveroom where status in (${statusQuery}) order by status asc, id desc limit #{offset},#{limit}")
    List<Long> getByStatusByPaging(@Param("statusQuery") String statusQuery, @Param("offset") int offset,
                                   @Param("limit") int limit);

    @Select("select count(1) from liveroom where status in (${statusQuery})")
    int getCountByStatus(@Param("statusQuery") String statusQuery);

    /**
     * 用户端主播直播间列表
     **/
    @Select("<script>" +
            "select id from liveroom " +
            "<where> " +
            "<if test=\"userId !=null\">host_id = #{userId} </if>" +
            "<if test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "order by status, create_time desc " +
            "<if test=\"offset != null and limit != null\"> limit #{offset},#{limit} </if>" +
            "</script>")
    List<Long> getByUser(@Param("userId") long userId, @Param("statusQuery") String statusQuery,
                         @Param("videoTypes") String videoTypes, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "select count(1) from liveroom " +
            "<where> " +
            "<if test=\"userId !=null\">host_id = #{userId} </if>" +
            "<if test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "</script>")
    int getCountByUser(@Param("userId") long userId, @Param("statusQuery") String statusQuery, @Param("videoTypes") String videoTypes);

    @Select("<script>" +
            "select id from liveroom " +
            "<where> " +
            "<if test=\"hostId !=null\">host_id = #{hostId} </if>" +
            "<if test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "order by create_time desc " +
            "limit #{offset},#{limit}" +
            "</script>")
    List<Long> getByHost(@Param("hostId") long hostId, @Param("statusQuery") String statusQuery,
                         @Param("videoTypes") String videoTypes, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "select count(1) from liveroom " +
            "<where> " +
            "<if test=\"hostId !=null\">host_id = #{hostId} </if>" +
            "<if test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "</script>")
    int getCountByHost(@Param("hostId") long hostId, @Param("statusQuery") String statusQuery, @Param("videoTypes") String videoTypes);

    @Select("select id from liveroom where host_id = #{hostId} and status = 2 and scheduled_time < now() " +
            "order by scheduled_time")
    List<Long> getExpiredPreviewsByHostId(@Param("hostId") long hostId);

    @MapKey("userId")
    @Select("select count(1) as count, host_id as userId from liveroom where host_id in (${userIds}) and `status` " +
            "in (1, 2, 4) GROUP BY host_id")
    Map<BigInteger, Map<String, Object>> getUserValidLiveroomCount(@Param("userIds") String userIds);

    /**
     * 预告列表
     */
    @Select("select id from liveroom where status = 2 and host_id = #{hostId} order by scheduled_time limit #{offset},#{limit} ")
    List<Long> getPreviewsByHost(@Param("hostId") long hostId, @Param("offset") int offset, @Param("limit") int limit);

    /*********************************building ********************/

    @Select("select l.id from liveroom as l join liveroom_building as lb force index(build_liveroomid_status_show_status_liveroom_id)" +
            " on l.id = lb.liveroom_id where lb.building_id = #{buildingId} and lb.status = 2 and " +
            "lb.show_status = 1 and (l.status in (${statusList})) ")
    List<Long> getByBuildingId(@Param("buildingId") long buildingId, @Param("statusList") String statusList);

    @Select("select count(1) from liveroom as l join liveroom_building as lb on l.id = lb.liveroom_id " +
            "where lb.building_id = #{buildingId}  and lb.status = 2 and " +
            "lb.show_status = 1 and (l.status in (${statusList}))")
    int getCountByBuildingId(@Param("buildingId") long buildingId, @Param("statusList") String statusList);

    /**
     * 获取楼盘预告列表
     */
    @Select("select l.id from liveroom as l inner join liveroom_building as lb on l.id = lb.liveroom_id "
            + " where lb.building_id = #{buildingId} and lb.status = 2 and lb.show_status = 1"
            + " and l.status = 2 order by scheduled_time")
    List<Long> getPrewiewsByBuildingId(@Param("buildingId") long buildingId);


    /*********************************city and tag  维度直播间 *******************************************************************/

    /**热门直播间**/
    @Select("select id from liveroom where status = 1 and city_id = #{cityId}")
    List<Long> getLiveByCity(@Param("cityId") int cityId);

    @Select("select id from liveroom where status = 4 and video_type = 1 and city_id = #{cityId} and live_end_time >= #{createTime} ")
    List<Long> getPlayBackByCityId(@Param("cityId") int cityId, @Param("createTime") Date startTime);

    @Select("select id from liveroom where status = 4 and video_type = 1 and city_id = #{cityId} order by live_end_time " +
            "desc limit #{limit}")
    List<Long> getPlayBackByCityIdLimit(@Param("cityId") int cityId, @Param("limit") int limit);

    /**tag直播间**/
    @Select("select a.id from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where b.live_tag_id in " +
            "(${liveTagIds}) and a.status = 1 and a.city_id = #{cityId} order by a.create_time desc")
    List<Long> getLiveByCityAndTag(@Param("liveTagIds") String liveTagIds, @Param("cityId") int cityId);

    @Select("select a.id from liveroom  as a left join liveroom_tag as b on a.id = b.liveroom_id where b.live_tag_id in " +
            "(${liveTagIds}) and a.status = 1 order by a.create_time desc")
    List<Long> getLiveByTag(@Param("liveTagIds") String liveTagIds);

    @Select("select a.id from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) and a.city_id = #{cityId} and a.live_end_time >= #{createTime}  ")
    List<Long> getPlayBackByCityAndTag(@Param("tagIds") String tagIds, @Param("cityId")
            int cityId, @Param("createTime") Date startTime);


    @Select("select a.id from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) and  a.live_end_time >= #{createTime}")
    List<Long> getPlayBackByTag(@Param("tagIds") String tagIds, @Param("createTime") Date startTime);

    @Select("select a.id from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) and a.city_id = #{cityId} order by a.live_end_time desc limit 1000")
    List<Long> getPlayBackByCityAndTagLimit(@Param("tagIds") String tagIds, @Param("cityId") int cityId);

    @Select("select a.* from liveroom as a join liveroom_tag as b on a.id = b.liveroom_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) order by a.live_end_time desc limit 1000")
    List<Long> getPlayBackByTagLimit(@Param("tagIds") String tagIds);

    /**城市预告列表*/
    @Select("select * from liveroom where city_id = #{cityId} and scheduled_time > now() and status = 2 "
            + "order by scheduled_time")
    List<Long> getPreviews(@Param("cityId") int cityId);

    @Select("select a.* from liveroom as a left join liveroom_tag as b on a.id = b.liveroom_id " +
            "where b.live_tag_id in (${liveTagIds}) and a.scheduled_time > now() and a.status = 2 " +
            "order by a.scheduled_time")
    List<Long> getGlobalPreviews(@Param("liveTagIds") String liveTagIds);

    /************************************搜索与推荐 *******************************************************************/

    @Select("select id from liveroom where status= #{status} and city_id = #{cityId} and scheduled_time >= #{time} " +
            "order by id desc limit 200")
    List<Long> getSuggestLiveroomPlayback(@Param("status") int status, @Param("cityId") Integer cityId,
                                          @Param("time") String time);


    @Select("select id from liveroom where status= #{status} and city_id = #{cityId} order by id desc ")
    List<Long> getSuggestLiveroomLive(@Param("status") int status, @Param("cityId") Integer cityId);


    @Select("<script> " +
            "select id from liveroom " +
            "<where> " +
            "<if test=\"cityId != null\"> city_id = #{cityId} </if> " +
            "<if test=\"startTime != null and endTime != null\" > and create_time between #{startTime} and  " +
            "#{endTime} </if> " +
            " and status in (1,4)" +
            "</where> " +
            "order by create_time desc limit #{offset},#{limit} " +
            "</script>")
    List<Long> getByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime, @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    @Select("<script> " +
            "select count(1) from liveroom " +
            "<where> " +
            "<if test=\"cityId != null\"> city_id = #{cityId} </if> " +
            "<if test=\"startTime != null and endTime != null\"> and create_time between #{startTime} and  #{endTime} </if> " +
            "and status in (1,4) " +
            "</where> " +
            "</script>")
    int getCountByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
                                   @Param("endTime") Date endTime);


    @Select("<script> " +
            "select l.id " +
            "from liveroom as l " +
            "<if test=\"hostId != null || hostName != null\">join user as u on l.host_id = u.id  </if>" +
            "<if test=\"tagIds != null\">join liveroom_tag as lt on l.id = lt.liveroom_id  </if>" +
            "<where> " +
            "<if test=\"id != null\">l.id = #{id} </if>" +
            "<if test=\"videoType != null\">and l.video_type = #{videoType} </if>" +
            "<if test=\"statusList != null \">and l.status in (${statusList}) </if>" +
            "<if test=\"cityString != null\">and l.city_id in (${cityString}) </if>" +
            "<if test=\"hostId != null\">and l.host_id = #{hostId} </if>" +
            "<if test=\"hostName != null\">and u.name = #{hostName} </if>" +
            "<if test=\"title != null\">and l.title regexp #{title} </if>" +
            "<if test=\"tagIds != null\">and lt.live_tag_id in (${tagIds}) and lt.status = 1 </if>" +
            "<if test=\"startTime != null and endTime != null \"> and l.create_time between #{startTime} and #{endTime} </if>" +
            "</where> " +
            "order by l.id desc " +
            "<if test=\"offset != null and limit != null\"> limit #{offset},#{limit} </if>" +
            "</script>")
    List<Long> searchLiveroom(@Param("videoType") Integer videoType, @Param("statusList") String statusList,
                              @Param("cityString") String cityString, @Param("hostId") Long hostId,
                              @Param("hostName") String hostName, @Param("id") Long id,
                              @Param("title") String title, @Param("tagIds") String tagIds,
                              @Param("offset") Integer offset, @Param("limit") Integer limit,
                              @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("<script> " +
            "select count(1) " +
            "from liveroom as l " +
            "<if test=\"hostId != null || hostName != null\">join user as u on l.host_id = u.id  </if>" +
            "<if test=\"tagIds != null\">join liveroom_tag as lt on l.id = lt.liveroom_id  </if>" +
            "<where> " +
            "<if test=\"id != null\">l.id = #{id} </if>" +
            "<if test=\"videoType != null\">and l.video_type = #{videoType} </if>" +
            "<if test=\"statusList != null \">and l.status in (${statusList}) </if>" +
            "<if test=\"cityString != null\">and l.city_id in (${cityString}) </if>" +
            "<if test=\"hostId != null\">and l.host_id = #{hostId} </if>" +
            "<if test=\"hostName != null\">and u.name = #{hostName} </if>" +
            "<if test=\"title != null\">and l.title regexp #{title} </if>" +
            "<if test=\"tagIds != null\">and lt.live_tag_id in (${tagIds}) and lt.status = 1 </if>" +
            "<if test=\"startTime != null and endTime != null \"> and l.create_time between #{startTime} and #{endTime} </if>" +
            "</where> " +
            "</script>")
    int searchLiveroomCount(@Param("videoType") Integer videoType, @Param("statusList") String statusList,
                            @Param("cityString") String cityString, @Param("hostId") Long hostId,
                            @Param("hostName") String hostName, @Param("id") Long id,
                            @Param("title") String title, @Param("tagIds") String tagIds,
                            @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Update("update liveroom set interrupt_time = null where id = #{liveroomId}")
    void resetLiveroomInterruptTime(@Param("liveroomId") long liveroomId);


    /**获取直播中*/
    @Select("select * from liveroom where status = 1")
    List<LiveroomEntity> getLiveList();

    /**获取已超时预告*/
    @Select("select id from liveroom where status = 2 and scheduled_time <= now() - interval #{minutes} minute order by scheduled_time")
    List<Long> getMissPreviewIds(@Param("minutes") int minutes);

    /**获取断流直播*/
    @Select("select id from liveroom where status = 1 and interrupt_time is not null and interrupt_time <= now() - interval #{minutes} minute")
    List<Long> getInterruptLiveIds(@Param("minutes") int minutes);

    /**获取直播中*/
    @Select("select * from liveroom where title like #{title} ")
    List<LiveroomEntity> test(@Param("title") String title);


}
