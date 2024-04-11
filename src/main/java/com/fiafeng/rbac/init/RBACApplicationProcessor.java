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

public class RBACApplicationProcessor {

    static {
        ObjectClassUtils.addClass(IPermissionController.class);
        ObjectClassUtils.addClass(IRolePermissionController.class);
        ObjectClassUtils.addClass(IRoleController.class);
        ObjectClassUtils.addClass(IUserRoleController.class);
    }
}
