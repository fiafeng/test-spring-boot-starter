package com.fiafeng.common.init;

import com.fiafeng.common.utils.ObjectClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;

public class CommonApplicationProcess implements  BeanDefinitionRegistryPostProcessor , BeanPostProcessor, Ordered {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ObjectClassUtils.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ObjectClassUtils.beanFactory = beanFactory;

        ObjectClassUtils.removeBeanDefinitions();

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
