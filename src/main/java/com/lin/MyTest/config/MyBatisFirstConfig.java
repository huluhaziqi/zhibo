package com.lin.MyTest.config;

import com.lin.MyTest.config.annotation.FirstRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashSet;

@Configuration
@MapperScan(basePackages = "com.lin.MyTest.dao", annotationClass = FirstRepository.class, sqlSessionTemplateRef = "appSqlSessionTemplate")
public class MyBatisFirstConfig {

    @Value("${datasource.first.jdbcUrl}")
    private String jdbcUrl;

    @Value("${datasource.first.username}")
    private String username;

    @Value("${datasource.first.password}")
    private String password;

    @Value("${datasource.first.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Bean(name = "firstDataSource")
    @Primary
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

    @Bean(name = "firstSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("firstDataSource") DataSource dataSource) throws Exception {
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

    @Bean(name = "firstTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("firstDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "firstSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("firstSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
