package com.fiafeng.mybatis.init;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.annotation.ApplicationInitAnnotation;
import com.fiafeng.common.init.ApplicationInitAfter;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.factory.CustomObjectFactory;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
@ConditionalOnClass(BaseMapper.class)
@ApplicationInitAnnotation(10000)
public class MybatisPlusApplicationInitAfter implements ApplicationInitAfter {

    @Override
    public void init() {
        try {
            SqlSessionFactory bean = FiafengSpringUtils.getBean(SqlSessionFactory.class);
            bean.getConfiguration().setObjectFactory(FiafengSpringUtils.getBean(CustomObjectFactory.class));
            Map<String, Interceptor> interceptorMap = FiafengSpringUtils.getBeanFactory().getBeansOfType(Interceptor.class);
            List<Interceptor> values = new ArrayList<>(interceptorMap.values());
            for (Interceptor value : values) {
                bean.getConfiguration().addInterceptor(value);
            }
        }catch (Exception ignore){

        }

        Set<Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>>> entries = MybatisPlusUtils.getHashMap().entrySet();
        for (Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>> classClassEntry : entries) {
            MybatisPlusUtils.addMybatisPlusTableNameORM(classClassEntry.getKey(), classClassEntry.getValue());
        }




    }



}
