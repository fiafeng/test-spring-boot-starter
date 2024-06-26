package com.fiafeng.comment.init;

import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.common.init.ApplicationInitAfter;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentApplicationInitAfter implements ApplicationInitAfter {

    @Autowired
    ConnectionPoolServiceImpl connectionPoolService;

    @Override
    public void init() {
        connectionPoolService.checkMysqlTableIsExist("base_comment", BaseComment.class);

    }
}
