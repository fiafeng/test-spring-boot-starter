package com.fiafeng.blog.init;

import com.fiafeng.blog.mapper.IBlogMapper;
import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.init.ApplicationInit;
import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.utils.ObjectClassUtils;

public class BlogApplicationInit extends ApplicationProcessor implements ApplicationInit{


    static {
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IBaseBlog.class);
    }

    @Override
    public void init() {
        ObjectClassUtils.refreshBaseMysqlMapperType(IBlogMapper.class, IBaseBlog.class);

    }
}
