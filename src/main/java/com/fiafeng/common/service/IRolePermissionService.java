package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IRolePermissionService {

    /**
     * 给角色添加权限
     * @param rolePermission 角色信息
     * @return 成功与否
     * @param <T>
     */
    <T extends IBaseRolePermission> boolean insertRolePermission(T rolePermission);

    /**
     *
     * @param rolePermission
     * @return
     * @param <T>
     */
    <T extends IBaseRolePermission> boolean deletedRolePermission(T rolePermission);


    boolean updateRolePermissionList(Long roleId, List<Long> permissionList);

    /**
     *
     * @param roleId
     * @return
     * @param <T>
     */
    <T extends IBasePermission> List<T> queryPermissionListByRoleId(Long roleId);


    List<String> queryPermissionNameListByRoleId(Long roleId);



}
