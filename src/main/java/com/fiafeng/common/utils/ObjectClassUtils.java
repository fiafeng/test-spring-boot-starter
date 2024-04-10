package com.fiafeng.common.utils;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.common.pojo.FiafengStaticBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class ObjectClassUtils {

    public static String url;


    /**
     * 判断bean的类型上是否存在annotation注解，如果存在，则保存到key为baseClass的 FiafengStaticBean.baseBeanClassHashMap集合中
     *
     * @param bean       加载到容器的对象
     * @param baseClass  默认接口类型
     * @param annotation 自定义注解
     */
    public static void addBaseBeanType(Object bean,
                                       Class<?> baseClass,
                                       Class<? extends Annotation> annotation) {
//        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        if (bean.getClass().getAnnotation(annotation) != null) {
            ObjectClassUtils.putBaseBean(baseClass, bean.getClass());
        }
    }

    /**
     * 将基础的bean添加到 FiafengStaticBean.baseBeanClassHashMap
     *
     * @param key   key
     * @param value value
     */
    public static void putBaseBean(Class<?> key, Class<?> value) {

        if (FiafengStaticBean.baseBeanClassHashMap.containsKey(key)) {
            FiafengStaticBean.baseBeanClassHashMap.get(key).add(value);
        } else {
            HashSet<Class<?>> list = new HashSet<>();
            list.add(value);
            FiafengStaticBean.baseBeanClassHashMap.put(key, list);
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
                        Map<String, ?> beansOfType = beanFactory.getBeansOfType(objectClass);
                        System.out.println(beansOfType);
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
    public static void refreshBaseMysqlMapperType(Class<?> iMapperClass, Class<?> iBaseObject) {
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
            Object bean = SpringUtils.getBean(iMapperClass);
            if (bean instanceof BaseMysqlMapper) {
                BaseMysqlMapper mapper = (BaseMysqlMapper) bean;

                mapper.type = SpringUtils.getBean(iBaseObject).getClass();
                if (createTable) {
                    mapper.checkMysqlTableIsExist(url);
                }

            }
        } catch (Exception e) {
            throw e;
        }
    }

}
