package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IRolePermissionMapper;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlRolePermissionMapper extends BaseObjectMysqlMapper implements IRolePermissionMapper {


    @Override
    public int insertRolePermission(IBaseRolePermission rolePermission) {
        return insertObject(rolePermission);
    }

    @Override
    public int deleteRolePermission(IBaseRolePermission rolePermission) {
        return deletedObjectById(rolePermission.getId());
    }

    @Override
    public int updateRolePermissionList(Long roleId, List<Long> permissionIdList) {
        List<Long> queryPermissionIdList = new ArrayList<>();
        List<IBaseRolePermission> iBaseRolePermissions = selectPermissionListByRoleId(roleId);
        for (IBaseRolePermission iBaseRolePermission : iBaseRolePermissions) {
            queryPermissionIdList.add(iBaseRolePermission.getId());
        }
        deletedObjectByIdList(queryPermissionIdList);
        List<IBaseRolePermission> rolePermissionList = new ArrayList<>();
        for (Long permissionId : permissionIdList) {
            try {
                Object o = getType().newInstance();
                Field field = o.getClass().getDeclaredField(properties.getRoleIdName());
                field.setAccessible(true);
                field.set(o, roleId);
                field = o.getClass().getDeclaredField(properties.getPermissionIdName());
                field.setAccessible(true);
                field.set(o, permissionId);
                rolePermissionList.add((IBaseRolePermission) o);
            } catch (Exception e) {
                throw new ServiceException("批量更新时，遇到意外的错误，错误消息为：" + e.getMessage());
            }
        }
        try {
            for (IBaseRolePermission iBaseUserRole : rolePermissionList) {
                insertRolePermission(iBaseUserRole);
            }
        } catch (Exception e) {
            for (IBaseRolePermission iBaseRolePermission : iBaseRolePermissions) {
                insertObject(iBaseRolePermission, false);
            }

            return 0;
        }


        return permissionIdList.size();
    }

    @Override
    public List<Long> selectPermissionIdListByRoleId(Long roleId) {
        List<IBaseRolePermission> objectList = (List<IBaseRolePermission>) selectObjectByObjectId(roleId);
        List<Long> permissionIdList = new ArrayList<>();
        for (IBaseRolePermission iBaseRolePermission : objectList) {
            permissionIdList.add(iBaseRolePermission.getPermissionId());
        }
        return permissionIdList;
    }

    @Override
    public IBaseRolePermission selectRolePermissionIdByRoleIdPermissionId(IBaseRolePermission rolePermission) {
        return (IBaseRolePermission) selectObjectByName1Name2AndValue1Value2(properties.getRoleIdName(), properties.getPermissionIdName(), rolePermission.getRoleId(), rolePermission.getPermissionId());
    }

    @Override
    public List<IBaseRolePermission> selectPermissionListByRoleId(Long roleId) {
        return (List<IBaseRolePermission>) selectObjectByObjectName(properties.getRoleIdName(), roleId);

    }

    @Override
    public List<IBaseRolePermission> selectPermissionListByPermissionId(Long permissionId) {
        return (List<IBaseRolePermission>) selectObjectByObjectName(properties.getRoleIdName(), permissionId);
    }
}
