package com.fiafeng.blog.mapper;

import com.fiafeng.blog.pojo.IBaseBlog;

import java.util.List;

public interface IBlogMapper {

    public boolean insertBlog(IBaseBlog baseBlog);

    public boolean deleteBoleById(Long blogId);


    public boolean deletedBlogByIdList(List<Long> blogIdList);

    public boolean updateBlogById(IBaseBlog baseBlog);

    public <T extends IBaseBlog> List<T> selectBlogListByUserId(Long userId);

    <T extends IBaseBlog> T selectBlogById(Long blogId);
}
