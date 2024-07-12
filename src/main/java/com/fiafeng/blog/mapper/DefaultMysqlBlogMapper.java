package com.fiafeng.blog.mapper;

import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.mysql.BaseObjectMysqlMapper;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlBlogMapper extends BaseObjectMysqlMapper implements IBlogMapper {

    @Override
    public int insertBlog(IBaseBlog baseBlog) {
        return insertObject(baseBlog);
    }

    @Override
    public int deleteBoleById(Long blogId) {
        return deletedObjectById(blogId);
    }


    @Override
    public int deletedBlogByIdList(List<Long> blogIdList) {
        return deletedObjectByIdList(blogIdList);
    }

    @Override
    public int updateBlogById(IBaseBlog baseBlog) {
        return updateObject(baseBlog);
    }

    @Override
    public <T extends IBaseBlog> List<T> selectBlogListByUserId(Long userId) {
        return selectObjectListByColValue(properties.getUserIdName(), userId);
    }

    @Override
    public <T extends IBaseBlog> T selectBlogById(Long blogId) {
        return selectObjectByObjectId(blogId);
    }

}
