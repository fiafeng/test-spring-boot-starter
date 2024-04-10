package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBaseRole;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface IRoleService {
    boolean insertRole(IBaseRole role);

    boolean deletedRoleById(Long roleId);

    boolean updateRole(IBaseRole role);

    <T extends IBaseRole> T queryRoleByRoleId(Long roleId);

    <T extends IBaseRole> T queryRoleByRoleName(String roleName);

    <T extends IBaseRole> List<T> queryRoleListAll();

}
