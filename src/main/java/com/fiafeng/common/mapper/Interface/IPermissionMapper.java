package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBasePermission;

import java.util.List;


public interface IPermissionMapper extends IMapper {


    /**
     * 添加权限
     *
     * @param permission 权限实体类
     */
    int insertPermission(IBasePermission permission);

    /**
     * 更新权限
     *
     * @param permission 权限实体类 是否成功
     */
    int updatePermission(IBasePermission permission);

    /**
     * 根据权限列表批量更新权限信息
     *
     * @param permissionList 权限id列表
     */
    int updatePermissionList(List<IBasePermission> permissionList);

    /**
     * @param PermissionId 权限id
     */
    int deletedPermission(Long PermissionId);


    /**
     * 根据权限id列表删除列表
     *
     * @param permissionIdList 权限id列表
     */
    int deletedPermissionByIdList(List<Long> permissionIdList);

    /**
     *
     */
    List<IBasePermission> selectPermissionListAll();


    /**
     * @param permissionName 权限名
     */
    IBasePermission selectPermissionByPermissionName(String permissionName);


    /**
     * @param permissionId 权限id
     */
    IBasePermission selectPermissionByPermissionId(Long permissionId);


    /**
     * @param permissionIdList 权限id列表
     */
    List<IBasePermission> selectPermissionListByPermissionIdList(List<Long> permissionIdList);

}
