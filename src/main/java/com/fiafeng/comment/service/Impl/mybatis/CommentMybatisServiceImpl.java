package com.fiafeng.comment.service.Impl.mybatis;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fiafeng.comment.dao.mybatis.CommentMybatisDao;
import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentMybatisServiceImpl extends ServiceImpl<CommentMybatisDao, BaseComment> {

    public static String commentObjectType = "视频";
    public static String commentObjectId = "4";


    public List<BaseComment> queryCommentTreeByParentId(String parentId) {
        return queryCommentTreeByParentId(parentId, commentObjectType, commentObjectId);
    }

    public BaseComment queryCommentById(String commentId) {
        return baseMapper.selectById(commentId);
    }

    public List<BaseComment> queryCommentTreeByParentId(String parentId, String commentObjectType, String commentObjectId) {
        LambdaQueryWrapper<BaseComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.strNotEmpty(parentId), BaseComment::getParentId, parentId);
        queryWrapper.eq(StringUtils.strNotEmpty(commentObjectType), BaseComment::getCommentObjectType, commentObjectType);
        queryWrapper.eq(StringUtils.strNotEmpty(commentObjectId), BaseComment::getCommentObjectId, commentObjectId);
        return baseMapper.selectList(queryWrapper);
    }


    public List<BaseComment> queryCommentTreeBySendUserId(String SenderUserId) {
        LambdaQueryWrapper<BaseComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.strNotEmpty(SenderUserId), BaseComment::getSenderUserId, SenderUserId);
        return baseMapper.selectList(queryWrapper);
    }


    public List<BaseComment> queryCommentTreeById(String id){
        return baseMapper.queryReplyTreeById(id);
    }

    public Integer queryReplyTreeByIdCount(String id){
        return baseMapper.queryReplyTreeByIdCount(id);
    }



    public boolean sendComment(BaseComment baseComment){
        try {
            if (!baseComment.getParentId().equals("-1")){
                BaseComment selectOne = baseMapper.selectById(baseComment.getParentId());
                if (selectOne == null){
                    throw new ServiceException("找不到要回复的评论");
                }
                if (!baseComment.getReceiverName().equals(selectOne.getReceiverName())){
                    baseComment.setReceiverName(selectOne.getSenderName());
                }
            }
            baseMapper.insert(baseComment);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
