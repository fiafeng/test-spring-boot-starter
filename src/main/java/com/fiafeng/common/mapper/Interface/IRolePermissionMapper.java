package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseRolePermission;

import java.util.List;

public interface IRolePermissionMapper extends IMapper {

    <T extends IBaseRolePermission> int insertRolePermission(T rolePermission);

    <T extends IBaseRolePermission>  int deleteRolePermission(T rolePermission);

    int updateRolePermissionList(Long roleId, List<Long> permissionIdList);

    List<Long> selectPermissionIdListByRoleId(Long roleId);

    <T extends IBaseRolePermission> T selectRolePermissionIdByRoleIdPermissionId(T rolePermission);

    <T extends IBaseRolePermission> List<T> selectPermissionListByRoleId(Long roleId);

    <T extends IBaseRolePermission> List<T> selectPermissionListByPermissionId(Long permissionId);
}
