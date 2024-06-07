package com.fiafeng.common.config;


import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import com.fiafeng.common.config.bean.DefaultDataSource;
import com.fiafeng.common.mapper.mysql.*;
import com.fiafeng.common.properties.mysql.*;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@EnableConfigurationProperties({
        FiafengMysqlPermissionProperties.class,
        FiafengMysqlRolePermissionProperties.class,
        FiafengMysqlRoleProperties.class,
        FiafengMysqlUserProperties.class,
        FiafengMysqlUserRoleProperties.class,
        FiafengMysqlProperties.class,
})
@ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
@ConditionalEnableProperty("fiafeng.mysql.enable")
@ConditionalOnWebApplication
public class MysqlMapperConfig {


    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    DefaultDataSource dataSource() {
        return new DefaultDataSource();
    }

    @Bean
    @Primary
    DefaultMysqlRoleMapper defaultMysqlRoleMapper(FiafengMysqlRoleProperties properties) {
        DefaultMysqlRoleMapper mapper = new DefaultMysqlRoleMapper();

        mapper.setProperties(properties);
        return mapper;
    }


    @Bean
    @Primary
    DefaultMysqlRolePermissionMapper defaultMysqlRolePermissionMapper(FiafengMysqlRolePermissionProperties properties) {
        DefaultMysqlRolePermissionMapper mapper = new DefaultMysqlRolePermissionMapper();
        mapper.setProperties(properties);
        return mapper;
    }


    @Bean
    DefaultMysqlUserMapper defaultMysqlUserMapper(FiafengMysqlUserProperties properties) {
        DefaultMysqlUserMapper mapper = new DefaultMysqlUserMapper();
        mapper.setProperties(properties);
        return mapper;
    }


    @Bean
    @Primary
    DefaultMysqlUserRoleMapper defaultMysqlUserRoleMapper(FiafengMysqlUserRoleProperties properties) {
        DefaultMysqlUserRoleMapper mapper = new DefaultMysqlUserRoleMapper();
        mapper.setProperties(properties);
        return mapper;
    }

    @Bean
    @Primary
    DefaultMysqlPermissionMapper defaultMysqlPermissionMapper(FiafengMysqlPermissionProperties properties) {
        DefaultMysqlPermissionMapper mapper = new DefaultMysqlPermissionMapper();
        mapper.setProperties(properties);
        return mapper;
    }

    @Bean
    ConnectionPoolServiceImpl connectionPoolService(DataSource dataSource, FiafengMysqlProperties mysqlProperties) {
        return new ConnectionPoolServiceImpl(dataSource, mysqlProperties);
    }
}
