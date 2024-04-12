package com.fiafeng.rbac.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.IPermissionMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@BeanDefinitionOrderAnnotation()
public class DefaultPermissionMapper implements IPermissionMapper {

    ConcurrentHashMap<Long, IBasePermission> permissionListMap;

    AtomicLong atomicLong = new AtomicLong(2);

    @Autowired
    FiafengRbacProperties rbacProperties;


    public ConcurrentHashMap<Long, IBasePermission> getPermissionListMap() {
        if (permissionListMap == null) {
            permissionListMap = new ConcurrentHashMap<>();
            permissionListMap.put(1L, SpringUtils.getBean(IBasePermission.class).setName(rbacProperties.permissionAdminName));
        }

        return permissionListMap;
    }



    @Override
    public boolean insertPermission(IBasePermission permission) {
        if (permission == null || permission.getName() == null) {
            return false;
        }
        for (IBasePermission iBasePermission : getPermissionListMap().values()) {
            if (iBasePermission.getName().equals(permission.getName())){
                throw new ServiceException("新增权限时，权限名已存在");
            }
        }

        long andIncrement = atomicLong.getAndIncrement();
        permission.setId(andIncrement);
        getPermissionListMap().put(andIncrement, permission);
        return true;
    }



    @Override
    public boolean updatePermission(IBasePermission permission) {
        if (permission.getId() == null) {
            throw new ServiceException("更新权限时，权限参数为空");
        }
        if (!getPermissionListMap().containsKey(permission.getId())) {
            throw new ServiceException("更新权限时,权限不存在");
        } else {
            getPermissionListMap().put(permission.getId(), permission);
        }

        return true;
    }

    @Override
    public <T extends IBasePermission> boolean updatePermissionList(List<T> permissionList) {
        for (IBasePermission permission : permissionList) {
            updatePermission(permission);
        }


        return false;
    }



    @Override
    public boolean deletedPermission(Long permissionId) {
        return getPermissionListMap().remove(permissionId) != null;
    }

    @Override
    public boolean deletedPermissionByIdList(List<Long> permissionIdList) {
        for (Long permissionId : permissionIdList) {
            if (!getPermissionListMap().containsKey(permissionId)) {
                return false;
            }
        }
        for (Long permissionId : permissionIdList) {
            getPermissionListMap().remove(permissionId);
        }

        return true;
    }



    @Override
    public <T extends IBasePermission> List<T> selectPermissionListAll() {
        List<IBasePermission> defaultPermissionList = new ArrayList<>();

        for (HashMap.Entry<Long, IBasePermission> entry : getPermissionListMap().entrySet()) {
            defaultPermissionList.add(JSONObject.from(entry.getValue()).toJavaObject(IBasePermission.class));
        }
        return (List<T>) defaultPermissionList;
    }

    @Override
    public <T extends IBasePermission> T selectPermissionByPermissionName(String permissionName) {
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        for (HashMap.Entry<Long, IBasePermission> entry : getPermissionListMap().entrySet()) {
            if (permissionName.equals(entry.getValue().getName())) {
                return (T) JSONObject.from(entry.getValue()).toJavaObject(IBasePermission.class);
            }
        }
        return null;
    }

    @Override
    public <T extends IBasePermission> T selectPermissionByPermissionId(Long permissionId) {
        if (getPermissionListMap().containsKey(permissionId)) {
            return (T) JSONObject.from(getPermissionListMap().get(permissionId)).toJavaObject(IBasePermission.class);
        }

        return null;
    }

    @Override
    public <T extends IBasePermission> List<T> selectPermissionListByPermissionIdList(List<Long> permissionIdList) {
        List<IBasePermission> permissionList = new ArrayList<>();
        for (Long permissionId : permissionIdList) {
            if (getPermissionListMap().containsKey(permissionId)) {
                permissionList.add(JSONObject.from(getPermissionListMap().get(permissionId)).toJavaObject(IBasePermission.class));
            }
        }
        return (List<T>) permissionList;
    }
}
