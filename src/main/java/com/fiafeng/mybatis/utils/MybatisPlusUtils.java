package com.fiafeng.mybatis.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.Interceptor.MyDynamicTableNameInnerInterceptor;
import lombok.Getter;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MybatisPlusUtils {

    @Getter
    private final static HashMap<Class<? extends IMapper>, Class<? extends IBasePojo>> hashMap = new HashMap<>();

    public static void putHashMapMybatisPlusTableName(Class<? extends IMapper> mapperClass, Class<? extends IBasePojo> baseClass) {
        if (!hashMap.containsKey(mapperClass)) {
            hashMap.put(mapperClass, baseClass);
        } else {
            throw new RuntimeException(mapperClass + "类已经存在当前集合里面，不允许重复添加，请检查代码！！");
        }
    }


    public static void addMybatisPlusTableNameORM(Class<? extends IMapper> mapperClass, Class<? extends IBasePojo> baseClass) {
        if (isMybatisClassProxy(FiafengSpringUtils.getBean(mapperClass))) {
            // 获取原始表名
            Object bean = FiafengSpringUtils.getBean(baseClass);
            String substring = bean.getClass().getName().substring(bean.getClass().getName().lastIndexOf(".") + 1);
            String oldName = StringUtils.camelToUnderline(substring);

            String mapperName = mapperClass.getName();
            mapperName = mapperName.substring(mapperName.lastIndexOf(".") + 2, mapperName.length() - 6);

            Map<String, IMysqlTableProperties> beansOfType = FiafengSpringUtils.getBeanFactory().getBeansOfType(IMysqlTableProperties.class);
            for (String key : beansOfType.keySet()) {
                if (key.endsWith(mapperName + "Properties")) {
                    IMysqlTableProperties properties = beansOfType.get(key);
                    Environment environment = FiafengSpringUtils.getBean(Environment.class);
                    String property = environment.getProperty("fiafeng.mysql-table." + mapperName.toLowerCase() + ".table-name");

                    String tableName = property == null ? properties.getTableName() : property;
                    MyDynamicTableNameInnerInterceptor.hashMap.put(oldName, tableName);
                    HashSet<String> hashSet = ObjectClassUtils.mybatisClassMap.get(baseClass);
                    if (hashSet != null) {
                        for (String beanName : hashSet) {
                            MyDynamicTableNameInnerInterceptor.hashMap.put(StringUtils.camelToUnderline(beanName), tableName);
                        }
                    }

                }
            }
        }
    }

    public static boolean isMybatisClassProxy(Object object) {
        return BaseMapper.class.isAssignableFrom(object.getClass());
    }
}
