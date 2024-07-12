package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.util.List;

public interface IUserRoleMapper extends IMapper {


    <T extends IBaseUserRole> int insertUserRole(T userRole);

    int updateUserRoleList(Long userId, List<Long> roleIdList);

    <T extends IBaseUserRole> int deleteUserRole(T userRole);

    int deleteUserRoleById(Long id);

    List<Long> selectRoleIdListByUserId(Long userId);


    <T extends IBaseUserRole> List<T> selectUserRoleListByUserId(Long userId);

    <T extends IBaseUserRole> List<T> selectRoleListByRoleId(Long roleId);

    <T extends IBaseUserRole> T selectUserRoleByUserRole(T userRole);


    <T extends IBaseUserRole> T selectRoleListById(Long id);

}
