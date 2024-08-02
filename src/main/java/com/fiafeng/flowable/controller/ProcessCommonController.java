package com.fiafeng.flowable.controller;

import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.flowable.service.Impl.FlowableImageServiceImpl;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/process/common")
public class ProcessCommonController {


    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private FlowableImageServiceImpl imageService;

    @Autowired
    private ITokenService tokenService;


    /**
     * 生成流程图
     *
     * @param processId 任务ID
     */
    @GetMapping(value = "/processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
        byte[] buf = new byte[1024];
        int legth = 0;
        try (InputStream in = imageService.generateImageByProcInstId(processId); OutputStream out = httpServletResponse.getOutputStream()) {
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        }
    }

    @GetMapping(value = "/getRuntimeTaskList")
    public AjaxResult getRuntimeTaskList(){

        String userId = String.valueOf(tokenService.getLoginUser().getUser().getId());
        String username = tokenService.getLoginUser().getUser().getUsername();
        List<Execution> executionList = runtimeService.createExecutionQuery().processVariableValueEquals("userId", userId).list();

        return AjaxResult.success();
    }
}
