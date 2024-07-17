package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseRole;

import java.util.List;

public interface IRoleMapper extends IMapper {


     int insertRole(IBaseRole role);

     int updateRole(IBaseRole role);

    int deletedRole(Long roleId);

     List<IBaseRole> selectRoleListALl();

     IBaseRole selectRoleByRoleName(String roleName);

     IBaseRole selectRoleByRoleId(Long roleId);
}
