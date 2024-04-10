package com.fiafeng.rbac.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IRolePermissionMapper;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
import com.fiafeng.rbac.pojo.DefaultRolePermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@BeanDefinitionOrderAnnotation()
public class DefaultRolePermissionMapper implements IRolePermissionMapper {

    // roleId, HashMap<permissionId, id>
    ConcurrentHashMap<Long, HashMap<Long, Long>> rolePermissionMap;

    AtomicLong atomicLong = new AtomicLong(2);

    public ConcurrentHashMap<Long, HashMap<Long, Long>> getRolePermissionMap() {
        if (rolePermissionMap == null) {
            rolePermissionMap = new ConcurrentHashMap<>();
            HashMap<Long, Long> permissionHashMap = new HashMap<>();
            permissionHashMap.put(1L, 1L);
            rolePermissionMap.put(1L, permissionHashMap);
        }
        return rolePermissionMap;
    }

    @Override
    public boolean insertRolePermission(IBaseRolePermission rolePermission) {
        if (getRolePermissionMap().containsKey(rolePermission.getRoleId())) {
            getRolePermissionMap().get(rolePermission.getRoleId()).put(rolePermission.getPermissionId(), atomicLong.getAndIncrement());
        } else {
            HashMap<Long, Long> permissionHashset = new HashMap<>();
            permissionHashset.put(rolePermission.getPermissionId(), atomicLong.getAndIncrement());
            getRolePermissionMap().put(rolePermission.getRoleId(), permissionHashset);
        }
        return true;
    }

    @Override
    public boolean deleteRolePermission(IBaseRolePermission rolePermission) {
        if (getRolePermissionMap().containsKey(rolePermission.getRoleId())) {
            return getRolePermissionMap().get(rolePermission.getRoleId()).remove(rolePermission.getPermissionId()) != null;
        }

        return false;
    }

    /**
     * @param roleId 角色Id
     * @param permissionIdList 权限列表
     * @return 是否更新成功
     */
    @Override
    public boolean updateRolePermissionList(Long roleId, List<Long> permissionIdList) {
        if (!getRolePermissionMap().containsKey(roleId)) {
            return false;
        }
        for (Long permissionId : permissionIdList) {
            if (!getRolePermissionMap().containsKey(permissionId)) {
                return false;
            }
        }
        HashMap<Long, Long> hashMap = new HashMap<>();

        for (Long permissionId : permissionIdList) {
            hashMap.put(permissionId, atomicLong.getAndIncrement());
        }

        getRolePermissionMap().put(roleId, hashMap);


        return false;
    }

    @Override
    public List<Long> selectPermissionIdListByRoleId(Long roleId) {
        if (getRolePermissionMap().containsKey(roleId)) {
            return new ArrayList<>(getRolePermissionMap().get(roleId).keySet());
        }
        return null;
    }

    /**
     * @param rolePermission 角色权限信息
     * @param <T> 角色权限信息实体类
     * @return 1
     */
    @Override
    public <T extends IBaseRolePermission> T selectRolePermissionIdByRoleIdPermissionId(T rolePermission) {
        if (getRolePermissionMap().containsKey(rolePermission.getRoleId())) {
            HashMap<Long, Long> hashMap = getRolePermissionMap().get(rolePermission.getRoleId());
            if (hashMap.containsKey(rolePermission.getPermissionId())) {
                return (T) new DefaultRolePermission()
                        .setPermissionId(rolePermission.getPermissionId())
                        .setRoleId(rolePermission.getRoleId())
                        .setId(hashMap.get(rolePermission.getPermissionId()));
            }
        }

        return null;
    }

    @Override
    public <T extends IBaseRolePermission> List<T> selectPermissionListByRoleId(Long roleId) {
        List<IBaseRolePermission> rolePermissionList = new ArrayList<>();
        if (getRolePermissionMap().containsKey(roleId)){
            HashMap<Long, Long> longLongHashMap = getRolePermissionMap().get(roleId);
            for (Long permissionId : longLongHashMap.keySet()) {
                rolePermissionList.add(
                        new DefaultRolePermission()
                                .setPermissionId(permissionId)
                                .setRoleId(roleId)
                                .setId(longLongHashMap.get(permissionId))
                );
            }

        }

        return (List<T>) rolePermissionList;
    }

    @Override
    public <T extends IBaseRolePermission> List<T> selectPermissionListByPermissionId(Long permissionId) {
        List<IBaseRolePermission> userRoleList = new ArrayList<>();
        for (Long roleId : getRolePermissionMap().keySet()) {
            HashMap<Long, Long> role = getRolePermissionMap().get(roleId);
            if (role.containsKey(permissionId)) {
                DefaultRolePermission defaultUserRole = new DefaultRolePermission()
                        .setRoleId(roleId)
                        .setId(role.get(roleId))
                        .setPermissionId(permissionId);
                userRoleList.add(defaultUserRole);
            }
        }
        return (List<T>) userRoleList;
    }

}
