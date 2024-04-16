package com.fiafeng.mybatis.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.Interceptor.MyDynamicTableNameInnerInterceptor;

import java.util.Map;

public class MybatisPlusUtils {




    public static void addMybatisPlusTableNameORM(Class<? extends IMapper> mapperClass, Class<? extends IBasePojo> baseClass) {
        if (BaseMapper.class.isAssignableFrom(FiafengSpringUtils.getBean(mapperClass).getClass())) {
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
                    String tableName = properties.getTableName();
                    MyDynamicTableNameInnerInterceptor.hashMap.put(oldName, tableName);
                    for (String beanName : ObjectClassUtils.mybatisClassMap.get(baseClass)) {
                        MyDynamicTableNameInnerInterceptor.hashMap.put(StringUtils.camelToUnderline(beanName), tableName);
                    }


                }
            }
        }
    }
}
