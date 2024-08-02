package com.fiafeng.flowable.service.Impl;

import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.flowable.service.IFlowableImageService;
import com.fiafeng.flowable.service.Impl.imgae.CustomProcessDiagramGenerator;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class FlowableImageServiceImpl implements IFlowableImageService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 通过流程实例ID获取历史流程实例
     *
     * @param procInstId 流程实例id
     * @return 历史流程实例
     */
    public HistoricProcessInstance getHistoricProcInst(String procInstId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
    }

    /**
     * 通过流程实例ID获取流程中已经执行的节点，按照执行先后顺序排序
     *
     * @param procInstId 流程实例id
     * @return 按照执行先后顺序排序的历史流程实例列表
     */
    public List<HistoricActivityInstance> getHistoricActivityInstAsc(String procInstId) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstId)
                .orderByHistoricActivityInstanceId().asc().list();
    }

    /**
     * 通过流程实例ID获取流程中正在执行的节点
     *
     * @param procInstId 流程实例id
     * @return 正在执行的节点
     */
    public List<Execution> getRunningActivityInst(String procInstId) {
        return runtimeService.createExecutionQuery().processInstanceId(procInstId).list();
    }

    /**
     * 通过流程实例ID获取已经完成的历史流程实例
     *
     * @param procInstId 流程实例id
     * @return 已经完成的历史流程实例
     */
    public List<HistoricProcessInstance> getHistoricFinishedProcInst(String procInstId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).finished().list();
    }

    /**
     * 获取已流经的流程线，需要高亮显示高亮流程已发生流转的线id集合
     *
     * @param bpmnModel                    流程文件
     * @param historicActivityInstanceList 历史节点列表
     * @return 需要高亮显示高亮流程已发生流转的线id集合
     */
    public List<String> getHighLightedFlows(BpmnModel bpmnModel,
                                            List<HistoricActivityInstance> historicActivityInstanceList) {
        // 已流经的流程线，需要高亮显示
        List<String> highLightedFlowIdList = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> allHistoricActivityNodeList = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstanceList = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            // 获取流程节点
            FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(historicActivityInstance
                    .getActivityId(), true);

            if (flowElement instanceof FlowNode) {
                FlowNode flowNode = (FlowNode) flowElement;
                allHistoricActivityNodeList.add(flowNode);
                // 结束时间不为空，当前节点则已经完成
                if (historicActivityInstance.getEndTime() != null) {
                    finishedActivityInstanceList.add(historicActivityInstance);
                }
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode;
        HistoricActivityInstance currentActivityInstance;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (int k = 0; k < finishedActivityInstanceList.size(); k++) {
            currentActivityInstance = finishedActivityInstanceList.get(k);
            // 获得当前活动对应的节点信息及outgoingFlows信息
            FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(currentActivityInstance
                    .getActivityId(), true);
            if (flowElement instanceof FlowNode) {
                currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance
                        .getActivityId(), true);
            }
            // 当前节点的所有流出线
            List<SequenceFlow> outgoingFlowList = null;
            if (currentFlowNode != null) {
                outgoingFlowList = currentFlowNode.getOutgoingFlows();
            }

            /*
              遍历outgoingFlows并找到已流转的 满足如下条件认为已流转：
              1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
              2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
              (第2点有问题，有过驳回的，会只绘制驳回的流程线，通过走向下一级的流程线没有高亮显示)
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(
                    currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                if (outgoingFlowList != null) {
                    for (SequenceFlow outgoingFlow : outgoingFlowList) {
                        // 获取当前节点流程线对应的下级节点
                        targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(outgoingFlow.getTargetRef(),
                                true);
                        // 如果下级节点包含在所有历史节点中，则将当前节点的流出线高亮显示
                        if (allHistoricActivityNodeList.contains(targetFlowNode)) {
                            highLightedFlowIdList.add(outgoingFlow.getId());
                        }
                    }
                }
            } else {
                /*
                  2、当前节点不是并行网关或兼容网关
                  【已解决-问题】如果当前节点有驳回功能，驳回到申请节点，
                  则因为申请节点在历史节点中，导致当前节点驳回到申请节点的流程线被高亮显示，但实际并没有进行驳回操作
                 */
                // 当前节点ID
                String currentActivityId = currentActivityInstance.getActivityId();
                int size = historicActivityInstanceList.size();
                boolean ifStartFind = false;
                boolean ifFound = false;
                HistoricActivityInstance historicActivityInstance;
                // 循环当前节点的所有流出线
                // 循环所有历史节点
                log.info("【开始】-匹配当前节点-ActivityId=【{}】需要高亮显示的流出线", currentActivityId);
                log.info("循环历史节点");
                for (int i = 0; i < historicActivityInstanceList.size(); i++) {
                    // // 如果当前节点流程线对应的下级节点在历史节点中，则该条流程线进行高亮显示（【问题】有驳回流程线时，即使没有进行驳回操作，因为申请节点在历史节点中，也会将驳回流程线高亮显示-_-||）
                    // if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                    // Map<String, Object> map = new HashMap<>();
                    // map.put("highLightedFlowId", sequenceFlow.getId());
                    // map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                    // tempMapList.add(map);
                    // // highLightedFlowIdList.add(sequenceFlow.getId());
                    // }
                    // 历史节点
                    historicActivityInstance = historicActivityInstanceList.get(i);
                    log.info("第【{}/{}】个历史节点-ActivityId=[{}]", i + 1, size, historicActivityInstance.getActivityId());
                    // 如果循环历史节点中的id等于当前节点id，从当前历史节点继续先后查找是否有当前节点流程线等于的节点
                    // 历史节点的序号需要大于等于已完成历史节点的序号，防止驳回重审一个节点经过两次是只取第一次的流出线高亮显示，第二次的不显示
                    if (i >= k && historicActivityInstance.getActivityId().equals(currentActivityId)) {
                        log.info("第[{}]个历史节点和当前节点一致-ActivityId=[{}]", i + 1, historicActivityInstance
                                .getActivityId());
                        ifStartFind = true;
                        // 跳过当前节点继续查找下一个节点
                        continue;
                    }
                    if (ifStartFind) {
                        log.info("[开始]-循环当前节点-ActivityId=【{}】的所有流出线", currentActivityId);

                        if (outgoingFlowList != null) {
                            for (SequenceFlow sequenceFlow : outgoingFlowList) {
                                // 如果当前节点流程线对应的下级节点在其后面的历史节点中，则该条流程线进行高亮显示
                                // 【问题】
                                log.info("当前流出线的下级节点=[{}]", sequenceFlow.getTargetRef());
                                if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                                    log.info("当前节点[{}]需高亮显示的流出线=[{}]", currentActivityId, sequenceFlow.getId());
                                    highLightedFlowIdList.add(sequenceFlow.getId());
                                    // 暂时默认找到离当前节点最近的下一级节点即退出循环，否则有多条流出线时将全部被高亮显示
                                    ifFound = true;
                                    break;
                                }
                            }
                        }
                        log.info("[完成]-循环当前节点-ActivityId=【{}】的所有流出线", currentActivityId);
                    }
                    if (ifFound) {
                        // 暂时默认找到离当前节点最近的下一级节点即退出历史节点循环，否则有多条流出线时将全部被高亮显示
                        break;
                    }
                }
                log.info("【完成】-匹配当前节点-ActivityId=【{}】需要高亮显示的流出线", currentActivityId);


            }

        }
        return highLightedFlowIdList;
    }

    /**
     * 根据流程实例Id,获取实时流程图片
     *
     * @param procInstId 流程实例id
     * @return 获取实时流程图片
     * @throws ServiceException 服务异常
     */
    @Override
    public InputStream generateImageByProcInstId(String procInstId) throws ServiceException {
        if (StringUtils.strIsEmpty(procInstId)) {
            log.error("[错误]-传入的参数procInstId为空！");
            throw new ServiceException("[异常]-传入的参数procInstId为空！");
        }
        InputStream imageStream;
        try {
            // 通过流程实例ID获取历史流程实例
            HistoricProcessInstance historicProcessInstance = getHistoricProcInst(procInstId);

            if (historicProcessInstance == null) {
                throw new ServiceException("根据传入参数procInstId，找不到对应的流程实例");
            }

            // 通过流程实例ID获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = getHistoricActivityInstAsc(procInstId);


            // 将已经执行的节点ID放入高亮显示节点集合
            List<String> highLightedActivitiIdList = new ArrayList<>();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                highLightedActivitiIdList.add(historicActivityInstance.getActivityId());
                log.info("已执行的节点[{}-{}-{}-{}]", historicActivityInstance.getId(), historicActivityInstance
                        .getActivityId(), historicActivityInstance.getActivityName(), historicActivityInstance
                        .getAssignee());
            }

            // 获取流程定义Model对象
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());


            // 获取已流经的流程线，需要高亮显示高亮流程已发生流转的线id集合
            List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstanceList);


            // 定义流程画布生成器
            ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();

            ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
            imageStream = diagramGenerator.generateDiagram(
                    bpmnModel, "png",
                    highLightedActivitiIdList, highLightedFlowIds, configuration.getActivityFontName(),
                    configuration.getLabelFontName(), configuration.getAnnotationFontName(), 
                    configuration.getClassLoader(), 1.0, true);

            return imageStream;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("通过流程实例ID[{0}]获取流程图时出现异常！", e);
            throw new ServiceException("通过流程实例ID" + procInstId + "获取流程图时出现意外异常！");
        }
    }
}
