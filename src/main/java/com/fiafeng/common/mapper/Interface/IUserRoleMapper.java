package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.util.List;

public interface IUserRoleMapper extends IMapper {


    int insertUserRole(IBaseUserRole userRole);

    int updateUserRoleList(Long userId, List<Long> roleIdList);

    int deleteUserRole(IBaseUserRole userRole);

    int deleteUserRoleById(Long id);

    List<Long> selectRoleIdListByUserId(Long userId);


    List<IBaseUserRole> selectUserRoleListByUserId(Long userId);

    List<IBaseUserRole> selectRoleListByRoleId(Long roleId);

    IBaseUserRole selectUserRoleByUserRole(IBaseUserRole userRole);


    IBaseUserRole selectRoleListById(Long id);

}
