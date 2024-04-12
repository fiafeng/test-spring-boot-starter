package com.fiafeng.common.init;

import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.config.FiafengStaticEnvironment;
import com.fiafeng.common.controller.ILoginController;
import com.fiafeng.common.filter.IJwtAuthenticationTokenFilter;
import com.fiafeng.common.mapper.*;
import com.fiafeng.common.pojo.Interface.*;
import com.fiafeng.common.service.*;
import com.fiafeng.common.utils.ClassUtils;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Map;

public class CommonApplicationInit extends ApplicationProcessor implements ApplicationListener<ContextRefreshedEvent>, Ordered {


    static {
        FiafengStaticEnvironment.security = ClassUtils.classIsExistsOR("org.springframework.security.config.annotation.web.configuration.AutowiredWebSecurityConfigurersIgnoreParents");
        FiafengStaticEnvironment.mybatis = ClassUtils.classIsExistsOR("org.mybatis.spring.SqlSessionFactoryBean");
        FiafengStaticEnvironment.mysql = ClassUtils.classIsExistsOR("com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver");
        FiafengStaticEnvironment.captcha = ClassUtils.classIsExistsOR("com.google.code.kaptcha.Producer");
        FiafengStaticEnvironment.redis = ClassUtils.classIsExistsOR("org.springframework.data.redis.core.RedisTemplate");

        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseMapping.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseRole.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseRolePermission.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseUser.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseUserRole.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRoleMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IRolePermissionMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleMapper.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IPermissionService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserRoleService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserTableInitService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ITokenService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ILoginService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ICacheService.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IJwtAuthenticationTokenFilter.class);
        ObjectClassUtils.addRemoveBeanDefinitionByClass(ILoginController.class);

    }


    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 防止事件运行多次
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        if (ObjectClassUtils.url == null) {
            Environment environment = event.getApplicationContext().getEnvironment();
            ObjectClassUtils.url = environment.getProperty("spring.datasource.url");
        }

        // 如果加载了默认提供的mysql的RBAC的Service时，从系统获取默认基础类型加载到
        ObjectClassUtils.refreshBaseMysqlMapperType(IPermissionMapper.class, IBasePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRolePermissionMapper.class, IBaseRolePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRoleMapper.class, IBaseRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserRoleMapper.class, IBaseUserRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserMapper.class, IBaseUser.class);

        mysqlMapperInit();

        Map<String, ApplicationInit> beansOfType = SpringUtils.getBeanFactory().getBeansOfType(ApplicationInit.class);
        Integer[] valuesArray = new Integer[beansOfType.size()];
        ApplicationInit[] applicationInitArray = beansOfType.values().toArray(new ApplicationInit[beansOfType.values().size()]);
        for (int i = 0; i < applicationInitArray.length; i++) {
            ApplicationInit applicationInit = applicationInitArray[i];
            try {
                Method method = applicationInit.getClass().getMethod("init", null);
                ApplicationInitAnnotation annotation = method.getAnnotation(ApplicationInitAnnotation.class);
                if (annotation != null) {
                    valuesArray[i] = annotation.value();
                } else {
                    valuesArray[i] = 0;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }

        for (int i = 0; i < valuesArray.length; i++) {
            int max = -9998;
            int pos = -1;
            for (int j = 0; j < valuesArray.length; j++) {
                if (valuesArray[j] > max){
                    max = valuesArray[j];
                    pos = j;
                }
            }
            applicationInitArray[pos].init();
            valuesArray[pos] = -9999;
        }
    }

    @Autowired
    FiafengRbacProperties rbacProperties;

    /**
     * 初始化mysql数据库，用户，角色，权限。默认添加管理员和管理员角色和管理员权限
     */
    private void mysqlMapperInit() {

        // 检查用户表
        try {
            IUserTableInitService userTableInitService = SpringUtils.getBean(IUserTableInitService.class);
            userTableInitService.init();

            System.out.println();
        } catch (Exception ignored) {
        }

        // 检查角色表
        if (SpringUtils.getBean(IRoleMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IRoleMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseRole baseRole = SpringUtils.getBean(IBaseRole.class);
                baseRole.setId(1L);
                baseRole.setName(rbacProperties.roleAdminName);
                baseMysqlMapper.insertObject(baseRole, false);
            }
        }

        // 检查权限表
        if (SpringUtils.getBean(IPermissionMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IPermissionMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBasePermission baseRole = SpringUtils.getBean(IBasePermission.class);
                baseRole.setId(1L);
                baseRole.setName(rbacProperties.permissionAdminName);
                baseMysqlMapper.insertObject(baseRole, false);

            }
        }

        // 检查用户角色表
        if (SpringUtils.getBean(IUserRoleMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IUserRoleMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseUserRole baseRole = SpringUtils.getBean(IBaseUserRole.class);
                baseRole.setId(1L);
                baseRole.setRoleId(1L);
                baseRole.setUserId(1L);
                baseMysqlMapper.insertObject(baseRole, false);
            }
        }


        // 检查角色权限表
        if (SpringUtils.getBean(IRolePermissionMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IRolePermissionMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseRolePermission baseRole = SpringUtils.getBean(IBaseRolePermission.class);
                baseRole.setId(1L);
                baseRole.setRoleId(1L);
                baseRole.setPermissionId(1L);
                baseMysqlMapper.insertObject(baseRole, false);
            }
        }
    }


}
