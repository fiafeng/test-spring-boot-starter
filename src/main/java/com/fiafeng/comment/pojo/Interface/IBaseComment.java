package com.fiafeng.comment.pojo.Interface;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;

import java.util.Date;

public interface IBaseComment extends IBasePojo {

     Long getId();

     void setId(Long id);

     Long getSenderUserId();

     void setSenderUserId(Long senderUserId);

     String getSenderName();

     void setSenderName(String senderName);

     String getSenderAvatar();

     void setSenderAvatar(String senderAvatar);

     String getReceiverName();

     void setReceiverName(String receiverName);

     String getReceiverUserId();

     void setReceiverUserId(String receiverUserId);

     String getReceiverUserAvatar() ;

     void setReceiverUserAvatar(String receiverUserAvatar);

     String getParentId();

     void setParentId(String parentId);

     String getCommentContent();

     void setCommentContent(String commentContent);

     String getCommentObjectType();

     void setCommentObjectType(String commentObjectType);

     String getCommentObjectId();

     void setCommentObjectId(String commentObjectId);

     void setCreateDate(Date createDate);
}
