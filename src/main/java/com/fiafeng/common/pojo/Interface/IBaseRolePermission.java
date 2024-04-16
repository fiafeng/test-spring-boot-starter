package com.fiafeng.common.pojo.Interface;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBaseRolePermission extends IBasePojo {

    Long getId();

    IBaseRolePermission setId(Long id);

    Long getRoleId();

    IBaseRolePermission setRoleId(Long roleId);

    Long getPermissionId();

    IBaseRolePermission setPermissionId(Long permissionId);

}

