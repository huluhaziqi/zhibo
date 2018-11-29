package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.liveroom.ListWithTotalCount;
import com.lin.MyTest.model.biz.liveroom.LiveroomInfo;
import com.lin.MyTest.model.request.PagingInterface;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LiveroomQueryService {

    /**
     * 综合查找
     */
    List<LiveroomInfo> list(Integer videoType, List<Integer> statusList, Long hostId, String hostName, Long id,
                            String title, List<Integer> tagIds, List<Integer> cityIds, Date startTime,
                            Date endTime, Integer offset, Integer limit);

    int count(Integer videoType, List<Integer> statusList, Long hostId, String hostName, Long id,
              String title, List<Integer> tagIds, List<Integer> cityIds, Date startTime, Date endTime);

    List<Long> getAllLiverooms();

    /*************************************************************Status 批量处理***************************************/
    List<LiveroomInfo> getByStatus(List<Byte> statusList, PagingInterface paging);

    int getCountByStatus(List<Byte> statusList);

    /*************************************************************User 批量处理***************************************/

    /**
     * 用户端、主播端直播间列表
     */
    List<LiveroomInfo> getUserLiverooms(Long userId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit);

    int getUserLiveroomsCount(Long userId, List<Byte> statusList, List<Byte> videoTypeList);


    List<LiveroomInfo> getHostLiverooms(Long hostId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit);

    int getHostLiveroomsCount(Long hostId, List<Byte> statusList, List<Byte> videoTypeList);


    List<LiveroomInfo> getPreviewsByHost(Long hostId, int offset, int limit);

    List<LiveroomInfo> getMissPreviewByHost(Long hostId);

    /**
     * 楼盘预告列表
     * */
    List<LiveroomInfo> getPreviewsByBuildingId(long buildingId);

    /**
     * 刷新楼盘预告列表、直播间列表
     * */
    void refreshBuildingLiveroomCache(long buildingId);

    /**
     * 刷新楼盘预告列表
     * */
    void refreshBuildingPreviewsCache(long buildingId, List<Long> liveroomIds);

    /**
     * 刷新楼盘直播回放列表
     * */
    void refreshBuildingLiveAndPlaybackCache(long buildingId, List<Long> liveroomIds);


    void refreshAllBuildingLiveroomCache();
    /**
     * 获取主播有效直播间数目
     *
     */

    Map<Long, Integer> getUserValidLiveroomCount(List<Long> userIds);

    /*************************************************************城市 热门、标签 批量处理***************************************/

    /**
     *  城市推荐直播列表
     */
    ListWithTotalCount<LiveroomInfo> getHotListByCity(Integer cityId, int offset, int limit);

    /**
     *  城市频道直播列表
     */
    ListWithTotalCount<LiveroomInfo> getLabelListByCity(Integer channelId, Integer cityId, int offset, int limit);

    /**
     * 刷新tag和hot list
     */
    void refreshHotAndTagCache(long liveroomId);

    void refreshTagCache(List<Long> tagIds, long liveroomId, Integer cityId, int videoType);


    /**worker 定时更新城市热门直播标签直播*/
    void refreshHotLiverooms();

    /**worker 定时更新城市热门直播标签直播*/
    void refreshLabelLiverooms();
        /**
         * 城市预告列表
         */
    ListWithTotalCount<LiveroomInfo> getPreviewsByCity(int cityId, int offset, int limit);

    /**
     * 刷新城市预告列表缓存
     */
    void refreshCityPreviewsCache(int cityId, List<Long> liverooms);

    /**
     * 城市直播间创建数目
     */
    int getCityLiveroomCount(Integer cityId, Date startTime, Date endTime);

    /**
     * 城市创建直播间
     */
    List<LiveroomInfo> getCityLiveroom(Integer cityId, Date startTime, Date endTime);



    /*************************************************************tag and channel***************************************/

    List<Long> getTagsByChannel(Integer channelId);

    Map<Long, Integer> getTagChannelMap();


    /*************************************************************推荐与搜索 ********************************************/

    List<LiveroomInfo> searchLiveroom(String key, Integer cityId);

    List<LiveroomInfo> suggestLiveroom(Integer cityId);

    //用于百度seo
    List<LiveroomInfo> getLiveroomToSeo(Integer cityId, Date startDate, Date endDate, Integer offset, Integer limit);

    int getLiveroomCountToSeo(Integer cityId, Date startDate, Date endDate);


    /*********************************************************预告关注********************************************************/
    void forecastOn(long uid, long id);

    void forecastOff(long uid, long id);

    List<Long> getForecastLiveroomIdList(long uid);

    List<Long> getForecastSubscriberList(long liveroomId);


    /**创建直播间频率限制*/
    void checkUserCreateLimit(long userId);

    void setUserCreateLimit(long userId);

    void removeUserCreateLimit(long userId);

    /********************国家*****************/
    List<LiveroomInfo> getByCountryCode(String code, Integer offset, Integer limit);

    Map<String,List<LiveroomInfo>> getByCountryCodes(List<String> codes);
}
