package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.mapper.mysql.BaseObjectMysqlMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.service.IUserTableInitService;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@BeanDefinitionOrderAnnotation()
public class DefaultUserTableInitServiceServiceImpl implements IUserTableInitService {

    @Autowired
    FiafengRbacProperties rbacProperties;

    @Override
    public void init() throws Exception {
        // 检查用户表
        IUserMapper userMapper = FiafengSpringUtils.getBean(IUserMapper.class);
        if (userMapper instanceof BaseObjectMysqlMapper) {
            BaseObjectMysqlMapper baseMysqlMapper = (BaseObjectMysqlMapper) userMapper;
            IBaseUser iBaseUser = (IBaseUser) baseMysqlMapper.selectObjectByObjectId(1L);
            if (iBaseUser == null) {

                IBaseUser user = FiafengSpringUtils.getBean(IBaseUser.class);
                user.setId(1L);
                user.setUsername(rbacProperties.defaultUserName);

                user.setPassword(rbacProperties.defaultUserPassword);
                baseMysqlMapper.insertObject(user, false);
            } else if (!rbacProperties.defaultUserName.equals(iBaseUser.getUsername())) {
                iBaseUser.setPassword(rbacProperties.getDefaultUserName());
                baseMysqlMapper.updateObject(iBaseUser);
            }
        } else {
            IBaseUser iBaseUser = userMapper.selectUserByUserName(rbacProperties.defaultUserName);
            IBaseUser user = null;
            if (iBaseUser == null) {
                user = FiafengSpringUtils.getBean(IBaseUser.class);
                user.setUsername(rbacProperties.defaultUserName);
                user.setPassword(rbacProperties.defaultUserPassword);
                MybatisPlusUtils.setUserAutoIncrementValue(userMapper, user);
                userMapper.insertUser(user);
            }
            else if (!rbacProperties.defaultUserName.equals(iBaseUser.getUsername())) {
                iBaseUser.setUsername(rbacProperties.getDefaultUserName());
                userMapper.updateUser(iBaseUser);
            }
        }
    }
}
