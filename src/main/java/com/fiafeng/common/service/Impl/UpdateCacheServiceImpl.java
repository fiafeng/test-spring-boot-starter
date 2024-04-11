package com.fiafeng.common.service.Impl;

import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.mapper.IRolePermissionMapper;
import com.fiafeng.common.mapper.IUserRoleMapper;
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

    // 令牌有效期（默认60分钟）
    @Value("${fiafeng.token.expireTime:60}")
    private Long expireTime;

    public void updateCacheByRole(Long roleId) {
        HashSet<Long> hashSet = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
        Long expire = cacheService.getExpire(CacheConstants.UPDATE_USER_INFO);
        if (hashSet == null) {
            hashSet = new HashSet<>();
            expire = expireTime;
        }
        for (IBaseUserRole userRole : userRoleMapper.selectRoleListByUserIdRoleId(roleId)) {
            hashSet.add(userRole.getUserId());
        }
        cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, hashSet, expire, TimeUnit.MINUTES);
    }


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


    public void updateCacheByPermission(Long permissionId){

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
