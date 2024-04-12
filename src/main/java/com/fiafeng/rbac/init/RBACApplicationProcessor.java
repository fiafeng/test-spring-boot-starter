package com.fiafeng.rbac.init;

import com.fiafeng.common.init.ApplicationInit;
import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.properties.FiafengTokenProperties;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.IUserRoleService;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.rbac.controller.Interface.IPermissionController;
import com.fiafeng.rbac.controller.Interface.IRoleController;
import com.fiafeng.rbac.controller.Interface.IRolePermissionController;
import com.fiafeng.rbac.controller.Interface.IUserRoleController;
import org.springframework.beans.factory.annotation.Autowired;

public class RBACApplicationProcessor extends ApplicationProcessor implements ApplicationInit {

    static {
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRolePermissionController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRoleController.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleController.class);
    }


    @Override
    public void init() {


    }
}
