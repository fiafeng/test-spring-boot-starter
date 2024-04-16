package com.fiafeng.common.config;

import com.fiafeng.common.Interceptor.FiafengI18nInterceptor;
import com.fiafeng.common.properties.FiafengI18NProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 资源文件配置加载
 *
 * @author ruoyi
 */


@Import({
        I18nBeanConfig.class
})
@EnableConfigurationProperties({
        FiafengI18NProperties.class
})
@ConditionalOnWebApplication
public class WebMvcI18nConfig implements WebMvcConfigurer {


    @Autowired
    FiafengI18nInterceptor i18nInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(i18nInterceptor).addPathPatterns("/**");
    }

}