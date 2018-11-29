package com.lin.MyTest.dao;


import com.lin.MyTest.model.entity.StatTestEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatTestDao {

    @Insert("insert into stat_Test (Test_id, total_vv, live_app_vv, live_wap_vv, live_pc_vv, live_top_audience_count, " +
            "playback_app_vv, playback_wap_vv, playback_pc_vv, robot_vv, like_count, " +
            "wechat_press_count, phone_press_count, comment_count, robot_comment_count, activity_subscribe_count) " +
            "values (#{TestId}, #{totalVv}, #{liveAppVv}, #{liveWapVv}, #{livePcVv}, #{liveTopAudienceCount}, " +
            "#{playbackAppVv}, #{playbackWapVv}, #{playbackPcVv}, #{robotVv}, #{likeCount}, " +
            "#{wechatPressCount}, #{phonePressCount}, #{commentCount}, #{robotCommentCount}, #{activitySubscribeCount}) " +
            "on duplicate key update total_vv = #{totalVv}, live_app_vv = #{liveAppVv}, live_wap_vv = #{liveWapVv}, " +
            "live_pc_vv = #{livePcVv}, live_top_audience_count = #{liveTopAudienceCount}, " +
            "playback_app_vv = #{playbackAppVv}, playback_wap_vv = #{playbackWapVv}, playback_pc_vv = #{playbackPcVv}, " +
            "robot_vv = #{robotVv}, like_count = #{likeCount}, wechat_press_count = #{wechatPressCount}, " +
            "phone_press_count = #{phonePressCount}, comment_count = #{commentCount}, robot_comment_count = #{robotCommentCount}, " +
            "activity_subscribe_count = #{activitySubscribeCount}")
    int insertOrUpdate(StatTestEntity record);

    @Select("select * from stat_Test where Test_id = #{TestId}")
    StatTestEntity getByTestId(@Param("TestId") Long TestId);

    @Select("select * from stat_Test where Test_id in (${ids})")
    List<StatTestEntity> getListByTestIds(@Param("ids") String ids);

}
