package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IPermissionMapper;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.mapper.Interface.IRolePermissionMapper;
import com.fiafeng.common.mapper.Interface.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.IRolePermissionService;
import com.fiafeng.common.utils.spring.FiafengMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */

@Service
@BeanDefinitionOrderAnnotation()
public class DefaultRolePermissionServiceImpl implements IRolePermissionService {
    @Autowired
    ICacheService cacheService;

    @Autowired
    public IPermissionMapper permissionMapper ;

    @Autowired
    public IRoleMapper roleMapper;

    @Autowired
    public IRolePermissionMapper rolePermissionMapper;

    @Autowired
    public IUserRoleMapper userRoleMapper;


    @Autowired
    UpdateCacheServiceImpl updateCacheService;



    @Override
    public boolean insertRolePermission(IBaseRolePermission rolePermission) {
        if (rolePermission == null || rolePermission.getPermissionId() == null || rolePermission.getRoleId() == null){
//            throw new ServiceException("有参数为空");
            throw new ServiceException(FiafengMessageUtils.message("rbac.common.parameterIsEmpty"));
        }

        if (roleMapper.selectRoleByRoleId(rolePermission.getRoleId()) == null) {
//            throw new ServiceException("没有找到角色信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.roleInfoNotExist"));
        }

        if (permissionMapper.selectPermissionByPermissionId(rolePermission.getPermissionId()) == null) {
//            throw new ServiceException("没有找到权限信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.permissionInfoNotExist"));
        }

        if (rolePermissionMapper.selectRolePermissionIdByRoleIdPermissionId(rolePermission) != null) {
//            throw new ServiceException("当前角色已经拥有该权限");
            throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.roleHasCurrentPermission"));
        }

        if (rolePermissionMapper.insertRolePermission(rolePermission) ==1 ) {

            updateCacheService.updateCacheByRole(rolePermission.getRoleId());
        }
        return true;
    }

    @Override
    public  boolean deletedRolePermission(IBaseRolePermission rolePermission) {
        if (rolePermission == null || rolePermission.getPermissionId() == null || rolePermission.getRoleId() == null){
//            throw new ServiceException("有参数为空");
            throw new ServiceException(FiafengMessageUtils.message("rbac.common.parameterIsEmpty"));
        }

        if (rolePermission.getRoleId() == 1L && rolePermission.getPermissionId() == 1L){
            throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.deletedAdminRole"));
        }

        if (rolePermission.getId() == null){
//            throw new ServiceException("删除Id不允许为空");
            rolePermission = rolePermissionMapper.selectRolePermissionIdByRoleIdPermissionId(rolePermission);
            if (rolePermission == null)
                throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.roleNotExistCurrent"));
        }

        if (rolePermissionMapper.selectRolePermissionIdByRoleIdPermissionId(rolePermission) == null) {
//            throw new ServiceException("当前角色没有这个权限");
            throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.roleNotHasCurrentPermission"));
        }
        if (rolePermissionMapper.deleteRolePermission(rolePermission) == 1) {
            updateCacheService.updateCacheByRole(rolePermission.getRoleId());
        }
        return true;
    }

    @Override
    public boolean updateRolePermissionList(Long roleId, List<Long> permissionList) {
        if (roleId == null || permissionList == null || permissionList.isEmpty()){
//            throw new ServiceException("有参数为空");
            throw new ServiceException(FiafengMessageUtils.message("rbac.common.parameterIsEmpty"));
        }
        if (roleMapper.selectRoleByRoleId(roleId) == null) {
//            throw new ServiceException("没有找到角色信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.roleInfoNotExist"));
        }

        List<IBasePermission> queryPermissionList = permissionMapper.selectPermissionListByPermissionIdList(permissionList);
        HashSet<Long> hashSet = new HashSet<>();
        for (IBasePermission iBasePermission : queryPermissionList) {
            hashSet.add(iBasePermission.getId());
        }
        for (Long permissionId : permissionList) {
            if (!hashSet.contains(permissionId)) {
//                throw new ServiceException("权限列表有权限不存在数据库");
                throw new ServiceException(FiafengMessageUtils.message("rbac.rolePermission.updateListRolePermissionByHasPermissionNotExist"));
            }
        }
        if (rolePermissionMapper.updateRolePermissionList(roleId, permissionList) == 1){
            updateCacheService.updateCacheByRole(roleId);
        }

        return true;
    }

    @Override
    public  List<IBasePermission> queryPermissionListByRoleId(Long roleId) {
        List<IBasePermission> permissionList = new ArrayList<>();
        for (Long permissionId : rolePermissionMapper.selectPermissionIdListByRoleId(roleId)) {
            permissionList.add(permissionMapper.selectPermissionByPermissionId(permissionId));
        }
        return permissionList;
    }

    @Override
    public List<String> queryPermissionNameListByRoleId(Long roleId) {
        List<String> permissionList = new ArrayList<>();
        List<Long> permissionIdListByRoleId = rolePermissionMapper.selectPermissionIdListByRoleId(roleId);
        if (permissionIdListByRoleId == null){
            throw new ServiceException("没有找到角色id为" + roleId + "的角色");
        }

        for (Long permissionId : permissionIdListByRoleId) {
            permissionList.add(permissionMapper.selectPermissionByPermissionId(permissionId).getName());
        }
        return permissionList;
    }
}
