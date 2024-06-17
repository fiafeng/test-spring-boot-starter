package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlUserRoleMapper extends BaseMysqlMapper implements IUserRoleMapper {


    @Override
    public <T extends IBaseUserRole> boolean insertUserRole(T userRole) {
        return insertObject(userRole);
    }

    @Override
    public boolean updateUserRoleList(Long userId, List<Long> roleIdList) {
        List<Long> queryUserIdList = new ArrayList<>();
        List<IBaseUserRole> iBaseUserRoles = selectRoleListByUserRole(userId);
        for (IBaseUserRole iBaseUserRole : iBaseUserRoles) {
            queryUserIdList.add(iBaseUserRole.getId());
        }

        deletedObjectByIdList(queryUserIdList);
        List<IBaseUserRole> userRoleList = new ArrayList<>();
        for (Long permissionId : roleIdList) {
            try {
                Object o = getType().newInstance();
                Field field = o.getClass().getDeclaredField(getUserIdName());
                field.setAccessible(true);
                field.set(o, userId);
                field = o.getClass().getDeclaredField(getRoleIdName());
                field.setAccessible(true);
                field.set(o, permissionId);
                userRoleList.add((IBaseUserRole) o);
            } catch (Exception e) {
//                throw new ServiceException("批量更新时，遇到意外的错误，错误消息为：" + e.getMessage());'
                return false;
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


            return false;
        }


        return true;
    }

    @Override
    public <T extends IBaseUserRole> boolean deleteUserRole(T userRole) {
        return deletedObjectById(userRole.getUserId());
    }

    @Override
    public boolean deleteUserRoleById(Long id) {
        return deletedObjectById(id);
    }

    @Override
    public List<Long> selectRoleIdListByUserId(Long userId) {

        List<Long> permissionIdList = new ArrayList<>();
        List<IBaseUserRole> objectList = selectRoleListByUserRole(userId);
        for (IBaseUserRole iBaseUserRole : objectList) {
            permissionIdList.add(iBaseUserRole.getRoleId());
        }
        return permissionIdList;

    }

    @Override
    public <T extends IBaseUserRole> List<T> selectRoleListByUserRole(Long userId) {
        return selectObjectByKeyAndValueList(getUserIdName(), userId);
    }

    @Override
    public <T extends IBaseUserRole> List<T> selectRoleListByRoleId(Long roleId) {
        return selectObjectByKeyAndValueList(getRoleIdName(), roleId);
    }

    @Override
    public <T extends IBaseUserRole> T selectRoleListByUserRole(T userRole) {
        return selectObjectByName1Name2AndValue1Value2(getUserIdName(), getRoleIdName(), userRole.getUserId(), userRole.getRoleId());
    }

    @Override
    public <T extends IBaseUserRole> T selectRoleListById(Long id) {
        return selectObjectByObjectId(id);
    }
}
