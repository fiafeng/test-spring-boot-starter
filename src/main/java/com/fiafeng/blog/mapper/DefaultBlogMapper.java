package com.fiafeng.blog.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.blog.pojo.IBaseBlog;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@BeanDefinitionOrderAnnotation()
public class DefaultBlogMapper implements IBlogMapper {

    private volatile ConcurrentHashMap<Long, IBaseBlog> blogMap;

    public ConcurrentHashMap<Long, IBaseBlog> getBlogMap() {
        if (blogMap == null) {
            synchronized (this) {
                if (blogMap == null) {
                    blogMap = new ConcurrentHashMap<>();
                }
            }
        }
        return blogMap;
    }

    AtomicLong atomicLong = new AtomicLong(1);

    /**
     * 添加博客
     *
     * @param baseBlog 博客
     * @return 是否添加成功
     */
    @Override
    public int insertBlog(IBaseBlog baseBlog) {
        if (baseBlog == null) {
            throw new ServiceException("参数不允许为空");
        }
        try {
            long andIncrement = atomicLong.getAndIncrement();
            baseBlog.setId(andIncrement);
            getBlogMap().put(andIncrement, JSONObject.from(baseBlog).toJavaObject(IBaseBlog.class));
        } catch (Exception e) {
            throw new ServiceException("参数不允许为空");
        }
        return 1;
    }

    /**
     * 根据博客Id删除博客
     *
     * @param blogId 博客id
     * @return 删除的数量
     */
    @Override
    public int deleteBoleById(Long blogId) {
        return getBlogMap().remove(blogId) != null ? 1 : 0;
    }

    /**
     * 根据博客Id列表批量删除博客
     *
     * @param blogIdList 博客id列表
     * @return 删除数量
     */
    @Override
    public int deletedBlogByIdList(List<Long> blogIdList) {
        for (Long blogId : blogIdList) {
            getBlogMap().remove(blogId);
        }
        return blogIdList.size();
    }

    /**
     * 更新博客，根据id
     *
     * @param baseBlog 博客实体类
     * @return 更新数量
     */
    @Override
    public int updateBlogById(IBaseBlog baseBlog) {
        if (baseBlog.getId() == null) {
            throw new ServiceException("id不允许为空");
        }
        if (!getBlogMap().containsKey(baseBlog.getId())) {
            throw new ServiceException("当前id不存在于数据库");
        } else {
            getBlogMap().put(baseBlog.getId(), baseBlog);
        }

        return 1;
    }

    /**
     * 查询用户id为userId的用户发的所有博客
     *
     * @param userId 用户Id
     * @return 用户发的所有博客
     */
    @Override
    public List<IBaseBlog> selectBlogListByUserId(Long userId) {
        List<IBaseBlog> iBaseRoleList = new ArrayList<>();
        for (HashMap.Entry<Long, IBaseBlog> entry : getBlogMap().entrySet()) {
            if (entry.getValue().getUserId().equals(userId)) {
                iBaseRoleList.add(JSONObject.from(entry.getValue()).toJavaObject(IBaseBlog.class));
            }
        }
        return iBaseRoleList;
    }

    /**
     * 根据博客id查询对应的博客具体内容
     *
     * @param blogId 博客Id
     * @return 博客具体内容
     */
    @Override
    public <T extends IBaseBlog> T selectBlogById(Long blogId) {
        return (T) getBlogMap().get(blogId);
    }
}
