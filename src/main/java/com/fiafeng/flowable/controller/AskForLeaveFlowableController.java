package com.fiafeng.flowable.controller;

import com.fiafeng.common.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/process")
public class AskForLeaveFlowableController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;


    @Autowired
    private ITokenService tokenService;


    /**
     * 员工提交请假申请
     *
     * @param employeeNo 员工工号
     * @param name       姓名
     * @param reason     原因
     * @param days       天数
     * @return
     */
    @GetMapping("/employeeSubmit")
    public String employeeSubmitAskForLeave(
            @RequestParam(value = "employeeNo") String employeeNo,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "reason") String reason,
            @RequestParam(value = "days") Integer days) {
        HashMap<String, Object> map = new HashMap<>();
        /**
         * 员工编号字段来自于配置文件
         */
        map.put("employeeNo", employeeNo);
        map.put("name", name);
        map.put("reason", reason);
        map.put("days", days);
        map.put("leaderNo", "002");
        String userId = String.valueOf(tokenService.getLoginUser().getUser().getId());
        String username = tokenService.getLoginUser().getUser().getUsername();
        map.put("userId", userId);
        map.put("username", username);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("askForLeave", map);

        log.info("{},提交请假申请，流程id：{}", name, processInstance.getId());
        return "提交成功，流程id：" + processInstance.getId();
    }

    /**
     * 领导审核通过
     *
     * @param employeeNo 员工工号
     * @return
     */
    @GetMapping("/leaderExaminePass")
    public String leaderExamine(@RequestParam(value = "employeeNo") String employeeNo) {
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(employeeNo).orderByTaskId().desc().list();
        if (null == taskList) {
            throw new RuntimeException("当前员工没有任何申请");
        }
        for (Task task : taskList) {
            if (task == null) {
                log.info("任务不存在 ID：{}；", task.getId());
                continue;
            }
            log.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            Map<String, Object> map = new HashMap<>();
            /**
             *      key：配置文件中的下个处理流程id
             *      value：默认老板工号为001
             */
            map.put("bossNo", "001");
            /**
             *      key：指定配置文件中的条件判断id
             *      value：指定配置文件中的审核条件
             */

            map.put("outcome", "通过");
            taskService.complete(task.getId(), map);
        }
        return "领导审核通过";
    }


    /**
     * 老板审核通过
     *
     * @param leaderNo 领导工号
     * @return
     */
    @GetMapping("/bossExaminePass")
    public String bossExamine(@RequestParam(value = "leaderNo") String leaderNo) {
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(leaderNo).orderByTaskId().desc().list();
        if (null == taskList) {
            throw new RuntimeException("当前员工没有任何申请");
        }
        for (Task task : taskList) {
            if (task == null) {
                continue;
            }
            log.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            Map<String, Object> map = new HashMap<>();
            /**
             *     老板是最后的审批人   无需指定下个流程
             */
            map.put("boss", "001");
            /**
             *      key：指定配置文件中的条件判断id
             *      value：指定配置文件中的审核条件
             */
            map.put("outcome", "通过");
            taskService.complete(task.getId(), map);
        }
        return "领导审核通过";
    }

    /**
     * 驳回
     *
     * @param employeeNo 员工工号
     * @return
     */
    @GetMapping("/reject")
    public String reject(@RequestParam(value = "employeeNo") String employeeNo) {
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(employeeNo).orderByTaskId().desc().list();
        if (null == taskList) {
            throw new RuntimeException("当前员工没有任何申请");
        }
        for (Task task : taskList) {
            if (task == null) {
                continue;
            }
            log.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            Map<String, Object> map = new HashMap<>();
            /**
             *      key：指定配置文件中的领导id
             *      value：指定配置文件中的审核条件
             */
            map.put("outcome", "驳回");
            taskService.complete(task.getId(), map);
        }
        return "申请被驳回";
    }


}
