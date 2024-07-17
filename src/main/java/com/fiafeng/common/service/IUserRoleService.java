package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IUserRoleService {

     boolean insertUserRole(IBaseUserRole userRole);

    void updateUserRoleList(Long userId, List<Long> roleIdList);


     void deletedUserRole(IBaseUserRole userRole);

     void deletedUserRoleById(Long id);

     List<IBaseRole> queryUserRoleListByUserId(Long userId);

     List<IBaseRole> queryUserRoleListByRoleId(Long roleId);

     List<IBasePermission> queryUserPermissionListByUserId(Long userId);

    List<String> queryUserPermissionNameListByUserId(Long userId);

    List<String> queryUserRoleNameListByUserId(Long userId);

}
