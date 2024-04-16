package com.fiafeng.common.utils;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.mapper.mysql.BaseMysqlMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.ResolvableType;

import java.util.*;

public class ObjectClassUtils {

    public static String url;

    public static BeanDefinitionRegistry registry;
    public static ConfigurableListableBeanFactory beanFactory;

    public static HashMap<Class, HashSet<String>> mybatisClassMap = new HashMap<>();

    private static HashSet<Class<?>> classList = new HashSet<>();

    public static void addRemoveBeanDefinitionByClass(Class<?> aClass) {
        classList.add(aClass);
    }


    public static void removeBeanDefinitions() {
        for (Class<?> aClass : ObjectClassUtils.classList) {
            ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, aClass);
        }
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
                    Map<String, ?> beansOfTypeMap = beanFactory.getBeansOfType(objectClass);
                    for (String string : beansOfTypeMap.keySet()) {
                        if (beanName.equals(string)) {
                            Object o = beansOfTypeMap.get(beanName);
                            rawClass = o.getClass();
                        }
                    }
                }
            }

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
        try {
            Object bean = FiafengSpringUtils.getBean(iMapperClass);
            if (bean instanceof BaseMysqlMapper) {
                BaseMysqlMapper mapper = (BaseMysqlMapper) bean;

                mapper.type = FiafengSpringUtils.getBean(iBaseObject).getClass();
                if (createTable) {
                    mapper.checkMysqlTableIsExist(url);
                }

            }
        } catch (Exception e) {
            throw e;
        }
    }

}
