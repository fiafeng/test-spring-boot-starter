package com.fiafeng.common.pojo.Interface;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBaseRolePermission extends IBasePojo {


    void setId(Long id);

    Long getRoleId();

    void setRoleId(Long roleId);

    Long getPermissionId();

    void setPermissionId(Long permissionId);

}

