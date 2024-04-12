package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.common.service.IUserTableInitService;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;


@BeanDefinitionOrderAnnotation(1)
public class DefaultUserTableInitServiceServiceImpl implements IUserTableInitService {

    @Autowired
    FiafengRbacProperties rbacProperties;

    @Override
    public void init() throws Exception {
        // 检查用户表
        if (SpringUtils.getBean(IUserMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IUserMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseUser user = SpringUtils.getBean(IBaseUser.class);
                user.setId(1L);
                user.setUsername(rbacProperties.defaultUserName);
                user.setPassword(rbacProperties.defaultUserPassword);
                baseMysqlMapper.insertObject(user, false);
            }
        }
    }
}
