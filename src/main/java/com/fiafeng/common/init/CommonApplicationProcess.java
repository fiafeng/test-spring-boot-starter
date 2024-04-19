package com.fiafeng.common.init;

import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.annotation.ApplicationProcessorAnnotation;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class CommonApplicationProcess implements  BeanDefinitionRegistryPostProcessor , BeanPostProcessor, Ordered {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ObjectClassUtils.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ObjectClassUtils.beanFactory = beanFactory;
        Map<String, ApplicationProcessor> beansOfType = beanFactory.getBeansOfType(ApplicationProcessor.class);
        Integer[] valuesArray = new Integer[beansOfType.size()];
        ApplicationProcessor[] ApplicationProcessorArray = beansOfType.values().toArray(new ApplicationProcessor[beansOfType.values().size()]);
        for (int i = 0; i < ApplicationProcessorArray.length; i++) {
            ApplicationProcessor applicationInit = ApplicationProcessorArray[i];
            ApplicationProcessorAnnotation annotation = applicationInit.getClass().getAnnotation(ApplicationProcessorAnnotation.class);
            if (annotation != null) {
                valuesArray[i] = annotation.value();
            } else {
                valuesArray[i] = 0;
            }
        }

        for (int i = 0; i < valuesArray.length; i++) {
            int max = -9997;
            int pos = -1;
            for (int j = 0; j < valuesArray.length; j++) {
                if (valuesArray[j] > max){
                    max = valuesArray[j];
                    pos = j;
                }
            }
            ApplicationProcessorArray[pos].postProcessBeanFactory();
            valuesArray[pos] = -9999;
        }

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
