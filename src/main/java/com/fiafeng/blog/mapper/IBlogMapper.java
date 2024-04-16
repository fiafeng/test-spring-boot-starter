package com.fiafeng.blog.mapper;

import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.mapper.Interface.IMapper;

import java.util.List;

public interface IBlogMapper extends IMapper {

     boolean insertBlog(IBaseBlog baseBlog);

     boolean deleteBoleById(Long blogId);


     boolean deletedBlogByIdList(List<Long> blogIdList);

     boolean updateBlogById(IBaseBlog baseBlog);

     <T extends IBaseBlog> List<T> selectBlogListByUserId(Long userId);

    <T extends IBaseBlog> T selectBlogById(Long blogId);
}
