package com.fiafeng.comment.controller;

import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.comment.pojo.dto.CommentDTO;
import com.fiafeng.comment.service.Impl.mybatis.CommentMybatisServiceImpl;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.validation.annotation.ValidationAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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


    @GetMapping("/send")
    @ValidationAnnotation
    public AjaxResult sendCommentGet(BaseComment baseComment) {
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

    @GetMapping("/query")
    public AjaxResult query(String commentObjectId, String commentObjectType) {
        List<CommentDTO> dtoList = new ArrayList<>();
        int count = 0;
        List<BaseComment> baseCommentParentIdList = commentService.queryCommentTreeByParentId("-1", commentObjectType, commentObjectId);
        for (BaseComment baseComment : baseCommentParentIdList) {
            List<BaseComment> baseCommentTreeByIdTree = commentService.queryCommentTreeById(String.valueOf(baseComment.id));
            count += commentService.queryReplyTreeByIdCount(String.valueOf(baseComment.id));
            CommentDTO dto = CommentDTO.convertDto(baseComment);
            dto.setChildrenCount(count);
            dto.setChildren(CommentDTO.convertDto(baseCommentTreeByIdTree, dto.layer + 1));
            dtoList.add(dto);
        }
        AjaxResult success = AjaxResult.success(dtoList);
        success.put("total", count);
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

            for (int i = 0; i < baseCommentTreeByIdTree.size(); i++) {
                CommentDTO treeDto = CommentDTO.convertDto(baseCommentTreeByIdTree.get(i));
                String parentId = treeDto.getParentId();
                for (int j = 0; j < dtoList.size(); j++) {
                    CommentDTO commentDTO = dtoList1.get(j);
                    String id = String.valueOf(commentDTO.getId());
                    if (parentId.equalsIgnoreCase(id)) {
                        treeDto.setLayer(commentDTO.getLayer() +1 );
                        dtoList1.add(treeDto);
                        commentDTO.getChildren().add(treeDto);
                        break;
                    }
                }
            }
        }


        for (CommentDTO commentDTO : dtoList) {
            ddd(commentDTO, String.valueOf(commentDTO.getId()));
        }

        return AjaxResult.success(dtoList);
    }

    public void ddd(CommentDTO dto, String string) {
        if (dto.getChildren().isEmpty()) {
            System.out.println(string);
            return;
        }
        for (CommentDTO child : dto.getChildren()) {
            ddd(child, string + "," + child.getId());
        }
    }


    public void xx(List<BaseComment> baseCommentList, List<CommentDTO> dtoList) {
        for (int i = 0; i < baseCommentList.size(); i++) {
            CommentDTO treeDto = CommentDTO.convertDto(baseCommentList.get(i));
            for (int j = 0; j < dtoList.size(); j++) {
                CommentDTO commentDTO = dtoList.get(i);
                if (treeDto.getParentId().equalsIgnoreCase(String.valueOf(commentDTO.getId()))) {
                    commentDTO.getChildren().add(treeDto);
                }
            }
        }
    }


    @GetMapping("/queryChildren")
    public AjaxResult queryChildren(String commentId) {
        if (StringUtils.strIsEmpty(commentId)) {
            throw new ServiceException("commentId，参数不允许为空");
        }

        List<CommentDTO> dtoList;
        BaseComment baseComment = commentService.queryCommentById(commentId);
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
