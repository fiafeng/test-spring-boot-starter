package com.fiafeng.security.config;

import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import com.fiafeng.common.properties.mysql.FiafengMysqlUserProperties;
import com.fiafeng.security.mapper.DefaultSecurityMysqlUserMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.AutowiredWebSecurityConfigurersIgnoreParents;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ConditionalOnClass(AutowiredWebSecurityConfigurersIgnoreParents.class)
@ConditionalEnableProperty(value = "fiafeng.security.enable")
@Import({
        SecurityWebConfig.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.security")
})
@ConditionalOnWebApplication
public class SecurityConfig {

    @ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
    @ConditionalEnableProperty("fiafeng.mysql.enable")
    @Bean
    DefaultSecurityMysqlUserMapper defaultSecurityMysqlUserMapper(FiafengMysqlUserProperties properties) {
        DefaultSecurityMysqlUserMapper mapper = new DefaultSecurityMysqlUserMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.tableColName = properties.tableColName;
        return mapper;
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
