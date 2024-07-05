package com.fiafeng.common.mapper.mysql;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IPermissionMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlPermissionMapper extends BaseObjectMysqlMapper implements IPermissionMapper {



    public <T extends IBasePermission> int insertPermission(T permission) {
        return insertObject(permission);
    }

    public <T extends IBasePermission> int updatePermission(T permission) {
        return updateObject(permission);
    }

    public int deletedPermission(Long permissionId) {
        return deletedObjectById(permissionId);
    }

    public <T extends IBasePermission> int updatePermissionList(List<T> permissionList) {
        return updateObjectList(permissionList);
    }

    public int deletedPermissionByIdList(List<Long> permissionIdList) {
        return deletedObjectByIdList(permissionIdList);
    }

    public <T extends IBasePermission> List<T> selectPermissionListByPermissionIdList(List<Long> permissionIdList) {
        return selectObjectListByObjectIdList(permissionIdList);
    }


    public <T extends IBasePermission> List<T> selectPermissionListAll() {
        return selectObjectListAll();
    }

    public <T extends IBasePermission> T selectPermissionByPermissionName(String permissionName) {
        return selectObjectByObjectName(permissionName, getTableColName());
    }

    public <T extends IBasePermission> T selectPermissionByPermissionId(Long permissionId) {
        return selectObjectByObjectId(permissionId);
    }
}
