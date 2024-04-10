package com.fiafeng.common.mapper;

import com.fiafeng.common.pojo.Interface.IBasePermission;

import java.util.List;


public interface IPermissionMapper {

    <T extends IBasePermission> boolean insertPermission(T permission);

    <T extends IBasePermission> boolean updatePermission(T permission);

    <T extends IBasePermission> boolean updatePermissionList(List<T> permissionList);

    boolean deletedPermission(Long PermissionId);

    boolean deletedPermissionByIdList(List<Long> permissionIdList);

    <T extends IBasePermission> List<T> selectPermissionListAll();

    <T extends IBasePermission> T selectPermissionByPermissionName(String permissionName);

    <T extends IBasePermission> T selectPermissionByPermissionId(Long permissionId);

    <T extends IBasePermission> List<T> selectPermissionListByPermissionIdList(List<Long> permissionIdList);

}
