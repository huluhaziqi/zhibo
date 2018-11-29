package com.lin.MyTest.dao.repository;

import com.lin.MyTest.model.entity.TestEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface TestDao {

    @Insert("insert into Test (id, host_id, chatroom_id, topic_id, status, video_type, type, title, info, img_path, " +
            "img_partial_path, city_id, host_rtmp_url, live_rtmp_url, live_hls_url, live_flv_url, history_flv_urls_json, " +
            "like_count, cur_audience_count, total_audience_count, activity_subscribe_count, comment_count, comment_user_count, " +
            "create_time, update_time, scheduled_time, live_start_time, live_end_time, interrupt_time) " +
            "values (#{id}, #{hostId}, #{chatroomId}, #{topicId}, #{status}, #{videoType}, #{type}, #{title}, null, #{imgPath}, " +
            "null, #{cityId}, #{hostRtmpUrl}, #{liveRtmpUrl}, #{liveHlsUrl}, #{liveFlvUrl}, null, " +
            "0, 0, 0, 0, 0, 0, " +
            "#{createTime}, #{updateTime}, #{scheduledTime}, #{liveStartTime}, #{liveEndTime}, #{interruptTime})")
    void insertWithPrimaryId(TestEntity record);

    @Update("<script> " +
            "update `Test` " +
            "<trim prefix=\"set\" suffixOverrides=\",\">" +
            "<if Test=\"title != null\"> title = #{title}, </if>" +
            "<if Test=\"imgPath != null\"> img_path = #{imgPath}, </if>" +
            "<if Test=\"cityId != null\"> city_id = #{cityId}, </if>" +
            "<if Test=\"scheduledTime != null\"> scheduled_time = #{scheduledTime}, </if>" +
            "update_time = NOW()" +
            "</trim>" +
            "where id = #{id}" +
            "</script>")
    int updateInfo(@Param("id") long id, @Param("title") String title, @Param("imgPath") String imagePath,
                   @Param("scheduledTime") Date scheduledTime, @Param("cityId") Integer cityId);

    @Update("<script> " +
            "update `Test` " +
            "<trim prefix=\"set\" suffixOverrides=\",\">" +
            "<if Test=\"status != null\"> status = #{status}, </if>" +
            "<if Test=\"updateTime != null\"> update_time = #{updateTime}, </if>" +
            "<if Test=\"liveStartTime != null\"> live_start_time = #{liveStartTime}, </if>" +
            "<if Test=\"liveEndTime != null\"> live_end_time = #{liveEndTime}, </if>" +
            "<if Test=\"interruptTime != null\"> interrupt_time = #{interruptTime}, </if>" +
            "<if Test=\"scheduledTime != null\"> scheduled_time = #{scheduledTime}, </if>" +
            "</trim>" +
            "where id = #{id}" +
            "</script>")
    int updateStatus(@Param("id") Long id, @Param("status") Byte status, @Param("updateTime") Date updateTime,
                     @Param("liveStartTime") Date liveStartTime, @Param("liveEndTime") Date liveEndTime,
                     @Param("interruptTime") Date interruptTime, @Param("scheduledTime") Date scheduledTime);

    @Update("<script> " +
            "update `Test` " +
            "<trim prefix=\"set\" suffixOverrides=\",\">" +
            "<if Test=\"type != null\"> type = #{type}, </if>" +
            "<if Test=\"status != null\"> status = #{status}, </if>" +
            "<if Test=\"updateTime != null\"> update_time = #{updateTime}, </if>" +
            "<if Test=\"liveEndTime != null\"> live_end_time = #{liveEndTime}, </if>" +
            "</trim>" +
            "where id = #{id}" +
            "</script>")
    int updateTypeAndStatus(@Param("id") Long id, @Param("type") Byte type, @Param("status") Byte status,
                            @Param("liveEndTime") Date liveEndTime, @Param("updateTime") Date updateTime);

    /**
     * 单个id查询
     */
    @Select("select * from Test where id = #{id}")
    TestEntity getById(@Param("id") Long id);

    /**
     * 批量ids查询
     */
    @Select("select * from Test WHERE id in (${ids}) order by FIND_IN_SET(id,'${ids}')")
    List<TestEntity> getByIds(@Param("ids") String ids);

    /**
     * 聊天室id查询
     */
    @Select("select id from Test WHERE chatroom_id = #{chatroomId}")
    Long getIdByChatroomId(@Param("chatroomId") String chatroomId);

    @Select("select id from Test where topic_id = #{topicId}")
    Long getByTopicId(@Param("topicId") long topicId);

    @Select("select id from Test where status in (1,2,4) order by id")
    List<Long> getAllRecord();

    /**
     * 根据status批量查询xxxxid
     */
    @Select("select id from Test where status in (${statusQuery}) order by status asc, id desc limit #{offset},#{limit}")
    List<Long> getByStatusByPaging(@Param("statusQuery") String statusQuery, @Param("offset") int offset,
                                   @Param("limit") int limit);

    @Select("select count(1) from Test where status in (${statusQuery})")
    int getCountByStatus(@Param("statusQuery") String statusQuery);

    /**
     * 用户端主播xxxx列表
     **/
    @Select("<script>" +
            "select id from Test " +
            "<where> " +
            "<if Test=\"userId !=null\">host_id = #{userId} </if>" +
            "<if Test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if Test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "order by status, create_time desc " +
            "<if Test=\"offset != null and limit != null\"> limit #{offset},#{limit} </if>" +
            "</script>")
    List<Long> getByUser(@Param("userId") long userId, @Param("statusQuery") String statusQuery,
                         @Param("videoTypes") String videoTypes, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "select count(1) from Test " +
            "<where> " +
            "<if Test=\"userId !=null\">host_id = #{userId} </if>" +
            "<if Test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if Test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "</script>")
    int getCountByUser(@Param("userId") long userId, @Param("statusQuery") String statusQuery, @Param("videoTypes") String videoTypes);

    @Select("<script>" +
            "select id from Test " +
            "<where> " +
            "<if Test=\"hostId !=null\">host_id = #{hostId} </if>" +
            "<if Test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if Test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "order by create_time desc " +
            "limit #{offset},#{limit}" +
            "</script>")
    List<Long> getByHost(@Param("hostId") long hostId, @Param("statusQuery") String statusQuery,
                         @Param("videoTypes") String videoTypes, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "select count(1) from Test " +
            "<where> " +
            "<if Test=\"hostId !=null\">host_id = #{hostId} </if>" +
            "<if Test=\"statusQuery !=null\">and status in (${statusQuery}) </if>" +
            "<if Test=\"videoTypes !=null\">and video_type in (${videoTypes}) </if>" +
            "</where>" +
            "</script>")
    int getCountByHost(@Param("hostId") long hostId, @Param("statusQuery") String statusQuery, @Param("videoTypes") String videoTypes);

    @Select("select id from Test where host_id = #{hostId} and status = 2 and scheduled_time < now() " +
            "order by scheduled_time")
    List<Long> getExpiredPreviewsByHostId(@Param("hostId") long hostId);

    @MapKey("userId")
    @Select("select count(1) as count, host_id as userId from Test where host_id in (${userIds}) and `status` " +
            "in (1, 2, 4) GROUP BY host_id")
    Map<BigInteger, Map<String, Object>> getUserValidTestCount(@Param("userIds") String userIds);

    /**
     * 预告列表
     */
    @Select("select id from Test where status = 2 and host_id = #{hostId} order by scheduled_time limit #{offset},#{limit} ")
    List<Long> getPreviewsByHost(@Param("hostId") long hostId, @Param("offset") int offset, @Param("limit") int limit);

    /*********************************building ********************/

    @Select("select l.id from Test as l join Test_building as lb force index(build_Testid_status_show_status_Test_id)" +
            " on l.id = lb.Test_id where lb.building_id = #{buildingId} and lb.status = 2 and " +
            "lb.show_status = 1 and (l.status in (${statusList})) ")
    List<Long> getByBuildingId(@Param("buildingId") long buildingId, @Param("statusList") String statusList);

    @Select("select count(1) from Test as l join Test_building as lb on l.id = lb.Test_id " +
            "where lb.building_id = #{buildingId}  and lb.status = 2 and " +
            "lb.show_status = 1 and (l.status in (${statusList}))")
    int getCountByBuildingId(@Param("buildingId") long buildingId, @Param("statusList") String statusList);

    /**
     * 获取xx预告列表
     */
    @Select("select l.id from Test as l inner join Test_building as lb on l.id = lb.Test_id "
            + " where lb.building_id = #{buildingId} and lb.status = 2 and lb.show_status = 1"
            + " and l.status = 2 order by scheduled_time")
    List<Long> getPrewiewsByBuildingId(@Param("buildingId") long buildingId);


    /*********************************city and tag  维度xxxx *******************************************************************/

    /**热门xxxx**/
    @Select("select id from Test where status = 1 and city_id = #{cityId}")
    List<Long> getLiveByCity(@Param("cityId") int cityId);

    @Select("select id from Test where status = 4 and video_type = 1 and city_id = #{cityId} and live_end_time >= #{createTime} ")
    List<Long> getPlayBackByCityId(@Param("cityId") int cityId, @Param("createTime") Date startTime);

    @Select("select id from Test where status = 4 and video_type = 1 and city_id = #{cityId} order by live_end_time " +
            "desc limit #{limit}")
    List<Long> getPlayBackByCityIdLimit(@Param("cityId") int cityId, @Param("limit") int limit);

    /**tagxxxx**/
    @Select("select a.id from Test as a left join Test_tag as b on a.id = b.Test_id where b.live_tag_id in " +
            "(${liveTagIds}) and a.status = 1 and a.city_id = #{cityId} order by a.create_time desc")
    List<Long> getLiveByCityAndTag(@Param("liveTagIds") String liveTagIds, @Param("cityId") int cityId);

    @Select("select a.id from Test  as a left join Test_tag as b on a.id = b.Test_id where b.live_tag_id in " +
            "(${liveTagIds}) and a.status = 1 order by a.create_time desc")
    List<Long> getLiveByTag(@Param("liveTagIds") String liveTagIds);

    @Select("select a.id from Test as a left join Test_tag as b on a.id = b.Test_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) and a.city_id = #{cityId} and a.live_end_time >= #{createTime}  ")
    List<Long> getPlayBackByCityAndTag(@Param("tagIds") String tagIds, @Param("cityId")
            int cityId, @Param("createTime") Date startTime);


    @Select("select a.id from Test as a left join Test_tag as b on a.id = b.Test_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) and  a.live_end_time >= #{createTime}")
    List<Long> getPlayBackByTag(@Param("tagIds") String tagIds, @Param("createTime") Date startTime);

    @Select("select a.id from Test as a left join Test_tag as b on a.id = b.Test_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) and a.city_id = #{cityId} order by a.live_end_time desc limit 1000")
    List<Long> getPlayBackByCityAndTagLimit(@Param("tagIds") String tagIds, @Param("cityId") int cityId);

    @Select("select a.* from Test as a join Test_tag as b on a.id = b.Test_id where a.status = 4 " +
            "and b.live_tag_id in (${tagIds}) order by a.live_end_time desc limit 1000")
    List<Long> getPlayBackByTagLimit(@Param("tagIds") String tagIds);

    /**城市预告列表*/
    @Select("select * from Test where city_id = #{cityId} and scheduled_time > now() and status = 2 "
            + "order by scheduled_time")
    List<Long> getPreviews(@Param("cityId") int cityId);

    @Select("select a.* from Test as a left join Test_tag as b on a.id = b.Test_id " +
            "where b.live_tag_id in (${liveTagIds}) and a.scheduled_time > now() and a.status = 2 " +
            "order by a.scheduled_time")
    List<Long> getGlobalPreviews(@Param("liveTagIds") String liveTagIds);

    /************************************搜索与推荐 *******************************************************************/

    @Select("select id from Test where status= #{status} and city_id = #{cityId} and scheduled_time >= #{time} " +
            "order by id desc limit 200")
    List<Long> getSuggestTestPlayback(@Param("status") int status, @Param("cityId") Integer cityId,
                                          @Param("time") String time);


    @Select("select id from Test where status= #{status} and city_id = #{cityId} order by id desc ")
    List<Long> getSuggestTestLive(@Param("status") int status, @Param("cityId") Integer cityId);


    @Select("<script> " +
            "select id from Test " +
            "<where> " +
            "<if Test=\"cityId != null\"> city_id = #{cityId} </if> " +
            "<if Test=\"startTime != null and endTime != null\" > and create_time between #{startTime} and  " +
            "#{endTime} </if> " +
            " and status in (1,4)" +
            "</where> " +
            "order by create_time desc limit #{offset},#{limit} " +
            "</script>")
    List<Long> getByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime, @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    @Select("<script> " +
            "select count(1) from Test " +
            "<where> " +
            "<if Test=\"cityId != null\"> city_id = #{cityId} </if> " +
            "<if Test=\"startTime != null and endTime != null\"> and create_time between #{startTime} and  #{endTime} </if> " +
            "and status in (1,4) " +
            "</where> " +
            "</script>")
    int getCountByCityAndDateToSeo(@Param("cityId") Integer cityId, @Param("startTime") Date startTime,
                                   @Param("endTime") Date endTime);


    @Select("<script> " +
            "select l.id " +
            "from Test as l " +
            "<if Test=\"hostId != null || hostName != null\">join user as u on l.host_id = u.id  </if>" +
            "<if Test=\"tagIds != null\">join Test_tag as lt on l.id = lt.Test_id  </if>" +
            "<where> " +
            "<if Test=\"id != null\">l.id = #{id} </if>" +
            "<if Test=\"videoType != null\">and l.video_type = #{videoType} </if>" +
            "<if Test=\"statusList != null \">and l.status in (${statusList}) </if>" +
            "<if Test=\"cityString != null\">and l.city_id in (${cityString}) </if>" +
            "<if Test=\"hostId != null\">and l.host_id = #{hostId} </if>" +
            "<if Test=\"hostName != null\">and u.name = #{hostName} </if>" +
            "<if Test=\"title != null\">and l.title regexp #{title} </if>" +
            "<if Test=\"tagIds != null\">and lt.live_tag_id in (${tagIds}) and lt.status = 1 </if>" +
            "<if Test=\"startTime != null and endTime != null \"> and l.create_time between #{startTime} and #{endTime} </if>" +
            "</where> " +
            "order by l.id desc " +
            "<if Test=\"offset != null and limit != null\"> limit #{offset},#{limit} </if>" +
            "</script>")
    List<Long> searchTest(@Param("videoType") Integer videoType, @Param("statusList") String statusList,
                              @Param("cityString") String cityString, @Param("hostId") Long hostId,
                              @Param("hostName") String hostName, @Param("id") Long id,
                              @Param("title") String title, @Param("tagIds") String tagIds,
                              @Param("offset") Integer offset, @Param("limit") Integer limit,
                              @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("<script> " +
            "select count(1) " +
            "from Test as l " +
            "<if Test=\"hostId != null || hostName != null\">join user as u on l.host_id = u.id  </if>" +
            "<if Test=\"tagIds != null\">join Test_tag as lt on l.id = lt.Test_id  </if>" +
            "<where> " +
            "<if Test=\"id != null\">l.id = #{id} </if>" +
            "<if Test=\"videoType != null\">and l.video_type = #{videoType} </if>" +
            "<if Test=\"statusList != null \">and l.status in (${statusList}) </if>" +
            "<if Test=\"cityString != null\">and l.city_id in (${cityString}) </if>" +
            "<if Test=\"hostId != null\">and l.host_id = #{hostId} </if>" +
            "<if Test=\"hostName != null\">and u.name = #{hostName} </if>" +
            "<if Test=\"title != null\">and l.title regexp #{title} </if>" +
            "<if Test=\"tagIds != null\">and lt.live_tag_id in (${tagIds}) and lt.status = 1 </if>" +
            "<if Test=\"startTime != null and endTime != null \"> and l.create_time between #{startTime} and #{endTime} </if>" +
            "</where> " +
            "</script>")
    int searchTestCount(@Param("videoType") Integer videoType, @Param("statusList") String statusList,
                            @Param("cityString") String cityString, @Param("hostId") Long hostId,
                            @Param("hostName") String hostName, @Param("id") Long id,
                            @Param("title") String title, @Param("tagIds") String tagIds,
                            @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Update("update Test set interrupt_time = null where id = #{TestId}")
    void resetTestInterruptTime(@Param("TestId") long TestId);


    /**获取xx中*/
    @Select("select * from Test where status = 1")
    List<TestEntity> getLiveList();

    /**获取已超时预告*/
    @Select("select id from Test where status = 2 and scheduled_time <= now() - interval #{minutes} minute order by scheduled_time")
    List<Long> getMissPreviewIds(@Param("minutes") int minutes);

    /**获取断流xx*/
    @Select("select id from Test where status = 1 and interrupt_time is not null and interrupt_time <= now() - interval #{minutes} minute")
    List<Long> getInterruptLiveIds(@Param("minutes") int minutes);

    /**获取xx中*/
    @Select("select * from Test where title like #{title} ")
    List<TestEntity> test(@Param("title") String title);


}
