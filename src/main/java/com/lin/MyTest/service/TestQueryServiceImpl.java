package com.lin.MyTest.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lin.MyTest.dao.repository.TestDao;
import com.lin.MyTest.enums.ChannelIdEnum;
import com.lin.MyTest.enums.TestStatusEnum;
import com.lin.MyTest.enums.VideoTypeEnum;
import com.lin.MyTest.exception.TestStateException;
import com.lin.MyTest.model.biz.Test.ListWithTotalCount;
import com.lin.MyTest.model.biz.Test.TestInfo;
import com.lin.MyTest.model.biz.Test.TestSearch;
import com.lin.MyTest.model.entity.TagEntity;
import com.lin.MyTest.model.request.PagingInterface;
import com.lin.MyTest.redisdao.TestRedisDao;
import com.lin.MyTest.util.TimeUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TestQueryServiceImpl implements TestQueryService {

    @Autowired
    private TestSearchService TestSearchService;

    @Autowired
    private StatTestService statTestService;

    @Autowired
    private TestService TestService;

    @Resource
    private TestRedisDao TestRedisDao;

    @Autowired
    private TestDao TestDao;

    @Value("${spring.H5BaseUrl}")
    private String H5BaseUrl;

    @Value("${spring.PCBaseUrl}")
    private String PCBaseUrl;

    @Value("${building.houseWebBaseUrl}")
    private String buildingWebBaseUrl;

    private ExecutorService threadPool;

    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    private static Map<Long, Integer> tagChannelMap = new HashMap<Long, Integer>() {
        {
            put(1L, 1);
            put(2L, 2);
            put(4L, 3);
            put(8L, 4);
            put(5L, 5);
            put(6L, 6);
            put(9L, 7);
        }
    }; //k:tagId v:channelId

    @Override
    public Map<Long, Integer> getTagChannelMap() {
        return tagChannelMap;
    }

    @PostConstruct
    void init() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Test-queryservice-pool-%d").build();
        threadPool = new ThreadPoolExecutor(10, 50, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /***
     * 综合筛选xxxx接口
     */
    @Override
    public List<TestInfo> list(Integer videoType, List<Integer> statusList, Long hostId,
                               String hostName, Long id, String title, List<Integer> tagIds,
                               List<Integer> cityIds, Date startTime, Date endTime, Integer offset,
                               Integer limit) {
        String cityString = null;
        if (!CollectionUtils.isEmpty(cityIds)) {
            cityString = StringUtils.join(cityIds, ",");
        }
        String statusListStr = null;
        if (!CollectionUtils.isEmpty(statusList)) {
            statusListStr = StringUtils.join(statusList, ",");
        }
        String tagsIdsStr = null;
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagsIdsStr = StringUtils.join(tagIds, ",");
        }
        List<Long> TestIds = TestDao.searchTest(videoType, statusListStr, cityString,
                hostId, hostName, id, title, tagsIdsStr, offset, limit, startTime, endTime);
        return TestService.getTestsInfoList(TestIds);
    }

    /**
     * 获取xxxx数目
     */
    @Override
    public int count(Integer videoType, List<Integer> statusList, Long hostId, String hostName,
                     Long id, String title, List<Integer> tagIds, List<Integer> cityIds, Date startTime,
                     Date endTime) {
        String cityString = null;
        if (!CollectionUtils.isEmpty(cityIds)) {
            cityString = StringUtils.join(cityIds, ",");
        }
        String statusListStr = null;
        if (!CollectionUtils.isEmpty(statusList)) {
            statusListStr = StringUtils.join(statusList, ",");
        }
        String tagsIdsStr = null;
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagsIdsStr = StringUtils.join(tagIds, ",");
        }
        return TestDao.searchTestCount(videoType, statusListStr, cityString,
                hostId, hostName, id, title, tagsIdsStr, startTime, endTime);
    }

    @Override
    public List<Long> getAllTests() {
        return TestDao.getAllRecord();
    }

    /*************************************************************Status 批量处理***************************************/
    /**
     * 通过状态查询xxxx
     */
    @Override
    public List<TestInfo> getByStatus(List<Byte> statusList, PagingInterface paging) {
        if (CollectionUtils.isEmpty(statusList)) {
            return new ArrayList<>();
        }
        String statusQuery = statusList.stream().map(String::valueOf).collect(Collectors.joining(", "));
        List<Long> TestIds = TestDao.getByStatusByPaging(statusQuery, paging.getOffset(), paging.getLimit());
        return TestService.getTestsInfoList(TestIds);
    }

    /**
     * 通过状态查询xxxx数目
     */
    @Override
    public int getCountByStatus(List<Byte> statusList) {
        if (CollectionUtils.isEmpty(statusList)) {
            return 0;
        }
        String statusQuery = statusList.stream().map(String::valueOf).collect(Collectors.joining(", "));
        return TestDao.getCountByStatus(statusQuery);

    }

    /*************************************************************User and host 批量处理***************************************/

    /**
     * 普通用户看到的列表
     */
    @Override
    public List<TestInfo> getUserTests(Long userId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit) {
        if (userId == null) {
            return new ArrayList<>();
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        List<Long> TestIds = TestDao.getByUser(userId, statusQuery, videoTypes, offset, limit);
        return TestService.getTestsInfoList(TestIds);
    }

    /**
     * 普通用户列表数目获取
     */
    @Override
    public int getUserTestsCount(Long userId, List<Byte> statusList, List<Byte> videoTypeList) {
        if (userId == null) {
            return 0;
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        return TestDao.getCountByUser(userId, statusQuery, videoTypes);
    }

    /**
     * 主播列表获取
     */
    @Override
    public List<TestInfo> getHostTests(Long hostId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit) {
        if (hostId == null) {
            return new ArrayList<>();
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        List<Long> TestIds = TestDao.getByHost(hostId, statusQuery, videoTypes, offset, limit);
        return TestService.getTestsInfoList(TestIds);
    }

    /**
     * 主播列表数目获取
     */
    @Override
    public int getHostTestsCount(Long hostId, List<Byte> statusList, List<Byte> videoTypeList) {
        if (hostId == null) {
            return 0;
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        return TestDao.getCountByHost(hostId, statusQuery, videoTypes);
    }

    /**
     * 主播预告列表
     */
    @Override
    public List<TestInfo> getPreviewsByHost(Long hostId, int offset, int limit) {
        if (hostId == null) {
            return new ArrayList<>();
        }
        List<Long> TestIds = TestDao.getPreviewsByHost(hostId, offset, limit);
        return TestService.getTestsInfoList(TestIds);
    }

    @Override
    public List<TestInfo> getMissPreviewByHost(Long hostId) {
        List<Long> ids = TestDao.getExpiredPreviewsByHostId(hostId);
        return TestService.getTestsInfoList(ids);
    }

    @Override
    public List<TestInfo> getPreviewsByBuildingId(long buildingId) {
        return null;
    }


    //需要排序，xx1，4
    private List<Long> getLiveAndPlaybackByBuildingIdFromDb(Long buildingId) {
        if (buildingId == null) {
            return new ArrayList<>();
        }
        List<Long> list = TestDao.getByBuildingId(buildingId, StringUtils.join(Arrays.asList(1, 4), ","));
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<TestInfo> TestInfos = TestService.getTestsInfoList(list);
        // 先排序
        TestInfos = sortTestInfo(TestInfos);
        return TestInfos.stream().map(TestInfo::getId).collect(Collectors.toList());
    }

    /**
     * 刷新xx预告、xxxx缓存
     */
    @Override
    public void refreshBuildingTestCache(long buildingId) {
        refreshBuildingPreviewsCache(buildingId, null);
        refreshBuildingLiveAndPlaybackCache(buildingId, null);
    }

    @Override
    public void refreshBuildingPreviewsCache(long buildingId, List<Long> TestIds) {

    }

    @Override
    public void refreshBuildingLiveAndPlaybackCache(long buildingId, List<Long> TestIds) {

    }

    @Override
    public void refreshAllBuildingTestCache() {

    }

    @Override
    public Map<Long, Integer> getUserValidTestCount(List<Long> userIds) {
        Map<Long, Integer> result = new HashMap<>();
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }
        if (userIds.size() > 50) {
            userIds = userIds.subList(0, 50);
        }
        String userIdsString = StringUtils.join(userIds, ',');
        Map<BigInteger, Map<String, Object>> queryResultMap = TestDao.getUserValidTestCount(userIdsString);
        if (MapUtils.isEmpty(queryResultMap)) {
            return result;
        }
        if (queryResultMap.containsKey(null)) {
            queryResultMap.remove(null);
        }
        userIds.forEach(o -> {
            if (queryResultMap.containsKey(BigInteger.valueOf(o))) {
                Map<String, Object> perMap = queryResultMap.get(BigInteger.valueOf(o));
                if (MapUtils.isNotEmpty(perMap) && perMap.containsKey("count")) {
                    result.put(o, Math.toIntExact((Long) perMap.get("count")));
                }
            }
        });
        return result;
    }

    @Override
    public ListWithTotalCount<TestInfo> getHotListByCity(Integer cityId, int offset, int limit) {
        ListWithTotalCount<TestInfo> result = new ListWithTotalCount<>();
        List<TestInfo> Tests;
        List<TestInfo> resultList = new ArrayList<>();
        int TestTotalSize = 0;

        // 缓存中存在
        if (checkCityHotTestExist(cityId)) {
            TestTotalSize = (int) TestRedisDao.getCityHotListCountRedis(cityId);
            int endIndex = (offset + limit - 1) < TestTotalSize ? (offset + limit - 1) : TestTotalSize;
            List<String> lrange = TestRedisDao.getCityHotListRedis(cityId, offset, endIndex);
            if (!CollectionUtils.isEmpty(lrange)) {
                try {
                    List<Long> TestIds = lrange.stream().map(Long::parseLong).collect(Collectors.toList());
                    List<TestInfo> TestInfosFromRedis = TestService.getTestsInfoList(TestIds);
                    resultList.addAll(TestInfosFromRedis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //检查更新缓存
            checkRefreshHotListPer5Min(cityId);
        } else {
            //从数据库获取缓存
            Tests = getHotListFromDB(cityId);
            if (!CollectionUtils.isEmpty(Tests)) {
                TestTotalSize = Tests.size();
                int endIndex = (offset + limit) < TestTotalSize ? (offset + limit) : TestTotalSize;
                for (int startIndex = offset; startIndex < endIndex; startIndex++) {
                    resultList.add(Tests.get(startIndex));
                }
                //将id写入缓存
                List<Long> TestIdList = Tests.stream().map(TestInfo::getId).collect(Collectors.toList());
                refreshHotListTestCache(cityId, TestIdList);
            }
        }
        result.setTotalCount(TestTotalSize);
        result.setList(resultList);
        return result;
    }

    @Override
    public ListWithTotalCount<TestInfo> getLabelListByCity(Integer channelId, Integer cityId, int offset, int limit) {
        List<TestInfo> Tests;
        ListWithTotalCount<TestInfo> result = new ListWithTotalCount<>();
        List<TestInfo> resultList = new ArrayList<>();
        int TestTotalSize = 0;
        Boolean isExist;
        if (channelId.equals((int) ChannelIdEnum.DECORATE.getValue()) ||
                channelId.equals((int) ChannelIdEnum.OVERSEAS.getValue())) {
            //设置全国属性
            cityId = null;
        }
        List<Long> tagIds = getTagsByChannel(channelId);
        isExist = checkCityLabelTestExist(channelId, cityId);
        if (isExist) {
            TestTotalSize = (int) TestRedisDao.getCityLabelListCountRedis(channelId, cityId);
            int endIndex = (offset + limit - 1) < TestTotalSize ? (offset + limit - 1) : TestTotalSize;
            List<String> lrange = TestRedisDao.getCityLabelListRedis(channelId, cityId, offset, endIndex);
            if (!CollectionUtils.isEmpty(lrange)) {
                List<Long> TestIds = lrange.stream().map(Long::parseLong).collect(Collectors.toList());
                List<TestInfo> TestInfos = TestService.getTestsInfoList(TestIds);
                resultList.addAll(TestInfos);
            }
            //检查更新缓存
            checkRefreshLabelListPer5Min(channelId, cityId, tagIds);
        } else {
            Tests = getTagListFromDB(tagIds, cityId);
            if (!CollectionUtils.isEmpty(Tests)) {
                TestTotalSize = Tests.size();
                int endIndex = (offset + limit) < TestTotalSize ? (offset + limit) : TestTotalSize;
                resultList = Tests.subList(offset, endIndex);
                //刷新缓存
                refreshLabelListTestCache(channelId, cityId, Tests);
            }
        }

        result.setTotalCount(TestTotalSize);
        //标签隐藏
        //如果不是专家频道那么就清空
        result.setList(resultList);
        return result;
    }


    @Override
    public void refreshHotTests() {
        List<Integer> cityIds = new ArrayList<>();
        threadPool.submit(() -> {
            logger.info("start refreshHotTests {}", TimeUtils.getSecondKey(new Date()));
            cityIds.forEach(o -> checkRefreshHotListPer5Min(o));
            logger.info("end refreshHotTests {}", TimeUtils.getSecondKey(new Date()));
        });
    }
    /**
     * 城市热门xxxx列表，从数据库中获取
     */
    private List<TestInfo> getHotListFromDB(Integer cityId) {
        // A xx中
        List<TestInfo> hotFirstList = getHotFirstList(cityId);
        // B 24小时内播放量前5
        List<TestInfo> hotSecondList = getHotSecondList(cityId);
        // C 回放1000个
        List<Long> hotSecondIds = hotSecondList.stream().map(TestInfo::getId).collect(Collectors.toList());
        List<TestInfo> hotThirdList = getHotThirdList(cityId, hotSecondIds);
        // 拼接A B C
        List<TestInfo> Tests = new ArrayList<>();
        Tests.addAll(hotFirstList);
        Tests.addAll(hotSecondList);
        Tests.addAll(hotThirdList);
        //过滤掉居家生活
        return Tests;
    }

    private List<TestInfo> getHotFirstList(Integer cityId) {
        List<Long> onLiveIds = TestDao.getLiveByCity(cityId);
        List<TestInfo> TestInfos = TestService.getTestsInfoList(onLiveIds);
        // 先排序
        TestInfos = sortTestInfo(TestInfos);
        // 经纪人xx优先展示
//        Set<Long> brokerIds = hostService.getBrokerIds();
        List<TestInfo> TestInfosByBroker = new ArrayList<>();
        if (!CollectionUtils.isEmpty(TestInfosByBroker)) {
            // 排序，最多取5个
            TestInfosByBroker = sortTestInfo(TestInfosByBroker);
            if (TestInfosByBroker.size() > 5) {
                TestInfosByBroker = TestInfosByBroker.subList(0, 5);
            }
            // 从原队列中剔除
            TestInfos.removeAll(TestInfosByBroker);
            // 拼接
            TestInfos = Stream.of(TestInfosByBroker, TestInfos).flatMap(List::stream).collect(Collectors.toList());
        }
        return TestInfos;
    }

    private List<TestInfo> getHotSecondList(Integer cityId) {
        Calendar dayBeforeCalendar = Calendar.getInstance();
        dayBeforeCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = dayBeforeCalendar.getTime();
        List<Long> playBack24HoursIds = TestDao.getPlayBackByCityId(cityId, dayBefore);
        List<TestInfo> playbackList = TestService.getTestsInfoList(playBack24HoursIds);
        //排序
        playbackList = sortTestInfo(playbackList);
        List<TestInfo> playBack24HoursHot = new ArrayList<>();
        if (!CollectionUtils.isEmpty(playbackList)) {
            playBack24HoursHot = removeDurationLessThanTime(playbackList, 60);
        }
        //只取5个
        return playBack24HoursHot.subList(0, playBack24HoursHot.size() < 5 ? playBack24HoursHot.size() : 5);
    }

    private List<TestInfo> getHotThirdList(Integer cityId, List<Long> secondIds) {
        List<Long> playBackIds = TestDao.getPlayBackByCityIdLimit(cityId, 1000);
        // 剔除第二队列的回放，防止重复
        if (!CollectionUtils.isEmpty(secondIds)) {
            playBackIds.removeIf(secondIds::contains);
        }
        if (CollectionUtils.isEmpty(playBackIds)) {
            return new ArrayList<>();
        }
        List<TestInfo> TestInfos = TestService.getTestsInfoList(playBackIds);
        // 去除60s以内的回放
        TestInfos = removeDurationLessThanTime(TestInfos, 60);
        // 筛选出10个经纪人7日内创建的回放，放在该队列之首，每个经纪人最多展示一个回放
        Set<Long> brokerIds = new HashSet<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        List<TestInfo> TestInfosByBroker = TestInfos.stream().filter(o -> (brokerIds.contains(o.getHostId())
                && o.getLiveStartTime().after(timestamp))).collect(Collectors.toList());
        List<TestInfo> TestInfosByBrokerSelected = new ArrayList<>();
        if (!CollectionUtils.isEmpty(TestInfosByBroker)) {
            TestInfosByBroker = sortTestInfo(TestInfosByBroker);
            HashSet<Long> selectedHost = new HashSet<>();
            for (TestInfo o : TestInfosByBroker) {
                if (!selectedHost.contains(o.getHostId())) {
                    selectedHost.add(o.getHostId());
                    TestInfosByBrokerSelected.add(o);
                }
                if (TestInfosByBrokerSelected.size() >= 10) {
                    break;
                }
            }
        }
        if (!CollectionUtils.isEmpty(TestInfosByBrokerSelected)) {
            // 从原队列中剔除
            TestInfos.removeAll(TestInfosByBrokerSelected);
            // 拼接
            TestInfos = Stream.of(TestInfosByBrokerSelected, TestInfos).flatMap(List::stream)
                    .collect(Collectors.toList());

        }
        return TestInfos;
    }


    /**
     * 全国属性的列表cityId == null
     */
    private List<TestInfo> getTagListFromDB(List<Long> tagIds, Integer cityId) {
        //全国属性的cityId == null
        // A xx中 按照当前在线人数排序
        List<TestInfo> tagFirstList = tagFirstList(tagIds, cityId);
        // B 24小时内取观看前5个
        List<TestInfo> tagSecondList = tagSecondList(tagIds, cityId);
        List<Long> tagSecondIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tagSecondList)) {
            tagSecondIds = tagSecondList.stream().map(TestInfo::getId).collect(Collectors.toList());
        }
        // C 回放1000个
        List<TestInfo> tagThirdList = tagThirdList(tagIds, cityId, tagSecondIds);
        // 拼接
        List<TestInfo> result = new ArrayList<>();
        result.addAll(tagFirstList);
        result.addAll(tagSecondList);
        result.addAll(tagThirdList);
        return result;
    }


    private List<TestInfo> tagFirstList(List<Long> tagIds, Integer cityId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        String tagIdsString = StringUtils.join(tagIds, ',');
        List<Long> live;
        live = (cityId == null) ? TestDao.getLiveByTag(tagIdsString) :
                TestDao.getLiveByCityAndTag(tagIdsString, cityId);
        List<TestInfo> TestInfos = TestService.getTestsInfoList(live);
        TestInfos = sortTestInfo(TestInfos);
        return TestInfos;
    }

    private List<TestInfo> tagSecondList(List<Long> tagIds, Integer cityId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        String tagIdsString = StringUtils.join(tagIds, ',');
        Calendar dayBeforeCalendar = Calendar.getInstance();
        dayBeforeCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = dayBeforeCalendar.getTime();
        List<Long> playBack24HoursIds;
        playBack24HoursIds = (cityId == null) ? TestDao.getPlayBackByTag(tagIdsString, dayBefore) :
                TestDao.getPlayBackByCityAndTag(tagIdsString, cityId, dayBefore);
        List<TestInfo> playback24HoursList = TestService.getTestsInfoList(playBack24HoursIds);
        playback24HoursList = sortTestInfo(playback24HoursList);
        return playback24HoursList;
    }

    private List<TestInfo> tagThirdList(List<Long> tagIds, Integer cityId, List<Long> secondList) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        String tagIdsString = StringUtils.join(tagIds, ',');
        List<Long> playBack;
        playBack = (cityId == null) ? TestDao.getPlayBackByTagLimit(tagIdsString) :
                TestDao.getPlayBackByCityAndTagLimit(tagIdsString, cityId);
        List<TestInfo> playbackLimitList = TestService.getTestsInfoList(playBack);
        if (!CollectionUtils.isEmpty(secondList)) {
            playbackLimitList.removeIf(o -> secondList.contains(o.getId()));
        }
        return playbackLimitList;
    }

    /**
     * 刷新热门xxxx缓存
     */
    private void refreshHotListTestCache(Integer cityId, List<Long> TestIdList) {
        //写入缓存
        String[] TestsStr = TestIdList.stream().map(String::valueOf).toArray(String[]::new);
        //删除缓存
        TestRedisDao.removeCityHotListRedis(cityId);
        //设置缓存
        if (TestsStr.length > 0) {
            TestRedisDao.setCityHotListRedis(cityId, TestsStr);
        }
        //设置刷新时xx
        TestRedisDao.setCityHotListRefreshTimeRedis(cityId, new Date());
    }

    /**
     * 每5min刷新缓存
     */
    private void checkRefreshHotListPer5Min(Integer cityId) {
        //判断是否需要刷新
        Date lastRefreshTime = TestRedisDao.getCityHotListRefreshTimeRedis(cityId);
        if (lastRefreshTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastRefreshTime);
            calendar.add(Calendar.MINUTE, 5);
            lastRefreshTime = calendar.getTime();
        }
        //大于五分钟需要刷新缓存
        if (lastRefreshTime == null || lastRefreshTime.before(new Date())) {
            threadPool.execute(() -> {
                //设置缓存最近刷新时xx
                TestRedisDao.setCityHotListRefreshTimeRedis(cityId, new Date());
                List<TestInfo> TestInfos = getHotListFromDB(cityId);
                //将id写入缓存
                List<Long> TestIdList = TestInfos.stream().map(TestInfo::getId).collect(Collectors.toList());
                refreshHotListTestCache(cityId, TestIdList);
            });
        }
    }

    /**
     * 每5min检查更新缓存
     */
    private void checkRefreshLabelListPer5Min(Integer channelId, Integer cityId, List<Long> tagIds) {
        //判断是否需要刷新
        Date lastRefreshTime = TestRedisDao.getLabelListRefreshTimeRedis(channelId, cityId);
        if (lastRefreshTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastRefreshTime);
            calendar.add(Calendar.MINUTE, 5);
            lastRefreshTime = calendar.getTime();
        }
        //大于五分钟需要刷新缓存
        if (lastRefreshTime == null || lastRefreshTime.before(new Date())) {
            threadPool.execute(() -> {
                //设置缓存最近刷新时xx
                TestRedisDao.setLabelListRefreshTimeRedis(channelId, cityId, new Date());
                List<TestInfo> Tests = getTagListFromDB(tagIds, cityId);
                //将id写入缓存
                refreshLabelListTestCache(channelId, cityId, Tests);
            });
        }
    }

    /**
     * 刷新tagxxxx缓存
     */
    private void refreshLabelListTestCache(Integer channelId, Integer cityId, List<TestInfo> Tests) {
        //写入缓存
        String[] TestsStr = Tests.stream().map(o -> String.valueOf(o.getId())).toArray(String[]::new);
        //全国属性的设置cityId = null
        //删除缓存
        TestRedisDao.removeCityLabelList(channelId, cityId);
        //设置缓存
        if (TestsStr.length > 0) {
            TestRedisDao.setCityLabelList(channelId, cityId, TestsStr);
        }
        //设置缓存最近刷新时xx
        TestRedisDao.setLabelListRefreshTimeRedis(channelId, cityId, new Date());
    }

    private Boolean checkCityHotTestExist(Integer cityId) {
        return TestRedisDao.checkCityHotListExists(cityId);
    }

    private Boolean checkCityLabelTestExist(Integer channelId, Integer cityId) {
        return TestRedisDao.checkCityLabelListExists(channelId, cityId);
    }

    /**
     * 当xxxx关闭，删除，被BAN时刷新该xxxx所在城市的推荐列表和频道列表缓存
     */
    @Override
    public void refreshHotAndTagCache(long TestId) {
        TestInfo TestInfo = TestService.getTestInfo(TestId);
        //xx视频才热门列表缓存，因为上传视频不展示在热门列表中
        if (TestInfo.getVideoType().equals(VideoTypeEnum.Test.getValue())) {
            //从数据库获取
            List<TestInfo> TestInfoList = getHotListFromDB(TestInfo.getCityId());
            List<Long> TestIdList = new ArrayList<>();
            refreshHotListTestCache(TestInfo.getCityId(), TestIdList);
        }
        //根据tagId获取channelId
        List<TagEntity> tagEntities = new ArrayList<>();
        if (CollectionUtils.isEmpty(tagEntities)) {
            return;
        }
        tagEntities.forEach(o -> {
            if (tagChannelMap.containsKey(o.getId())) {
                Integer channelId = tagChannelMap.get(o.getId());
                Integer cityId = TestInfo.getCityId();
                if (channelId == 3 || channelId == 6) {
                    cityId = null;
                }
                //从数据库获取
                List<TestInfo> tagTestInfos = getTagListFromDB(Collections.singletonList(o.getId()), cityId);
                refreshLabelListTestCache(channelId, cityId, tagTestInfos);
            }
        });
    }

    @Override
    public void refreshTagCache(List<Long> tagIds, long TestId, Integer cityId, int videoType) {
        //根据tagId获取channelId
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        //xx视频才热门列表缓存，因为上传视频不展示在热门列表中
        if (videoType == (int) (VideoTypeEnum.Test.getValue())) {
            //从数据库获取
            List<TestInfo> TestInfoList = getHotListFromDB(cityId);
            List<Long> TestIdList = TestInfoList.stream().map(TestInfo::getId).collect(Collectors.toList());
            refreshHotListTestCache(cityId, TestIdList);
        }
        for (long id : tagIds) {
            if (tagChannelMap.containsKey(id)) {
                Integer channelId = tagChannelMap.get(id);
                if (channelId == 3 || channelId == 6) {
                    cityId = null;
                }
                //从数据库获取
                List<TestInfo> tagTestInfos = getTagListFromDB(Collections.singletonList(id), cityId);
                refreshLabelListTestCache(channelId, cityId, tagTestInfos);
            }
        }
    }

    /***
     * 城市预告列表
     */
    @Override
    public ListWithTotalCount<TestInfo> getPreviewsByCity(int cityId, int offset, int limit) {
        ListWithTotalCount<TestInfo> result = new ListWithTotalCount<>();
        List<TestInfo> TestsTotal;
        List<TestInfo> TestInfos = new ArrayList<>();
        int totalCount = 0;
        boolean isCached = TestRedisDao.checkCityPreviewListExists(cityId);
        if (isCached) {
            totalCount = (int) TestRedisDao.getCityPreviewListCountRedis(cityId);
            int endIndex = (offset + limit - 1) < totalCount ? (offset + limit - 1) : totalCount;
            List<String> lrange = TestRedisDao.getCityPreviewListRedis(cityId, offset, endIndex);
            if (!CollectionUtils.isEmpty(lrange)) {
                List<Long> ids = lrange.stream().map(Long::parseLong).collect(Collectors.toList());
                TestInfos = TestService.getTestsInfoList(ids);
            }
        } else {
            TestsTotal = getPreviewsByCityFromDb(cityId);
            if (!CollectionUtils.isEmpty(TestsTotal)) {
                totalCount = TestsTotal.size();
                int endIndex = (offset + limit) < totalCount ? (offset + limit) : totalCount;
                TestInfos = TestsTotal.subList(offset, endIndex);
                //更新缓存
                refreshCityPreviewsCache(cityId, TestsTotal.stream().map(TestInfo::getId).collect(Collectors.toList()));
            }
        }
        result.setTotalCount(totalCount);
        result.setList(TestInfos);
        return result;
    }

    /**
     * 刷新城市预告列表缓存
     */
    @Override
    public void refreshCityPreviewsCache(int cityId, List<Long> Tests) {
        //删除缓存
        TestRedisDao.removeCityPreviewList(cityId);
        if (CollectionUtils.isEmpty(Tests)) {
            List<TestInfo> TestInfos = getPreviewsByCityFromDb(cityId);
            if (!CollectionUtils.isEmpty(TestInfos)) {
                Tests = TestInfos.stream().map(TestInfo::getId).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isEmpty(Tests)) {
            return;
        }
        //写入缓存
        String[] TestsStr = Tests.stream().map(o -> String.valueOf(o)).toArray(String[]::new);
        //设置缓存
        TestRedisDao.setCityPreviewList(cityId, TestsStr);
    }


    private List<TestInfo> getPreviewsByCityFromDb(int cityId) {
        List<Long> previewsIds = TestDao.getPreviews(cityId);
        List<Long> globalPreviewsIds = TestDao.getGlobalPreviews(StringUtils.join(Arrays.asList(4, 6), ","));
        //合并
        List<Long> TestIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(previewsIds)) {
            TestIds.addAll(previewsIds);
        }
        if (!CollectionUtils.isEmpty(globalPreviewsIds)) {
            TestIds.addAll(globalPreviewsIds);
        }
        if (!CollectionUtils.isEmpty(TestIds)) {
            TestIds = TestIds.stream().distinct().collect(Collectors.toList());
        }
        List<TestInfo> TestInfos = TestService.getTestsInfoList(TestIds);
        if (!CollectionUtils.isEmpty(TestIds)) {
            TestInfos = TestInfos.stream().sorted(Comparator.comparing(TestInfo::getScheduledTime)).collect(Collectors.toList());
        }
        return TestInfos;
    }

    @Override
    public int getCityTestCount(Integer cityId, Date startTime, Date endTime) {
        List<Integer> cityIds = new ArrayList<>();
        if (cityId != null) {
            cityIds.add(cityId);
        }
        return count(null, null, null, null, null, null, null,
                cityIds, startTime, endTime);
    }

    @Override
    public List<TestInfo> getCityTest(Integer cityId, Date startTime, Date endTime) {
        return list(null, Collections.singletonList(4), null, null, null, null, null,
                Collections.singletonList(cityId), startTime, endTime, null, null);
    }


    /**
     * 首页xxxx推荐搜索
     */
    @Override
    public List<TestInfo> searchTest(String key, Integer cityId) {
        if (cityId == null) {
            return new ArrayList<>();
        }
        //限制搜索关键词长度
        if (key.length() > 8) {
            key = key.substring(0, 8);
        }
        List<TestInfo> TestInfos;
        try {
            //搜索ES
            List<TestSearch> TestSearchList = TestSearchService.search(key, cityId, 10);
            if (CollectionUtils.isEmpty(TestSearchList)) {
                return new ArrayList<>();
            }
            List<Long> TestIds = TestSearchList.stream().map(TestSearch::getId).collect(Collectors.toList());
            TestInfos = TestService.getTestsInfoList(TestIds);
            sortTestInfo(TestInfos);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            TestInfos = new ArrayList<>();
        }
        return TestInfos;
    }

    /**
     * xxxx默认
     *
     * @param cityId
     * @return
     */
    @Override
    public List<TestInfo> suggestTest(Integer cityId) {
        List<Long> suggestTestLive = TestDao.getSuggestTestLive(1, cityId);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        List<Long> suggestTestPlayback = TestDao.getSuggestTestPlayback(4, cityId, TimeUtils.getSecondKey(calendar.getTime()));
        List<Long> TestIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(suggestTestLive)) {
            TestIds.addAll(suggestTestLive);
        }
        if (!CollectionUtils.isEmpty(suggestTestPlayback)) {
            TestIds.addAll(suggestTestPlayback);
        }
        List<TestInfo> TestInfos = TestService.getTestsInfoList(TestIds);
        TestInfos = sortTestInfo(TestInfos);
        return TestInfos.subList(0, TestInfos.size() > 10 ? 10 : TestInfos.size());
    }


    @Override
    public List<TestInfo> getTestToSeo(Integer cityId, Date startDate, Date endDate, Integer
            offset, Integer limit) {
        List<TestInfo> TestInfos = new ArrayList<>();
        List<Long> ids = TestDao.getByCityAndDateToSeo(cityId, startDate, endDate, offset, limit);
        if (CollectionUtils.isEmpty(ids)) {
            return TestInfos;
        }
        TestInfos = TestService.getTestsInfoList(ids);
        return TestInfos;
    }

    @Override
    public int getTestCountToSeo(Integer cityId, Date startDate, Date endDate) {
        return TestDao.getCountByCityAndDateToSeo(cityId, startDate, endDate);
    }

    @Override
    public void forecastOn(long uid, long id) {

    }

    @Override
    public void forecastOff(long uid, long id) {

    }

    @Override
    public List<Long> getForecastTestIdList(long uid) {
        return null;
    }

    @Override
    public List<Long> getForecastSubscriberList(long TestId) {
        return null;
    }

    /**
     * 用户xxxx创建限制
     */
    @Override
    public void checkUserCreateLimit(long userId) {
        if (TestRedisDao.getUserCreateLimitRedis(userId) != null) {
            throw new TestStateException(TestStateException.TestStateExceptionEnum.Test_CREATE_LIMIT_EXCEPTION);
        }
    }

    @Override
    public void setUserCreateLimit(long userId) {
        TestRedisDao.setUserCreateLimitRedis(userId);
    }


    @Override
    public void removeUserCreateLimit(long userId) {
        TestRedisDao.removeUserCreateLimitRedis(userId);
    }

    @Override
    public List<TestInfo> getByCountryCode(String code, Integer offset, Integer limit) {
        return null;
    }

    @Override
    public Map<String, List<TestInfo>> getByCountryCodes(List<String> codes) {
        return null;
    }


    /**
     * 把xx中和回放的xxxx排序，xx中的排序高于回放，内部排序，xx中-在线人数从高到低 回放-总观看人数从高到低。
     */
    private List<TestInfo> sortTestInfo(List<TestInfo> TestInfos) {
        if (CollectionUtils.isEmpty(TestInfos)) {
            return new ArrayList<>();
        }
        Map<Long, Integer> robotCounts = new HashMap<>();
        TestInfos.sort((o1, o2) -> {
            Byte status1 = o1.getStatus();
            Byte status2 = o2.getStatus();
            if (!status1.equals(status2)) {
                return status1.compareTo(status2);
            }

            if (status1.equals(TestStatusEnum.STATUS_ONSHOW.getValue())) {
                int o1Cur = statTestService.getCurrentAudienceCount(o1.getId()) + robotCounts.get(o1.getId());
                int o2Cur = statTestService.getCurrentAudienceCount(o2.getId()) + robotCounts.get(o2.getId());
                if (o2Cur == o1Cur) {
                    return Integer.compare(statTestService.getTotalVv(o2.getId()), statTestService.getTotalVv(o1.getId()));
                }
                return Integer.compare(o2Cur, o1Cur);
            } else {
                return Integer.compare(statTestService.getTotalVv(o2.getId()), statTestService.getTotalVv(o1.getId()));
            }
        });
        return TestInfos;
    }

    //热门列表过滤时长不足1分钟，以及上传的视频
    private List<TestInfo> removeDurationLessThanTime(List<TestInfo> TestInfos, int duration) {
        if (CollectionUtils.isEmpty(TestInfos)) {
            return new ArrayList<>();
        }
        TestInfos.removeIf(o -> o.getDuration() < duration);
        return TestInfos;
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public void refreshLabelTests() {

    }

    @Override
    public List<Long> getTagsByChannel(Integer channelId) {
        return null;
    }
}
