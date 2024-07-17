package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseRolePermission;

import java.util.List;

public interface IRolePermissionMapper extends IMapper {

    int insertRolePermission(IBaseRolePermission rolePermission);

      int deleteRolePermission(IBaseRolePermission rolePermission);

    int updateRolePermissionList(Long roleId, List<Long> permissionIdList);

    List<Long> selectPermissionIdListByRoleId(Long roleId);

     IBaseRolePermission selectRolePermissionIdByRoleIdPermissionId(IBaseRolePermission rolePermission);

     List<IBaseRolePermission> selectPermissionListByRoleId(Long roleId);

     List<IBaseRolePermission> selectPermissionListByPermissionId(Long permissionId);
}
