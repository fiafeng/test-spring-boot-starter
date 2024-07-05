package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.mapper.Interface.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.IRoleService;
import com.fiafeng.common.utils.spring.FiafengMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
@Service
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class DefaultRoleServiceImpl implements IRoleService {


    @Autowired
    ICacheService cacheService;

    @Autowired
    public IRoleMapper roleMapper;

    @Autowired
    public IUserRoleMapper userRoleMapper;

    @Autowired
    UpdateCacheServiceImpl updateCacheService;


    @Override
    public int insertRole(IBaseRole role) {
        IBaseRole baseRole = roleMapper.selectRoleByRoleName(role.getName());
        if (baseRole != null) {
//            throw new ServiceException("角色名字已经存在");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.roleNameRepeat"));

        }
        return roleMapper.insertRole(role);
    }

    @Override
    public int deletedRoleById(Long roleId) {
        if (roleId == 1L) {
//            throw new ServiceException("不允许删除超级管理员角色");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.deletedAdminRole"));
        }
        IBaseRole iBaseRole = roleMapper.selectRoleByRoleId(roleId);
        if (iBaseRole == null) {
//            throw new ServiceException("找不到该角色");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.roleNotExist"));
        }

        List<IBaseUserRole> userRoleList = userRoleMapper.selectRoleListByRoleId(roleId);
        if (userRoleList != null && !userRoleList.isEmpty()) {
//            throw new ServiceException("当前角色还有用户拥有，不允许删除");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.deletedRoleByUserHasCurrentRole"));
        }

        if (roleMapper.deletedRole(roleId) != 1) {
            updateCacheService.updateCacheByRole(roleId);
            cacheService.deleteObject(CacheConstants.ROLE_PERMISSION_PREFIX + roleId);

        } else {
            throw new ServiceException("删除角色遇到意外的异常");
        }
        return 1;
    }


    @Override
    public int updateRole(IBaseRole role) {
        if (role.getId() == 1L) {
//            throw new ServiceException("不允许修改超级管理员角色");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.updateAdminRole"));
        }

        if (role.getName() == null || role.getName().isEmpty()) {
//            throw new ServiceException("角色名不允许为空");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.roleNameIsEmpty"));
        }
        IBaseRole iBaseRole = roleMapper.selectRoleByRoleId(role.getId());
        if (iBaseRole == null) {
//            throw new ServiceException("没有找到角色信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.roleNotExist"));
        }

        // 检查内部的名字是否重复
        List<IBaseRole> roleList = roleMapper.selectRoleListALl();
        for (IBaseRole baseRole : roleList) {
            if (!Objects.equals(baseRole.getId(), role.getId()) && role.getName().equals(baseRole.getName())) {
                throw new ServiceException("角色名字不允许重复");
            }

        }
        if (roleMapper.updateRole(role) == 1) {
            updateCacheService.updateCacheByRole(role.getId());
        } else {
            throw new ServiceException("更新角色遇到意外的异常");
        }
        return 1;
    }

    @Override
    public <T extends IBaseRole> T queryRoleByRoleId(Long roleId) {
        IBaseRole baseRole = roleMapper.selectRoleByRoleId(roleId);
        if (baseRole == null) {
//            throw new ServiceException("角色名字不存在");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.roleNotExist"));
        }
        return (T) baseRole;
    }

    @Override
    public <T extends IBaseRole> T queryRoleByRoleName(String roleName) {

        IBaseRole baseRole = roleMapper.selectRoleByRoleName(roleName);
        if (baseRole == null) {
//            throw new ServiceException("角色名字不存在");
            throw new ServiceException(FiafengMessageUtils.message("rbac.role.roleNotExist"));
        }
        return (T) baseRole;
    }

    @Override
    public <T extends IBaseRole> List<T> queryRoleListAll() {

        return roleMapper.selectRoleListALl();
    }
}
