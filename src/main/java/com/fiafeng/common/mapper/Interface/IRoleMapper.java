package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseRole;

import java.util.List;

public interface IRoleMapper extends IMapper {


    <T extends IBaseRole> int insertRole(T role);

    <T extends IBaseRole> int updateRole(T role);

    int deletedRole(Long roleId);

    <T extends IBaseRole> List<T> selectRoleListALl();

    <T extends IBaseRole> T selectRoleByRoleName(String roleName);

    <T extends IBaseRole> T selectRoleByRoleId(Long roleId);
}
