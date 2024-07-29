package com.fiafeng.common.controller.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.HasRoleAnnotation;
import com.fiafeng.common.controller.controller.Interface.IUserRoleController;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.properties.mysql.FiafengMysqlUserRoleProperties;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.IUserRoleService;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
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
@BeanDefinitionOrderAnnotation()
public class RBACUserRoleController implements IUserRoleController {

    @Autowired
    IUserRoleService userRoleService;

    @Autowired
    FiafengRbacProperties rbacProperties;

    @PostMapping("/insert")
    public AjaxResult insertRole(@RequestBody JSONObject jsonObject) {

        IBaseUserRole bean = FiafengSpringUtils.getBean(IBaseUserRole.class);
        IBaseUserRole userRole = jsonObject.toJavaObject(bean.getClass());

        if (rbacProperties.allowHasRoles) {
            List<IBaseRole> iBaseRoles = userRoleService.queryUserRoleListByUserId(userRole.getUserId());
            if (!iBaseRoles.isEmpty()) {
                throw new ServiceException("用户单一角色限制已经开启,当前用户已经拥有一个角色了！");
            }
        }
        ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
        IMysqlTableProperties mysqlUserProperties = FiafengSpringUtils.getBeanObject(FiafengMysqlUserRoleProperties.class);
        Long autoIncrementValue = connectionPoolService.getAutoIncrementValue(mysqlUserProperties.getTableName());
        userRole.setId(autoIncrementValue);

        userRoleService.insertUserRole(userRole);
        return AjaxResult.success();
    }

    @PostMapping("/deleted/{userId}/{roleId}")
    public AjaxResult deletedRole(@PathVariable Long roleId, @PathVariable Long userId) {
        IBaseUserRole userRole = FiafengSpringUtils.getBean(IBaseUserRole.class);
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        userRoleService.deletedUserRole(userRole);
        return AjaxResult.success();
    }


    @PostMapping("/deleted")
    public AjaxResult deletedRole(@RequestBody JSONObject jsonObject) {
        IBaseUserRole bean = FiafengSpringUtils.getBean(IBaseUserRole.class);
        IBaseUserRole userRole = jsonObject.toJavaObject(bean.getClass());
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
        if (rbacProperties.getAllowHasRoles() && !roleList.isEmpty()) {
            throw new ServiceException("用户单一角色限制已经开启,不允许添加多个角色！");
        }
        userRoleService.updateUserRoleList(userId, roleList);
        return AjaxResult.success();
    }

    @HasRoleAnnotation
    @GetMapping("/queryRoleNameList/{userId}")
    public AjaxResult queryRoleNameByUserId(@PathVariable Long userId) {
        List<String> roleList = userRoleService.queryUserRoleNameListByUserId(userId);
        return AjaxResult.success(roleList);
    }
}
