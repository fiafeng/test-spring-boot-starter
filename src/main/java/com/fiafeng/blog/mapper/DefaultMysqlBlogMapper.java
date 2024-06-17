package com.fiafeng.blog.mapper;

import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.mysql.BaseMysqlMapper;

import java.util.List;


@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlBlogMapper extends BaseMysqlMapper implements IBlogMapper {

    @Override
    public boolean insertBlog(IBaseBlog baseBlog) {
        return insertObject(baseBlog);
    }

    @Override
    public boolean deleteBoleById(Long blogId) {
        return deletedObjectById(blogId);
    }


    @Override
    public boolean deletedBlogByIdList(List<Long> blogIdList) {
        return deletedObjectByIdList(blogIdList);
    }

    @Override
    public boolean updateBlogById(IBaseBlog baseBlog) {
        return updateObject(baseBlog);
    }

    @Override
    public <T extends IBaseBlog> List<T> selectBlogListByUserId(Long userId) {
        return selectObjectByKeyAndValueList( getUserIdName(),userId);
    }

    @Override
    public <T extends IBaseBlog> T selectBlogById(Long blogId) {
        return selectObjectByObjectId(blogId);
    }

}
