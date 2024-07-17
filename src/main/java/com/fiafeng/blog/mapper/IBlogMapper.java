package com.fiafeng.blog.mapper;

import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.mapper.Interface.IMapper;

import java.util.List;

public interface IBlogMapper extends IMapper {

     /**
      * 添加博客
      * @param baseBlog 博客
      * @return 是否添加成功
      */
     int insertBlog(IBaseBlog baseBlog);

     /**
      * 根据博客Id删除博客
      * @param blogId 博客id
      * @return 删除数量
      */
     int deleteBoleById(Long blogId);


     /**
      * 根据博客Id列表批量删除博客
      * @param blogIdList 博客id列表
      * @return 删除数量
      */
     int deletedBlogByIdList(List<Long> blogIdList);

     /**
      * 更新博客，根据id
      * @param baseBlog 博客实体类
      * @return 更新数量
      */
     int updateBlogById(IBaseBlog baseBlog);

     /**
      * 查询用户id为userId的用户发的所有博客
      * @param userId 用户Id
      * @return 用户发的所有博客
      */
     List<IBaseBlog> selectBlogListByUserId(Long userId);

     /**
      * 根据博客id查询对应的博客具体内容
      * @param blogId 博客Id
      * @return 博客具体内容
      * @param <T> 博客实体类
      */
    <T extends IBaseBlog> T selectBlogById(Long blogId);
}
