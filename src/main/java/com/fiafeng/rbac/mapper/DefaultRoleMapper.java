package com.fiafeng.rbac.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.utils.SpringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@BeanDefinitionOrderAnnotation()
public class DefaultRoleMapper implements IRoleMapper {

    ConcurrentHashMap<Long, IBaseRole> roleMap;

    AtomicLong atomicLong = new AtomicLong(2);

    public ConcurrentHashMap<Long, IBaseRole> getRoleMap() {
        if (roleMap == null) {
            roleMap = new ConcurrentHashMap<>();
            roleMap.put(1L, SpringUtils.getBean(IBaseRole.class));
        }

        return roleMap;
    }

    @Override
    public <T extends IBaseRole> boolean insertRole(T role) {
        if (role == null) {
            return false;
        }
        try {
            long andIncrement = atomicLong.getAndIncrement();
            role.setId(andIncrement);

            getRoleMap().put(andIncrement, role);
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
            getRoleMap().put(role.getId(), role);
        }

        return true;
    }

    @Override
    public boolean deletedRole(Long roleId) {
        return getRoleMap().remove(roleId) != null;
    }

    @Override
    public <T extends IBaseRole> List<T> selectRoleListALl() {
        List<IBaseRole> iBaseRoleList = new ArrayList<>();
        for (HashMap.Entry<Long, IBaseRole> entry : getRoleMap().entrySet()) {
            iBaseRoleList.add(JSONObject.from(entry.getValue()).toJavaObject(IBaseRole.class));
        }
        return (List<T>) iBaseRoleList;
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return null;
        }

        for (HashMap.Entry<Long, IBaseRole> entry : getRoleMap().entrySet()) {
            if (roleName.equals(entry.getValue().getName())) {
                return (T) SpringUtils.getBean(IBaseRole.class)
                        .setId(entry.getKey())
                        .setName(entry.getValue().getName());
            }
        }
        return null;
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleId(Long roleId) {
        if (getRoleMap().containsKey(roleId)) {
            return (T) SpringUtils.getBean(IBaseRole.class).setId(roleId).setName(getRoleMap().get(roleId).getName());
        }

        return null;
    }

}
