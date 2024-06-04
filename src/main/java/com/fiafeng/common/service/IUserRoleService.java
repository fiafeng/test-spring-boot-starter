package com.fiafeng.common.service;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IUserRoleService {

    <T extends IBaseUserRole> boolean insertUserRole(T userRole);

    boolean updateUserRoleList(Long userId, List<Long> roleIdList);


    <T extends IBaseUserRole> boolean deletedUserRole(T userRole);

    <T extends IBaseUserRole> boolean deletedUserRoleById(Long id);

    <T extends IBaseRole> List<T> queryUserRoleListByUserId(Long userId);

    <T extends IBaseRole> List<T> queryUserRoleListByRoleId(Long roleId);

    <T extends IBasePermission> List<T> queryUserPermissionListByUserId(Long userId);

    List<String> queryUserPermissionNameListByUserId(Long userId);

    List<String> queryUserRoleNameListByUserId(Long userId);

}
