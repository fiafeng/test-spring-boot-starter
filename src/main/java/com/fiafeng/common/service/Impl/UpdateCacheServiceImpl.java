package com.fiafeng.common.service.Impl;

import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.mapper.IPermissionMapper;
import com.fiafeng.common.mapper.IRoleMapper;
import com.fiafeng.common.mapper.IRolePermissionMapper;
import com.fiafeng.common.mapper.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateCacheServiceImpl {

    @Autowired
    ICacheService cacheService;

    @Autowired
    IUserRoleMapper userRoleMapper;

    @Autowired
    IRolePermissionMapper rolePermissionMapper;

    @Autowired
    IPermissionMapper permissionMapper;


    @Autowired
    IRoleMapper roleMapper;

    // 令牌有效期（默认60分钟）
    @Value("${fiafeng.token.expireTime:60}")
    private Long expireTime;

    /**
     * 指定角色的权限列表更新了
     */
    public void updateCacheByRole(Long roleId) {
        HashSet<Long> hashSet = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
        Long expire = cacheService.getExpire(CacheConstants.UPDATE_USER_INFO);
        if (hashSet == null) {
            hashSet = new HashSet<>();
            expire = expireTime;
        }
        for (IBaseUserRole userRole : userRoleMapper.selectRoleListByUserRole(roleId)) {
            hashSet.add(userRole.getUserId());
        }
        cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, hashSet, expire, TimeUnit.MINUTES);

        // 更新角色权限列表的缓存
        IBaseRole iBaseRole = roleMapper.selectRoleByRoleId(roleId);
        String key = CacheConstants.ROLE_PERMISSION_PREFIX + iBaseRole.getId();
        HashSet<String> permissionHashSet = new HashSet<>();
        for (Long permissionId : rolePermissionMapper.selectPermissionIdListByRoleId(roleId)) {
            IBasePermission permission = permissionMapper.selectPermissionByPermissionId(permissionId);
            permissionHashSet.add(permission.getName());
        }
        cacheService.setCacheObject(key, permissionHashSet);
    }


    /**
     * 指定用户的权限列表或者权限列表更新了
     */
    public void updateCacheByUser(Long userId) {
        HashSet<Long> hashSet = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
        Long expire = cacheService.getExpire(CacheConstants.UPDATE_USER_INFO);
        if (hashSet == null) {
            hashSet = new HashSet<>();
            expire = expireTime;
        }
        hashSet.add(userId);

        cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, hashSet, expire, TimeUnit.MINUTES);
    }


    /**
     * 指定权限被修改了
     */
    public void updateCacheByPermission(Long permissionId) {

        List<IBaseRolePermission> rolePermissionList = rolePermissionMapper.selectPermissionListByPermissionId(permissionId);
        HashSet<Long> roleIdHashSet = new HashSet<>();
        for (IBaseRolePermission iBaseRolePermission : rolePermissionList) {
            roleIdHashSet.add(iBaseRolePermission.getRoleId());
        }
        for (Long roleId : roleIdHashSet) {
            updateCacheByRole(roleId);
        }

    }
}
