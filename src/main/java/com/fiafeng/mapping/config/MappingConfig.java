package com.fiafeng.mapping.config;


import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import com.fiafeng.mapping.init.MappingApplicationInit;
import com.fiafeng.mapping.interceptor.FiafengPermissionInterceptor;
import com.fiafeng.mapping.mapper.DefaultMappingMapper;
import com.fiafeng.mapping.mapper.DefaultMysqlMappingMapper;
import com.fiafeng.mapping.pojo.DefaultMapping;
import com.fiafeng.mapping.pojo.vo.RequestMappingBean;
import com.fiafeng.mapping.properties.FiafengMappingProperties;
import com.fiafeng.mapping.properties.FiafengMysqlMappingProperties;
import com.fiafeng.rbac.config.RbacConfig;
import com.fiafeng.common.config.DefaultConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties(value = {
        FiafengMappingProperties.class,
        FiafengMysqlMappingProperties.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.mapping.controller")
})
@ConditionalEnableProperty("fiafeng.mapping.enable")
@AutoConfigureAfter({
        RbacConfig.class,
        DefaultConfig.class
})
@Import({
        MappingWebConfig.class
})
@ConditionalOnWebApplication
public class MappingConfig implements WebMvcConfigurer {

    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource", name = {"url", "username", "password"})
    @ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
    DefaultMysqlMappingMapper defaultMysqlMappingMapper(FiafengMysqlMappingProperties properties) {
        DefaultMysqlMappingMapper mapper = new DefaultMysqlMappingMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        return mapper;
    }

    @Bean
    @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
    DefaultMapping defaultMapping(){
        DefaultMapping defaultMapping = new DefaultMapping();
        return defaultMapping;
    }

    @Bean
    @Primary
    DefaultMappingMapper defaultMappingMapper() {
        return new DefaultMappingMapper();
    }

    @Bean
    RequestMappingBean requestMappingBean() {
        return new RequestMappingBean();
    }

    @Bean
    MappingApplicationInit mappingApplicationInit() {
        return new MappingApplicationInit();
    }


    @Bean
    FiafengPermissionInterceptor fiafengMappingProperties(){
        return new FiafengPermissionInterceptor();
    }
}
