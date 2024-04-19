package com.fiafeng.mybatis.init;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.init.ApplicationInit;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@ConditionalOnClass(BaseMapper.class)
@ApplicationInitAnnotation(-10000)
public class MybatisPlusApplicationInit implements ApplicationInit {

    private final static HashMap<Class<? extends IMapper>, Class<? extends IBasePojo>> hashMap = new HashMap<>();

    public static void putHashMapMybatisPlusTableName(Class<? extends IMapper> mapperClass, Class<? extends IBasePojo> baseClass) {
        if (!hashMap.containsKey(mapperClass)) {
            hashMap.put(mapperClass, baseClass);
        } else {
            throw new RuntimeException(mapperClass + "类已经存在当前集合里面，不允许重复添加，请检查代码！！");
        }

    }

    @Override
    public void init() {
        for (Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>> classClassEntry : hashMap.entrySet()) {
            MybatisPlusUtils.addMybatisPlusTableNameORM(classClassEntry.getKey(), classClassEntry.getValue());
        }
    }

}
