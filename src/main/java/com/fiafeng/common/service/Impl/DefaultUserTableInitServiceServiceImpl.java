package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IUserMapper;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.common.service.IUserTableInitService;

import java.lang.reflect.Field;


@BeanDefinitionOrderAnnotation(1)
public class DefaultUserTableInitServiceServiceImpl implements IUserTableInitService {

    @Override
    public void init() throws Exception {
        // 检查用户表
        if (SpringUtils.getBean(IUserMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IUserMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                Object o = baseMysqlMapper.type.newInstance();
                Field field = o.getClass().getDeclaredField(baseMysqlMapper.idName);
                field.setAccessible(true);
                field.set(o, 1L);
                field = o.getClass().getDeclaredField(baseMysqlMapper.tableColName);
                field.setAccessible(true);
                field.set(o, "admin");

                field = o.getClass().getDeclaredField("password");
                field.setAccessible(true);
                field.set(o, "123456");

                baseMysqlMapper.insertObject(o, false);
            }
        }
    }
}
