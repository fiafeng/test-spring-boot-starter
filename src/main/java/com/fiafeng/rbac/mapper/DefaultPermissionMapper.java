package com.fiafeng.rbac.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.IPermissionMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.rbac.pojo.DefaultPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@BeanDefinitionOrderAnnotation()
public class DefaultPermissionMapper implements IPermissionMapper {

    ConcurrentHashMap<Long, String> permissionListMap;

    AtomicLong atomicLong = new AtomicLong(2);


    public ConcurrentHashMap<Long, String> getPermissionListMap() {
        if (permissionListMap == null) {
            permissionListMap = new ConcurrentHashMap<>();
            permissionListMap.put(1L, "admin");
        }

        return permissionListMap;
    }

    @Override
    public <T extends IBasePermission> T selectPermissionByPermissionName(String permissionName) {
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        for (HashMap.Entry<Long, String> entry : getPermissionListMap().entrySet()) {
            if (permissionName.equals(entry.getValue())) {
                return (T) new DefaultPermission().setId(entry.getKey()).setName(entry.getValue());
            }
        }
        return null;
    }

    @Override
    public <T extends IBasePermission> T selectPermissionByPermissionId(Long permissionId) {
        if (getPermissionListMap().containsKey(permissionId)) {
            return (T) new DefaultPermission().setId(permissionId).setName(getPermissionListMap().get(permissionId));
        }

        return null;
    }

    /**
     * @param permissionIdList
     * @param <T>
     * @return
     */
    @Override
    public <T extends IBasePermission> List<T> selectPermissionListByPermissionIdList(List<Long> permissionIdList) {
        List<IBasePermission> permissionList = new ArrayList<>();
        for (Long permissionId : permissionIdList) {
            if (getPermissionListMap().containsKey(permissionId)) {
                permissionList.add(new DefaultPermission().setName(getPermissionListMap().get(permissionId)).setId(permissionId));
            }
        }
        return (List<T>) permissionList;
    }

    @Override
    public boolean insertPermission(IBasePermission permission) {
        if (permission == null) {
            return false;
        }
        getPermissionListMap().put(atomicLong.getAndIncrement(), permission.getName());
        return true;
    }

    @Override
    public boolean updatePermission(IBasePermission permission) {
        if (permission.getId() == null) {
            throw new ServiceException("请输入正确的参数");
        }
        if (!getPermissionListMap().containsKey(permission.getId())) {
            throw new ServiceException("权限不存在");

        } else {
            getPermissionListMap().put(permission.getId(), permission.getName());
        }

        return true;
    }

    /**
     * @param permissionList
     * @param <T>
     * @return
     */
    @Override
    public <T extends IBasePermission> boolean updatePermissionList(List<T> permissionList) {
        for (IBasePermission permission : permissionList) {
            if (permission.getName() == null || permission.getName().isEmpty()) {
                return false;
            }
        }
        for (IBasePermission permission : permissionList) {
            insertPermission(permission);
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
        List<DefaultPermission> defaultPermissionList = new ArrayList<>();

        for (HashMap.Entry<Long, String> entry : getPermissionListMap().entrySet()) {
            defaultPermissionList.add(new DefaultPermission().setId(entry.getKey()).setName(entry.getValue()));
        }
        return (List<T>) defaultPermissionList;
    }
}
