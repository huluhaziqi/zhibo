package com.lin.MyTest.config;

import com.lin.MyTest.config.annotation.SecondRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashSet;

@Configuration
@MapperScan(basePackages = "com.lin.MyTest.dao", annotationClass = SecondRepository.class, sqlSessionTemplateRef = "liveSqlSessionTemplate")
public class MyBatisSecondLiveConfig {

    @Value("${datasource.second.jdbcUrl}")
    private String jdbcUrl;

    @Value("${datasource.second.username}")
    private String username;

    @Value("${datasource.second.password}")
    private String password;

    @Value("${datasource.second.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Bean(name = "secondDataSource")
    public DataSource dataSource(){
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaximumPoolSize(maximumPoolSize);
        ds.setMinimumIdle(5);
        ds.setConnectionTimeout(10000);
        ds.setMaxLifetime(40000);
        ds.setIdleTimeout(15000);
        ds.setLeakDetectionThreshold(10000);
        return ds;
    }

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("secondDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setAggressiveLazyLoading(false);
        configuration.setLazyLoadingEnabled(true);
        configuration.setLazyLoadTriggerMethods(new HashSet<>());
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

    @Bean(name = "secondTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("secondDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "secondSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("secondSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
