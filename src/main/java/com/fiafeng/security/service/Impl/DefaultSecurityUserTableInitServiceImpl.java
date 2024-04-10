package com.fiafeng.security.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.common.mapper.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.IUserTableInitService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;

@BeanDefinitionOrderAnnotation(2)
public class DefaultSecurityUserTableInitServiceImpl implements IUserTableInitService {

    @Override
    public void init() throws Exception {
        // 检查用户表
        if (SpringUtils.getBean(IUserMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IUserMapper.class);
            IBaseUser iBaseUser = baseMysqlMapper.selectObjectByObjectId(1L);
            if (iBaseUser == null || !"admin".equals(iBaseUser.getUsername())) {
                Object o = baseMysqlMapper.type.newInstance();
                Field field = o.getClass().getDeclaredField(baseMysqlMapper.idName);
                field.setAccessible(true);
                field.set(o, 1L);
                field = o.getClass().getDeclaredField(baseMysqlMapper.tableColName);
                field.setAccessible(true);
                field.set(o, "admin");

                field = o.getClass().getDeclaredField("password");
                field.setAccessible(true);
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                field.set(o, bCryptPasswordEncoder.encode("123456"));

                baseMysqlMapper.insertObject(o, false);
            }
        }
    }
}
