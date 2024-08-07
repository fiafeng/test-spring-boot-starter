package com.fiafeng.security.mapper;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.mapper.mysql.BaseObjectMysqlMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.properties.mysql.FiafengMysqlUserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@BeanDefinitionOrderAnnotation(value = ModelConstant.fourthOrdered)
public class DefaultSecurityMysqlUserMapper extends BaseObjectMysqlMapper implements IUserMapper {


    @Autowired
    FiafengMysqlUserProperties userProperties;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public int insertUser(IBaseUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return insertObject(user);
    }

    @Override
    public int updateUser(IBaseUser user) {
        if (!user.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return updateObject(user);
    }

    @Override
    public int deletedUserByUserId(Long userId) {
        return deletedObjectById(userId);
    }

    @Override
    public List<IBaseUser> selectUserListAll() {
        return (List<IBaseUser>) selectObjectListAll();
    }

    @Override
    public  IBaseUser selectUserByUserName(String username) {
        return (IBaseUser) selectObjectByObjectName(getTableColName(), username);
    }

    @Override
    public  IBaseUser selectUserByUserId(Long userId) {

        return (IBaseUser) selectObjectByObjectId(userId);
    }
}
