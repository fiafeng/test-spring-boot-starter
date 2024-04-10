package com.fiafeng.common.init;

import com.fiafeng.common.annotation.*;
import com.fiafeng.common.config.FiafengStaticEnvironment;
import com.fiafeng.common.mapper.*;
import com.fiafeng.common.pojo.Interface.*;
import com.fiafeng.common.service.*;
import com.fiafeng.common.utils.ClassUtils;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import com.fiafeng.security.service.ILoginService;
import com.fiafeng.common.controller.ILoginController;
import com.fiafeng.common.filter.IJwtAuthenticationTokenFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class CommonApplicationInit implements ApplicationListener<ContextRefreshedEvent>, BeanDefinitionRegistryPostProcessor, BeanPostProcessor {


    BeanDefinitionRegistry registry;

    @Autowired
    FiafengRbacProperties rbacProperties;

    static {
        FiafengStaticEnvironment.security = ClassUtils.classIsExistsOr("org.springframework.security.config.annotation.web.configuration.AutowiredWebSecurityConfigurersIgnoreParents");
        FiafengStaticEnvironment.mybatis = ClassUtils.classIsExistsOr("org.mybatis.spring.SqlSessionFactoryBean");
        FiafengStaticEnvironment.mybatis = ClassUtils.classIsExistsOr("org.mybatis.spring.SqlSessionFactoryBean");
        FiafengStaticEnvironment.mysql = ClassUtils.classIsExistsOr("com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver");
        FiafengStaticEnvironment.captcha = ClassUtils.classIsExistsOr("com.google.code.kaptcha.Producer");
        FiafengStaticEnvironment.redis = ClassUtils.classIsExistsOr("org.springframework.data.redis.core.RedisTemplate");
    }


    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8}")
    String url;

    @Autowired
    DataSourceProperties dataSourceProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 防止事件运行多次
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        if (ObjectClassUtils.url == null){
            ObjectClassUtils.url = url;
            if (ObjectClassUtils.url == null){
                dataSourceProperties = SpringUtils.getBean(DataSourceProperties.class);
                ObjectClassUtils.url = dataSourceProperties.getUrl();
            }
        }


        // 如果加载了默认提供的mysql的RBAC的Service时，从系统获取默认基础类型加载到
        ObjectClassUtils.refreshBaseMysqlMapperType(IPermissionMapper.class, IBasePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRolePermissionMapper.class, IBaseRolePermission.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IRoleMapper.class, IBaseRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserRoleMapper.class, IBaseUserRole.class);

        ObjectClassUtils.refreshBaseMysqlMapperType(IUserMapper.class, IBaseUser.class);

        mysqlMapperInit();
    }

    // 移除多余的实现类
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IBaseMapping.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IBaseRole.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IBaseRolePermission.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IBaseUser.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IBaseUserRole.class);


        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IPermissionMapper.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IRoleMapper.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IRolePermissionMapper.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IUserMapper.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IUserRoleMapper.class);


        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IPermissionService.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IUserRoleService.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IUserService.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IUserTableInitService.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , ITokenService.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , ILoginService.class);
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , ICacheService.class);


        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , IJwtAuthenticationTokenFilter.class);


        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory , ILoginController.class);

    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ObjectClassUtils.addBaseBeanType(bean, IBasePermission.class, BasePermissionAnnotation.class);
        ObjectClassUtils.addBaseBeanType(bean, IBaseRolePermission.class, BaseRolePermissionAnnotation.class);
        ObjectClassUtils.addBaseBeanType(bean, IBaseRole.class, BaseRoleAnnotation.class);
        ObjectClassUtils.addBaseBeanType(bean, IBaseUserRole.class, BaseUserRoleAnnotation.class);
        ObjectClassUtils.addBaseBeanType(bean, IBaseUser.class, BaseUserAnnotation.class);
        ObjectClassUtils.addBaseBeanType(bean, IBaseUserInfo.class, BaseUserInfoAnnotation.class);

        return bean;
    }

    /**
     * 初始化mysql数据库，用户，角色，权限。默认添加管理员和管理员角色和管理员权限
     */
    private void mysqlMapperInit() {

        // 检查用户表
        try {
            SpringUtils.getBean(IUserTableInitService.class).init();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 检查角色表
        if (SpringUtils.getBean(IRoleMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IRoleMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseRole baseRole = SpringUtils.getBean(IBaseRole.class);
                baseRole.setId(1L);
                baseRole.setName("admin");
                baseMysqlMapper.insertObject(baseRole, false);
            }
        }

        // 检查权限表
        if (SpringUtils.getBean(IPermissionMapper.class) instanceof BaseMysqlMapper) {
            BaseMysqlMapper baseMysqlMapper = (BaseMysqlMapper) SpringUtils.getBean(IPermissionMapper.class);
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBasePermission baseRole = SpringUtils.getBean(IBasePermission.class);
                baseRole.setId(1L);
                baseRole.setName("admin");
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
