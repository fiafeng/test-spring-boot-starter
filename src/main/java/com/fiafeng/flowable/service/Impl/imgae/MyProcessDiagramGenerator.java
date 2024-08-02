package com.fiafeng.flowable.service.Impl.imgae;


import lombok.Getter;
import org.flowable.bpmn.model.Event;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.*;


/**
 * 生成图像
 *
 * @author liuxz
 */
@Getter
public class MyProcessDiagramGenerator implements ProcessDiagramGenerator {
    protected static final Logger logger = LoggerFactory.getLogger(MyProcessDiagramGenerator.class);


    protected Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions = new HashMap<>();
    protected Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions = new HashMap<>();

    public MyProcessDiagramGenerator() {
        this(1.0);
    }

    // The instructions on how to draw a certain construct is
    // created statically and stored in a map for performance.
    public MyProcessDiagramGenerator(final double scaleFactor) {
        // start event
        activityDrawInstructions.put(StartEvent.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            StartEvent startEvent = (StartEvent) flowNode;
            if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty()) {
                EventDefinition eventDefinition = startEvent.getEventDefinitions().get(0);
                if (eventDefinition instanceof TimerEventDefinition) {
                    processDiagramCanvas.drawTimerStartEvent(graphicInfo, scaleFactor);
                } else if (eventDefinition instanceof ErrorEventDefinition) {
                    processDiagramCanvas.drawErrorStartEvent(graphicInfo, scaleFactor);
                } else if (eventDefinition instanceof SignalEventDefinition) {
                    processDiagramCanvas.drawSignalStartEvent(graphicInfo, scaleFactor);
                } else if (eventDefinition instanceof MessageEventDefinition) {
                    processDiagramCanvas.drawMessageStartEvent(graphicInfo, scaleFactor);
                } else {
                    processDiagramCanvas.drawNoneStartEvent(graphicInfo);
                }
            } else {
                processDiagramCanvas.drawNoneStartEvent(graphicInfo);
            }
        });

        // signal catch
        activityDrawInstructions.put(IntermediateCatchEvent.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) flowNode;
            if (intermediateCatchEvent.getEventDefinitions() != null && !intermediateCatchEvent
                    .getEventDefinitions().isEmpty()) {
                if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                    processDiagramCanvas.drawCatchingSignalEvent(flowNode.getName(), graphicInfo, true,
                            scaleFactor);
                } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                    processDiagramCanvas.drawCatchingTimerEvent(flowNode.getName(), graphicInfo, true, scaleFactor);
                } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                    processDiagramCanvas.drawCatchingMessageEvent(flowNode.getName(), graphicInfo, true,
                            scaleFactor);
                }
            }
        });

        // signal throw
        activityDrawInstructions.put(ThrowEvent.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            ThrowEvent throwEvent = (ThrowEvent) flowNode;
            if (throwEvent.getEventDefinitions() != null && !throwEvent.getEventDefinitions().isEmpty()) {
                if (throwEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                    processDiagramCanvas.drawThrowingSignalEvent(graphicInfo, scaleFactor);
                } else if (throwEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                    processDiagramCanvas.drawThrowingCompensateEvent(graphicInfo, scaleFactor);
                } else {
                    processDiagramCanvas.drawThrowingNoneEvent(graphicInfo, scaleFactor);
                }
            } else {
                processDiagramCanvas.drawThrowingNoneEvent(graphicInfo, scaleFactor);
            }
        });

        // end event
        activityDrawInstructions.put(EndEvent.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            EndEvent endEvent = (EndEvent) flowNode;
            if (endEvent.getEventDefinitions() != null && !endEvent.getEventDefinitions().isEmpty()) {
                if (endEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                    processDiagramCanvas.drawErrorEndEvent(flowNode.getName(), graphicInfo, scaleFactor);
                } else {
                    processDiagramCanvas.drawNoneEndEvent(graphicInfo, scaleFactor);
                }
            } else {
                processDiagramCanvas.drawNoneEndEvent(graphicInfo, scaleFactor);
            }
        });

        // task
        activityDrawInstructions.put(Task.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawTask(flowNode.getName(), graphicInfo);
        });

        // user task
        activityDrawInstructions.put(UserTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawUserTask(flowNode.getName(), graphicInfo, scaleFactor);
        });

        // script task
        activityDrawInstructions.put(ScriptTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawScriptTask(flowNode.getName(), graphicInfo, scaleFactor);
        });

        // service task
        activityDrawInstructions.put(ServiceTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            ServiceTask serviceTask = (ServiceTask) flowNode;
            if ("camel".equalsIgnoreCase(serviceTask.getType())) {
                processDiagramCanvas.drawCamelTask(serviceTask.getName(), graphicInfo, scaleFactor);
            } else if ("mule".equalsIgnoreCase(serviceTask.getType())) {
                processDiagramCanvas.drawMuleTask(serviceTask.getName(), graphicInfo, scaleFactor);
            } else {
                processDiagramCanvas.drawServiceTask(serviceTask.getName(), graphicInfo, scaleFactor);
            }
        });

        // receive task
        activityDrawInstructions.put(ReceiveTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawReceiveTask(flowNode.getName(), graphicInfo, scaleFactor);
        });

        // send task
        activityDrawInstructions.put(SendTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawSendTask(flowNode.getName(), graphicInfo, scaleFactor);
        });

        // manual task
        activityDrawInstructions.put(ManualTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawManualTask(flowNode.getName(), graphicInfo, scaleFactor);
        });

        // businessRuleTask task
        activityDrawInstructions.put(BusinessRuleTask.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawBusinessRuleTask(flowNode.getName(), graphicInfo, scaleFactor);
        });

        // exclusive gateway
        activityDrawInstructions.put(ExclusiveGateway.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawExclusiveGateway(graphicInfo, scaleFactor);
        });

        // inclusive gateway
        activityDrawInstructions.put(InclusiveGateway.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawInclusiveGateway(graphicInfo, scaleFactor);
        });

        // parallel gateway
        activityDrawInstructions.put(ParallelGateway.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawParallelGateway(graphicInfo, scaleFactor);
        });

        // event based gateway
        activityDrawInstructions.put(EventGateway.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawEventBasedGateway(graphicInfo, scaleFactor);
        });

        // Boundary timer
        activityDrawInstructions.put(BoundaryEvent.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            BoundaryEvent boundaryEvent = (BoundaryEvent) flowNode;
            if (boundaryEvent.getEventDefinitions() != null && !boundaryEvent.getEventDefinitions().isEmpty()) {
                if (boundaryEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {

                    processDiagramCanvas.drawCatchingTimerEvent(flowNode.getName(), graphicInfo, boundaryEvent
                            .isCancelActivity(), scaleFactor);

                } else if (boundaryEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {

                    processDiagramCanvas.drawCatchingErrorEvent(graphicInfo, boundaryEvent.isCancelActivity(),
                            scaleFactor);

                } else if (boundaryEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                    processDiagramCanvas.drawCatchingSignalEvent(flowNode.getName(), graphicInfo, boundaryEvent
                            .isCancelActivity(), scaleFactor);

                } else if (boundaryEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                    processDiagramCanvas.drawCatchingMessageEvent(flowNode.getName(), graphicInfo, boundaryEvent
                            .isCancelActivity(), scaleFactor);

                } else if (boundaryEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                    processDiagramCanvas.drawCatchingCompensateEvent(graphicInfo, boundaryEvent.isCancelActivity(),
                            scaleFactor);
                }
            }

        });

        // subprocess
        activityDrawInstructions.put(SubProcess.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                processDiagramCanvas.drawCollapsedSubProcess(flowNode.getName(), graphicInfo, false);
            } else {
                processDiagramCanvas.drawExpandedSubProcess(flowNode.getName(), graphicInfo, false, scaleFactor);
            }
        });

        // Event subprocess
        activityDrawInstructions.put(EventSubProcess.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                processDiagramCanvas.drawCollapsedSubProcess(flowNode.getName(), graphicInfo, true);
            } else {
                processDiagramCanvas.drawExpandedSubProcess(flowNode.getName(), graphicInfo, true, scaleFactor);
            }
        });

        // call activity
        activityDrawInstructions.put(CallActivity.class, (processDiagramCanvas, bpmnModel, flowNode) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            processDiagramCanvas.drawCollapsedCallActivity(flowNode.getName(), graphicInfo);
        });

        // text annotation
        artifactDrawInstructions.put(TextAnnotation.class, (processDiagramCanvas, bpmnModel, artifact) -> {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
            TextAnnotation textAnnotation = (TextAnnotation) artifact;
            processDiagramCanvas.drawTextAnnotation(textAnnotation.getText(), graphicInfo);
        });

        // association
        artifactDrawInstructions.put(Association.class, (processDiagramCanvas, bpmnModel, artifact) -> {
            Association association = (Association) artifact;
            String sourceRef = association.getSourceRef();
            String targetRef = association.getTargetRef();

            // source and target can be instanced of FlowElement or Artifact
            BaseElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            BaseElement targetElement = bpmnModel.getFlowElement(targetRef);
            if (sourceElement == null) {
                sourceElement = bpmnModel.getArtifact(sourceRef);
            }
            if (targetElement == null) {
                targetElement = bpmnModel.getArtifact(targetRef);
            }
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            graphicInfoList = connectionPerfectionist(processDiagramCanvas, bpmnModel, sourceElement,
                    targetElement, graphicInfoList);
            int[] xPoints = new int[graphicInfoList.size()];
            int[] yPoints = new int[graphicInfoList.size()];
            for (int i = 1; i < graphicInfoList.size(); i++) {
                GraphicInfo graphicInfo = graphicInfoList.get(i);
                GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                if (i == 1) {
                    xPoints[0] = (int) previousGraphicInfo.getX();
                    yPoints[0] = (int) previousGraphicInfo.getY();
                }
                xPoints[i] = (int) graphicInfo.getX();
                yPoints[i] = (int) graphicInfo.getY();
            }

            AssociationDirection associationDirection = association.getAssociationDirection();
            processDiagramCanvas.drawAssociation(xPoints, yPoints, associationDirection, false, scaleFactor);
        });
    }

    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                       List<String> highLightedFlows, String activityFontName, String labelFontName, ClassLoader customClassLoader,
                                       double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

        return generateProcessDiagram(bpmnModel, imageType, highLightedActivities, new ArrayList<>(),
                highLightedFlows, activityFontName, labelFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI).generateImage(
                imageType);
    }


    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                       List<String> highLightedFlows, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, null, null, null, 1.0, drawSequenceFlowNameWithNoLabelDI);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                       List<String> highLightedFlows, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, null, null, null,
                scaleFactor, drawSequenceFlowNameWithNoLabelDI);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, imageType, highLightedActivities, Collections.emptyList(), drawSequenceFlowNameWithNoLabelDI);
    }


    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                       double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, imageType, highLightedActivities, Collections.emptyList(),
                scaleFactor, drawSequenceFlowNameWithNoLabelDI);
    }

    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName,
                                       String labelFontName, ClassLoader customClassLoader, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, imageType, Collections.emptyList(), Collections.emptyList(),
                activityFontName, labelFontName, customClassLoader, 1.0, drawSequenceFlowNameWithNoLabelDI);
    }

    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName,
                                       String labelFontName, ClassLoader customClassLoader, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

        return generateDiagram(bpmnModel, imageType, Collections.emptyList(), Collections.emptyList(),
                activityFontName, labelFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI);
    }

    public InputStream generatePngDiagram(BpmnModel bpmnModel) {

        return generatePngDiagram(bpmnModel, 1.0, true);
    }

    @Override
    public InputStream generatePngDiagram(BpmnModel bpmnModel, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, "png", Collections.emptyList(), Collections.emptyList(),
                scaleFactor, drawSequenceFlowNameWithNoLabelDI);
    }

    @Override
    public InputStream generateJpgDiagram(BpmnModel bpmnModel) {
        return generateDiagram(bpmnModel, "jpg", Collections.emptyList(), Collections.emptyList(), true);

    }

    @Override
    public InputStream generateJpgDiagram(BpmnModel bpmnModel, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, "jpg", Collections.emptyList(), Collections.emptyList(), drawSequenceFlowNameWithNoLabelDI);
    }


    public BufferedImage generateImage(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                       List<String> highLightedFlows, String activityFontName, String labelFontName, ClassLoader customClassLoader,
                                       double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

        return generateProcessDiagram(bpmnModel, imageType, highLightedActivities, new ArrayList<>(),
                highLightedFlows, activityFontName, labelFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI)
                .generateBufferedImage();
    }

    public BufferedImage generateImage(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                       List<String> highLightedFlows, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

        return generateImage(bpmnModel, imageType, highLightedActivities, highLightedFlows, null, null, null,
                scaleFactor, drawSequenceFlowNameWithNoLabelDI);
    }


    /**
     * Desc: 自定义生成Diagram方法，添加对正在执行中的节点红色高亮显示
     *
     * @param bpmnModel                         流程定义图
     * @param imageType                         图片类型
     * @param highLightedActivities             高亮历史节点集合
     * @param runningActivitiIdList             运行中的流程节点集合
     * @param highLightedFlows                  高亮线集合
     * @param activityFontName                  流程字体名字
     * @param labelFontName                     标签字体名字
     * @param customClassLoader                 自定义类加载器
     * @param scaleFactor                       比例
     * @param drawSequenceFlowNameWithNoLabelDI 是否显示标签
     * @return 输入流
     */
    protected MyProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel, String imageType,
                                                            List<String> highLightedActivities, List<String> runningActivitiIdList, List<String> highLightedFlows,
                                                            String activityFontName, String labelFontName, ClassLoader customClassLoader,
                                                            double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

        MyProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, imageType,
                activityFontName, labelFontName, customClassLoader);

        // Draw pool shape, if process is participant in collaboration
        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getName(), graphicInfo);
        }

        // Draw lanes
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane lane : process.getLanes()) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getName(), graphicInfo);
            }
        }

        // Draw activities and their sequence-flows
        /*
          绘制流程图上的所有节点和流程线，对高亮显示的节点和流程线进行特殊处理
         */
        for (FlowNode flowNode : bpmnModel.getProcesses().get(0).findFlowElementsOfType(FlowNode.class)) {
            drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedActivities, runningActivitiIdList,
                    highLightedFlows, scaleFactor);
        }

        // Draw artifacts
        if (drawSequenceFlowNameWithNoLabelDI) {
            for (Process process : bpmnModel.getProcesses()) {
                for (Artifact artifact : process.getArtifacts()) {
                    drawArtifact(processDiagramCanvas, bpmnModel, artifact);
                }
            }
        }

        return processDiagramCanvas;
    }

    /**
     * Desc: 绘制流程图上的所有节点和流程线，对高亮显示的节点和流程线进行特殊处理
     *
     * @param processDiagramCanvas  画板
     * @param bpmnModel             流程图定义
     * @param flowNode              流程节点
     * @param highLightedActivities 高亮节点集合
     * @param highLightedFlows      高亮流程线集合
     * @param scaleFactor           比例
     */
    protected void drawActivity(MyProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode,
                                List<String> highLightedActivities, List<String> runningActivitiIdList, List<String> highLightedFlows,
                                double scaleFactor) {
        ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(flowNode.getClass());
        if (drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);
            // Gather info on the multi instance marker
            boolean multiInstanceSequential = false, multiInstanceParallel = false, collapsed = false;
            if (flowNode instanceof Activity) {
                Activity activity = (Activity) flowNode;
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                if (multiInstanceLoopCharacteristics != null) {
                    multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                    multiInstanceParallel = !multiInstanceSequential;
                }
            }

            // Gather info on the collapsed marker
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (flowNode instanceof SubProcess) {
                collapsed = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
            } else if (flowNode instanceof CallActivity) {
                collapsed = true;
            }

            processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(),
                    (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(), multiInstanceSequential,
                    multiInstanceParallel, collapsed);

            // 绘制需要突出强调的活动
            if (highLightedActivities.contains(flowNode.getId())) {
                //如果节点为当前正在处理中的节点，则红色高亮显示
                if (runningActivitiIdList.contains(flowNode.getId())) {
                    logger.debug("[绘制]-当前正在处理中的节点-红色高亮显示节点[{}-{}]", flowNode.getId(), flowNode.getName());
                    drawRunningActivitiHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                } else {
                    logger.debug("[绘制]-高亮显示节点[{}-{}]", flowNode.getId(), flowNode.getName());
                    drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                }
            }
        }
        // 绘制当前节点的流程线
        for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
            boolean highLighted = (highLightedFlows.contains(sequenceFlow.getId()));
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            boolean isDefault = defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId());
            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && !(flowNode instanceof Gateway);

            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
            if (graphicInfoList != null && !graphicInfoList.isEmpty()) {
                graphicInfoList = connectionPerfectionist(processDiagramCanvas, bpmnModel, sourceElement,
                        targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];
                for (int i = 1; i < graphicInfoList.size(); i++) {
                    GraphicInfo graphicInfo = graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);
                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }
                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();

                }

                processDiagramCanvas.drawSequenceFlow(xPoints, yPoints, drawConditionalIndicator, isDefault,
                        highLighted, scaleFactor);
                //绘制流程线名称
                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
                if (labelGraphicInfo != null) {
                    GraphicInfo lineCenter = getLineCenter(graphicInfoList);
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), lineCenter, true);
                }
            }
        }

        // Nested elements
        if (flowNode instanceof FlowElementsContainer) {
            for (FlowElement nestedFlowElement : ((FlowElementsContainer) flowNode).getFlowElements()) {
                if (nestedFlowElement instanceof FlowNode) {
                    drawActivity(processDiagramCanvas, bpmnModel, (FlowNode) nestedFlowElement, highLightedActivities,
                            runningActivitiIdList, highLightedFlows, scaleFactor);
                }
            }
        }
    }

    /**
     * This method makes coordinates of connection flow better.
     *
     * @param processDiagramCanvas 画布
     * @param bpmnModel            流程定义文件
     * @param sourceElement        源元素
     * @param targetElement        目标元素
     * @param graphicInfoList      画笔列表
     * @return
     */
    protected static List<GraphicInfo> connectionPerfectionist(MyProcessDiagramCanvas processDiagramCanvas,
                                                               BpmnModel bpmnModel, BaseElement sourceElement, BaseElement targetElement,
                                                               List<GraphicInfo> graphicInfoList) {
        GraphicInfo sourceGraphicInfo = bpmnModel.getGraphicInfo(sourceElement.getId());
        GraphicInfo targetGraphicInfo = bpmnModel.getGraphicInfo(targetElement.getId());

        MyProcessDiagramCanvas.SHAPE_TYPE sourceShapeType = getShapeType(sourceElement);
        MyProcessDiagramCanvas.SHAPE_TYPE targetShapeType = getShapeType(targetElement);

        return processDiagramCanvas.connectionPerfectionist(sourceShapeType, targetShapeType, sourceGraphicInfo,
                targetGraphicInfo, graphicInfoList);
    }

    /**
     * This method returns shape type of base element.<br>
     * Each element can be presented as rectangle, rhombus, or ellipse.
     *
     * @param baseElement
     * @return MyProcessDiagramCanvas.SHAPE_TYPE
     */
    protected static MyProcessDiagramCanvas.SHAPE_TYPE getShapeType(BaseElement baseElement) {
        if (baseElement instanceof Activity || baseElement instanceof TextAnnotation) {
            return MyProcessDiagramCanvas.SHAPE_TYPE.Rectangle;
        } else if (baseElement instanceof Gateway) {
            return MyProcessDiagramCanvas.SHAPE_TYPE.Rhombus;
        } else if (baseElement instanceof Event) {
            return MyProcessDiagramCanvas.SHAPE_TYPE.Ellipse;
        }
        return null;
    }

    protected static GraphicInfo getLineCenter(List<GraphicInfo> graphicInfoList) {
        GraphicInfo gi = new GraphicInfo();

        double length = 0;
        double[] lengths = new double[graphicInfoList.size()];
        lengths[0] = 0;
        double m;
        for (int i = 1; i < graphicInfoList.size(); i++) {
            GraphicInfo graphicInfo = graphicInfoList.get(i);
            GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

            length += Math.sqrt(Math.pow((int) graphicInfo.getX() - (int) previousGraphicInfo.getX(), 2) + Math.pow(
                    (int) graphicInfo.getY() - (int) previousGraphicInfo.getY(), 2));
            lengths[i] = length;
        }
        m = length / 2;
        int p1 = 0, p2 = 1;
        for (int i = 1; i < lengths.length; i++) {
            double len = lengths[i];
            p1 = i - 1;
            p2 = i;
            if (len > m) {
                break;
            }
        }

        GraphicInfo graphicInfo1 = graphicInfoList.get(p1);
        GraphicInfo graphicInfo2 = graphicInfoList.get(p2);

        double AB = (int) graphicInfo2.getX() - (int) graphicInfo1.getX();
        double OA = (int) graphicInfo2.getY() - (int) graphicInfo1.getY();
        double OB = lengths[p2] - lengths[p1];
        double ob = m - lengths[p1];
        double ab = AB * ob / OB;
        double oa = OA * ob / OB;

        double mx = graphicInfo1.getX() + ab;
        double my = graphicInfo1.getY() + oa;

        gi.setX(mx);
        gi.setY(my);
        return gi;
    }

    protected void drawArtifact(MyProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel,
                                Artifact artifact) {
        ArtifactDrawInstruction drawInstruction = artifactDrawInstructions.get(artifact.getClass());
        if (drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas, bpmnModel, artifact);
        }
    }


    private static void drawHighLight(MyProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo
                .getWidth(), (int) graphicInfo.getHeight());
    }

    private static void drawHighLight(MyProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo, Color color) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo
                .getWidth(), (int) graphicInfo.getHeight(), color);

    }

    /**
     * Desc:绘制正在执行中的节点红色高亮显示
     *
     * @param processDiagramCanvas 画布
     * @param graphicInfo          画笔
     */
    private static void drawRunningActivitiHighLight(MyProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawRunningActivitiHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo
                .getWidth(), (int) graphicInfo.getHeight());
    }

    /**
     * Desc:绘制正在执行中的节点红色高亮显示
     *
     * @param processDiagramCanvas 画布
     * @param graphicInfo          画笔
     */
    private static void drawRunningActivitiHighLight(MyProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo, Color color) {
        processDiagramCanvas.drawRunningActivitiHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo
                .getWidth(), (int) graphicInfo.getHeight(), color);
    }

    protected static MyProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String imageType,
                                                                     String activityFontName, String labelFontName, ClassLoader customClassLoader) {
        // 我们需要计算最大值以了解图像的整体大小
        double minX = Double.MAX_VALUE;
        double maxX = 0;
        double minY = Double.MAX_VALUE;
        double maxY = 0;

        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
            maxY = graphicInfo.getY() + graphicInfo.getHeight();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        for (FlowNode flowNode : flowNodes) {

            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }
            if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }
            minX = Math.min(flowNodeGraphicInfo.getX(), minX);
            minY = Math.min(flowNodeGraphicInfo.getY(), minY);

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                if (graphicInfoList != null) {
                    for (GraphicInfo graphicInfo : graphicInfoList) {

                        maxX = Math.max(graphicInfo.getX(), maxX);
                        minX = Math.min(graphicInfo.getX(), minX);
                        maxY = Math.max(graphicInfo.getY(), maxY);
                        minY = Math.min(graphicInfo.getY(), minY);

                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
        for (Artifact artifact : artifacts) {

            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());

            if (artifactGraphicInfo != null) {
                // 宽度
                if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }
                if (artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }
                // 高度
                if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }
                if (artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // 宽度
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }
                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }
                    // 高度
                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }
                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane l : process.getLanes()) {
                nrOfLanes++;
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                maxX = Math.max(graphicInfo.getX(), maxX);
                minX = Math.min(graphicInfo.getX(), minX);
                maxY = Math.max(graphicInfo.getY(), maxY);
                minY = Math.min(graphicInfo.getY(), minY);
            }
        }

        // Special case, see http://jira.codehaus.org/browse/ACT-1431
        if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
            // Nothing to show
            minX = 0;
            minY = 0;
        }

        if (minX < 0) {
            maxX -= minX;
            minX = 0;
        }

        if (minY < 0) {
            maxY -= minY;
            minY = 0;
        }

        return new MyProcessDiagramCanvas((int) maxX + 10, (int) maxY + 10, (int) minX, (int) minY, imageType,
                activityFontName, labelFontName, customClassLoader);
    }

    protected static List<Artifact> gatherAllArtifacts(BpmnModel bpmnModel) {
        List<Artifact> artifacts = new ArrayList<>();
        for (Process process : bpmnModel.getProcesses()) {
            artifacts.addAll(process.getArtifacts());
        }
        return artifacts;
    }

    protected static List<FlowNode> gatherAllFlowNodes(BpmnModel bpmnModel) {
        List<FlowNode> flowNodes = new ArrayList<>();
        for (Process process : bpmnModel.getProcesses()) {
            flowNodes.addAll(gatherAllFlowNodes(process));
        }
        return flowNodes;
    }

    protected static List<FlowNode> gatherAllFlowNodes(FlowElementsContainer flowElementsContainer) {
        List<FlowNode> flowNodes = new ArrayList<>();
        for (FlowElement flowElement : flowElementsContainer.getFlowElements()) {
            if (flowElement instanceof FlowNode) {
                flowNodes.add((FlowNode) flowElement);
            }
            if (flowElement instanceof FlowElementsContainer) {
                flowNodes.addAll(gatherAllFlowNodes((FlowElementsContainer) flowElement));
            }
        }
        return flowNodes;
    }

    public void setActivityDrawInstructions(
            Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions) {
        this.activityDrawInstructions = activityDrawInstructions;
    }

    public void setArtifactDrawInstructions(
            Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions) {
        this.artifactDrawInstructions = artifactDrawInstructions;
    }

    protected interface ActivityDrawInstruction {

        void draw(MyProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode);
    }

    protected interface ArtifactDrawInstruction {

        void draw(MyProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact);
    }


    /**
     * Desc: 自定义生成Diagram方法，添加对正在执行中的节点红色高亮显示
     *
     * @param bpmnModel             流程定义图
     * @param imageType             图片类型
     * @param highLightedActivities 高亮历史节点集合
     * @param runningActivitiIdList 运行中的流程节点集合
     * @param highLightedFlows      高亮线集合
     * @param activityFontName      流程字体名字
     * @param labelFontName         标签字体名字
     * @param customClassLoader     自定义类加载器
     * @param scaleFactor           比例
     * @return 输入流
     */
    public InputStream generateDiagramCustom(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities,
                                             List<String> runningActivitiIdList, List<String> highLightedFlows, String activityFontName,
                                             String labelFontName, ClassLoader customClassLoader, double scaleFactor) {
        return generateProcessDiagram(bpmnModel, imageType, highLightedActivities, runningActivitiIdList,
                highLightedFlows, activityFontName, labelFontName, customClassLoader, scaleFactor, true).generateImage(
                imageType);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName,
                                       String labelFontName, String annotationFontName, ClassLoader customClassLoader, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, imageType, Collections.emptyList(), Collections.emptyList(),
                activityFontName, labelFontName, customClassLoader, 1.0, drawSequenceFlowNameWithNoLabelDI);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName,
                                       String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {
        // TODO Auto-generated method stub
        return generateDiagram(bpmnModel, imageType, Collections.emptyList(), Collections.emptyList(),
                activityFontName, labelFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI);
    }

    @Override
    public InputStream generatePngDiagram(BpmnModel bpmnModel, boolean drawSequenceFlowNameWithNoLabelDI) {
        return generateDiagram(bpmnModel, "png", Collections.emptyList(), Collections.emptyList(), drawSequenceFlowNameWithNoLabelDI);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       String imageType,
                                       List<String> highLightedActivities,
                                       List<String> highLightedFlows,
                                       String activityFontName,
                                       String labelFontName,
                                       String annotationFontName,
                                       ClassLoader customClassLoader,
                                       double scaleFactor,
                                       boolean drawSequenceFlowNameWithNoLabelDI) {
        // TODO Auto-generated method stub
        return generateProcessDiagram(bpmnModel, imageType, highLightedActivities, new ArrayList<>(),
                highLightedFlows, activityFontName, labelFontName, customClassLoader, scaleFactor
                , drawSequenceFlowNameWithNoLabelDI).generateImage(
                imageType);
    }

    @Override
    public BufferedImage generatePngImage(BpmnModel bpmnModel, double scaleFactor) {
        return generateImage(bpmnModel, "png", Collections.emptyList(), Collections.emptyList(),
                scaleFactor, true);
    }

}