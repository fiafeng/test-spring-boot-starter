package com.fiafeng.rbac.service;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.service.Impl.UpdateCacheServiceImpl;
import com.fiafeng.common.utils.FiafengMessageUtils;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.IPermissionMapper;
import com.fiafeng.common.mapper.IRolePermissionMapper;
import com.fiafeng.common.mapper.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */


@BeanDefinitionOrderAnnotation()
public class DefaultPermissionsServiceImpl implements IPermissionService {

    @Autowired
    ICacheService cacheService;

    @Autowired
    public IPermissionMapper permissionMapper;

    @Autowired
    public IRolePermissionMapper rolePermissionMapper;

    @Autowired
    public IUserRoleMapper userRoleMapper;

    @Autowired
    UpdateCacheServiceImpl updateCacheService;



    @Override
    public boolean insertPermission(IBasePermission permission) {
        if (permission.getName() == null || permission.getName().isEmpty()){
//            throw new ServiceException("权限名为空");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.permissionNameIsEmpty"));
        }

        IBasePermission basePermission = permissionMapper.selectPermissionByPermissionName(permission.getName());
        if (basePermission != null) {
//            throw new ServiceException("权限名已经存在");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.permissionNameRepeat"));
        }
        if (permissionMapper.insertPermission(permission)) {
//            updateCache(permission.getId());
        }else {
            throw new ServiceException("新增权限时遇到意外的异常");
        }

        return true;
    }

    @Override
    public boolean updatePermission(IBasePermission permission) {
        if (permission.getId() == 1) {
//            throw new ServiceException("不允许修改管理员权限");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.updateAdminPermission"));
        }
        if (permission.getName() == null || permission.getName().isEmpty()){
//            throw new ServiceException("权限名为空");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.permissionNameIsEmpty"));
        }
        if (permissionMapper.selectPermissionByPermissionId(permission.getId()) == null){
//            throw new ServiceException("权限不存在");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.permissionInfoNotExist"));
        }

        for (IBasePermission basePermission : permissionMapper.selectPermissionListAll()) {
            if (!Objects.equals(permission.getId(), basePermission.getId())
                    && permission.getName().equals(basePermission.getName())){
//                throw new ServiceException("权限名已经存在");
                throw new ServiceException(FiafengMessageUtils.message("rbac.permission.permissionNameRepeat"));
            }
        }
        if (permissionMapper.updatePermission(permission)) {
            updateCacheService.updateCacheByPermission(permission.getId());
        }else {
            throw new ServiceException("更新权限时遇到意外的异常");
        }

        return true;
    }

    @Override
    public boolean deletedPermission(Long permissionId) {
        if (permissionId == 1) {
//            throw new ServiceException("不允许删除管理员权限");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.deletedAdminRole"));
        }

        IBasePermission basePermission = permissionMapper.selectPermissionByPermissionId(permissionId);
        if (basePermission == null){
//            throw new ServiceException("找不到权限信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.permissionInfoNotExist"));
        }

        List<IBaseRolePermission> rolePermissionList = rolePermissionMapper.selectPermissionListByPermissionId(permissionId);
        if (rolePermissionList != null && !rolePermissionList.isEmpty()){
//            throw new ServiceException("当前角色还有用户拥有，不允许删除");
            throw new ServiceException(FiafengMessageUtils.message("rbac.permission.deletedPermissionByRoleHasCurrentPermission"));
        }

        if (permissionMapper.deletedPermission(permissionId)) {
            updateCacheService.updateCacheByPermission(permissionId);
        }else {
            throw new ServiceException("删除权限时遇到意外的异常");
        }

        return true;
    }

    @Override
    public <T extends IBasePermission> T queryPermissionByPermissionId(Long permissionId) {
        return permissionMapper.selectPermissionByPermissionId(permissionId);
    }

    @Override
    public <T extends IBasePermission> T queryPermissionByPermissionName(String permissionName) {
        return permissionMapper.selectPermissionByPermissionName(permissionName);
    }

    @Override
    public <T extends IBasePermission> List<T> queryPermissionListALl() {
        return permissionMapper.selectPermissionListAll();
    }

}
