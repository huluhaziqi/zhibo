package com.lin.MyTest.datasource.elasticsearch;


import com.lin.MyTest.model.biz.liveroom.LiveroomSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface LiveroomSearchDao extends ElasticsearchRepository<LiveroomSearch, Long> {
}
