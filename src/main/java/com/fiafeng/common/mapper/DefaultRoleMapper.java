package com.fiafeng.common.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.properties.FiafengRbacProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class DefaultRoleMapper implements IRoleMapper {

    ConcurrentHashMap<Long, IBaseRole> roleMap;

    AtomicLong atomicLong = new AtomicLong(2);

    @Autowired
    FiafengRbacProperties rbacProperties;

    public ConcurrentHashMap<Long, IBaseRole> getRoleMap() {
        if (roleMap == null) {
            roleMap = new ConcurrentHashMap<>();
            roleMap.put(1L, FiafengSpringUtils.getBean(IBaseRole.class).setName(rbacProperties.roleAdminName).setId(1L));
        }

        return roleMap;
    }

    @Override
    public <T extends IBaseRole> int insertRole(T role) {
        if (role == null) {
            throw new ServiceException("参数不允许为空");
        }
        try {
            long andIncrement = atomicLong.getAndIncrement();
            role.setId(andIncrement);

            getRoleMap().put(andIncrement, role);
        } catch (NullPointerException e) {
        }

        return 1;
    }

    @Override
    public <T extends IBaseRole> int updateRole(T role) {
        if (role.getId() == null) {
            throw new ServiceException("参数不允许为空");
        }
        if (!getRoleMap().containsKey(role.getId())) {
            throw new ServiceException("id找不到对应的数据");
        } else {
            getRoleMap().put(role.getId(), role);
        }

        return 1;
    }

    @Override
    public int deletedRole(Long roleId) {
        return getRoleMap().remove(roleId) != null ? 0 : 1;
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
                return (T) FiafengSpringUtils.getBean(IBaseRole.class)
                        .setId(entry.getKey())
                        .setName(entry.getValue().getName());
            }
        }
        return null;
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleId(Long roleId) {
        if (getRoleMap().containsKey(roleId)) {
            return (T) FiafengSpringUtils.getBean(IBaseRole.class).setId(roleId).setName(getRoleMap().get(roleId).getName());
        }

        return null;
    }

}
