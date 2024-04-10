package com.fiafeng.mybatis.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;

import com.fiafeng.mybatis.factory.CustomObjectFactory;

import javax.sql.DataSource;


// 在配置类中进行相关配置
@ConditionalOnClass({SqlSessionFactoryBean.class})
public class MyBatisSupportConfig {

    @Autowired
    DataSource dataSource;

    @Autowired
    CustomObjectFactory customObjectFactory;


    @Bean(name = "sqlSessionFactory")
    @ConditionalOnClass(SqlSessionFactoryBean.class)
    @ConditionalOnMissingClass("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        SqlSessionFactory object = sessionFactory.getObject();
        if (object != null) {
            org.apache.ibatis.session.Configuration configuration = object.getConfiguration();
            if (configuration != null)
                configuration.setObjectFactory(customObjectFactory);
        }
        return object;
    }


    @Bean(name = "mybatisSqlSessionFactoryBean")
    @ConditionalOnClass({MybatisSqlSessionFactoryBean.class, SqlSessionFactoryBean.class})
    public SqlSessionFactory MybatisPlusSqlSessionFactory() throws Exception {
        Class.forName("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean");
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setObjectFactory(customObjectFactory);
        return mybatisSqlSessionFactoryBean.getObject();
    }



}