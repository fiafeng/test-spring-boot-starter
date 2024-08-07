package com.fiafeng.common.utils;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.Interface.*;
import com.fiafeng.common.mapper.mysql.BaseObjectMysqlMapper;
import com.fiafeng.common.pojo.Interface.*;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.properties.FiafengRbacProperties;
import com.fiafeng.common.properties.mysql.*;
import com.fiafeng.common.service.IUserTableInitService;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.ResolvableType;

import java.util.*;

public class ObjectClassUtils {

    public static BeanDefinitionRegistry registry;
    public static ConfigurableListableBeanFactory beanFactory;

    public static HashMap<Class<?>, HashSet<String>> mybatisClassMap = new HashMap<>();

    private static HashSet<Class<?>> classList = new HashSet<>();

    public static void addRemoveBeanDefinitionByClass(Class<?> aClass) {
        classList.add(aClass);
    }


    public static void registerBean(Class<?> aClass, Object[] args, String bean) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(aClass);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        if (args != null) {
            for (Object arg : args) {
                beanDefinitionBuilder.addConstructorArgValue(arg);
            }
        }
        registry.registerBeanDefinition(bean, beanDefinition);
    }

    public static void registerBean(Class<?> aClass, Object[] args) {
        registerBean(aClass,args, aClass.getSimpleName());
    }

    public static void removeBeanDefinitions() {
        for (Class<?> aClass : ObjectClassUtils.classList) {
            ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, aClass);
        }
        classList = null;
    }

    /**
     * 查看容器内ObjectClass类型的数量。根据他的数量和hashSetName的数量觉得需要移除的bean
     *
     * @param registry    注册器
     * @param beanFactory bean工厂
     * @param objectClass 需要查找的类
     */
    public static void removeBeanDefinitions(BeanDefinitionRegistry registry,
                                             ConfigurableListableBeanFactory beanFactory,
                                             Class<?> objectClass) {
        String[] beanNames = beanFactory.getBeanNamesForType(objectClass);
        if (beanNames.length <= 1) {
            return;
        }

        HashMap<String, Integer> hashMap = new HashMap<>();
        String maxBennName = "";
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            ResolvableType resolvableType = beanDefinition.getResolvableType();
            Class<?> rawClass = resolvableType.getRawClass();
            if (rawClass == null) {
                String beanClassName = beanDefinition.getBeanClassName();
                if (beanClassName != null) {
                    try {
                        rawClass = Class.forName(beanClassName);
                    } catch (Exception e) {
                        break;
                    }
                } else {
                    try {
                        Object bean = beanFactory.getBean(beanName);
                        rawClass = bean.getClass();
                    } catch (Exception ignore) {
                    }

                }
            }

            assert rawClass != null;
            BeanDefinitionOrderAnnotation annotation = rawClass.getDeclaredAnnotation(BeanDefinitionOrderAnnotation.class);
            if (annotation != null) {
                int value = annotation.value();
                hashMap.put(beanName, value);
            } else {
                // 找到第一个没有BeanDefinitionOrderAnnotation注解的
                maxBennName = beanName;
                break;
            }
        }

        if (maxBennName.isEmpty()) {
            int maxValue = -99999;

            for (String beanName : hashMap.keySet()) {
                if (hashMap.get(beanName) > maxValue) {
                    maxValue = hashMap.get(beanName);
                    maxBennName = beanName;
                }
            }
        }
        for (String beanName : beanNames) {
            if (!Objects.equals(beanName, maxBennName)) {
                registry.removeBeanDefinition(beanName);
                if (mybatisClassMap.containsKey(objectClass)) {
                    mybatisClassMap.get(objectClass).add(beanName);
                } else {
                    HashSet<String> hashSet = new HashSet<>();
                    hashSet.add(beanName);
                    mybatisClassMap.put(objectClass, hashSet);
                }
            }
        }

    }

    /**
     * 初始化mysql数据库，用户，角色，权限。默认添加管理员和管理员角色和管理员权限
     */
    public static void mysqlMapperInit(FiafengRbacProperties rbacProperties) throws Exception {


        // 检查用户表
        // TODO 因为 BCryptPasswordEncoder 是属于Security框架的。所以为了兼容没有使用Security框架的，使用了一个接口两个实现类的方式

        IUserTableInitService userTableInitService = FiafengSpringUtils.getBean(IUserTableInitService.class);
        userTableInitService.init();

        IUserMapper userMapper = FiafengSpringUtils.getBean(IUserMapper.class);
        IBaseUser adminUser = userMapper.selectUserByUserName(rbacProperties.getDefaultUserName());


        IRoleMapper roleMapper = FiafengSpringUtils.getBean(IRoleMapper.class);
        // 检查角色表
        if (roleMapper instanceof BaseObjectMysqlMapper) {
            BaseObjectMysqlMapper baseMysqlMapper = (BaseObjectMysqlMapper) roleMapper;
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBaseRole baseRole = FiafengSpringUtils.getBean(IBaseRole.class);
                baseRole.setId(1L);
                baseRole.setName(rbacProperties.roleAdminName);
                baseMysqlMapper.insertObject(baseRole, false);
            }
        }else {
            IBaseRole baseRole = roleMapper.selectRoleByRoleName(rbacProperties.getRoleAdminName());
            if (baseRole == null){
                IBaseRole role = FiafengSpringUtils.getBean(IBaseRole.class);
                role.setName(rbacProperties.roleAdminName);
                if (MybatisPlusUtils.isMybatisClassProxy(roleMapper)) {
                    ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
                    IMysqlTableProperties mysqlUserProperties = FiafengSpringUtils.getBeanObject(FiafengMysqlRoleProperties.class);
                    Long autoIncrementValue = connectionPoolService.getAutoIncrementValue(mysqlUserProperties.getTableName());
                    role.setId(autoIncrementValue);
                }
                roleMapper.insertRole(role);

            }
        }



        IBaseRole adminRole = roleMapper.selectRoleByRoleName(rbacProperties.getRoleAdminName());

        // 检查权限表
        IPermissionMapper permissionMapper = FiafengSpringUtils.getBean(IPermissionMapper.class);
        if (permissionMapper instanceof BaseObjectMysqlMapper) {
            BaseObjectMysqlMapper baseMysqlMapper = (BaseObjectMysqlMapper) permissionMapper;
            if (baseMysqlMapper.selectObjectByObjectId(1L) == null) {
                IBasePermission basePermission = FiafengSpringUtils.getBean(IBasePermission.class);
                basePermission.setId(1L);
                basePermission.setName(rbacProperties.permissionAdminName);
                baseMysqlMapper.insertObject(basePermission, false);
            }
        }else {
            IBasePermission basePermission = permissionMapper.selectPermissionByPermissionName(rbacProperties.getPermissionAdminName());
            if (basePermission == null){
                IBasePermission permission = FiafengSpringUtils.getBean(IBasePermission.class);
                permission.setName(rbacProperties.roleAdminName);
                if (MybatisPlusUtils.isMybatisClassProxy(permissionMapper)) {
                    ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
                    IMysqlTableProperties mysqlUserProperties = FiafengSpringUtils.getBeanObject(FiafengMysqlPermissionProperties.class);
                    Long autoIncrementValue = connectionPoolService.getAutoIncrementValue(mysqlUserProperties.getTableName());
                    permission.setId(autoIncrementValue);
                }
                permissionMapper.insertPermission(permission);
            }
        }


        IBasePermission adminPermission = permissionMapper.selectPermissionByPermissionName(rbacProperties.getPermissionAdminName());

        // 检查用户角色表
        IUserRoleMapper userRoleMapper = FiafengSpringUtils.getBean(IUserRoleMapper.class);
        IBaseUserRole baseUserRole = FiafengSpringUtils.getBean(IBaseUserRole.class);
        baseUserRole.setRoleId(adminRole.getId());
        baseUserRole.setUserId(adminUser.getId());
        baseUserRole.setId(null);
        IBaseUserRole iBaseUserRole = userRoleMapper.selectUserRoleByUserRole(baseUserRole);
        if (iBaseUserRole == null){
            if (MybatisPlusUtils.isMybatisClassProxy(permissionMapper)) {
                ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
                IMysqlTableProperties mysqlUserProperties = FiafengSpringUtils.getBeanObject(FiafengMysqlUserRoleProperties.class);
                Long autoIncrementValue = connectionPoolService.getAutoIncrementValue(mysqlUserProperties.getTableName());
                baseUserRole.setId(autoIncrementValue);
            }
            userRoleMapper.insertUserRole(baseUserRole);
        }



        // 检查角色权限表
        IRolePermissionMapper rolePermissionMapper = FiafengSpringUtils.getBean(IRolePermissionMapper.class);
        IBaseRolePermission rolePermission = FiafengSpringUtils.getBean(IBaseRolePermission.class);
        rolePermission.setRoleId(adminRole.getId());
        rolePermission.setPermissionId(adminPermission.getId());
        rolePermission.setId(null);
        IBaseRolePermission baseRolePermission = rolePermissionMapper.selectRolePermissionIdByRoleIdPermissionId(rolePermission);
        if (baseRolePermission == null){
            if (MybatisPlusUtils.isMybatisClassProxy(permissionMapper)) {
                ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
                IMysqlTableProperties mysqlUserProperties = FiafengSpringUtils.getBeanObject(FiafengMysqlRoleProperties.class);
                Long autoIncrementValue = connectionPoolService.getAutoIncrementValue(mysqlUserProperties.getTableName());
                rolePermission.setId(autoIncrementValue);
            }
            rolePermissionMapper.insertRolePermission(rolePermission);
        }
    }


    /**
     * 在IOC容器里面寻找@param iMapperClass类。判断@param iMapperClass是不是继承了BaseMysqlMapper的类型。
     * 如果是就从容器里面找到对应的基础类型的iBaseObject的类型赋值给mysqlClass.type，如果在容器里面找不到类，则将defaultClass复制给mysqlClass.type
     *
     * @param iMapperClass mapper接口类
     * @param iBaseObject  pojo接口类
     */
    public static void refreshBaseMysqlMapperType(Class<? extends IMapper> iMapperClass, Class<? extends IBasePojo> iBaseObject) {
        refreshBaseMysqlMapperType(iMapperClass, iBaseObject, true);
    }

    /**
     * 在IOC容器里面寻找@param iMapperClass类。判断@param iMapperClass是不是继承了BaseMysqlMapper的类型。
     * 如果是就从容器里面找到对应的基础类型的iBaseObject的类型赋值给mysqlClass.type，如果在容器里面找不到类，则将defaultClass复制给mysqlClass.type
     *
     * @param iMapperClass mapper接口类
     * @param iBaseObject  pojo接口类
     * @param createTable  是否需要创建表
     */
    public static void refreshBaseMysqlMapperType(Class<?> iMapperClass, Class<?> iBaseObject, boolean createTable) {
        Object bean;
        bean = FiafengSpringUtils.getBean(iMapperClass);
        Object baseObject = FiafengSpringUtils.getBean(iBaseObject);
        if (bean instanceof BaseObjectMysqlMapper) {
            BaseObjectMysqlMapper mapper = (BaseObjectMysqlMapper) bean;

            mapper.setType(baseObject.getClass());
            if (createTable) {
                if (mapper.getConnectionPoolService() == null) {
                    mapper.setConnectionPoolService(FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class));
                }
                mapper.createdMysqlTable();
            }
        } else {
            try {
                // TODO 添加对应的属性，判断是否创建对应的表
                // TODO 是否创建默认用户

                ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
                IMysqlTableProperties tableProperties = new FiafengMysqlUserProperties();
                Map<String, IMysqlTableProperties> mysqlTablePropertiesMap = FiafengSpringUtils.getBeansOfType(IMysqlTableProperties.class);
                String pojoSubstringName = iBaseObject.getSimpleName().substring(5);
                for (String key : mysqlTablePropertiesMap.keySet()) {
                    String beanName = key;
                    if (beanName.contains(".")) {
                        beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
                    }
                    if (beanName.equals("FiafengMysql" + pojoSubstringName + "Properties")) {
                        tableProperties = mysqlTablePropertiesMap.get(key);
                    }
                }
                String tableName = tableProperties.getTableName();
                connectionPoolService.createdMysqlTable(tableName, baseObject.getClass());

            } catch (Exception ignore) {

            }
        }
    }

}
