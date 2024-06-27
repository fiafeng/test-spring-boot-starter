package com.fiafeng.mybatis.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;

import com.fiafeng.mybatis.factory.CustomObjectFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// 在配置类中进行相关配置
@ConditionalOnClass({SqlSessionFactoryBean.class})
public class MyBatisSupportConfig {

    @Autowired
    CustomObjectFactory customObjectFactory;

    @Bean(name = "sqlSessionFactory")
    @ConditionalOnClass(SqlSessionFactoryBean.class)
    @ConditionalOnMissingClass("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
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


    @Bean(name = "sqlSessionFactory")
    @ConditionalOnClass({MybatisSqlSessionFactoryBean.class, SqlSessionFactoryBean.class})
    public SqlSessionFactory MybatisPlusSqlSessionFactory(DataSource dataSource) throws Exception {
        Class.forName("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean");
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);

        Map<String, Interceptor> interceptorMap = FiafengSpringUtils.getBeanFactory().getBeansOfType(Interceptor.class);
        List<Interceptor> values = new ArrayList<>(interceptorMap.values());

        mybatisSqlSessionFactoryBean.setObjectFactory(customObjectFactory);
        mybatisSqlSessionFactoryBean.setPlugins(values.toArray(new Interceptor[values.size()]));


        return mybatisSqlSessionFactoryBean.getObject();
    }
}