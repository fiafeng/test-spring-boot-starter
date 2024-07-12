package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlUserRoleMapper extends BaseObjectMysqlMapper implements IUserRoleMapper {


    @Override
    public <T extends IBaseUserRole> int insertUserRole(T userRole) {
        return insertObject(userRole);
    }

    @Override
    public int updateUserRoleList(Long userId, List<Long> roleIdList) {
        List<Long> queryUserIdList = new ArrayList<>();
        List<IBaseUserRole> iBaseUserRoles = selectUserRoleListByUserId(userId);
        for (IBaseUserRole iBaseUserRole : iBaseUserRoles) {
            queryUserIdList.add(iBaseUserRole.getId());
        }

        deletedObjectByIdList(queryUserIdList);
        List<IBaseUserRole> userRoleList = new ArrayList<>();
        for (Long permissionId : roleIdList) {
            try {
                Object o = getType().newInstance();
                Field field = o.getClass().getDeclaredField(properties.getUserIdName());
                field.setAccessible(true);
                field.set(o, userId);
                field = o.getClass().getDeclaredField(properties.getRoleIdName());
                field.setAccessible(true);
                field.set(o, permissionId);
                userRoleList.add((IBaseUserRole) o);
            } catch (Exception e) {
//                throw new ServiceException("批量更新时，遇到意外的错误，错误消息为：" + e.getMessage());'
                return 0;
            }
        }
        try {
            for (IBaseUserRole iBaseUserRole : userRoleList) {
                insertUserRole(iBaseUserRole);
            }
        } catch (Exception e) {
            for (IBaseUserRole iBaseUserRole : iBaseUserRoles) {
                insertObject(iBaseUserRole, false);
            }


            return 0;
        }


        return 1;
    }

    @Override
    public <T extends IBaseUserRole> int deleteUserRole(T userRole) {
        return deletedObjectById(userRole.getUserId());
    }

    @Override
    public int deleteUserRoleById(Long id) {
        return deletedObjectById(id);
    }

    @Override
    public List<Long> selectRoleIdListByUserId(Long userId) {

        List<Long> permissionIdList = new ArrayList<>();
        List<IBaseUserRole> objectList = selectUserRoleListByUserId(userId);
        for (IBaseUserRole iBaseUserRole : objectList) {
            permissionIdList.add(iBaseUserRole.getRoleId());
        }
        return permissionIdList;

    }

    @Override
    public <T extends IBaseUserRole> List<T> selectUserRoleListByUserId(Long userId) {
        return selectObjectListByColValue(properties.getUserIdName(), userId);
    }

    @Override
    public <T extends IBaseUserRole> List<T> selectRoleListByRoleId(Long roleId) {
        return selectObjectListByColValue(properties.getRoleIdName(), roleId);
    }

    @Override
    public <T extends IBaseUserRole> T selectUserRoleByUserRole(T userRole) {
        return selectObjectByName1Name2AndValue1Value2(properties.getUserIdName(), properties.getRoleIdName(), userRole.getUserId(), userRole.getRoleId());
    }

    @Override
    public <T extends IBaseUserRole> T selectRoleListById(Long id) {
        return selectObjectByObjectId(id);
    }
}
