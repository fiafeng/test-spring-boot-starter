package com.fiafeng.rbac.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.rbac.pojo.DefaultRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@BeanDefinitionOrderAnnotation()
public class DefaultRoleMapper implements IRoleMapper {

    ConcurrentHashMap<Long, String> roleMap;

    AtomicLong atomicLong = new AtomicLong(2);

    public ConcurrentHashMap<Long, String> getRoleMap() {
        if (roleMap == null) {
            roleMap = new ConcurrentHashMap<>();
            roleMap.put(1L, "admin");
        }

        return roleMap;
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return null;
        }

        for (HashMap.Entry<Long, String> entry : getRoleMap().entrySet()) {
            if (roleName.equals(entry.getValue())) {
                return (T) new DefaultRole().setId(entry.getKey()).setName(entry.getValue());
            }
        }
        return null;
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleId(Long roleId) {
        if (getRoleMap().containsKey(roleId)) {
            return (T) new DefaultRole().setId(roleId).setName(getRoleMap().get(roleId));
        }

        return null;
    }

    @Override
    public <T extends IBaseRole> boolean insertRole(T role) {
        if (role == null) {
            return false;
        }
        try {
            getRoleMap().put(atomicLong.getAndIncrement(), role.getName());
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }

    @Override
    public <T extends IBaseRole> boolean updateRole(T role) {
        if (role.getId() == null) {
            return false;
        }
        if (!getRoleMap().containsKey(role.getId())) {
            return false;
        } else {
            getRoleMap().put(role.getId(), role.getName());
        }

        return true;
    }

    @Override
    public boolean deletedRole(Long roleId) {
        return getRoleMap().remove(roleId) != null;
    }

    @Override
    public <T extends IBaseRole> List<T> selectRoleListALl() {
        List<DefaultRole> defaultRoleList = new ArrayList<>();
        for (HashMap.Entry<Long, String> entry : getRoleMap().entrySet()) {
            defaultRoleList.add(new DefaultRole().setId(entry.getKey()).setName(entry.getValue()));
        }
        return (List<T>) defaultRoleList;
    }
}
