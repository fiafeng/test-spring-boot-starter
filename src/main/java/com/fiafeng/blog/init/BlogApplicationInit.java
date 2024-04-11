package com.fiafeng.blog.init;

import com.fiafeng.blog.mapper.IBlogMapper;
import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.init.ApplicationInit;
import com.fiafeng.common.mapper.IMappingMapper;
import com.fiafeng.common.pojo.Interface.IBaseMapping;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class BlogApplicationInit implements ApplicationInit{


    static {
        ObjectClassUtils.addClass(IBaseBlog.class);
    }

    @Override
    public void init() {
        ObjectClassUtils.refreshBaseMysqlMapperType(IBlogMapper.class, IBaseBlog.class);

    }
}
