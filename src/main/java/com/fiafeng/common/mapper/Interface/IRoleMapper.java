package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseRole;

import java.util.List;

public interface IRoleMapper extends IMapper {


    <T extends IBaseRole> boolean insertRole(T role);

    <T extends IBaseRole> boolean updateRole(T role);

    boolean deletedRole(Long roleId);

    <T extends IBaseRole> List<T> selectRoleListALl();

    <T extends IBaseRole> T selectRoleByRoleName(String roleName);

    <T extends IBaseRole> T selectRoleByRoleId(Long roleId);
}
