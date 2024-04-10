package com.fiafeng.mybatis.config;


import com.fiafeng.mybatis.factory.CustomObjectFactory;
import com.fiafeng.mybatis.factory.ObjectFactoryConverter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@Import({
        MyBatisSupportConfig.class
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
