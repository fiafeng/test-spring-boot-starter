package com.fiafeng.mapping.init;

import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.springframework.stereotype.Component;

@Component
public class MappingApplicationProcessor extends ApplicationProcessor {

    static {
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IMappingMapper.class);
        MybatisPlusUtils.putHashMapMybatisPlusTableName(IMappingMapper.class, IBaseMapping.class);
    }
}
