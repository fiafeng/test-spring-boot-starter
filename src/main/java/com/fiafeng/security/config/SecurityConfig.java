package com.fiafeng.security.config;

import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import com.fiafeng.mysql.properties.FiafengMysqlUserProperties;
import com.fiafeng.security.filter.DefaultSecurityJwtAuthenticationTokenFilter;
import com.fiafeng.security.handler.AuthenticationEntryPointHandler;
import com.fiafeng.security.handler.LogoutSuccessHandlerImpl;
import com.fiafeng.security.init.SecurityApplicationProcessor;
import com.fiafeng.security.mapper.DefaultSecurityMysqlUserMapper;
import com.fiafeng.security.mapper.DefaultSecurityUserMapper;
import com.fiafeng.security.pojo.DefaultSecurityLoginUserInfo;
import com.fiafeng.security.service.Impl.DefaultSecurityLoginServiceImpl;
import com.fiafeng.security.service.Impl.DefaultSecurityUserTableInitServiceImpl;
import com.fiafeng.security.service.Impl.DefaultTokenSecurityServiceImpl;
import com.fiafeng.security.service.Impl.DefaultUserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.AutowiredWebSecurityConfigurersIgnoreParents;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ConditionalOnClass(AutowiredWebSecurityConfigurersIgnoreParents.class)
@ConditionalEnableProperty(value = "fiafeng.security.enable")
@Import({
        SecurityWebConfig.class
})
@ConditionalOnWebApplication
public class SecurityConfig {

    @Bean
    @Primary
    DefaultSecurityLoginServiceImpl defaultSecurityLoginService(){
        return new DefaultSecurityLoginServiceImpl();
    }

    @Bean
    DefaultUserDetailsServiceImpl defaultUserDetailsService(){
        return new DefaultUserDetailsServiceImpl();
    }

    @Bean
    DefaultSecurityLoginUserInfo defaultSecurityLoginUserInfo(){
        return new DefaultSecurityLoginUserInfo();
    }

    @Bean
    @Primary
    DefaultSecurityUserMapper defaultSecurityUserMapper(){
        return new DefaultSecurityUserMapper();
    }


    @ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
    @ConditionalEnableProperty("fiafeng.mysql.enable")
    @Bean
    DefaultSecurityMysqlUserMapper defaultSecurityMysqlUserMapper(FiafengMysqlUserProperties properties){
        DefaultSecurityMysqlUserMapper mapper = new DefaultSecurityMysqlUserMapper();
        mapper.tableName = properties.tableName;
        mapper.idName = properties.idName;
        mapper.tableColName = properties.tableColName;
        return mapper;
    }

    @Bean
    DefaultSecurityUserTableInitServiceImpl defaultSecurityUserTableInitService(){
        return new DefaultSecurityUserTableInitServiceImpl();
    }

    @Bean
    DefaultSecurityJwtAuthenticationTokenFilter defaultSecurityJwtAuthenticationTokenFilter(){
        return  new DefaultSecurityJwtAuthenticationTokenFilter();
    }

    @Bean
    LogoutSuccessHandlerImpl logoutSuccessHandler(){
        return new LogoutSuccessHandlerImpl();
    }

    @Bean
    AuthenticationEntryPointHandler authenticationEntryPointHandler(){
        return new AuthenticationEntryPointHandler();
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    SecurityApplicationProcessor securityBeanDefinitionRegistryPostProcessor(){
        return new SecurityApplicationProcessor();
    }

    @Bean
    @Primary
    DefaultTokenSecurityServiceImpl defaultTokenSecurityService(){
        return new DefaultTokenSecurityServiceImpl();
    }

}
