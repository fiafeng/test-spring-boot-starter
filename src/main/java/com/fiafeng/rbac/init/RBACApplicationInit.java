package com.fiafeng.rbac.init;

import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.rbac.controller.Interface.IPermissionController;
import com.fiafeng.rbac.controller.Interface.IRoleController;
import com.fiafeng.rbac.controller.Interface.IRolePermissionController;
import com.fiafeng.rbac.controller.Interface.IUserRoleController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class RBACApplicationInit  implements  BeanDefinitionRegistryPostProcessor, BeanPostProcessor {


    BeanDefinitionRegistry registry;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IPermissionController.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IRolePermissionController.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IRoleController.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IUserRoleController.class);

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }
}
