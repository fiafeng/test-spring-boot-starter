package com.fiafeng.test;

import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.comment.pojo.dto.CommentDTO;
import com.fiafeng.comment.service.Impl.mybatis.CommentMybatisServiceImpl;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.demo.TestSpringBootStarterApplication;
import com.mysql.cj.log.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest(classes = {TestSpringBootStarterApplication.class})
//@SpringBootTest
class TestSpringBootStarterApplicationTests {


    @Autowired
    IUserMapper mapper;

    @Autowired
    CommentMybatisServiceImpl commentService;

    public static String commentObjectType = "视频";
    public static String commentObjectId = "4";


    public static void main(String[] args) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -2);
        System.out.println(calendar.get(Calendar.DATE));
    }

    @Test
    void contextLoads() {
        List<CommentDTO> dtoList = new ArrayList<>();
        List<BaseComment> baseCommentParentIdList = commentService.queryCommentTreeByParentId("-1", commentObjectType, commentObjectId);
        for (BaseComment baseComment : baseCommentParentIdList) {
            Integer layer = 0;
            List<BaseComment> baseCommentTreeByIdTree = commentService.queryCommentTreeById(String.valueOf(baseComment.id));
            Integer count = commentService.queryReplyTreeByIdCount(String.valueOf(baseComment.id));

            CommentDTO baseDto = CommentDTO.convertDto(baseComment);

            for (int i = 0; i < baseCommentTreeByIdTree.size(); i++) {
                BaseComment treeComment = baseCommentTreeByIdTree.get(i);
                CommentDTO treeDto = CommentDTO.convertDto(treeComment);
                if (treeComment.getParentId().equalsIgnoreCase(String.valueOf(baseComment.getId()))) {
                    treeDto.getChildren().add(CommentDTO.convertDto(treeComment));
                } else {
                    for (int j = i - 1; j < 0; j--) {
                        BaseComment beforeComment = baseCommentTreeByIdTree.get(j);
                        CommentDTO beforeDto = CommentDTO.convertDto(beforeComment);
                        if (beforeComment.getParentId().equalsIgnoreCase(String.valueOf(treeComment.getId()))) {
                            beforeDto.getChildren().add(CommentDTO.convertDto(treeComment));
                        }else {

                        }
                    }

                }
            }
            baseDto.setChildrenCount(count);
            baseDto.setChildren(CommentDTO.convertDto(baseCommentTreeByIdTree, baseDto.layer + 1));
            dtoList.add(baseDto);
        }
        System.out.println(dtoList);
    }

}
