package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.properties.mysql.FiafengMysqlRoleProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlRoleMapper extends BaseObjectMysqlMapper implements IRoleMapper {

    @Autowired
    FiafengMysqlRoleProperties properties;


    @Override
    public  int insertRole(IBaseRole role) {
        return insertObject(role);
    }

    @Override
    public  int updateRole(IBaseRole role) {
        return updateObject(role);
    }

    @Override
    public int deletedRole(Long roleId) {
        return deletedObjectById(roleId);
    }

    @Override
    public  List<IBaseRole> selectRoleListALl() {
        return selectObjectListAll();
    }

    @Override
    public  IBaseRole selectRoleByRoleName(String roleName) {
        return (IBaseRole) selectObjectByObjectName(getTableColName(), roleName);
    }

    @Override
    public  IBaseRole selectRoleByRoleId(Long roleId) {
        return (IBaseRole) selectObjectByObjectId(roleId);
    }
}
