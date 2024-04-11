package com.fiafeng.common.config.bean;

import com.fiafeng.common.filter.RefreshUserFilter;
import com.fiafeng.common.service.Impl.*;
import com.fiafeng.common.utils.FiafengMessageUtils;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import com.fiafeng.common.filter.DefaultJwtAuthenticationTokenFilter;
import com.fiafeng.common.properties.FiafengTokenProperties;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@ComponentScans({
        @ComponentScan("com.fiafeng.common.controller"),
        @ComponentScan("com.fiafeng.common.handler")
})
@EnableConfigurationProperties({
        FiafengTokenProperties.class,
        FiafengSecurityProperties.class
})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class WebBeanConfig {

    @Bean
    DefaultCacheServiceImpl defaultCacheService() {
        return new DefaultCacheServiceImpl();
    }

    @Bean
    DefaultLoginServiceImpl defaultLoginService() {
        return new DefaultLoginServiceImpl();
    }

    @Bean
    DefaultTokenServiceImpl defaultTokenService() {
        return new DefaultTokenServiceImpl();
    }

    @Bean
    UpdateCacheServiceImpl updateCacheService(){
        return new UpdateCacheServiceImpl();
    }

    @Bean
    @Primary
    DefaultUserTableInitServiceServiceImpl defaultUserTableInitServiceService() {
        return new DefaultUserTableInitServiceServiceImpl();
    }

    @Bean
    DefaultJwtAuthenticationTokenFilter defaultJwtAuthenticationTokenFilter(){
        return new DefaultJwtAuthenticationTokenFilter();
    }


    @Bean
    RefreshUserFilter refreshUserFilter(){
        return new RefreshUserFilter();
    }

    @Bean
    SpringUtils springUtils() {
        return new SpringUtils();
    }

    @Bean
    FiafengMessageUtils messageUtils() {
        return new FiafengMessageUtils();
    }


    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 有效期 1800秒
        config.setMaxAge(1800L);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }
}
