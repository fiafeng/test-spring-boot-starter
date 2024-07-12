package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBaseRole;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface IRoleService {
    void insertRole(IBaseRole role);

    void deletedRoleById(Long roleId);

    void deletedRoleByName(String roleName);

    void updateRole(IBaseRole role);

    <T extends IBaseRole> T queryRoleByRoleId(Long roleId);

    <T extends IBaseRole> T queryRoleByRoleName(String roleName);

    <T extends IBaseRole> List<T> queryRoleListAll();

}
