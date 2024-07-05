package com.fiafeng.common.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IPermissionMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
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
public class DefaultPermissionMapper implements IPermissionMapper {

    ConcurrentHashMap<Long, IBasePermission> permissionListMap;

    AtomicLong atomicLong = new AtomicLong(2);

    @Autowired
    FiafengRbacProperties rbacProperties;


    public ConcurrentHashMap<Long, IBasePermission> getPermissionListMap() {
        if (permissionListMap == null) {
            permissionListMap = new ConcurrentHashMap<>();
            permissionListMap.put(1L, FiafengSpringUtils.getBean(IBasePermission.class).setName(rbacProperties.permissionAdminName).setId(1L));
        }

        return permissionListMap;
    }



    @Override
    public int insertPermission(IBasePermission permission) {
        if (permission == null || permission.getName() == null) {

            throw new ServiceException("权限名不允许为空");
        }
        for (IBasePermission iBasePermission : getPermissionListMap().values()) {
            if (iBasePermission.getName().equals(permission.getName())){
                throw new ServiceException("新增权限时，权限名已存在");
            }
        }

        long andIncrement = atomicLong.getAndIncrement();
        permission.setId(andIncrement);
        getPermissionListMap().put(andIncrement, permission);
        return 1;
    }



    @Override
    public int updatePermission(IBasePermission permission) {
        if (permission.getId() == null) {
            throw new ServiceException("更新权限时，权限参数为空");
        }
        if (!getPermissionListMap().containsKey(permission.getId())) {
            throw new ServiceException("更新权限时,权限不存在");
        } else {
            getPermissionListMap().put(permission.getId(), permission);
        }

        return 1;
    }

    @Override
    public <T extends IBasePermission> int updatePermissionList(List<T> permissionList) {
        for (IBasePermission permission : permissionList) {
            updatePermission(permission);
        }
        return permissionList.size();
    }



    @Override
    public int deletedPermission(Long permissionId) {
        return getPermissionListMap().remove(permissionId) == null ? 1 : 0 ;
    }

    @Override
    public int deletedPermissionByIdList(List<Long> permissionIdList) {

        for (Long permissionId : permissionIdList) {
            if (!getPermissionListMap().containsKey(permissionId)) {
                throw new ServiceException("有id找不到对应的实体信息");
            }
        }
        for (Long permissionId : permissionIdList) {
            getPermissionListMap().remove(permissionId);
        }

        return permissionIdList.size();
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
