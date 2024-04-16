package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IRolePermissionMapper;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@BeanDefinitionOrderAnnotation(1)
public class DefaultMysqlRolePermissionMapper extends BaseMysqlMapper implements IRolePermissionMapper {


    @Override
    public <T extends IBaseRolePermission> boolean insertRolePermission(T rolePermission) {
        return insertObject(rolePermission);
    }

    @Override
    public <T extends IBaseRolePermission> boolean deleteRolePermission(T rolePermission) {
        return deletedObjectById(rolePermission.getId());
    }

    @Override
    public boolean updateRolePermissionList(Long roleId, List<Long> permissionIdList) {
        List<Long> queryPermissionIdList = new ArrayList<>();
        List<IBaseRolePermission> iBaseRolePermissions = selectPermissionListByRoleId(roleId);
        for (IBaseRolePermission iBaseRolePermission : iBaseRolePermissions) {
            queryPermissionIdList.add(iBaseRolePermission.getId());
        }
        deletedObjectByIdList(queryPermissionIdList);
        List<IBaseRolePermission> rolePermissionList = new ArrayList<>();
        for (Long permissionId : permissionIdList) {
            try {
                Object o = type.newInstance();
                Field field = o.getClass().getDeclaredField(roleIdName);
                field.setAccessible(true);
                field.set(o, roleId);
                field = o.getClass().getDeclaredField(permissionIdName);
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


            return false;
        }


        return true;
    }

    @Override
    public List<Long> selectPermissionIdListByRoleId(Long roleId) {


        List<IBaseRolePermission> objectList = selectObjectByKeyAndValueList(roleIdName, roleId);
        List<Long> permissionIdList = new ArrayList<>();
        for (IBaseRolePermission iBaseRolePermission : objectList) {
            permissionIdList.add(iBaseRolePermission.getPermissionId());
        }
        return permissionIdList;
    }

    @Override
    public <T extends IBaseRolePermission> T selectRolePermissionIdByRoleIdPermissionId(T rolePermission) {
        return selectObjectByName1Name2AndValue1Value2(roleIdName, permissionIdName, rolePermission.getRoleId(), rolePermission.getPermissionId());
    }

    @Override
    public <T extends IBaseRolePermission> List<T> selectPermissionListByRoleId(Long roleId) {
        return selectObjectByKeyAndValueList(roleIdName, roleId);

    }

    @Override
    public <T extends IBaseRolePermission> List<T> selectPermissionListByPermissionId(Long permissionId) {
        return selectObjectByKeyAndValueList(permissionIdName, permissionId);
    }

}
