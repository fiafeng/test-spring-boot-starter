package com.fiafeng.i18n.config;

import com.fiafeng.i18n.Interceptor.FiafengI18nInterceptor;
import com.fiafeng.i18n.properties.FiafengI18nProperties;
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
        FiafengI18nProperties.class
})
@ConditionalOnWebApplication
public class I18nConfig implements WebMvcConfigurer {


    @Autowired
    FiafengI18nInterceptor i18nInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(i18nInterceptor).addPathPatterns("/**");
    }

}