package com.fiafeng.flowable.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


@Configuration
@MapperScans({
        @MapperScan(basePackages = "com.fiafeng.flowable.flowable.dao", sqlSessionTemplateRef = "sqlSessionTemplate",
                sqlSessionFactoryRef = "sqlSessionFactory")
})
@ComponentScans({
        @ComponentScan("com.fiafeng.flowable")
})
@DependsOn("dataSource")
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    /**
     * 防止生成的流程图中文乱码
     *
     * @param springProcessEngineConfiguration
     */
    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        springProcessEngineConfiguration.setActivityFontName("宋体");
        springProcessEngineConfiguration.setLabelFontName("宋体");
        springProcessEngineConfiguration.setAnnotationFontName("宋体");
    }
}
