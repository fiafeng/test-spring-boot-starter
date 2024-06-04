package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.IUserTableInitService;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.mapper.mysql.BaseMysqlMapper;
import com.fiafeng.common.properties.FiafengRbacProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class DefaultUserTableInitServiceServiceImpl implements IUserTableInitService {

    @Autowired
    FiafengRbacProperties rbacProperties;

    @Override
    public void init() throws Exception {
        // 检查用户表
        if (FiafengSpringUtils.getBean(IUserMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) FiafengSpringUtils.getBean(IUserMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseUser user = FiafengSpringUtils.getBean(IBaseUser.class);
                user.setId(1L);
                user.setUsername(rbacProperties.defaultUserName);
                user.setPassword(rbacProperties.defaultUserPassword);
                baseMysqlMapper.insertObject(user, false);
            }
        }
    }
}
