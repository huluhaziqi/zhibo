package com.lin.MyTest.datasource.elasticsearch;


import com.lin.MyTest.model.biz.Test.TestSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface TestSearchDao extends ElasticsearchRepository<TestSearch, Long> {
}
