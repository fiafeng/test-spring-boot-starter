package com.fiafeng.common.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@BeanDefinitionOrderAnnotation()
public class DefaultRoleMapper implements IRoleMapper {

    ConcurrentHashMap<Long, IBaseRole> roleMap;

    AtomicLong atomicLong = new AtomicLong(2);

    @Autowired
    FiafengRbacProperties rbacProperties;

    public ConcurrentHashMap<Long, IBaseRole> getRoleMap() {
        if (roleMap == null) {
            roleMap = new ConcurrentHashMap<>();
            IBaseRole baseRole = FiafengSpringUtils.getBeanObject(IBaseRole.class);
            baseRole.setName(rbacProperties.roleAdminName);
            baseRole.setId(1L);
            roleMap.put(1L, baseRole);
        }

        return roleMap;
    }

    @Override
    public int insertRole(IBaseRole role) {
        if (role == null) {
            throw new ServiceException("参数不允许为空");
        }
        try {
            long andIncrement = atomicLong.getAndIncrement();
            role.setId(andIncrement);
            getRoleMap().put(andIncrement, role);
        } catch (NullPointerException ignore) {
        }

        return 1;
    }

    @Override
    public int updateRole(IBaseRole role) {
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
    public List<IBaseRole> selectRoleListALl() {
        List<IBaseRole> iBaseRoleList = new ArrayList<>();
        for (HashMap.Entry<Long, IBaseRole> entry : getRoleMap().entrySet()) {
            iBaseRoleList.add(ObjectUtils.getNewObejct(entry.getValue()));
        }
        return iBaseRoleList;
    }

    @Override
    public IBaseRole selectRoleByRoleName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return null;
        }

        for (HashMap.Entry<Long, IBaseRole> entry : getRoleMap().entrySet()) {
            if (roleName.equals(entry.getValue().getName())) {
                return ObjectUtils.getNewObejct(entry.getValue());
            }
        }
        return null;
    }

    @Override
    public IBaseRole selectRoleByRoleId(Long roleId) {
        if (getRoleMap().containsKey(roleId)) {
            return ObjectUtils.getNewObejct(getRoleMap().get(roleId));
        }

        return null;
    }


}
