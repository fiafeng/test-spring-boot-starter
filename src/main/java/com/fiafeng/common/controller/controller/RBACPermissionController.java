package com.fiafeng.common.controller.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.HasPermissionAnnotation;
import com.fiafeng.common.annotation.HasRoleAnnotation;
import com.fiafeng.common.controller.controller.Interface.IPermissionController;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.properties.mysql.FiafengMysqlPermissionProperties;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.IPermissionService;
import com.fiafeng.common.service.IRolePermissionService;
import com.fiafeng.common.service.IRoleService;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
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
@BeanDefinitionOrderAnnotation()
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
        ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
        IMysqlTableProperties mysqlUserProperties = FiafengSpringUtils.getBeanObject(FiafengMysqlPermissionProperties.class);
        Long autoIncrementValue = connectionPoolService.getAutoIncrementValue(mysqlUserProperties.getTableName());
        iBasePermission.setId(autoIncrementValue);

        permissionService.insertPermission(iBasePermission);
        return AjaxResult.success("添加成功");
    }

    @PostMapping("/deleted/{permissionId}")
    @HasPermissionAnnotation("rbac:permission:deleted")
    public AjaxResult deletedRole(@PathVariable Long permissionId){
        // 如果还有角色拥有这个权限，则不允许删除
        permissionService.deletedPermissionById(permissionId);
        return AjaxResult.success("删除成功");
    }

    @PostMapping("/deletedByName/{permissionName}")
    @HasPermissionAnnotation("rbac:permission:deleted")
    public AjaxResult deletedRoleByName(@PathVariable String permissionName){
        // 如果还有角色拥有这个权限，则不允许删除
        permissionService.deletedPermissionByName(permissionName);
        return AjaxResult.success("删除成功");
    }

    @PostMapping("/update")
    @HasPermissionAnnotation("rbac:permission:update")
    public AjaxResult updateRole(@RequestBody  JSONObject jsonObject){
        IBasePermission bean = FiafengSpringUtils.getBean(IBasePermission.class);
        IBasePermission iBasePermission = jsonObject.toJavaObject(bean.getClass());
        permissionService.updatePermission(iBasePermission);
        return AjaxResult.success("修改成功");
    }

    @GetMapping("/queryList")
    @HasPermissionAnnotation("rbac:permission:all")
    public AjaxResult queryRoleList(){
        List<IBasePermission> permissionList = permissionService.queryPermissionListALl();
        return AjaxResult.success(permissionList);
    }
}
