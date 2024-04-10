package com.fiafeng.common.mapper;

import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.util.List;

public interface IUserRoleMapper {


    <T extends IBaseUserRole> boolean insertUserRole(T userRole);

    boolean updateUserRoleList(Long userId, List<Long> roleIdList);

    <T extends IBaseUserRole>  boolean deleteUserRole(T userRole);

    boolean deleteUserRoleById(Long id);

    List<Long> selectRoleIdListByUserId(Long userId);



    <T extends IBaseUserRole> List<T> selectRoleListByUserIdRoleId(Long userId);

    <T extends IBaseUserRole> List<T> selectRoleListByRoleId(Long roleId);

    <T extends IBaseUserRole> T selectRoleListByUserIdRoleId(T userRole);


    <T extends IBaseUserRole> T selectRoleListById(Long id);

}
