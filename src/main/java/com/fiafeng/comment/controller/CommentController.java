package com.fiafeng.comment.controller;

import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.comment.pojo.Interface.IBaseComment;
import com.fiafeng.comment.pojo.dto.CommentDTO;
import com.fiafeng.comment.service.Impl.mybatis.CommentMybatisServiceImpl;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.service.IUserService;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.validation.annotation.ValidationAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/comment")
public class CommentController {


    @Autowired
    ITokenService tokenService;

    @Autowired
    CommentMybatisServiceImpl commentService;



    @PostMapping("/send")
    @ValidationAnnotation
    public AjaxResult sendCommentPost(@RequestBody BaseComment baseComment) {
        // 如果是回复的评论，检查回复评论名字和Id
        if (!"-1".equals(baseComment.getParentId()) && (StringUtils.strIsEmpty(baseComment.getReceiverName()) && StringUtils.strIsEmpty(baseComment.getReceiverUserId()))) {
            throw new ServiceException("回复评论参数不正确");
        }
        IBaseUserInfo loginUserInfo = tokenService.getLoginUser();
        baseComment.setSenderUserId(loginUserInfo.getUser().getId());
        baseComment.setSenderName(loginUserInfo.getUser().getUsername());
        commentService.sendComment(baseComment);
        return AjaxResult.success();
    }

    @PostMapping("/checkUserId/deletedById")
    public AjaxResult deletedComment(@RequestBody BaseComment baseComment) {
        if (ObjectUtils.isNull(baseComment.getId()) || StringUtils.strIsEmpty(baseComment.getId() + "")){
            throw new ServiceException("删除，id不允许为空");
        }
        IBaseComment queryCommentById = commentService.queryCommentById(baseComment.getId());
        if (!Objects.equals(queryCommentById.getSenderUserId(), baseComment.getSenderUserId())){
            throw new ServiceException("删除时发现传递数据出现错误！！！");
        }

        IBaseUserInfo loginUserInfo = tokenService.getLoginUser();
        if (!Objects.equals(loginUserInfo.getUser().getId(), queryCommentById.getSenderUserId())){
            throw new ServiceException("当前评论不是你发送的，删除失败！！！");
        }

        commentService.deletedById(baseComment.getId());
        return AjaxResult.success();
    }

    @Autowired
    IUserService userService;


    @GetMapping("/send")
    @ValidationAnnotation
    public AjaxResult sendCommentGet(BaseComment baseComment) {
        // 如果是回复的评论，则消息父id不允许为-1
        if (!"-1".equals(baseComment.getParentId()) && (StringUtils.strIsEmpty(baseComment.getReceiverName()) || StringUtils.strIsEmpty(baseComment.getReceiverUserId()))) {
            throw new ServiceException("回复评论参数不正确");
        }

        if (!"-1".equals(baseComment.getParentId())){
            IBaseComment iBaseComment = commentService.queryCommentById(baseComment.getParentId());
            if (iBaseComment == null){
                throw new ServiceException("找不到回复的评论信息");
            }
        }




        if (!StringUtils.strIsEmpty(baseComment.getReceiverName()) && !StringUtils.strIsEmpty(baseComment.getReceiverUserId())){
            IBaseUser baseUser = userService.selectUserByUserId(Long.valueOf(baseComment.getReceiverUserId()));
            if (baseUser == null){
                throw new ServiceException("找不到需要回复的用户");
            }
            if (!baseComment.getReceiverName().equals(baseUser.getUsername())){
                throw new ServiceException("回复用户的信息不正确！！");
            }
        }

        IBaseUserInfo loginUserInfo = tokenService.getLoginUser();
        baseComment.setSenderUserId(loginUserInfo.getUser().getId());
        baseComment.setSenderName(loginUserInfo.getUser().getUsername());
        commentService.sendComment(baseComment);
        return AjaxResult.success();
    }

    @GetMapping("/query")
    public AjaxResult query(String commentObjectId, String commentObjectType) {
        List<CommentDTO> dtoList = new ArrayList<>();
        int total = 0;
        List<BaseComment> baseCommentParentIdList = commentService.queryCommentTreeByParentId("-1", commentObjectType, commentObjectId);
        for (BaseComment baseComment : baseCommentParentIdList) {
            List<BaseComment> baseCommentTreeByIdTree = commentService.queryCommentTreeById(String.valueOf(baseComment.id));
            total++;
            Integer count = commentService.queryReplyTreeByIdCount(String.valueOf(baseComment.id));
            total += count;
            CommentDTO dto = CommentDTO.convertDto(baseComment);
            dto.setChildrenCount(count);
            dto.setChildren(CommentDTO.convertDto(baseCommentTreeByIdTree, dto.layer + 1));
            dtoList.add(dto);
        }
        AjaxResult success = AjaxResult.success(dtoList);
        success.put("total", total);
        return success;
    }


    @GetMapping("/queryByTree")
    public AjaxResult queryByTree(String commentObjectId, String commentObjectType) {
        List<CommentDTO> dtoList = new ArrayList<>(); // 最终返回值
        List<CommentDTO> dtoList1 = new ArrayList<>();  // 存储已经遍历过的元素
        List<BaseComment> baseCommentParentIdList = commentService.queryCommentTreeByParentId("-1", commentObjectType, commentObjectId);
        for (BaseComment baseComment : baseCommentParentIdList) {
            List<BaseComment> baseCommentTreeByIdTree = commentService.queryCommentTreeById(String.valueOf(baseComment.id));
            CommentDTO dto = CommentDTO.convertDto(baseComment);
            dtoList.add(dto);
            dtoList1.add(dto);

            for (BaseComment comment : baseCommentTreeByIdTree) {
                CommentDTO treeDto = CommentDTO.convertDto(comment);
                String parentId = treeDto.getParentId();
                for (int j = 0; j < dtoList.size(); j++) {
                    CommentDTO commentDTO = dtoList1.get(j);
                    String id = String.valueOf(commentDTO.getId());
                    if (parentId.equalsIgnoreCase(id)) {
                        treeDto.setLayer(commentDTO.getLayer() + 1);
                        dtoList1.add(treeDto);
                        commentDTO.getChildren().add(treeDto);
                        break;
                    }
                }
            }
        }


        return AjaxResult.success(dtoList);
    }

    @GetMapping("/queryChildren")
    public AjaxResult queryChildren(String commentId) {
        if (StringUtils.strIsEmpty(commentId)) {
            throw new ServiceException("commentId，参数不允许为空");
        }

        List<CommentDTO> dtoList;
        IBaseComment baseComment = commentService.queryCommentById(commentId);
        if (baseComment == null) {
            throw new ServiceException("找不到主评论");
        }

        List<BaseComment> baseCommentTreeByIdTree = commentService.queryCommentTreeById(commentId);
        dtoList = CommentDTO.convertDto(baseCommentTreeByIdTree, 1);

        AjaxResult ajaxResult = AjaxResult.success(dtoList);
        Integer count = commentService.queryReplyTreeByIdCount(String.valueOf(commentId));
        ajaxResult.put("count", count);
        return ajaxResult;
    }
}
