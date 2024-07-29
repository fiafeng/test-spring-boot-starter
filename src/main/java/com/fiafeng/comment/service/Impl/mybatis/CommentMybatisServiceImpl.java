package com.fiafeng.comment.service.Impl.mybatis;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fiafeng.comment.dao.mybatis.CommentMybatisDao;
import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.comment.pojo.Interface.IBaseComment;
import com.fiafeng.comment.service.ICommentService;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.utils.StringUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

@Service
@ConditionalOnClass({SqlSessionFactoryBean.class})
@BeanDefinitionOrderAnnotation()
public class CommentMybatisServiceImpl  <T extends IBaseComment>  extends ServiceImpl<CommentMybatisDao, BaseComment> implements ICommentService {

    public static String commentObjectType = "视频";
    public static String commentObjectId = "4";


    public List<T> queryCommentTreeByParentId(String parentId) {
        return queryCommentTreeByParentId(parentId, commentObjectType, commentObjectId);
    }

    public T queryCommentById(Serializable commentId) {
        return (T) baseMapper.selectById(commentId);
    }

    public List<T> queryCommentTreeByParentId(String parentId, String commentObjectType, String commentObjectId) {
        LambdaQueryWrapper<BaseComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.strNotEmpty(parentId), BaseComment::getParentId, parentId);
        queryWrapper.eq(StringUtils.strNotEmpty(commentObjectType), BaseComment::getCommentObjectType, commentObjectType);
        queryWrapper.eq(StringUtils.strNotEmpty(commentObjectId), BaseComment::getCommentObjectId, commentObjectId);
        queryWrapper.orderBy(true, true, BaseComment::getCreateDate);
        return (List<T>) baseMapper.selectList(queryWrapper);
    }


    public List<T> queryCommentTreeBySendUserId(String SenderUserId) {
        LambdaQueryWrapper<BaseComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.strNotEmpty(SenderUserId), BaseComment::getSenderUserId, SenderUserId);
        return (List<T>) baseMapper.selectList(queryWrapper);
    }


    public List<BaseComment> queryCommentTreeById(String id) {
        return baseMapper.queryReplyTreeById(id);
    }

    public Integer queryReplyTreeByIdCount(String id) {
        return baseMapper.queryReplyTreeByIdCount(id);
    }


    @Transactional
    public void deletedById(Long serializable) {
        List<Long> longList = baseMapper.queryReplyTreeIdById(String.valueOf(serializable));
        HashSet<Long> hashSet = new HashSet<>(longList);
        hashSet.add(serializable);
        int i = baseMapper.deleteBatchIds(hashSet);
        if (i != hashSet.size()) {
            throw new ServiceException("删除消息时，关联删除失败");
        }
    }


    public boolean sendComment(IBaseComment baseComment) {
        try {
            if (!baseComment.getParentId().equals("-1")) {
                BaseComment selectOne = baseMapper.selectById(baseComment.getParentId());
                if (selectOne == null) {
                    throw new ServiceException("找不到要回复的评论");
                }
                if (!baseComment.getReceiverName().equals(selectOne.getReceiverName())) {
                    baseComment.setReceiverName(selectOne.getSenderName());
                }
            }
            baseMapper.insert((BaseComment) baseComment);
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }

    }
}
