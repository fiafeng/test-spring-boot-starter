package com.fiafeng.mysql.config;


import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import com.fiafeng.mysql.init.MysqlApplicationInit;
import com.fiafeng.mysql.mapper.*;
import com.fiafeng.mysql.properties.*;
import com.fiafeng.mysql.service.ConnectionPoolService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
        FiafengMysqlProperties.class
})
@ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
@ConditionalEnableProperty("fiafeng.mysql.enable")
@ConditionalOnWebApplication
public class MysqlMapperConfig {


    @Bean
    @ConditionalOnMissingClass("com.fiafeng.mysql.config.DefaultDataSource")
    DefaultDataSource dataSource() {
        // 这里只是为了让项目不暴红，实际会在MysqlApplicationInit里面检查如果不存在dataSource,就会自动注入这个DefaultDataSource
        return new DefaultDataSource();
    }

    @Bean
    @Primary
    DefaultMysqlRoleMapper defaultMysqlRoleMapper(FiafengMysqlRoleProperties properties) {
        DefaultMysqlRoleMapper mapper = new DefaultMysqlRoleMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.tableColName = properties.tableColName;
        return mapper;
    }


    @Bean
    @Primary
    DefaultMysqlRolePermissionMapper defaultMysqlRolePermissionMapper(FiafengMysqlRolePermissionProperties properties) {
        DefaultMysqlRolePermissionMapper mapper = new DefaultMysqlRolePermissionMapper();

        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.roleIdName = properties.roleIdName;
        mapper.permissionIdName = properties.permissionIdName;
        return mapper;
    }


    @Bean
    DefaultMysqlUserMapper defaultMysqlUserMapper( FiafengMysqlUserProperties properties) {
        DefaultMysqlUserMapper mapper = new DefaultMysqlUserMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.tableColName = properties.tableColName;
        return mapper;
    }


    @Bean
    @Primary
    DefaultMysqlUserRoleMapper defaultMysqlUserRoleMapper(FiafengMysqlUserRoleProperties properties) {
        DefaultMysqlUserRoleMapper mapper = new DefaultMysqlUserRoleMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.roleIdName = properties.roleIdName;
        mapper.userIdName = properties.userIdName;
        return mapper;
    }

    @Bean
    @Primary
    DefaultMysqlPermissionMapper defaultMysqlPermissionMapper( FiafengMysqlPermissionProperties properties) {
        DefaultMysqlPermissionMapper mapper = new DefaultMysqlPermissionMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.tableColName = properties.tableColName;
        return mapper;
    }

    @Bean
    ConnectionPoolService connectionPoolService(DataSource dataSource, FiafengMysqlProperties mysqlProperties) {
        return new ConnectionPoolService(dataSource, mysqlProperties);
    }

    @Bean
    MysqlApplicationInit mysqlApplicationInit() {
        return new MysqlApplicationInit();
    }
}
