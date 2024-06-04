package com.fiafeng.mybatis.init;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.init.ApplicationInit;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Component
@ConditionalOnClass(BaseMapper.class)
@ApplicationInitAnnotation(-10000)
public class MybatisPlusApplicationInit implements ApplicationInit {

    @Override
    public void init() {
        Set<Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>>> entries = MybatisPlusUtils.getHashMap().entrySet();
        for (Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>> classClassEntry : entries) {
            MybatisPlusUtils.addMybatisPlusTableNameORM(classClassEntry.getKey(), classClassEntry.getValue());
        }
    }

}
