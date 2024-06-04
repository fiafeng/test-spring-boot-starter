package com.fiafeng.security.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.IUserTableInitService;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.mapper.mysql.BaseMysqlMapper;
import com.fiafeng.common.properties.FiafengRbacProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@BeanDefinitionOrderAnnotation(value = ModelConstant.secondOrdered)
@Component
public class DefaultSecurityUserTableInitServiceImpl implements IUserTableInitService {


    @Autowired
    FiafengRbacProperties rbacProperties;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public void init() throws Exception {
        // 检查用户表
        IUserMapper bean = FiafengSpringUtils.getBean(IUserMapper.class);
        if (bean instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) bean;
            IBaseUser iBaseUser = baseMysqlMapper.selectObjectByObjectId(1L);
            if (iBaseUser == null || !rbacProperties.defaultUserName.equals(iBaseUser.getUsername())) {

                IBaseUser user = FiafengSpringUtils.getBean(IBaseUser.class);
                user.setId(1L);
                user.setUsername(rbacProperties.defaultUserName);

                user.setPassword(bCryptPasswordEncoder.encode(rbacProperties.defaultUserPassword));
                baseMysqlMapper.insertObject(user, false);
            }
        }
    }
}
