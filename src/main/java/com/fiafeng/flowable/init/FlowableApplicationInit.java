package com.fiafeng.flowable.init;


import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.init.ApplicationInitAfter;
import com.fiafeng.common.service.IRoleService;
import com.fiafeng.common.service.IUserRoleService;
import com.fiafeng.common.service.IUserService;
import org.flowable.idm.api.IdmIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ApplicationInitAnnotation(-2)
public class FlowableApplicationInit implements ApplicationInitAfter {

    @Autowired
    IUserService userService;

    @Autowired
    IRoleService roleService;

    @Autowired
    IUserRoleService userRoleService;


    @Autowired
    IdmIdentityService idmIdentityService;


    @Override
    public void init() {

    }
}
