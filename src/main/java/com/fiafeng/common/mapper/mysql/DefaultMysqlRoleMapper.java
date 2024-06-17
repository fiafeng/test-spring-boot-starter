package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.properties.mysql.FiafengMysqlRoleProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlRoleMapper extends BaseMysqlMapper implements IRoleMapper {

    @Autowired
    FiafengMysqlRoleProperties properties;


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
        return selectObjectByObjectName(roleName, getTableColName());
    }

    @Override
    public <T extends IBaseRole> T selectRoleByRoleId(Long roleId) {
        return selectObjectByObjectId(roleId);
    }
}
