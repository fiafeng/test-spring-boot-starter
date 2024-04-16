package com.fiafeng.common.controller.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.annotation.HasPermissionAnnotation;
import com.fiafeng.common.annotation.HasRoleAnnotation;
import com.fiafeng.common.controller.controller.Interface.IRoleController;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description RBAC角色控制器
 */
@RestController
@RequestMapping("/user/rbac/role")
@BeanDefinitionOrderAnnotation
public class RBACRoleController implements IRoleController {

    @Autowired
    IRoleService roleService;

    @PostMapping("/insert")
    public AjaxResult insertRole(@RequestBody JSONObject jsonObject){

        IBaseRole bean = FiafengSpringUtils.getBean(IBaseRole.class);
        IBaseRole iBaseRole = jsonObject.toJavaObject(bean.getClass());
        roleService.insertRole(iBaseRole);
        return AjaxResult.success();
    }

    @HasPermissionAnnotation("rbac:role:deleted")
    @DeleteMapping("/deleted/{roleId}")
    public AjaxResult deletedRole(@PathVariable Long roleId){
        // 如果还有角色拥有这个权限，则不允许删除
        roleService.deletedRoleById(roleId);
        return AjaxResult.success();
    }

    @PostMapping("/update")
    public AjaxResult updateRole(@RequestBody JSONObject jsonObject){
        IBaseRole bean = FiafengSpringUtils.getBean(IBaseRole.class);
        IBaseRole iBaseRole = jsonObject.toJavaObject(bean.getClass());
        roleService.updateRole(iBaseRole);
        return AjaxResult.success();
    }

    @HasRoleAnnotation
    @GetMapping("/queryList")
    public AjaxResult queryRoleMap(){
        List<IBaseRole> roleList = roleService.queryRoleListAll();
        return AjaxResult.success(roleList);
    }



}
