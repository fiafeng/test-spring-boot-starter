package com.fiafeng.mybatis.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fiafeng.mybatis.factory.CustomObjectFactory;
import com.fiafeng.mybatis.factory.ObjectFactoryConverter;
import com.fiafeng.mybatis.properties.FiafengMybatisPageProperties;
import com.fiafeng.mybatis.properties.FiafengMybatisProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;


@Import({
        MyBatisSupportConfig.class
})
@EnableConfigurationProperties({
        FiafengMybatisProperties.class,
        FiafengMybatisPageProperties.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.mybatis.config"),
        @ComponentScan("com.fiafeng.mybatis.factory"),
        @ComponentScan("com.fiafeng.mybatis.init"),
        @ComponentScan("com.fiafeng.mybatis.Interceptor"),
        @ComponentScan("com.fiafeng.mybatis.utils"),
        @ComponentScan("com.fiafeng.mybatis.aop")
//        , @ComponentScan("com.fiafeng.mybatis.dao")
})
@MapperScans({
        @MapperScan("com.fiafeng.mybatis.dao")
})
@ConditionalOnClass({SqlSessionFactoryBean.class})
@ConditionalOnWebApplication
public class MyBatisConfig {

    @Bean
    public CustomObjectFactory customObjectFactory(){
        return new CustomObjectFactory();
    }

    @Bean
    ObjectFactoryConverter objectFactoryConverter(){
        return new ObjectFactoryConverter();
    }


}
