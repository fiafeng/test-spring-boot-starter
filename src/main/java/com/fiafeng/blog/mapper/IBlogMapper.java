package com.fiafeng.blog.mapper;

import com.fiafeng.blog.pojo.IBaseBlog;

import java.util.List;

public interface IBlogMapper {

     boolean insertBlog(IBaseBlog baseBlog);

     boolean deleteBoleById(Long blogId);


     boolean deletedBlogByIdList(List<Long> blogIdList);

     boolean updateBlogById(IBaseBlog baseBlog);

     <T extends IBaseBlog> List<T> selectBlogListByUserId(Long userId);

    <T extends IBaseBlog> T selectBlogById(Long blogId);
}
