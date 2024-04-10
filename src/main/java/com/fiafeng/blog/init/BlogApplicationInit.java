package com.fiafeng.blog.init;

import com.fiafeng.blog.annotation.BaseBlogAnnotation;
import com.fiafeng.blog.mapper.IBlogMapper;
import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.utils.ObjectClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BlogApplicationInit implements BeanPostProcessor, BeanDefinitionRegistryPostProcessor, BeanFactoryPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 防止事件运行多次
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        ObjectClassUtils.refreshBaseMysqlMapperType(IBlogMapper.class, IBaseBlog.class);

    }

    BeanDefinitionRegistry registry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ObjectClassUtils.addBaseBeanType(bean, IBaseBlog.class, BaseBlogAnnotation.class);
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IBaseBlog.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }
}
