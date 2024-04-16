package com.fiafeng.mapping.config;

import com.fiafeng.mapping.interceptor.FiafengPermissionInterceptor;
import com.fiafeng.mapping.properties.FiafengMappingProperties;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MappingWebConfig implements WebMvcConfigurer {


    @Autowired
    FiafengMappingProperties mappingProperties;

    @Autowired
    FiafengPermissionInterceptor permissionInterceptor;

    @Autowired
    FiafengSecurityProperties securityProperties;

    /**
     * 添加自定义的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> list = mappingProperties.getInterceptor().isEmpty() ? securityProperties.permitAllList : mappingProperties.getInterceptor();
        if (mappingProperties.permissionInterceptorEnable) {
            registry.addInterceptor(permissionInterceptor).addPathPatterns("/**").excludePathPatterns(list);
        }
    }
}
