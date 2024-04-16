package com.fiafeng.common.init;

import com.fiafeng.common.controller.controller.Interface.IPermissionController;
import com.fiafeng.common.controller.controller.Interface.IRoleController;
import com.fiafeng.common.controller.controller.Interface.IRolePermissionController;
import com.fiafeng.common.controller.controller.Interface.IUserRoleController;
import com.fiafeng.common.utils.ObjectClassUtils;
import org.springframework.stereotype.Component;

@Component
public class RBACApplicationProcessor extends ApplicationProcessor {

    static {
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRolePermissionController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRoleController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleController.class);
    }
}
