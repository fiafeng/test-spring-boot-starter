package com.fiafeng.common.init;


import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.mapper.Interface.*;
import com.fiafeng.common.pojo.Interface.*;
import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.stereotype.Component;

@ApplicationInitAnnotation(100)
@Component
public class MysqlApplicationInit implements ApplicationInitAfter{


    @Override
    public void init() {
        // 如果加载了默认提供的mysql的RBAC的Service时，从系统获取默认基础类型加载到
        ObjectClassUtils.refreshBaseMysqlMapperType(IPermissionMapper.class, IBasePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRolePermissionMapper.class, IBaseRolePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRoleMapper.class, IBaseRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserRoleMapper.class, IBaseUserRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserMapper.class, IBaseUser.class);

        try {
            ObjectClassUtils.mysqlMapperInit(FiafengSpringUtils.getBean(FiafengRbacProperties.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
