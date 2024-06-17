package com.fiafeng.comment.pojo.dto;

import com.fiafeng.comment.pojo.BaseComment;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Data
@ToString
public class CommentDTO {

    /**
     * 主键
     */
    public Long id;

    /**
     * 评论发布人的用户名Id
     */
    public Long senderUserId;

    /**
     * 评论发布人的用户名
     */
    public String senderName;

    /**
     * 回复人的用户名
     */
    public String receiverName;

    /**
     * 回复人的用户名Id
     */
    public String receiverUserId;


    /**
     * 回复的评论id
     */
    public String parentId;


    /**
     * 回复评论内容
     */
    public String comment;


    /**
     * 评论的对象类型
     */
    public String commentObjectType;

    /**
     * 评论对象的id
     */
    public String commentObjectId;

    /**
     * 评论时间
     */
    public String createDate;

    /**
     * 层级
     */
    public Integer layer = 0;


    /**
     * 子评论集合
     */
    List<CommentDTO> children = new ArrayList<>();

    /**
     * 子评论总数
     */
    Integer childrenCount;


    public static CommentDTO convertDto(BaseComment baseComment) {
        return convertDto(baseComment, 0);
    }

    public static CommentDTO convertDto(BaseComment baseComment, Integer layer) {
        CommentDTO dto = new CommentDTO();
        dto.setComment(baseComment.getCommentContent());
        dto.setId(baseComment.getId());
        dto.setCommentObjectId(baseComment.getCommentObjectId());
        dto.setCommentObjectType(baseComment.getCommentObjectType());
        dto.setParentId(baseComment.getParentId());
        dto.setReceiverName(baseComment.getReceiverName());
        dto.setReceiverUserId(baseComment.getReceiverUserId());
        dto.setSenderName(baseComment.getSenderName());
        dto.setSenderUserId(baseComment.getSenderUserId());
        dto.setCreateDate(baseComment.getCreateDate());
        dto.setLayer(layer);
        return dto;
    }

    public static List<CommentDTO> convertDto(List<BaseComment> baseCommentList, Integer layer) {
        List<CommentDTO> dtoList = new ArrayList<>();
        for (BaseComment baseComment : baseCommentList) {
            CommentDTO dto = CommentDTO.convertDto(baseComment);
            dto.setLayer(layer);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
