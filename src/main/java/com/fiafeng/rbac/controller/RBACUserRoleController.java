package com.fiafeng.rbac.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.rbac.annotation.HasRole;
import com.fiafeng.rbac.controller.Interface.IUserRoleController;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.rbac.pojo.DefaultUserRole;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description 用户角色控制器
 */
@RestController
@RequestMapping("/user/rbac/userRole")
@BeanDefinitionOrderAnnotation
public class RBACUserRoleController implements IUserRoleController {

    @Autowired
    IUserRoleService userRoleService;

    @Autowired
    FiafengRbacProperties rbacProperties;

    @GetMapping("/insert/{userId}/{roleId}")
    public AjaxResult insertRole(@PathVariable Long roleId, @PathVariable Long userId) {
        if (rbacProperties.allowHasRoles){
            List<IBaseRole> iBaseRoles = userRoleService.queryUserRoleListByUserId(userId);
            if (!iBaseRoles.isEmpty()){
                throw new ServiceException("用户单一角色限制已经开启！");
            }
        }

        IBaseUserRole userRole = SpringUtils.getBean(IBaseUserRole.class);
        userRole.setRoleId(roleId).setUserId(userId);


        userRoleService.insertUserRole(userRole);
        return AjaxResult.success();
    }

    @PostMapping("/deleted/{userId}/{roleId}")
    public AjaxResult deletedRole(@PathVariable Long roleId, @PathVariable Long userId) {
        IBaseUserRole userRole = SpringUtils.getBean(IBaseUserRole.class);
        userRole.setRoleId(roleId).setUserId(userId);
        userRoleService.deletedUserRole(userRole);
        return AjaxResult.success();
    }


    @PostMapping("/deleted")
    public AjaxResult deletedRole(@RequestBody DefaultUserRole userRole) {

        userRoleService.deletedUserRole(userRole);
        return AjaxResult.success();
    }

    @PostMapping("/deleted/{id}")
    public AjaxResult deletedRole(@PathVariable Long id) {
        userRoleService.deletedUserRoleById(id);
        return AjaxResult.success();
    }

    @PostMapping("/updateRoleList")
    public AjaxResult updateRole(@RequestBody JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            throw new ServiceException("参数为空");
        }
        if (!jsonObject.containsKey("userId") || !jsonObject.containsKey("roleList")) {
            throw new ServiceException("参数错误");
        }
        Long userId = jsonObject.getLong("userId");
        List<Long> roleList = jsonObject.getList("roleList", Long.class);

        userRoleService.updateUserRoleList(userId, roleList);
        return AjaxResult.success();
    }

    @HasRole
    @GetMapping("/queryRoleNameList/{userId}")
    public AjaxResult queryRoleNameByUserId(@PathVariable Long userId) {
        List<String> roleList = userRoleService.queryUserRoleNameListByUserId(userId);
        return AjaxResult.success(roleList);
    }
}
