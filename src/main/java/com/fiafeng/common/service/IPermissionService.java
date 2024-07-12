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

    <T extends IBasePermission> T queryPermissionByPermissionId(Long permissionId);

    <T extends IBasePermission> T queryPermissionByPermissionName(String permissionName);

    <T extends IBasePermission> List<T> queryPermissionListALl();

}
