package com.fiafeng.common.mapper.mysql;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IPermissionMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import org.springframework.lang.NonNull;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlPermissionMapper extends BaseObjectMysqlMapper implements IPermissionMapper {



    public  int insertPermission(IBasePermission permission) {
        return insertObject(permission);
    }

    public  int updatePermission(IBasePermission permission) {
        return updateObject(permission);
    }

    public int deletedPermission(Long permissionId) {
        return deletedObjectById(permissionId);
    }

    public  int updatePermissionList(List<IBasePermission> permissionList) {
        return updateObjectList(permissionList);
    }

    public int deletedPermissionByIdList(List<Long> permissionIdList) {
        return deletedObjectByIdList(permissionIdList);
    }

    public  List<IBasePermission> selectPermissionListByPermissionIdList(List<Long> permissionIdList) {
        return selectObjectListByObjectIdList(permissionIdList);
    }


    public  List<IBasePermission> selectPermissionListAll() {
        return selectObjectListAll();
    }

    public  IBasePermission selectPermissionByPermissionName(@NonNull String permissionName) {
        return (IBasePermission) selectObjectByObjectName(getTableColName(), permissionName);
    }

    public  IBasePermission selectPermissionByPermissionId(Long permissionId) {
        return (IBasePermission) selectObjectByObjectId(permissionId);
    }
}
