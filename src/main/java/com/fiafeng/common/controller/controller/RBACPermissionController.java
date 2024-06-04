package com.fiafeng.common.controller.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.annotation.HasPermissionAnnotation;
import com.fiafeng.common.annotation.HasRoleAnnotation;
import com.fiafeng.common.controller.controller.Interface.IPermissionController;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.service.IPermissionService;
import com.fiafeng.common.service.IRolePermissionService;
import com.fiafeng.common.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description RBAC权限控制器
 */
@RestController
@RequestMapping("/user/rbac/permission")
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class RBACPermissionController implements IPermissionController {

    @Autowired
    IPermissionService permissionService;

    @Autowired
    IRoleService roleService;

    @Autowired
    IRolePermissionService rolePermissionService;


    @PostMapping("/insert")
    @HasPermissionAnnotation("rbac:permission:insert")
    @HasRoleAnnotation("rbac:permission")
    public AjaxResult insertRole(@RequestBody JSONObject jsonObject){
        IBasePermission bean = FiafengSpringUtils.getBean(IBasePermission.class);
        IBasePermission iBasePermission = jsonObject.toJavaObject(bean.getClass());
        permissionService.insertPermission(iBasePermission);
        return AjaxResult.success();
    }

    @PostMapping("/deleted/{permissionId}")
    @HasPermissionAnnotation("rbac:permission:deleted")
    public AjaxResult deletedRole(@PathVariable Long permissionId){
        // 如果还有角色拥有这个权限，则不允许删除
        permissionService.deletedPermission(permissionId);
        return AjaxResult.success();
    }

    @PostMapping("/update")
    @HasPermissionAnnotation("rbac:permission:update")
    public AjaxResult updateRole(@RequestBody  JSONObject jsonObject){
        IBasePermission bean = FiafengSpringUtils.getBean(IBasePermission.class);
        IBasePermission iBasePermission = jsonObject.toJavaObject(bean.getClass());
        permissionService.updatePermission(iBasePermission);
        return AjaxResult.success();
    }

    @GetMapping("/queryList")
    @HasPermissionAnnotation("rbac:permission:all")
    public AjaxResult queryRoleList(){
        List<IBasePermission> permissionList = permissionService.queryPermissionListALl();
        return AjaxResult.success(permissionList);
    }
}
