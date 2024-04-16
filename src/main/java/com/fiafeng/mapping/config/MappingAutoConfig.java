package com.fiafeng.mapping.config;


import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import com.fiafeng.common.config.CommonConfig;
import com.fiafeng.mapping.mapper.DefaultMysqlMappingMapper;
import com.fiafeng.mapping.properties.FiafengMappingProperties;
import com.fiafeng.mapping.properties.FiafengMysqlMappingProperties;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@EnableConfigurationProperties(value = {
        FiafengMappingProperties.class,
        FiafengSecurityProperties.class,
        FiafengMysqlMappingProperties.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.mapping")
})
@ConditionalEnableProperty("fiafeng.mapping.enable")
@AutoConfigureAfter({
        CommonConfig.class
})
@ConditionalOnWebApplication
public class MappingAutoConfig {



    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource", name = {"url", "username", "password"})
    @ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
    DefaultMysqlMappingMapper defaultMysqlMappingMapper(FiafengMysqlMappingProperties properties) {
        DefaultMysqlMappingMapper mapper = new DefaultMysqlMappingMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        return mapper;
    }
}
