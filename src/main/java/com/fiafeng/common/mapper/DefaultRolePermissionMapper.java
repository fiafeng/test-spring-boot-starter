package com.fiafeng.common.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IRolePermissionMapper;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
@Component
public class DefaultRolePermissionMapper implements IRolePermissionMapper {

    // roleId, HashMap<permissionId, id>
    ConcurrentHashMap<Long, IBaseRolePermission> rolePermissionMap;

    AtomicLong atomicLong = new AtomicLong(2);

    public ConcurrentHashMap<Long, IBaseRolePermission> getRolePermissionMap() {
        if (rolePermissionMap == null) {
            rolePermissionMap = new ConcurrentHashMap<>();
            IBaseRolePermission iBaseRolePermission = FiafengSpringUtils.getBean(IBaseRolePermission.class);

            rolePermissionMap.put(1L, iBaseRolePermission);
        }
        return rolePermissionMap;
    }

    @Override
    public boolean insertRolePermission(IBaseRolePermission rolePermission) {
        for (IBaseRolePermission iBaseRolePermission : getRolePermissionMap().values()) {
            if (Objects.equals(iBaseRolePermission.getRoleId(), rolePermission.getRoleId())
                    && Objects.equals(iBaseRolePermission.getPermissionId(), rolePermission.getPermissionId())) {
                throw new ServiceException("当前角色已经拥有此权限");
            }
        }

        long andIncrement = atomicLong.getAndIncrement();
        rolePermission.setId(andIncrement);
        getRolePermissionMap().put(andIncrement, rolePermission);

        return true;
    }

    @Override
    public boolean deleteRolePermission(IBaseRolePermission rolePermission) {
        getRolePermissionMap().remove(rolePermission.getId());
        return false;
    }

    /**
     * @param roleId           角色Id
     * @param permissionIdList 权限列表
     * @return 是否更新成功
     */
    @Override
    public boolean updateRolePermissionList(Long roleId, List<Long> permissionIdList) {
        List<Long> longList = new ArrayList<>();
        for (IBaseRolePermission iBaseRolePermission : getRolePermissionMap().values()) {
            if (Objects.equals(iBaseRolePermission.getRoleId(), roleId)) {
                longList.add(iBaseRolePermission.getId());
            }
        }
        for (Long id : longList) {
            getRolePermissionMap().remove(id);
        }
        for (Long permissionId : permissionIdList) {
            IBaseRolePermission iBaseRolePermission = FiafengSpringUtils.getBean(IBaseRolePermission.class);
            iBaseRolePermission.setPermissionId(permissionId).setRoleId(roleId);
            insertRolePermission(iBaseRolePermission);
        }


        return false;
    }

    @Override
    public List<Long> selectPermissionIdListByRoleId(Long roleId) {
        List<Long> longList = new ArrayList<>();
        for (IBaseRolePermission iBaseRolePermission : getRolePermissionMap().values()) {
            if (Objects.equals(iBaseRolePermission.getRoleId(), roleId)) {
                longList.add(iBaseRolePermission.getId());
            }
        }
        return longList;
    }

    /**
     * @param rolePermission 角色权限信息
     * @param <T>            角色权限信息实体类
     * @return 1
     */
    @Override
    public <T extends IBaseRolePermission> T selectRolePermissionIdByRoleIdPermissionId(T rolePermission) {

        for (IBaseRolePermission iBaseRolePermission : getRolePermissionMap().values()) {
            if (Objects.equals(iBaseRolePermission.getRoleId(), rolePermission.getRoleId())
                    && Objects.equals(iBaseRolePermission.getPermissionId(), rolePermission.getPermissionId())) {
                return (T) JSONObject.from(iBaseRolePermission).toJavaObject(IBaseRolePermission.class);

            }
        }

        return null;
    }

    @Override
    public <T extends IBaseRolePermission> List<T> selectPermissionListByRoleId(Long roleId) {
        List<IBaseRolePermission> rolePermissionList = new ArrayList<>();
        for (IBaseRolePermission iBaseRolePermission : getRolePermissionMap().values()) {
            if (Objects.equals(iBaseRolePermission.getRoleId(), roleId)) {
                rolePermissionList.add(JSONObject.from(iBaseRolePermission).toJavaObject(IBaseRolePermission.class));
            }
        }

        return (List<T>) rolePermissionList;
    }

    @Override
    public <T extends IBaseRolePermission> List<T> selectPermissionListByPermissionId(Long permissionId) {
        List<IBaseRolePermission> rolePermissionList = new ArrayList<>();
        for (IBaseRolePermission iBaseRolePermission : getRolePermissionMap().values()) {
            if (Objects.equals(iBaseRolePermission.getPermissionId(), permissionId)) {
                rolePermissionList.add(
                        JSONObject.from(iBaseRolePermission).toJavaObject(IBaseRolePermission.class));
            }
        }
        return (List<T>) rolePermissionList;
    }

}
