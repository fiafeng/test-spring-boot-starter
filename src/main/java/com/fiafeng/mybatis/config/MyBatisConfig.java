package com.fiafeng.mybatis.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.fiafeng.mybatis.factory.CustomObjectFactory;
import com.fiafeng.mybatis.factory.ObjectFactoryConverter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;


@Import({
        MyBatisSupportConfig.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.mybatis.config"),
        @ComponentScan("com.fiafeng.mybatis.factory"),
        @ComponentScan("com.fiafeng.mybatis.init"),
        @ComponentScan("com.fiafeng.mybatis.Interceptor"),
        @ComponentScan("com.fiafeng.mybatis.utils")
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
