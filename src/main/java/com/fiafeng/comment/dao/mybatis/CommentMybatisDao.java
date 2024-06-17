package com.fiafeng.comment.dao.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.mybatis.annotation.PageAnnotation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMybatisDao extends BaseMapper<BaseComment> {

    @Select(
            "SELECT *" +
                    " FROM (SELECT t1.*," +
                    "             IF" +
                    "             (FIND_IN_SET(parent_id, @parent_ids) > 0, @parent_ids := CONCAT(@parent_ids, ',', id), '0') AS ischild" +
                    "      FROM (SELECT * FROM base_comment AS t WHERE t.deleted = 0 ORDER BY t.id ASC) t1," +
                    "           (SELECT @parent_ids := #{id}) t2) t3" +
                    " WHERE ischild != '0' "
    )
    @PageAnnotation(pageSize = 3)
    List<BaseComment> queryReplyTreeById(String id);

    @Select(
            "SELECT t3.id" +
                    " FROM (SELECT t1.*," +
                    "             IF" +
                    "             (FIND_IN_SET(parent_id, @parent_ids) > 0, @parent_ids := CONCAT(@parent_ids, ',', id), '0') AS ischild" +
                    "      FROM (SELECT * FROM base_comment AS t WHERE t.deleted = 0 ORDER BY t.id ASC) t1," +
                    "           (SELECT @parent_ids := #{id}) t2) t3" +
                    " WHERE ischild != '0' "
    )
    @PageAnnotation(pageSize = 3)
    List<Long> queryReplyTreeIdById(String id);


    @Select(
            "SELECT count(*)" +
                    " FROM (SELECT t1.*," +
                    "             IF" +
                    "             (FIND_IN_SET(parent_id, @parent_ids) > 0, @parent_ids := CONCAT(@parent_ids, ',', id), '0') AS ischild" +
                    "      FROM (SELECT * FROM base_comment AS t WHERE t.deleted = 0 ORDER BY t.id ASC) t1," +
                    "           (SELECT @parent_ids := #{id}) t2) t3" +
                    " WHERE ischild != '0' "
    )
    Integer queryReplyTreeByIdCount(String id);

}
