package com.fiafeng.rbac.service;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.*;
import com.fiafeng.common.utils.FiafengMessageUtils;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
@BeanDefinitionOrderAnnotation(1)
public class DefaultUserRoleServiceImpl implements IUserRoleService{

    @Autowired
    ICacheService cacheService;

    // 令牌有效期（默认60分钟）
    @Value("${fiafeng.token.expireTime:60}")
    private Long expireTime;

    @Autowired
    public IPermissionMapper permissionMapper;

    @Autowired
    public IRoleMapper roleMapper;

    @Autowired
    public IRolePermissionMapper rolePermissionMapper;

    @Autowired
    public IUserMapper userMapper;

    @Autowired
    public IUserRoleMapper userRoleMapper;


    @Override
    public <T extends IBaseUserRole> boolean insertUserRole(T userRole) {
        if (userMapper.selectUserByUserId(userRole.getUserId()) == null) {
//            throw new ServiceException("找不到用户信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userInfoNotExist"));
        }

        if (roleMapper.selectRoleByRoleId(userRole.getRoleId()) == null) {
//            throw new ServiceException("找不到角色信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.roleInfoNotExist"));
        }

        if (userRoleMapper.selectRoleListByUserIdRoleId(userRole) != null){
//            throw new ServiceException("当前用户已经拥有该角色了！");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userHasCurrentRole"));
        }

        synchronized (this) {
            if (userRoleMapper.insertUserRole(userRole)) {
                userRole = userRoleMapper.selectRoleListByUserIdRoleId(userRole);
                updateCache(userRole.getUserId());
            }
        }
        return true;
    }


    @Override
    public boolean updateUserRoleList(Long userId, List<Long> roleIdList) {
        if (userMapper.selectUserByUserId(userId) == null) {
//            throw new ServiceException("找不到用户信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userInfoNotExist"));
        }
        // 获取系统内所有角色id
        List<IBaseRole> iBaseRoles = roleMapper.selectRoleListALl();
        HashSet<Long> hashSet = new HashSet<>();
        for (IBaseRole iBaseRole : iBaseRoles) {
            hashSet.add(iBaseRole.getId());
        }
        // 判断需要更新的角色列表是否存在系统
        for (Long roleId : roleIdList) {
            if (!hashSet.contains(roleId)){
//            throw new ServiceException("找不到用户信息");
                throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.roleListHasInfoNotExist"));
            }
        }


        if (!userRoleMapper.updateUserRoleList(userId, roleIdList)) {
//            throw new ServiceException("更新用户的角色列表失败");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.updateRoleListError"));
        }

        updateCache(userId);
        return true;
    }


    /**
     * @param userRole
     * @param <T>
     * @return
     */
    @Override
    public <T extends IBaseUserRole> boolean deletedUserRole(T userRole) {
        if (userRole.getUserId() == 1L && userRole.getRoleId() == 1L){
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.deletedAdminUser"));
        }
        if (userRole.getId() == null){
//            throw new ServiceException("删除Id不允许为空");
            userRole = userRoleMapper.selectRoleListByUserIdRoleId(userRole);
            if (userRole == null)
                throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userNotExistCurrent"));
        }

        if (userRoleMapper.deleteUserRole(userRole)){
            updateCache(userRole.getUserId());
        }

        return true;
    }

    @Override
    public <T extends IBaseUserRole> boolean deletedUserRoleById(Long id) {
        IBaseUserRole userRole = userRoleMapper.selectRoleListById(id);
        if (userRole == null){
//            throw new ServiceException("找不到当前这条信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.common.notFoundCurrentById"));
        }
        if (userRoleMapper.deleteUserRoleById(id)){
            updateCache(userRole.getUserId());
        }
        return true;
    }

    private void updateCache(Long userId) {
        HashSet<Long> hashSet = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
        if (hashSet == null) {
            hashSet = new HashSet<>();
        }
        hashSet.add(userId);
        cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, hashSet, expireTime, TimeUnit.MINUTES);
    }


    /**
     * @param userId
     * @param <T>
     * @return
     */
    @Override
    public <T extends IBaseRole> List<T> queryUserRoleListByUserId(Long userId) {
        if (userMapper.selectUserByUserId(userId) == null) {
//            throw new ServiceException("找不到用户信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userInfoNotExist"));
        }
        List<IBaseRole> roleList = new ArrayList<>();
        for (Long roleId : userRoleMapper.selectRoleIdListByUserId(userId)) {
            roleList.add(roleMapper.selectRoleByRoleId(roleId));
        }

        return (List<T>) roleList;
    }

    @Override
    public <T extends IBaseRole> List<T> queryUserRoleListByRoleId(Long roleId) {
        List<IBaseRole> roleList = new ArrayList<>();
        for (IBaseUserRole iBaseUserRole : userRoleMapper.selectRoleListByRoleId(roleId)) {
           roleList.add(roleMapper.selectRoleByRoleId(iBaseUserRole.getId()));

        }
        return (List<T>) roleList;
    }

    @Override
    public <T extends IBasePermission> List<T> queryUserPermissionListByUserId(Long userId) {
        if (userMapper.selectUserByUserId(userId) == null) {
//            throw new ServiceException("找不到用户信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userInfoNotExist"));
        }
        List<IBasePermission> permissionList = new ArrayList<>();
        HashSet<Long> permissionIdHashSet = new HashSet<>();

        for (Long roleId : userRoleMapper.selectRoleIdListByUserId(userId)) {
            permissionIdHashSet.addAll(rolePermissionMapper.selectPermissionIdListByRoleId(roleId));
        }
        for (Long permissionId : permissionIdHashSet) {
            permissionList.add(permissionMapper.selectPermissionByPermissionId(permissionId));
        }

        return (List<T>) permissionList;
    }

    @Override
    public List<String> queryUserPermissionNameListByUserId(Long userId) {
        if (userMapper.selectUserByUserId(userId) == null) {
//            throw new ServiceException("找不到用户信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userInfoNotExist"));
        }
        List<String> permissionNameList = new ArrayList<>();
        HashSet<Long> permissionIdHashSet = new HashSet<>();
        List<Long> roleList = userRoleMapper.selectRoleIdListByUserId(userId);
        for (Long roleId : roleList) {
            permissionIdHashSet.addAll(rolePermissionMapper.selectPermissionIdListByRoleId(roleId));
        }
        for (Long permissionId : permissionIdHashSet) {
            IBasePermission defaultPermission = permissionMapper.selectPermissionByPermissionId(permissionId);
            if (defaultPermission == null){
                return new ArrayList<>();
            }
            permissionNameList.add(defaultPermission.getName());
        }
        // TODO 添加到缓存

        return permissionNameList;
    }

    @Override
    public List<String> queryUserRoleNameListByUserId(Long userId) {
        if (userMapper.selectUserByUserId(userId) == null) {
//            throw new ServiceException("找不到用户信息");
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.userInfoNotExist"));
        }
        List<String> permissionNameList = new ArrayList<>();
        List<Long> roleIdList = userRoleMapper.selectRoleIdListByUserId(userId);
        if (roleIdList == null){
            throw new ServiceException(FiafengMessageUtils.message("rbac.userRole.roleListIsEmpty"));
        }
        for (Long roleId : roleIdList) {
            permissionNameList.add(roleMapper.selectRoleByRoleId(roleId).getName());
        }


        return permissionNameList;
    }
}
