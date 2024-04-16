package com.fiafeng.common.config;

import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.properties.FiafengTokenProperties;
import com.fiafeng.common.utils.spring.FiafengMessageUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.security.config.SecurityConfig;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@ComponentScans({
        @ComponentScan("com.fiafeng.common")
})
@EnableConfigurationProperties({
        FiafengTokenProperties.class,
        FiafengRbacProperties.class,
        FiafengSecurityProperties.class
})
@Import({
        WebMvcI18nConfig.class,
        SecurityConfig.class,
        MysqlMapperConfig.class,
})
public class CommonConfig {

    @Bean
    FiafengSpringUtils springUtils() {
        return new FiafengSpringUtils();
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
