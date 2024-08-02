package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;

import java.util.List;



@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlUserMapper extends BaseObjectMysqlMapper implements IUserMapper {

    @Override
    public int insertUser(IBaseUser user) {
        return insertObject(user);
    }

    @Override
    public int updateUser(IBaseUser user) {
        return updateObject(user);
    }

    @Override
    public int deletedUserByUserId(Long userId) {
        return deletedObjectById(userId);
    }

    @Override
    public List<IBaseUser> selectUserListAll() {
        return selectObjectListAll();
    }

    @Override
    public  IBaseUser selectUserByUserName(String userName) {
        return (IBaseUser) selectObjectByObjectName(getTableColName(),userName);
    }

//    @Override
//    public List<IBaseUser> selectUserListAll(IBaseUser baseUser) {
//        return null;
//    }

    @Override
    public  IBaseUser selectUserByUserId(Long userId) {
        return (IBaseUser) selectObjectByObjectId(userId);
    }
}
