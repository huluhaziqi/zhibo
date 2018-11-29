package com.lin.MyTest.webclient;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

public interface WebClient {

    @POST("/liveA")
    void createTopic(
            @Query("forumId") int forumId,
            @Query("userId") long hostId,
            @Query(value = "topicTitle") String topicTitle,
            @Query(value = "topicContent") String topicContent);

    @POST("/liveB")
    Void deleteTopic(@Query("topicId") long topicId,
                     @Query("userId") long userId);

    @POST("/v4/")
    Void test2(@Body Map<String, Object> importAccount);
}
