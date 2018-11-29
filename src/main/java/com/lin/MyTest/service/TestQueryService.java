package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.Test.ListWithTotalCount;
import com.lin.MyTest.model.biz.Test.TestInfo;
import com.lin.MyTest.model.request.PagingInterface;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TestQueryService {

    /**
     * 综合查找
     */
    List<TestInfo> list(Integer videoType, List<Integer> statusList, Long hostId, String hostName, Long id,
                            String title, List<Integer> tagIds, List<Integer> cityIds, Date startTime,
                            Date endTime, Integer offset, Integer limit);

    int count(Integer videoType, List<Integer> statusList, Long hostId, String hostName, Long id,
              String title, List<Integer> tagIds, List<Integer> cityIds, Date startTime, Date endTime);

    List<Long> getAllTests();

    /*************************************************************Status 批量处理***************************************/
    List<TestInfo> getByStatus(List<Byte> statusList, PagingInterface paging);

    int getCountByStatus(List<Byte> statusList);

    /*************************************************************User 批量处理***************************************/

    /**
     * 用户端、主播端xxxx列表
     */
    List<TestInfo> getUserTests(Long userId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit);

    int getUserTestsCount(Long userId, List<Byte> statusList, List<Byte> videoTypeList);


    List<TestInfo> getHostTests(Long hostId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit);

    int getHostTestsCount(Long hostId, List<Byte> statusList, List<Byte> videoTypeList);


    List<TestInfo> getPreviewsByHost(Long hostId, int offset, int limit);

    List<TestInfo> getMissPreviewByHost(Long hostId);

    /**
     * xx预告列表
     * */
    List<TestInfo> getPreviewsByBuildingId(long buildingId);

    /**
     * 刷新xx预告列表、xxxx列表
     * */
    void refreshBuildingTestCache(long buildingId);

    /**
     * 刷新xx预告列表
     * */
    void refreshBuildingPreviewsCache(long buildingId, List<Long> TestIds);

    /**
     * 刷新xxxx回放列表
     * */
    void refreshBuildingLiveAndPlaybackCache(long buildingId, List<Long> TestIds);


    void refreshAllBuildingTestCache();
    /**
     * 获取主播有效xxxx数目
     *
     */

    Map<Long, Integer> getUserValidTestCount(List<Long> userIds);

    /*************************************************************城市 热门、标签 批量处理***************************************/

    /**
     *  城市推荐xx列表
     */
    ListWithTotalCount<TestInfo> getHotListByCity(Integer cityId, int offset, int limit);

    /**
     *  城市频道xx列表
     */
    ListWithTotalCount<TestInfo> getLabelListByCity(Integer channelId, Integer cityId, int offset, int limit);

    /**
     * 刷新tag和hot list
     */
    void refreshHotAndTagCache(long TestId);

    void refreshTagCache(List<Long> tagIds, long TestId, Integer cityId, int videoType);


    /**worker 定时更新城市热门xx标签xx*/
    void refreshHotTests();

    /**worker 定时更新城市热门xx标签xx*/
    void refreshLabelTests();
        /**
         * 城市预告列表
         */
    ListWithTotalCount<TestInfo> getPreviewsByCity(int cityId, int offset, int limit);

    /**
     * 刷新城市预告列表缓存
     */
    void refreshCityPreviewsCache(int cityId, List<Long> Tests);

    /**
     * 城市xxxx创建数目
     */
    int getCityTestCount(Integer cityId, Date startTime, Date endTime);

    /**
     * 城市创建xxxx
     */
    List<TestInfo> getCityTest(Integer cityId, Date startTime, Date endTime);



    /*************************************************************tag and channel***************************************/

    List<Long> getTagsByChannel(Integer channelId);

    Map<Long, Integer> getTagChannelMap();


    /*************************************************************推荐与搜索 ********************************************/

    List<TestInfo> searchTest(String key, Integer cityId);

    List<TestInfo> suggestTest(Integer cityId);

    //用于百度seo
    List<TestInfo> getTestToSeo(Integer cityId, Date startDate, Date endDate, Integer offset, Integer limit);

    int getTestCountToSeo(Integer cityId, Date startDate, Date endDate);


    /*********************************************************预告关注********************************************************/
    void forecastOn(long uid, long id);

    void forecastOff(long uid, long id);

    List<Long> getForecastTestIdList(long uid);

    List<Long> getForecastSubscriberList(long TestId);


    /**创建xxxx频率限制*/
    void checkUserCreateLimit(long userId);

    void setUserCreateLimit(long userId);

    void removeUserCreateLimit(long userId);

    /********************国家*****************/
    List<TestInfo> getByCountryCode(String code, Integer offset, Integer limit);

    Map<String,List<TestInfo>> getByCountryCodes(List<String> codes);
}
