package com.fiafeng.common.controller.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.HasRoleAnnotation;
import com.fiafeng.common.controller.controller.Interface.IRolePermissionController;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
import com.fiafeng.common.service.IRolePermissionService;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
@RestController
@RequestMapping("/user/rbac/rolePermission")
@BeanDefinitionOrderAnnotation()
public class RBACRolePermissionController implements IRolePermissionController {

    @Autowired
    IRolePermissionService rolePermissionService;

    @PostMapping("/insertPermission")
    public AjaxResult insertRole(@RequestBody JSONObject jsonObject) {

        IBaseRolePermission bean = FiafengSpringUtils.getBean(IBaseRolePermission.class);
        IBaseRolePermission rolePermission = jsonObject.toJavaObject(bean.getClass());

        rolePermissionService.insertRolePermission(rolePermission);
        return AjaxResult.success();
    }

    @DeleteMapping("/deleted")
    public AjaxResult deletedRole(@RequestBody JSONObject jsonObject) {
        IBaseRolePermission bean = FiafengSpringUtils.getBean(IBaseRolePermission.class);
        IBaseRolePermission rolePermission = jsonObject.toJavaObject(bean.getClass());
        rolePermissionService.deletedRolePermission(rolePermission);
        return AjaxResult.success();
    }

    /**
     * 更新用户角色列表
     */
    @PostMapping("/update")
    public AjaxResult updateRole(@RequestBody JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            throw new ServiceException("参数为空");
        }
        if (!jsonObject.containsKey("roleId") || !jsonObject.containsKey("permissionList")) {
            throw new ServiceException("参数错误");
        }
        Long roleId = jsonObject.getLong("roleId");
        List<Long> permissionList = jsonObject.getList("permissionList", Long.class);

        rolePermissionService.updateRolePermissionList(roleId, permissionList);
        return AjaxResult.success();
    }

    /**
     * 查询角色的权限列表
     * @param roleId 角色Id
     * @return 角色权限列表
     */
    @HasRoleAnnotation
    @GetMapping("/query/{roleId}")
    public AjaxResult queryRolePermission(@PathVariable Long roleId){
        List<String> permissionList = rolePermissionService.queryPermissionNameListByRoleId(roleId);
        return AjaxResult.success(permissionList);
    }


}
