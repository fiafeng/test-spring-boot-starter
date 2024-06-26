package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;

import java.util.List;



@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
//@Component
//@Primary
public class DefaultMysqlUserMapper extends BaseMysqlMapper implements IUserMapper {

    @Override
    public boolean insertUser(IBaseUser user) {
        return insertObject(user);
    }

    @Override
    public boolean updateUser(IBaseUser user) {
        return updateObject(user);
    }

    @Override
    public boolean deletedUserByUserId(Long userId) {
        return deletedObjectById(userId);
    }

    @Override
    public <T extends IBaseUser> List<T> selectUserListAll() {
        return selectObjectListAll();
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserName(String userName) {
        return selectObjectByObjectName(userName,getTableColName());
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserId(Long userId) {
        return selectObjectByObjectId(userId);
    }
}
