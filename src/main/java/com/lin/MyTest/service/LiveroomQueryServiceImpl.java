package com.lin.MyTest.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lin.MyTest.dao.repository.LiveroomDao;
import com.lin.MyTest.enums.ChannelIdEnum;
import com.lin.MyTest.enums.LiveroomStatusEnum;
import com.lin.MyTest.enums.VideoTypeEnum;
import com.lin.MyTest.exception.LiveroomStateException;
import com.lin.MyTest.model.biz.liveroom.ListWithTotalCount;
import com.lin.MyTest.model.biz.liveroom.LiveroomInfo;
import com.lin.MyTest.model.biz.liveroom.LiveroomSearch;
import com.lin.MyTest.model.entity.TagEntity;
import com.lin.MyTest.model.request.PagingInterface;
import com.lin.MyTest.redisdao.LiveroomRedisDao;
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
public class LiveroomQueryServiceImpl implements LiveroomQueryService {

    @Autowired
    private LiveroomSearchService liveroomSearchService;

    @Autowired
    private StatLiveroomService statLiveroomService;

    @Autowired
    private LiveroomService liveroomService;

    @Resource
    private LiveroomRedisDao liveroomRedisDao;

    @Autowired
    private LiveroomDao liveroomDao;

    @Value("${spring.H5BaseUrl}")
    private String H5BaseUrl;

    @Value("${spring.PCBaseUrl}")
    private String PCBaseUrl;

    @Value("${building.houseWebBaseUrl}")
    private String buildingWebBaseUrl;

    private ExecutorService threadPool;

    private static final Logger logger = LoggerFactory.getLogger(LiveroomServiceImpl.class);

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
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("liveroom-queryservice-pool-%d").build();
        threadPool = new ThreadPoolExecutor(10, 50, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /***
     * 综合筛选直播间接口
     */
    @Override
    public List<LiveroomInfo> list(Integer videoType, List<Integer> statusList, Long hostId,
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
        List<Long> liveroomIds = liveroomDao.searchLiveroom(videoType, statusListStr, cityString,
                hostId, hostName, id, title, tagsIdsStr, offset, limit, startTime, endTime);
        return liveroomService.getLiveroomsInfoList(liveroomIds);
    }

    /**
     * 获取直播间数目
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
        return liveroomDao.searchLiveroomCount(videoType, statusListStr, cityString,
                hostId, hostName, id, title, tagsIdsStr, startTime, endTime);
    }

    @Override
    public List<Long> getAllLiverooms() {
        return liveroomDao.getAllRecord();
    }

    /*************************************************************Status 批量处理***************************************/
    /**
     * 通过状态查询直播间
     */
    @Override
    public List<LiveroomInfo> getByStatus(List<Byte> statusList, PagingInterface paging) {
        if (CollectionUtils.isEmpty(statusList)) {
            return new ArrayList<>();
        }
        String statusQuery = statusList.stream().map(String::valueOf).collect(Collectors.joining(", "));
        List<Long> liveroomIds = liveroomDao.getByStatusByPaging(statusQuery, paging.getOffset(), paging.getLimit());
        return liveroomService.getLiveroomsInfoList(liveroomIds);
    }

    /**
     * 通过状态查询直播间数目
     */
    @Override
    public int getCountByStatus(List<Byte> statusList) {
        if (CollectionUtils.isEmpty(statusList)) {
            return 0;
        }
        String statusQuery = statusList.stream().map(String::valueOf).collect(Collectors.joining(", "));
        return liveroomDao.getCountByStatus(statusQuery);

    }

    /*************************************************************User and host 批量处理***************************************/

    /**
     * 普通用户看到的列表
     */
    @Override
    public List<LiveroomInfo> getUserLiverooms(Long userId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit) {
        if (userId == null) {
            return new ArrayList<>();
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        List<Long> liveroomIds = liveroomDao.getByUser(userId, statusQuery, videoTypes, offset, limit);
        return liveroomService.getLiveroomsInfoList(liveroomIds);
    }

    /**
     * 普通用户列表数目获取
     */
    @Override
    public int getUserLiveroomsCount(Long userId, List<Byte> statusList, List<Byte> videoTypeList) {
        if (userId == null) {
            return 0;
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        return liveroomDao.getCountByUser(userId, statusQuery, videoTypes);
    }

    /**
     * 主播列表获取
     */
    @Override
    public List<LiveroomInfo> getHostLiverooms(Long hostId, List<Byte> statusList, List<Byte> videoTypeList, int offset, int limit) {
        if (hostId == null) {
            return new ArrayList<>();
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        List<Long> liveroomIds = liveroomDao.getByHost(hostId, statusQuery, videoTypes, offset, limit);
        return liveroomService.getLiveroomsInfoList(liveroomIds);
    }

    /**
     * 主播列表数目获取
     */
    @Override
    public int getHostLiveroomsCount(Long hostId, List<Byte> statusList, List<Byte> videoTypeList) {
        if (hostId == null) {
            return 0;
        }
        String statusQuery = StringUtils.join(statusList, ",");
        String videoTypes = StringUtils.join(videoTypeList, ",");
        return liveroomDao.getCountByHost(hostId, statusQuery, videoTypes);
    }

    /**
     * 主播预告列表
     */
    @Override
    public List<LiveroomInfo> getPreviewsByHost(Long hostId, int offset, int limit) {
        if (hostId == null) {
            return new ArrayList<>();
        }
        List<Long> liveroomIds = liveroomDao.getPreviewsByHost(hostId, offset, limit);
        return liveroomService.getLiveroomsInfoList(liveroomIds);
    }

    @Override
    public List<LiveroomInfo> getMissPreviewByHost(Long hostId) {
        List<Long> ids = liveroomDao.getExpiredPreviewsByHostId(hostId);
        return liveroomService.getLiveroomsInfoList(ids);
    }

    @Override
    public List<LiveroomInfo> getPreviewsByBuildingId(long buildingId) {
        return null;
    }


    //需要排序，直播1，4
    private List<Long> getLiveAndPlaybackByBuildingIdFromDb(Long buildingId) {
        if (buildingId == null) {
            return new ArrayList<>();
        }
        List<Long> list = liveroomDao.getByBuildingId(buildingId, StringUtils.join(Arrays.asList(1, 4), ","));
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(list);
        // 先排序
        liveroomInfos = sortLiveroomInfo(liveroomInfos);
        return liveroomInfos.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
    }

    /**
     * 刷新楼盘预告、直播间缓存
     */
    @Override
    public void refreshBuildingLiveroomCache(long buildingId) {
        refreshBuildingPreviewsCache(buildingId, null);
        refreshBuildingLiveAndPlaybackCache(buildingId, null);
    }

    @Override
    public void refreshBuildingPreviewsCache(long buildingId, List<Long> liveroomIds) {

    }

    @Override
    public void refreshBuildingLiveAndPlaybackCache(long buildingId, List<Long> liveroomIds) {

    }

    @Override
    public void refreshAllBuildingLiveroomCache() {

    }

    @Override
    public Map<Long, Integer> getUserValidLiveroomCount(List<Long> userIds) {
        Map<Long, Integer> result = new HashMap<>();
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }
        if (userIds.size() > 50) {
            userIds = userIds.subList(0, 50);
        }
        String userIdsString = StringUtils.join(userIds, ',');
        Map<BigInteger, Map<String, Object>> queryResultMap = liveroomDao.getUserValidLiveroomCount(userIdsString);
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
    public ListWithTotalCount<LiveroomInfo> getHotListByCity(Integer cityId, int offset, int limit) {
        ListWithTotalCount<LiveroomInfo> result = new ListWithTotalCount<>();
        List<LiveroomInfo> liverooms;
        List<LiveroomInfo> resultList = new ArrayList<>();
        int liveroomTotalSize = 0;

        // 缓存中存在
        if (checkCityHotLiveroomExist(cityId)) {
            liveroomTotalSize = (int) liveroomRedisDao.getCityHotListCountRedis(cityId);
            int endIndex = (offset + limit - 1) < liveroomTotalSize ? (offset + limit - 1) : liveroomTotalSize;
            List<String> lrange = liveroomRedisDao.getCityHotListRedis(cityId, offset, endIndex);
            if (!CollectionUtils.isEmpty(lrange)) {
                try {
                    List<Long> liveroomIds = lrange.stream().map(Long::parseLong).collect(Collectors.toList());
                    List<LiveroomInfo> liveroomInfosFromRedis = liveroomService.getLiveroomsInfoList(liveroomIds);
                    resultList.addAll(liveroomInfosFromRedis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //检查更新缓存
            checkRefreshHotListPer5Min(cityId);
        } else {
            //从数据库获取缓存
            liverooms = getHotListFromDB(cityId);
            if (!CollectionUtils.isEmpty(liverooms)) {
                liveroomTotalSize = liverooms.size();
                int endIndex = (offset + limit) < liveroomTotalSize ? (offset + limit) : liveroomTotalSize;
                for (int startIndex = offset; startIndex < endIndex; startIndex++) {
                    resultList.add(liverooms.get(startIndex));
                }
                //将id写入缓存
                List<Long> liveroomIdList = liverooms.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
                refreshHotListLiveroomCache(cityId, liveroomIdList);
            }
        }
        result.setTotalCount(liveroomTotalSize);
        result.setList(resultList);
        return result;
    }

    @Override
    public ListWithTotalCount<LiveroomInfo> getLabelListByCity(Integer channelId, Integer cityId, int offset, int limit) {
        List<LiveroomInfo> liverooms;
        ListWithTotalCount<LiveroomInfo> result = new ListWithTotalCount<>();
        List<LiveroomInfo> resultList = new ArrayList<>();
        int liveroomTotalSize = 0;
        Boolean isExist;
        if (channelId.equals((int) ChannelIdEnum.DECORATE.getValue()) ||
                channelId.equals((int) ChannelIdEnum.OVERSEAS.getValue())) {
            //设置全国属性
            cityId = null;
        }
        List<Long> tagIds = getTagsByChannel(channelId);
        isExist = checkCityLabelLiveroomExist(channelId, cityId);
        if (isExist) {
            liveroomTotalSize = (int) liveroomRedisDao.getCityLabelListCountRedis(channelId, cityId);
            int endIndex = (offset + limit - 1) < liveroomTotalSize ? (offset + limit - 1) : liveroomTotalSize;
            List<String> lrange = liveroomRedisDao.getCityLabelListRedis(channelId, cityId, offset, endIndex);
            if (!CollectionUtils.isEmpty(lrange)) {
                List<Long> liveroomIds = lrange.stream().map(Long::parseLong).collect(Collectors.toList());
                List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(liveroomIds);
                resultList.addAll(liveroomInfos);
            }
            //检查更新缓存
            checkRefreshLabelListPer5Min(channelId, cityId, tagIds);
        } else {
            liverooms = getTagListFromDB(tagIds, cityId);
            if (!CollectionUtils.isEmpty(liverooms)) {
                liveroomTotalSize = liverooms.size();
                int endIndex = (offset + limit) < liveroomTotalSize ? (offset + limit) : liveroomTotalSize;
                resultList = liverooms.subList(offset, endIndex);
                //刷新缓存
                refreshLabelListLiveroomCache(channelId, cityId, liverooms);
            }
        }

        result.setTotalCount(liveroomTotalSize);
        //标签隐藏
        //如果不是专家频道那么就清空
        result.setList(resultList);
        return result;
    }


    @Override
    public void refreshHotLiverooms() {
        List<Integer> cityIds = new ArrayList<>();
        threadPool.submit(() -> {
            logger.info("start refreshHotLiverooms {}", TimeUtils.getSecondKey(new Date()));
            cityIds.forEach(o -> checkRefreshHotListPer5Min(o));
            logger.info("end refreshHotLiverooms {}", TimeUtils.getSecondKey(new Date()));
        });
    }
    /**
     * 城市热门直播间列表，从数据库中获取
     */
    private List<LiveroomInfo> getHotListFromDB(Integer cityId) {
        // A 直播中
        List<LiveroomInfo> hotFirstList = getHotFirstList(cityId);
        // B 24小时内播放量前5
        List<LiveroomInfo> hotSecondList = getHotSecondList(cityId);
        // C 回放1000个
        List<Long> hotSecondIds = hotSecondList.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
        List<LiveroomInfo> hotThirdList = getHotThirdList(cityId, hotSecondIds);
        // 拼接A B C
        List<LiveroomInfo> liverooms = new ArrayList<>();
        liverooms.addAll(hotFirstList);
        liverooms.addAll(hotSecondList);
        liverooms.addAll(hotThirdList);
        //过滤掉居家生活
        return liverooms;
    }

    private List<LiveroomInfo> getHotFirstList(Integer cityId) {
        List<Long> onLiveIds = liveroomDao.getLiveByCity(cityId);
        List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(onLiveIds);
        // 先排序
        liveroomInfos = sortLiveroomInfo(liveroomInfos);
        // 经纪人直播优先展示
//        Set<Long> brokerIds = hostService.getBrokerIds();
        List<LiveroomInfo> liveroomInfosByBroker = new ArrayList<>();
        if (!CollectionUtils.isEmpty(liveroomInfosByBroker)) {
            // 排序，最多取5个
            liveroomInfosByBroker = sortLiveroomInfo(liveroomInfosByBroker);
            if (liveroomInfosByBroker.size() > 5) {
                liveroomInfosByBroker = liveroomInfosByBroker.subList(0, 5);
            }
            // 从原队列中剔除
            liveroomInfos.removeAll(liveroomInfosByBroker);
            // 拼接
            liveroomInfos = Stream.of(liveroomInfosByBroker, liveroomInfos).flatMap(List::stream).collect(Collectors.toList());
        }
        return liveroomInfos;
    }

    private List<LiveroomInfo> getHotSecondList(Integer cityId) {
        Calendar dayBeforeCalendar = Calendar.getInstance();
        dayBeforeCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = dayBeforeCalendar.getTime();
        List<Long> playBack24HoursIds = liveroomDao.getPlayBackByCityId(cityId, dayBefore);
        List<LiveroomInfo> playbackList = liveroomService.getLiveroomsInfoList(playBack24HoursIds);
        //排序
        playbackList = sortLiveroomInfo(playbackList);
        List<LiveroomInfo> playBack24HoursHot = new ArrayList<>();
        if (!CollectionUtils.isEmpty(playbackList)) {
            playBack24HoursHot = removeDurationLessThanTime(playbackList, 60);
        }
        //只取5个
        return playBack24HoursHot.subList(0, playBack24HoursHot.size() < 5 ? playBack24HoursHot.size() : 5);
    }

    private List<LiveroomInfo> getHotThirdList(Integer cityId, List<Long> secondIds) {
        List<Long> playBackIds = liveroomDao.getPlayBackByCityIdLimit(cityId, 1000);
        // 剔除第二队列的回放，防止重复
        if (!CollectionUtils.isEmpty(secondIds)) {
            playBackIds.removeIf(secondIds::contains);
        }
        if (CollectionUtils.isEmpty(playBackIds)) {
            return new ArrayList<>();
        }
        List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(playBackIds);
        // 去除60s以内的回放
        liveroomInfos = removeDurationLessThanTime(liveroomInfos, 60);
        // 筛选出10个经纪人7日内创建的回放，放在该队列之首，每个经纪人最多展示一个回放
        Set<Long> brokerIds = new HashSet<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        List<LiveroomInfo> liveroomInfosByBroker = liveroomInfos.stream().filter(o -> (brokerIds.contains(o.getHostId())
                && o.getLiveStartTime().after(timestamp))).collect(Collectors.toList());
        List<LiveroomInfo> liveroomInfosByBrokerSelected = new ArrayList<>();
        if (!CollectionUtils.isEmpty(liveroomInfosByBroker)) {
            liveroomInfosByBroker = sortLiveroomInfo(liveroomInfosByBroker);
            HashSet<Long> selectedHost = new HashSet<>();
            for (LiveroomInfo o : liveroomInfosByBroker) {
                if (!selectedHost.contains(o.getHostId())) {
                    selectedHost.add(o.getHostId());
                    liveroomInfosByBrokerSelected.add(o);
                }
                if (liveroomInfosByBrokerSelected.size() >= 10) {
                    break;
                }
            }
        }
        if (!CollectionUtils.isEmpty(liveroomInfosByBrokerSelected)) {
            // 从原队列中剔除
            liveroomInfos.removeAll(liveroomInfosByBrokerSelected);
            // 拼接
            liveroomInfos = Stream.of(liveroomInfosByBrokerSelected, liveroomInfos).flatMap(List::stream)
                    .collect(Collectors.toList());

        }
        return liveroomInfos;
    }


    /**
     * 全国属性的列表cityId == null
     */
    private List<LiveroomInfo> getTagListFromDB(List<Long> tagIds, Integer cityId) {
        //全国属性的cityId == null
        // A 直播中 按照当前在线人数排序
        List<LiveroomInfo> tagFirstList = tagFirstList(tagIds, cityId);
        // B 24小时内取观看前5个
        List<LiveroomInfo> tagSecondList = tagSecondList(tagIds, cityId);
        List<Long> tagSecondIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tagSecondList)) {
            tagSecondIds = tagSecondList.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
        }
        // C 回放1000个
        List<LiveroomInfo> tagThirdList = tagThirdList(tagIds, cityId, tagSecondIds);
        // 拼接
        List<LiveroomInfo> result = new ArrayList<>();
        result.addAll(tagFirstList);
        result.addAll(tagSecondList);
        result.addAll(tagThirdList);
        return result;
    }


    private List<LiveroomInfo> tagFirstList(List<Long> tagIds, Integer cityId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        String tagIdsString = StringUtils.join(tagIds, ',');
        List<Long> live;
        live = (cityId == null) ? liveroomDao.getLiveByTag(tagIdsString) :
                liveroomDao.getLiveByCityAndTag(tagIdsString, cityId);
        List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(live);
        liveroomInfos = sortLiveroomInfo(liveroomInfos);
        return liveroomInfos;
    }

    private List<LiveroomInfo> tagSecondList(List<Long> tagIds, Integer cityId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        String tagIdsString = StringUtils.join(tagIds, ',');
        Calendar dayBeforeCalendar = Calendar.getInstance();
        dayBeforeCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = dayBeforeCalendar.getTime();
        List<Long> playBack24HoursIds;
        playBack24HoursIds = (cityId == null) ? liveroomDao.getPlayBackByTag(tagIdsString, dayBefore) :
                liveroomDao.getPlayBackByCityAndTag(tagIdsString, cityId, dayBefore);
        List<LiveroomInfo> playback24HoursList = liveroomService.getLiveroomsInfoList(playBack24HoursIds);
        playback24HoursList = sortLiveroomInfo(playback24HoursList);
        return playback24HoursList;
    }

    private List<LiveroomInfo> tagThirdList(List<Long> tagIds, Integer cityId, List<Long> secondList) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        String tagIdsString = StringUtils.join(tagIds, ',');
        List<Long> playBack;
        playBack = (cityId == null) ? liveroomDao.getPlayBackByTagLimit(tagIdsString) :
                liveroomDao.getPlayBackByCityAndTagLimit(tagIdsString, cityId);
        List<LiveroomInfo> playbackLimitList = liveroomService.getLiveroomsInfoList(playBack);
        if (!CollectionUtils.isEmpty(secondList)) {
            playbackLimitList.removeIf(o -> secondList.contains(o.getId()));
        }
        return playbackLimitList;
    }

    /**
     * 刷新热门直播间缓存
     */
    private void refreshHotListLiveroomCache(Integer cityId, List<Long> liveroomIdList) {
        //写入缓存
        String[] liveroomsStr = liveroomIdList.stream().map(String::valueOf).toArray(String[]::new);
        //删除缓存
        liveroomRedisDao.removeCityHotListRedis(cityId);
        //设置缓存
        if (liveroomsStr.length > 0) {
            liveroomRedisDao.setCityHotListRedis(cityId, liveroomsStr);
        }
        //设置刷新时间
        liveroomRedisDao.setCityHotListRefreshTimeRedis(cityId, new Date());
    }

    /**
     * 每5min刷新缓存
     */
    private void checkRefreshHotListPer5Min(Integer cityId) {
        //判断是否需要刷新
        Date lastRefreshTime = liveroomRedisDao.getCityHotListRefreshTimeRedis(cityId);
        if (lastRefreshTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastRefreshTime);
            calendar.add(Calendar.MINUTE, 5);
            lastRefreshTime = calendar.getTime();
        }
        //大于五分钟需要刷新缓存
        if (lastRefreshTime == null || lastRefreshTime.before(new Date())) {
            threadPool.execute(() -> {
                //设置缓存最近刷新时间
                liveroomRedisDao.setCityHotListRefreshTimeRedis(cityId, new Date());
                List<LiveroomInfo> liveroomInfos = getHotListFromDB(cityId);
                //将id写入缓存
                List<Long> liveroomIdList = liveroomInfos.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
                refreshHotListLiveroomCache(cityId, liveroomIdList);
            });
        }
    }

    /**
     * 每5min检查更新缓存
     */
    private void checkRefreshLabelListPer5Min(Integer channelId, Integer cityId, List<Long> tagIds) {
        //判断是否需要刷新
        Date lastRefreshTime = liveroomRedisDao.getLabelListRefreshTimeRedis(channelId, cityId);
        if (lastRefreshTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastRefreshTime);
            calendar.add(Calendar.MINUTE, 5);
            lastRefreshTime = calendar.getTime();
        }
        //大于五分钟需要刷新缓存
        if (lastRefreshTime == null || lastRefreshTime.before(new Date())) {
            threadPool.execute(() -> {
                //设置缓存最近刷新时间
                liveroomRedisDao.setLabelListRefreshTimeRedis(channelId, cityId, new Date());
                List<LiveroomInfo> liverooms = getTagListFromDB(tagIds, cityId);
                //将id写入缓存
                refreshLabelListLiveroomCache(channelId, cityId, liverooms);
            });
        }
    }

    /**
     * 刷新tag直播间缓存
     */
    private void refreshLabelListLiveroomCache(Integer channelId, Integer cityId, List<LiveroomInfo> liverooms) {
        //写入缓存
        String[] liveroomsStr = liverooms.stream().map(o -> String.valueOf(o.getId())).toArray(String[]::new);
        //全国属性的设置cityId = null
        //删除缓存
        liveroomRedisDao.removeCityLabelList(channelId, cityId);
        //设置缓存
        if (liveroomsStr.length > 0) {
            liveroomRedisDao.setCityLabelList(channelId, cityId, liveroomsStr);
        }
        //设置缓存最近刷新时间
        liveroomRedisDao.setLabelListRefreshTimeRedis(channelId, cityId, new Date());
    }

    private Boolean checkCityHotLiveroomExist(Integer cityId) {
        return liveroomRedisDao.checkCityHotListExists(cityId);
    }

    private Boolean checkCityLabelLiveroomExist(Integer channelId, Integer cityId) {
        return liveroomRedisDao.checkCityLabelListExists(channelId, cityId);
    }

    /**
     * 当直播间关闭，删除，被BAN时刷新该直播间所在城市的推荐列表和频道列表缓存
     */
    @Override
    public void refreshHotAndTagCache(long liveroomId) {
        LiveroomInfo liveroomInfo = liveroomService.getLiveroomInfo(liveroomId);
        //直播视频才热门列表缓存，因为上传视频不展示在热门列表中
        if (liveroomInfo.getVideoType().equals(VideoTypeEnum.LIVEROOM.getValue())) {
            //从数据库获取
            List<LiveroomInfo> liveroomInfoList = getHotListFromDB(liveroomInfo.getCityId());
            List<Long> liveroomIdList = liveroomInfoList.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
            refreshHotListLiveroomCache(liveroomInfo.getCityId(), liveroomIdList);
        }
        //根据tagId获取channelId
        List<TagEntity> tagEntities = new ArrayList<>();
        if (CollectionUtils.isEmpty(tagEntities)) {
            return;
        }
        tagEntities.forEach(o -> {
            if (tagChannelMap.containsKey(o.getId())) {
                Integer channelId = tagChannelMap.get(o.getId());
                Integer cityId = liveroomInfo.getCityId();
                if (channelId == 3 || channelId == 6) {
                    cityId = null;
                }
                //从数据库获取
                List<LiveroomInfo> tagLiveroomInfos = getTagListFromDB(Collections.singletonList(o.getId()), cityId);
                refreshLabelListLiveroomCache(channelId, cityId, tagLiveroomInfos);
            }
        });
    }

    @Override
    public void refreshTagCache(List<Long> tagIds, long liveroomId, Integer cityId, int videoType) {
        //根据tagId获取channelId
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        //直播视频才热门列表缓存，因为上传视频不展示在热门列表中
        if (videoType == (int) (VideoTypeEnum.LIVEROOM.getValue())) {
            //从数据库获取
            List<LiveroomInfo> liveroomInfoList = getHotListFromDB(cityId);
            List<Long> liveroomIdList = liveroomInfoList.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
            refreshHotListLiveroomCache(cityId, liveroomIdList);
        }
        for (long id : tagIds) {
            if (tagChannelMap.containsKey(id)) {
                Integer channelId = tagChannelMap.get(id);
                if (channelId == 3 || channelId == 6) {
                    cityId = null;
                }
                //从数据库获取
                List<LiveroomInfo> tagLiveroomInfos = getTagListFromDB(Collections.singletonList(id), cityId);
                refreshLabelListLiveroomCache(channelId, cityId, tagLiveroomInfos);
            }
        }
    }

    /***
     * 城市预告列表
     */
    @Override
    public ListWithTotalCount<LiveroomInfo> getPreviewsByCity(int cityId, int offset, int limit) {
        ListWithTotalCount<LiveroomInfo> result = new ListWithTotalCount<>();
        List<LiveroomInfo> liveroomsTotal;
        List<LiveroomInfo> liveroomInfos = new ArrayList<>();
        int totalCount = 0;
        boolean isCached = liveroomRedisDao.checkCityPreviewListExists(cityId);
        if (isCached) {
            totalCount = (int) liveroomRedisDao.getCityPreviewListCountRedis(cityId);
            int endIndex = (offset + limit - 1) < totalCount ? (offset + limit - 1) : totalCount;
            List<String> lrange = liveroomRedisDao.getCityPreviewListRedis(cityId, offset, endIndex);
            if (!CollectionUtils.isEmpty(lrange)) {
                List<Long> ids = lrange.stream().map(Long::parseLong).collect(Collectors.toList());
                liveroomInfos = liveroomService.getLiveroomsInfoList(ids);
            }
        } else {
            liveroomsTotal = getPreviewsByCityFromDb(cityId);
            if (!CollectionUtils.isEmpty(liveroomsTotal)) {
                totalCount = liveroomsTotal.size();
                int endIndex = (offset + limit) < totalCount ? (offset + limit) : totalCount;
                liveroomInfos = liveroomsTotal.subList(offset, endIndex);
                //更新缓存
                refreshCityPreviewsCache(cityId, liveroomsTotal.stream().map(LiveroomInfo::getId).collect(Collectors.toList()));
            }
        }
        result.setTotalCount(totalCount);
        result.setList(liveroomInfos);
        return result;
    }

    /**
     * 刷新城市预告列表缓存
     */
    @Override
    public void refreshCityPreviewsCache(int cityId, List<Long> liverooms) {
        //删除缓存
        liveroomRedisDao.removeCityPreviewList(cityId);
        if (CollectionUtils.isEmpty(liverooms)) {
            List<LiveroomInfo> liveroomInfos = getPreviewsByCityFromDb(cityId);
            if (!CollectionUtils.isEmpty(liveroomInfos)) {
                liverooms = liveroomInfos.stream().map(LiveroomInfo::getId).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isEmpty(liverooms)) {
            return;
        }
        //写入缓存
        String[] liveroomsStr = liverooms.stream().map(o -> String.valueOf(o)).toArray(String[]::new);
        //设置缓存
        liveroomRedisDao.setCityPreviewList(cityId, liveroomsStr);
    }


    private List<LiveroomInfo> getPreviewsByCityFromDb(int cityId) {
        List<Long> previewsIds = liveroomDao.getPreviews(cityId);
        List<Long> globalPreviewsIds = liveroomDao.getGlobalPreviews(StringUtils.join(Arrays.asList(4, 6), ","));
        //合并
        List<Long> liveroomIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(previewsIds)) {
            liveroomIds.addAll(previewsIds);
        }
        if (!CollectionUtils.isEmpty(globalPreviewsIds)) {
            liveroomIds.addAll(globalPreviewsIds);
        }
        if (!CollectionUtils.isEmpty(liveroomIds)) {
            liveroomIds = liveroomIds.stream().distinct().collect(Collectors.toList());
        }
        List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(liveroomIds);
        if (!CollectionUtils.isEmpty(liveroomIds)) {
            liveroomInfos = liveroomInfos.stream().sorted(Comparator.comparing(LiveroomInfo::getScheduledTime)).collect(Collectors.toList());
        }
        return liveroomInfos;
    }

    @Override
    public int getCityLiveroomCount(Integer cityId, Date startTime, Date endTime) {
        List<Integer> cityIds = new ArrayList<>();
        if (cityId != null) {
            cityIds.add(cityId);
        }
        return count(null, null, null, null, null, null, null,
                cityIds, startTime, endTime);
    }

    @Override
    public List<LiveroomInfo> getCityLiveroom(Integer cityId, Date startTime, Date endTime) {
        return list(null, Collections.singletonList(4), null, null, null, null, null,
                Collections.singletonList(cityId), startTime, endTime, null, null);
    }


    /**
     * 首页直播间推荐搜索
     */
    @Override
    public List<LiveroomInfo> searchLiveroom(String key, Integer cityId) {
        if (cityId == null) {
            return new ArrayList<>();
        }
        //限制搜索关键词长度
        if (key.length() > 8) {
            key = key.substring(0, 8);
        }
        List<LiveroomInfo> liveroomInfos;
        try {
            //搜索ES
            List<LiveroomSearch> liveroomSearchList = liveroomSearchService.search(key, cityId, 10);
            if (CollectionUtils.isEmpty(liveroomSearchList)) {
                return new ArrayList<>();
            }
            List<Long> liveroomIds = liveroomSearchList.stream().map(LiveroomSearch::getId).collect(Collectors.toList());
            liveroomInfos = liveroomService.getLiveroomsInfoList(liveroomIds);
            sortLiveroomInfo(liveroomInfos);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            liveroomInfos = new ArrayList<>();
        }
        return liveroomInfos;
    }

    /**
     * 直播间默认
     *
     * @param cityId
     * @return
     */
    @Override
    public List<LiveroomInfo> suggestLiveroom(Integer cityId) {
        List<Long> suggestLiveroomLive = liveroomDao.getSuggestLiveroomLive(1, cityId);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        List<Long> suggestLiveroomPlayback = liveroomDao.getSuggestLiveroomPlayback(4, cityId, TimeUtils.getSecondKey(calendar.getTime()));
        List<Long> liveroomIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(suggestLiveroomLive)) {
            liveroomIds.addAll(suggestLiveroomLive);
        }
        if (!CollectionUtils.isEmpty(suggestLiveroomPlayback)) {
            liveroomIds.addAll(suggestLiveroomPlayback);
        }
        List<LiveroomInfo> liveroomInfos = liveroomService.getLiveroomsInfoList(liveroomIds);
        liveroomInfos = sortLiveroomInfo(liveroomInfos);
        return liveroomInfos.subList(0, liveroomInfos.size() > 10 ? 10 : liveroomInfos.size());
    }


    @Override
    public List<LiveroomInfo> getLiveroomToSeo(Integer cityId, Date startDate, Date endDate, Integer
            offset, Integer limit) {
        List<LiveroomInfo> liveroomInfos = new ArrayList<>();
        List<Long> ids = liveroomDao.getByCityAndDateToSeo(cityId, startDate, endDate, offset, limit);
        if (CollectionUtils.isEmpty(ids)) {
            return liveroomInfos;
        }
        liveroomInfos = liveroomService.getLiveroomsInfoList(ids);
        return liveroomInfos;
    }

    @Override
    public int getLiveroomCountToSeo(Integer cityId, Date startDate, Date endDate) {
        return liveroomDao.getCountByCityAndDateToSeo(cityId, startDate, endDate);
    }

    @Override
    public void forecastOn(long uid, long id) {

    }

    @Override
    public void forecastOff(long uid, long id) {

    }

    @Override
    public List<Long> getForecastLiveroomIdList(long uid) {
        return null;
    }

    @Override
    public List<Long> getForecastSubscriberList(long liveroomId) {
        return null;
    }

    /**
     * 用户直播间创建限制
     */
    @Override
    public void checkUserCreateLimit(long userId) {
        if (liveroomRedisDao.getUserCreateLimitRedis(userId) != null) {
            throw new LiveroomStateException(LiveroomStateException.LiveroomStateExceptionEnum.LIVEROOM_CREATE_LIMIT_EXCEPTION);
        }
    }

    @Override
    public void setUserCreateLimit(long userId) {
        liveroomRedisDao.setUserCreateLimitRedis(userId);
    }


    @Override
    public void removeUserCreateLimit(long userId) {
        liveroomRedisDao.removeUserCreateLimitRedis(userId);
    }

    @Override
    public List<LiveroomInfo> getByCountryCode(String code, Integer offset, Integer limit) {
        return null;
    }

    @Override
    public Map<String, List<LiveroomInfo>> getByCountryCodes(List<String> codes) {
        return null;
    }


    /**
     * 把直播中和回放的直播间排序，直播中的排序高于回放，内部排序，直播中-在线人数从高到低 回放-总观看人数从高到低。
     */
    private List<LiveroomInfo> sortLiveroomInfo(List<LiveroomInfo> liveroomInfos) {
        if (CollectionUtils.isEmpty(liveroomInfos)) {
            return new ArrayList<>();
        }
        Map<Long, Integer> robotCounts = new HashMap<>();
        liveroomInfos.sort((o1, o2) -> {
            Byte status1 = o1.getStatus();
            Byte status2 = o2.getStatus();
            if (!status1.equals(status2)) {
                return status1.compareTo(status2);
            }

            if (status1.equals(LiveroomStatusEnum.STATUS_ONSHOW.getValue())) {
                int o1Cur = statLiveroomService.getCurrentAudienceCount(o1.getId()) + robotCounts.get(o1.getId());
                int o2Cur = statLiveroomService.getCurrentAudienceCount(o2.getId()) + robotCounts.get(o2.getId());
                if (o2Cur == o1Cur) {
                    return Integer.compare(statLiveroomService.getTotalVv(o2.getId()), statLiveroomService.getTotalVv(o1.getId()));
                }
                return Integer.compare(o2Cur, o1Cur);
            } else {
                return Integer.compare(statLiveroomService.getTotalVv(o2.getId()), statLiveroomService.getTotalVv(o1.getId()));
            }
        });
        return liveroomInfos;
    }

    //热门列表过滤时长不足1分钟，以及上传的视频
    private List<LiveroomInfo> removeDurationLessThanTime(List<LiveroomInfo> liveroomInfos, int duration) {
        if (CollectionUtils.isEmpty(liveroomInfos)) {
            return new ArrayList<>();
        }
        liveroomInfos.removeIf(o -> o.getDuration() < duration);
        return liveroomInfos;
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public void refreshLabelLiverooms() {

    }

    @Override
    public List<Long> getTagsByChannel(Integer channelId) {
        return null;
    }
}
