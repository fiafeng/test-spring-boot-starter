package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlRoleMapper extends BaseMysqlMapper implements IRoleMapper {


    @Override
    public <T extends IBaseRole> boolean insertRole(T role) {
        return insertObject(role);
    }

    @Override
    public <T extends IBaseRole> boolean updateRole(T role) {
        return updateObject(role);
    }

    @Override
    public boolean deletedRole(Long roleId) {
        return deletedObjectById(roleId);
    }

    @Override
    public <T extends IBaseRole> List<T> selectRoleListALl() {
        return selectObjectListAll();
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleName(String roleName) {
        return selectObjectByObjectName(roleName);
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleId(Long roleId) {
        return selectObjectByObjectId(roleId);
    }
}
