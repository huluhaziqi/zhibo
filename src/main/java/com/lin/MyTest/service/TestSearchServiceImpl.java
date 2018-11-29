package com.lin.MyTest.service;


import com.lin.MyTest.model.biz.Test.TestInfo;
import com.lin.MyTest.model.biz.Test.TestSearch;
import com.lin.MyTest.model.entity.TestEntity;
import com.lin.MyTest.model.entity.StatTestEntity;
import com.lin.MyTest.util.PinyinUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestSearchServiceImpl implements TestSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Autowired
    private StatTestService statTestService;

    @Autowired
    private TestService TestService;

    @Value("${spring.data.elasticsearch.Test-index}")
    private String TestIndex;

    @Override
    public void refreshTest(long TestId) {
        try {
            TestInfo TestInfo = TestService.getTestInfo(TestId);
            List<TestInfo> TestInfos = new ArrayList<>();
            TestInfos.add(TestInfo);
            List<TestSearch> TestSearches = convertTestInfoToTestSearch(TestInfos);
            TestSearch TestSearch;
            if (!CollectionUtils.isEmpty(TestSearches)) {
                TestSearch = TestSearches.get(0);
                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setId(TestSearch.getId() + "");
                indexQuery.setObject(TestSearch);
                indexQuery.setIndexName(TestIndex);
                elasticsearchTemplate.index(indexQuery);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void save(TestEntity entity) {
        try {
            List<TestEntity> list = new ArrayList<>();
            list.add(entity);
            List<TestSearch> TestSearches = convertTestEntityToTestSearch(list);
            if (!CollectionUtils.isEmpty(TestSearches)) {
                TestSearch TestSearch = TestSearches.get(0);
                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setId(TestSearch.getId() + "");
                indexQuery.setObject(TestSearch);
                indexQuery.setIndexName(TestIndex);
                elasticsearchTemplate.index(indexQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bulkSave(List<Long> TestIds) {
        if (CollectionUtils.isEmpty(TestIds)) {
            return;
        }
        final int[] count = new int[1];
        List<TestEntity> TestEntities = TestService.getByIds(TestIds);
        List<TestSearch> TestSearches = convertTestEntityToTestSearch(TestEntities);
        List<IndexQuery> queries = new ArrayList<>();
        TestSearches.forEach(o -> {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(o.getId() + "");
            indexQuery.setObject(o);
            indexQuery.setIndexName(TestIndex);
            queries.add(indexQuery);
            //每500个存储一次，可存储大量的数据
            if (count[0] % 500 == 0) {
                elasticsearchTemplate.bulkIndex(queries);
                queries.clear();
            }
            count[0]++;
        });
        if (queries.size() > 0) {
            elasticsearchTemplate.bulkIndex(queries);
        }
    }

    @Override
    public List<TestSearch> findByChineseWithOthers(String key, Integer cityId) {
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        /**
         * 不做拼音转换
         */
        QueryBuilder chineseSearchBuilder = QueryBuilders.matchQuery("title", key).boost(1.5f);

        /**
         * 拼音简写搜索
         */
        String firstCharacter = PinyinUtil.getFirstCharacter(key);
        QueryBuilder pinyinFirstQueryBuilder = QueryBuilders.termQuery("firstCharacter", firstCharacter).boost(1.0f);

        /**
         * 拼音简写包含匹配，如njdl可以查出 "地方 * 南京东路店"，虽然非南京东路开头
         * 权重*0.8
         */
        QueryBuilder pingYinFirstContainQueryBuilder = null;
        if (firstCharacter.length() > 1) {
            pingYinFirstContainQueryBuilder = QueryBuilders.wildcardQuery("firstCharacter", "*" + firstCharacter + "*").boost(0.8f);
        }
        QueryBuilder pingYinFullQueryBuilder = null;
        if (key.length() > 1) {
            pingYinFullQueryBuilder = QueryBuilders.matchPhraseQuery("pinyin", key).analyzer("pinyinFullSearchAnalyzer").boost(1.0f);
        }
        QueryBuilder containSearchBuilder = QueryBuilders.matchQuery("iks", key).boost(0.8f);

        disMaxQueryBuilder
                .add(chineseSearchBuilder)
                .add(pinyinFirstQueryBuilder)
                .add(containSearchBuilder);

        //单个字符不执行此类搜索，减少性能影响
        if (pingYinFullQueryBuilder != null) {
            disMaxQueryBuilder.add(pingYinFullQueryBuilder);
        }
        if (pingYinFirstContainQueryBuilder != null) {
            disMaxQueryBuilder.add(pingYinFirstContainQueryBuilder);
        }

        //筛选状态
        BoolQueryBuilder status = QueryBuilders.boolQuery();
        status.should(QueryBuilders.termQuery("status", 1))
                .should(QueryBuilders.termQuery("status", 2))
                .should(QueryBuilders.termQuery("status", 4));
        //筛选城市和tag
        BoolQueryBuilder cityBuilder = QueryBuilders.boolQuery();
        cityBuilder.should(QueryBuilders.termQuery("cityId", cityId))
                .should(QueryBuilders.matchQuery("tag", 4))
                .should(QueryBuilders.matchQuery("tag", 6));
        //合并搜索
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(disMaxQueryBuilder).must(status).must(cityBuilder);

        SearchRequestBuilder searchRequestBuilder = elasticsearchTemplate.getClient().prepareSearch(TestIndex).
                setQuery(qb).setSize(500);

        SearchResponse response = searchRequestBuilder.execute().actionGet();
        DefaultResultMapper resultMapper = new DefaultResultMapper();
        List<TestSearch> result = resultMapper.mapResults(response, TestSearch.class, new PageRequest(0, 500)).getContent();
        return result;
    }

    /**
     * 全词匹配
     *
     * @param key
     * @param cityId
     * @return
     */
    @Override
    public List<TestSearch> findByChinese(String key, Integer cityId) {
        DisMaxQueryBuilder queryBuilder = QueryBuilders.disMaxQuery();
        QueryBuilder chineseSearchBuilder = QueryBuilders.matchQuery("title", key).analyzer("ngramSearchAnalyzer").boost(1.5f);
        QueryBuilder pingYinFullQueryBuilder = null;
        if (key.length() > 1) {
            pingYinFullQueryBuilder = QueryBuilders.matchPhraseQuery("pinyin", key).analyzer("pinyinFullSearchAnalyzer").boost(0.8f);
        }
        queryBuilder.add(chineseSearchBuilder);
        if (pingYinFullQueryBuilder != null) {
            queryBuilder.add(pingYinFullQueryBuilder);
        }

        /**
         * 排序规则
         */
        SortBuilder totalVvSortBuilder = SortBuilders.fieldSort("totalVv").order(SortOrder.DESC);
        SortBuilder statusSortBuilder = SortBuilders.fieldSort("status").order(SortOrder.ASC);
        //筛选状态
        BoolQueryBuilder status = QueryBuilders.boolQuery();
        status.should(QueryBuilders.termQuery("status", 1))
                .should(QueryBuilders.termQuery("status", 2))
                .should(QueryBuilders.termQuery("status", 4));
        //筛选城市和tag
        BoolQueryBuilder cityBuilder = QueryBuilders.boolQuery();
        cityBuilder.should(QueryBuilders.termQuery("cityId", cityId))
                .should(QueryBuilders.matchQuery("tag", 4))
                .should(QueryBuilders.matchQuery("tag", 6));
        //合并搜索
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(queryBuilder).must(status).must(cityBuilder);

        SearchRequestBuilder searchRequestBuilder = elasticsearchTemplate.getClient().prepareSearch(TestIndex)
                .setQuery(qb).setSize(500).addSort(totalVvSortBuilder).addSort(statusSortBuilder);

        SearchResponse response = searchRequestBuilder.execute().actionGet();
        DefaultResultMapper resultMapper = new DefaultResultMapper();
        List<TestSearch> result = resultMapper.mapResults(response, TestSearch.class, new PageRequest(0, 500)).getContent();
        return result;
    }

    @Override
    public List<TestSearch> search(String key, Integer cityId, int size) {
        List<TestSearch> chineseList = findByChinese(key, cityId);
        List<TestSearch> allList = new ArrayList<>(findByChineseWithOthers(key, cityId));
        List<TestSearch> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(chineseList)) {
            List<Long> chineseListIds = chineseList.stream().map(TestSearch::getId).collect(Collectors.toList());
            allList.removeIf(o -> chineseListIds.contains(o.getId()));
        }
        result.addAll(chineseList);
        result.addAll(allList);
        return result.subList(0, result.size() > size ? size : result.size());
    }

    /**
     * es2.4.1无法同时过滤query与suggest
     *
     * @param key
     * @param cityId
     * @param size
     * @return
     */
    @Override
    public List<TestEntity> suggest(String key, Integer cityId, int size) {
        String field = "titleSuggest";
        //筛选状态
        BoolQueryBuilder status = QueryBuilders.boolQuery();
        status.should(QueryBuilders.termQuery("status", 1))
                .should(QueryBuilders.termQuery("status", 2))
                .should(QueryBuilders.termQuery("status", 4));
        //筛选城市和tag
        BoolQueryBuilder cityBuilder = QueryBuilders.boolQuery();
        cityBuilder.should(QueryBuilders.termQuery("cityId", cityId))
                .should(QueryBuilders.matchQuery("tag", 4))
                .should(QueryBuilders.matchQuery("tag", 6));
        /**
         * 排序规则
         */
        SortBuilder totalVvSortBuilder = SortBuilders.fieldSort("totalVv").order(SortOrder.DESC);
        SortBuilder statusSortBuilder = SortBuilders.fieldSort("status").order(SortOrder.ASC);

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(status).must(status).mustNot(cityBuilder);

        CompletionSuggestionBuilder titleBuilder = new CompletionSuggestionBuilder("titleSuggest")
                .field(field).text(key).size(size);
        SearchRequestBuilder builder = elasticsearchTemplate.getClient().prepareSearch(TestIndex)
                .setQuery(qb).addSuggestion(titleBuilder).addSort(totalVvSortBuilder).addSort(statusSortBuilder);
        SearchResponse response = builder.execute().actionGet();
        CompletionSuggestion completionSuggestion = response.getSuggest().getSuggestion("titleSuggest");
        List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getEntries().get(0).getOptions();
        List<Long> ids = options.stream().map(option -> Long.parseLong(option.getPayload().toUtf8())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return TestService.getByIds(ids);
    }

    @Override
    public void delete(long id) {
        try {
            elasticsearchTemplate.delete(TestIndex, "Testsearch", String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Override
    public List<TestSearch> findByPinyin(String key, Integer cityId) {
        //区分拼音和文字
        boolean isPinyin = false;
        //包含非字母及数字
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isDigit(c) || isLetter(c)) {
                isPinyin = true;
                break;
            }
        }

        String pinyin = PinyinUtil.getPinyin(key);

        //筛选状态
        BoolQueryBuilder status = QueryBuilders.boolQuery();
        status.should(QueryBuilders.termQuery("status", 1))
                .should(QueryBuilders.termQuery("status", 2))
                .should(QueryBuilders.termQuery("status", 4));
        //筛选城市和tag
        BoolQueryBuilder cityBuilder = QueryBuilders.boolQuery();
        cityBuilder.must(QueryBuilders.termQuery("cityId", cityId))
                .should(QueryBuilders.matchQuery("tag", 4))
                .should(QueryBuilders.matchQuery("tag", 6));
        //合并
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        //是否为拼音查询
        if (isPinyin) {
            qb.must(QueryBuilders.wildcardQuery("pinyin", '*' + pinyin + '*'))
                    .must(status).must(cityBuilder);
        } else {
            //中文查询，需要每个字分开查
            for (int i = 0; i < key.length(); i++) {
                qb.must(QueryBuilders.wildcardQuery("title", String.valueOf(key.charAt(i))));
            }
            qb.must(status).must(cityBuilder);
        }
        SortBuilder totalVvSortBuilder = SortBuilders.fieldSort("totalVv").order(SortOrder.DESC);
        SortBuilder statusSortBuilder = SortBuilders.fieldSort("status").order(SortOrder.ASC);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(qb);
        nativeSearchQueryBuilder.withPageable(new PageRequest(0, 500)).withSort(statusSortBuilder).withSort(totalVvSortBuilder);
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        List<TestSearch> list = elasticsearchTemplate.queryForList(searchQuery, TestSearch.class);
        List<TestSearch> result;
        if (list == null) {
            return new ArrayList<>();
        } else {
            result = list.subList(0, list.size() > 10 ? 10 : list.size());
        }
        return result;
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private List<TestSearch> convertTestEntityToTestSearch(List<TestEntity> TestEntities) {
        if (CollectionUtils.isEmpty(TestEntities)) {
            return null;
        }
        List<TestSearch> TestSearches = new ArrayList<>();
        TestEntities.forEach(o -> {
            String pinyin = PinyinUtil.getPinyin(o.getTitle());
            String firstCharacter = PinyinUtil.getFirstCharacter(o.getTitle());
            TestSearch TestSearch = new TestSearch();
            TestSearch.setTitle(o.getTitle());
            TestSearch.setIks(o.getTitle());
            TestSearch.setId(o.getId());
            TestSearch.setPinyin(pinyin);
            TestSearch.setFirstCharacter(firstCharacter);
            TestSearch.setCityId(o.getCityId());
            TestSearch.setStatus(o.getStatus().intValue());
            Completion completion = new Completion(new String[]{o.getTitle()});
            completion.setPayload(o.getId());
            TestSearch.setTitleSuggest(completion);
            StatTestEntity statTestEntity = statTestService.getByTestId(o.getId());
            TestSearch.setTotalVv(statTestEntity.getTotalVv());
            TestSearches.add(TestSearch);
        });
        return TestSearches;
    }

    private List<TestSearch> convertTestInfoToTestSearch(List<TestInfo> TestInfos) {
        if (CollectionUtils.isEmpty(TestInfos)) {
            return null;
        }
        List<TestSearch> TestSearches = new ArrayList<>();
        TestInfos.forEach(o -> {
            String pinyin = PinyinUtil.getPinyin(o.getTitle());
            String firstCharacter = PinyinUtil.getFirstCharacter(o.getTitle());
            TestSearch TestSearch = new TestSearch();
            TestSearch.setTitle(o.getTitle());
            TestSearch.setIks(o.getTitle());
            TestSearch.setId(o.getId());
            TestSearch.setPinyin(pinyin);
            TestSearch.setFirstCharacter(firstCharacter);
            TestSearch.setCityId(o.getCityId());
            TestSearch.setStatus(o.getStatus().intValue());
            Completion completion = new Completion(new String[]{o.getTitle()});
            completion.setPayload(o.getId());
            TestSearch.setTitleSuggest(completion);
            StatTestEntity statTestEntity = statTestService.getByTestId(o.getId());
            TestSearch.setTotalVv(statTestEntity.getTotalVv());
            TestSearches.add(TestSearch);
        });
        return TestSearches;
    }
}
