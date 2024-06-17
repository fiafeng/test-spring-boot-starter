package com.fiafeng.common.init;

import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.annotation.ApplicationProcessorAnnotation;
import com.fiafeng.common.mapper.Interface.*;
import com.fiafeng.common.pojo.Interface.*;
import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.Impl.DefaultCacheServiceImpl;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CommonApplicationInitAfter implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, Ordered {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
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
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        // 如果加载了默认提供的mysql的RBAC的Service时，从系统获取默认基础类型加载到
        ObjectClassUtils.refreshBaseMysqlMapperType(IPermissionMapper.class, IBasePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRolePermissionMapper.class, IBaseRolePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRoleMapper.class, IBaseRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserRoleMapper.class, IBaseUserRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserMapper.class, IBaseUser.class);

        ObjectClassUtils.mysqlMapperInit(FiafengSpringUtils.getBean(FiafengRbacProperties.class));



        Map<String, ApplicationInit> beansOfType = FiafengSpringUtils.getBeanFactory().getBeansOfType(ApplicationInit.class);
        Integer[] valuesArray = new Integer[beansOfType.size()];
        ApplicationInit[] applicationInitArray = beansOfType.values().toArray(new ApplicationInit[beansOfType.values().size()]);
        for (int i = 0; i < applicationInitArray.length; i++) {
            ApplicationInit applicationInit = applicationInitArray[i];
            try {
                Method method = applicationInit.getClass().getMethod("init", null);
                ApplicationInitAnnotation annotation = method.getAnnotation(ApplicationInitAnnotation.class);
                if (annotation != null) {
                    valuesArray[i] = annotation.value();
                } else {
                    valuesArray[i] = 0;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }

        for (int i = 0; i < valuesArray.length; i++) {
            int max = -9998;
            int pos = -1;
            for (int j = 0; j < valuesArray.length; j++) {
                if (valuesArray[j] > max){
                    max = valuesArray[j];
                    pos = j;
                }
            }
            applicationInitArray[pos].init();
            valuesArray[pos] = -9999;
        }

        // 查看当前缓存类是否是默认实现类，如果是则添加定时清理k过期key任务
        ICacheService cacheService = FiafengSpringUtils.getBean(ICacheService.class);
        if (cacheService instanceof DefaultCacheServiceImpl){
            DefaultCacheServiceImpl defaultCacheService = (DefaultCacheServiceImpl) cacheService;
            ThreadFactory namedThreadFactory = new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("默认缓存定时清理过期key任务线程-" + threadNumber.getAndIncrement());
                    return t;
                }
            };

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, namedThreadFactory);
            Runnable task = () -> {
                for (String key : defaultCacheService.cacheHashMap.keySet()) {
                    cacheService.getCacheObject(key);
                }
            };
            // 没小时清理一次
            executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.HOURS);
        }


    }




    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
