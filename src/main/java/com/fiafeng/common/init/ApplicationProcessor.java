package com.fiafeng.common.init;


import com.fiafeng.common.annotation.ApplicationProcessorAnnotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;

@ApplicationProcessorAnnotation
public abstract class ApplicationProcessor implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor, Ordered {

    public void postProcessBeanFactory(){

     }



    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }



    @Override
    public int getOrder() {
        return 0;
    }
}
