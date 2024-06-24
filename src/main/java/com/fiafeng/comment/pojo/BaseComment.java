package com.fiafeng.comment.pojo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fiafeng.comment.pojo.Interface.IBaseComment;
import com.fiafeng.common.annotation.AutoFiledAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@TableName("base_comment")
@Data
@Component
@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class BaseComment implements IBaseComment {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 评论发布人的头像
     */
    public String senderAvatar;

    /**
     * 回复人的用户名
     */
    public String receiverName;

    /**
     * 回复人的用户名Id
     */
    public String receiverUserId;

    /**
     * 回复人的用户头像
     */
    public String receiverUserAvatar;

    /**
     * 回复的评论id
     */
    public String parentId;


    /**
     * 回复评论内容
     */
    public String commentContent;


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
    public Date createDate = new Date();

    /**
     * 逻辑删除
     */
    @TableLogic
    @JSONField(serialize = false)
    @AutoFiledAnnotation(defaultValue = "0")
    public Integer deleted = 0;


    public String getCreateDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(this.createDate);
    }
}
