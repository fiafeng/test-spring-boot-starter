package com.fiafeng.mysql.init;

import com.fiafeng.mysql.config.DefaultDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import javax.sql.DataSource;

public class MysqlApplicationInit implements  BeanDefinitionRegistryPostProcessor {
    BeanDefinitionRegistry registry;

    @Override
    // 移除多余的实现类
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForType(DataSource.class);
        if (beanNames.length == 0){
            // 注册自定义数据源
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultDataSource.class);
            BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            registry.registerBeanDefinition("defaultDataSource",beanDefinition);
        }

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }
}
