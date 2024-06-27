package com.fiafeng.comment.init;

import com.fiafeng.comment.pojo.BaseComment;
import com.fiafeng.common.init.ApplicationInitAfter;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentApplicationInitAfter implements ApplicationInitAfter {



    @Override
    public void init() {
        ConnectionPoolServiceImpl connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);

        connectionPoolService.checkMysqlTableIsExist("base_comment", BaseComment.class);

    }
}
