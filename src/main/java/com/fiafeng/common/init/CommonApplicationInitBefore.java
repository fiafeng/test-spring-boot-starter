package com.fiafeng.common.init;

import com.fiafeng.common.controller.ILoginController;
import com.fiafeng.common.controller.controller.Interface.IPermissionController;
import com.fiafeng.common.controller.controller.Interface.IRoleController;
import com.fiafeng.common.controller.controller.Interface.IRolePermissionController;
import com.fiafeng.common.controller.controller.Interface.IUserRoleController;
import com.fiafeng.common.filter.IJwtAuthenticationTokenFilter;
import com.fiafeng.common.mapper.Interface.*;
import com.fiafeng.common.pojo.Interface.*;
import com.fiafeng.common.properties.IFiafengProperties;
import com.fiafeng.common.properties.mysql.FiafengMysqlPermissionProperties;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.*;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

@Component
public class CommonApplicationInitBefore implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, Ordered {

    // 添加需要保持唯一的类
    static {
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseMapping.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseRole.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseRolePermission.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseUser.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseUserRole.class);

        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRoleMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRolePermissionMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleMapper.class);

        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserTableInitService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ITokenService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ILoginService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ICacheService.class);

        ObjectClassUtils.addRemoveBeanDefinitionByClass(IJwtAuthenticationTokenFilter.class);

        ObjectClassUtils.addRemoveBeanDefinitionByClass(ILoginController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRolePermissionController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRoleController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleController.class);
    }

    // 添加mybatisPlus表名映射
    static {
        MybatisPlusUtils.putHashMapMybatisPlusTableName(IUserMapper.class, IBaseUser.class);
        MybatisPlusUtils.putHashMapMybatisPlusTableName(IUserRoleMapper.class, IBaseUserRole.class);
        MybatisPlusUtils.putHashMapMybatisPlusTableName(IRoleMapper.class, IBaseRole.class);
        MybatisPlusUtils.putHashMapMybatisPlusTableName(IRolePermissionMapper.class, IBaseRolePermission.class);
        MybatisPlusUtils.putHashMapMybatisPlusTableName(IPermissionMapper.class, IBasePermission.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (ObjectClassUtils.registry == null) {
            ObjectClassUtils.registry = registry;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (ObjectClassUtils.beanFactory == null) {
            ObjectClassUtils.beanFactory = beanFactory;
        }
        if (FiafengSpringUtils.beanFactory == null) {
            FiafengSpringUtils.beanFactory = beanFactory;
        }


    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (ObjectClassUtils.url == null) {
            Environment environment = event.getApplicationContext().getEnvironment();
            ObjectClassUtils.url = environment.getProperty("spring.datasource.url");
        }

        if (FiafengSpringUtils.applicationContext == null) {
            FiafengSpringUtils.applicationContext = event.getApplicationContext();
        }

        // 注入配置文件里面的属性
        Environment environment = event.getApplicationContext().getEnvironment();
        Map<String, IFiafengProperties> beansOfType = FiafengSpringUtils.getBeansOfType(IFiafengProperties.class);
        for (IFiafengProperties properties : beansOfType.values()) {
            Class<? extends IFiafengProperties> propertiesClass = properties.getClass();
            ConfigurationProperties annotation = propertiesClass.getAnnotation(ConfigurationProperties.class);
            String value = annotation.value();
            for (Field field : propertiesClass.getFields()) {
                String key = value + "." + StringUtils.camelToKebab(field.getName());
                Object property = environment.getProperty(key + "", field.getType());
                if (property != null) {
                    field.setAccessible(true);
                    try {
                        field.set(properties, property);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }


}
