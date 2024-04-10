package com.fiafeng.common.mapper;

import com.fiafeng.common.pojo.Interface.IBaseRolePermission;

import java.util.List;

public interface IRolePermissionMapper {

    <T extends IBaseRolePermission> boolean insertRolePermission(T rolePermission);

    <T extends IBaseRolePermission>  boolean deleteRolePermission(T rolePermission);

    boolean updateRolePermissionList(Long roleId, List<Long> permissionIdList);

    List<Long> selectPermissionIdListByRoleId(Long roleId);

    <T extends IBaseRolePermission> T selectRolePermissionIdByRoleIdPermissionId(T rolePermission);

    <T extends IBaseRolePermission> List<T> selectPermissionListByRoleId(Long roleId);

    <T extends IBaseRolePermission> List<T> selectPermissionListByPermissionId(Long permissionId);
}
