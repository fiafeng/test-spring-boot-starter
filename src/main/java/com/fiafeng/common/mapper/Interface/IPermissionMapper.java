package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBasePermission;

import java.util.List;


public interface IPermissionMapper extends IMapper {


    /**
     * 添加权限
     *
     * @param permission 权限实体类
     * @param <T>        权限实体类的类型
     */
    <T extends IBasePermission> int insertPermission(T permission);

    /**
     * 更新权限
     *
     * @param permission 权限实体类 是否成功
     * @param <T>        权限实体类的类型
     */
    <T extends IBasePermission> int updatePermission(T permission);

    /**
     * 根据权限列表批量更新权限信息
     *
     * @param permissionList 权限id列表
     * @param <T>            权限实体类的类型
     */
    <T extends IBasePermission> int updatePermissionList(List<T> permissionList);

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
     * @param <T> 权限实体类的类型
     */
    <T extends IBasePermission> List<T> selectPermissionListAll();


    /**
     * @param permissionName 权限名
     * @param <T>            权限实体类的类型
     */
    <T extends IBasePermission> T selectPermissionByPermissionName(String permissionName);


    /**
     * @param permissionId 权限id
     * @param <T>          权限实体类的类型
     */
    <T extends IBasePermission> T selectPermissionByPermissionId(Long permissionId);


    /**
     * @param permissionIdList 权限id列表
     *                         `* @param <T> 权限实体类的类型`
     */
    <T extends IBasePermission> List<T> selectPermissionListByPermissionIdList(List<Long> permissionIdList);

}
