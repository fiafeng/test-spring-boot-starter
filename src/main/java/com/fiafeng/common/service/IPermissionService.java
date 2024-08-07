package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBasePermission;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface IPermissionService {

    boolean insertPermission(IBasePermission permission);

    boolean deletedPermissionById(Long permissionId);

    boolean deletedPermissionByName(String permissionName);

    boolean updatePermission(IBasePermission permission);

     IBasePermission queryPermissionByPermissionId(Long permissionId);

     IBasePermission queryPermissionByPermissionName(String permissionName);

     List<IBasePermission> queryPermissionListALl();

}
