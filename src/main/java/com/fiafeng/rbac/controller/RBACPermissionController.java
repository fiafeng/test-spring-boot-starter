package com.fiafeng.rbac.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.rbac.annotation.HasRole;
import com.fiafeng.rbac.controller.Interface.IPermissionController;
import com.fiafeng.common.pojo.AjaxResult;
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
@BeanDefinitionOrderAnnotation
public class RBACPermissionController implements IPermissionController {

    @Autowired
    IPermissionService permissionService;

    @Autowired
    IRoleService roleService;

    @Autowired
    IRolePermissionService rolePermissionService;


    @PostMapping("/insert")
    public AjaxResult insertRole(@RequestBody JSONObject jsonObject){
        IBasePermission bean = SpringUtils.getBean(IBasePermission.class);
        IBasePermission iBasePermission = jsonObject.toJavaObject(bean.getClass());
        permissionService.insertPermission(iBasePermission);
        return AjaxResult.success();
    }

    @PostMapping("/deleted/{permissionId}")
    public AjaxResult deletedRole(@PathVariable Long permissionId){
        // 如果还有角色拥有这个权限，则不允许删除
        permissionService.deletedPermission(permissionId);
        return AjaxResult.success();
    }

    @PostMapping("/update")
    public AjaxResult updateRole(@RequestBody  JSONObject jsonObject){
        IBasePermission bean = SpringUtils.getBean(IBasePermission.class);
        IBasePermission iBasePermission = jsonObject.toJavaObject(bean.getClass());
        permissionService.updatePermission(iBasePermission);
        return AjaxResult.success();
    }

    @HasRole
    @GetMapping("/queryList")
    public AjaxResult queryRoleMap(){
        List<IBasePermission> permissionList = permissionService.queryPermissionListALl();
        return AjaxResult.success(permissionList);
    }
}
