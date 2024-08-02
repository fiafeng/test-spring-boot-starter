package com.fiafeng.flowable.service.Impl.imgae;


import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.image.impl.DefaultProcessDiagramCanvas;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;

import java.util.Iterator;
import java.util.List;

/**
 * @author Tony
 */
public class CustomProcessDiagramGenerator extends DefaultProcessDiagramGenerator {
    @Override
    protected DefaultProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {
        this.prepareBpmnModel(bpmnModel);
        DefaultProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
        Iterator poolIterator = bpmnModel.getPools().iterator();

        while (poolIterator.hasNext()) {
            Pool process = (Pool) poolIterator.next();
            GraphicInfo subProcesses = bpmnModel.getGraphicInfo(process.getId());
            processDiagramCanvas.drawPoolOrLane(process.getName(), subProcesses, scaleFactor);
        }

        poolIterator = bpmnModel.getProcesses().iterator();

        Process process1;
        Iterator iterator;
        while (poolIterator.hasNext()) {
            process1 = (Process) poolIterator.next();
            iterator = process1.getLanes().iterator();

            while (iterator.hasNext()) {
                Lane artifact = (Lane) iterator.next();
                GraphicInfo subProcess = bpmnModel.getGraphicInfo(artifact.getId());
                processDiagramCanvas.drawPoolOrLane(artifact.getName(), subProcess, scaleFactor);
            }
        }

        poolIterator = bpmnModel.getProcesses().iterator();

        while (poolIterator.hasNext()) {
            process1 = (Process) poolIterator.next();
            iterator = process1.findFlowElementsOfType(FlowNode.class).iterator();

            while (iterator.hasNext()) {
                FlowNode artifact1 = (FlowNode) iterator.next();
                if (!this.isPartOfCollapsedSubProcess(artifact1, bpmnModel)) {
                    this.drawActivity(processDiagramCanvas, bpmnModel, artifact1, highLightedActivities, highLightedFlows, scaleFactor, drawSequenceFlowNameWithNoLabelDI);
                }
            }
        }

        poolIterator = bpmnModel.getProcesses().iterator();

        label75:
        while (true) {
            List subProcesses2;
            do {
                if (!poolIterator.hasNext()) {
                    return processDiagramCanvas;
                }

                process1 = (Process) poolIterator.next();
                iterator = process1.getArtifacts().iterator();

                while (iterator.hasNext()) {
                    Artifact artifact2 = (Artifact) iterator.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, artifact2);
                }

                subProcesses2 = process1.findFlowElementsOfType(SubProcess.class, true);
            } while (subProcesses2 == null);

            Iterator subProcesses2Iterator = subProcesses2.iterator();

            while (true) {
                GraphicInfo graphicInfo;
                SubProcess subProcess1;
                do {
                    do {
                        if (!subProcesses2Iterator.hasNext()) {
                            continue label75;
                        }

                        subProcess1 = (SubProcess) subProcesses2Iterator.next();
                        graphicInfo = bpmnModel.getGraphicInfo(subProcess1.getId());
                    } while (graphicInfo != null && graphicInfo.getExpanded() != null && !graphicInfo.getExpanded());
                } while (this.isPartOfCollapsedSubProcess(subProcess1, bpmnModel));

                for (Artifact subProcessArtifact : subProcess1.getArtifacts()) {
                    this.drawArtifact(processDiagramCanvas, bpmnModel, subProcessArtifact);
                }
            }
        }
    }

    protected static DefaultProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
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
                // 高度
                if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }
                // 宽度
                if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }
                minX = Math.min(artifactGraphicInfo.getX(), minX);
                minY = Math.min(artifactGraphicInfo.getY(), minY);

            }
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    maxY = Math.max(graphicInfo.getY(), maxY);
                    minY = Math.min(graphicInfo.getY(), minY);
                    maxX = Math.max(graphicInfo.getX(), maxX);
                    minX = Math.min(graphicInfo.getX(), minX);
                }
            }
        }

        int temp = 0;
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane l : process.getLanes()) {
                temp++;
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                maxX = Math.max(graphicInfo.getX(), maxX);
                minX = Math.min(graphicInfo.getX(), minX);
                maxY = Math.max(graphicInfo.getY(), maxY);
                minY = Math.min(graphicInfo.getY(), minY);
            }
        }

        // Special case, see http://jira.codehaus.org/browse/ACT-1431
        if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && temp == 0) {
            // Nothing to show
            minX = 0;
            minY = 0;
        }
        return new CustomProcessDiagramCanvas((int) maxX + 10, (int) maxY + 10, (int) minX, (int) minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
    }


    private static void drawHighLight(DefaultProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());

    }

    private static void drawHighLightNow(CustomProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLightNow((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());

    }

    private static void drawHighLightEnd(CustomProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLightEnd((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());

    }

    @Override
    protected void drawActivity(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel,
                                FlowNode flowNode, List<String> highLightedActivities, List<String> highLightedFlows, double scaleFactor, Boolean drawSequenceFlowNameWithNoLabelDI) {

        ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(flowNode.getClass());
        if (drawInstruction != null) {

            drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);

            // Gather info on the multi instance marker
            boolean multiInstanceSequential = false;
            boolean multiInstanceParallel = false;
            boolean collapsed = false;
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

            if (scaleFactor == 1.0) {
                // Actually draw the markers
                processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(),
                        multiInstanceSequential, multiInstanceParallel, collapsed);
            }

            // Draw highlighted activities
            if (highLightedActivities.contains(flowNode.getId())) {

                if (highLightedActivities.get(highLightedActivities.size() - 1).equals(flowNode.getId())
                        && !"endenv".equals(flowNode.getId())) {
                    if ((flowNode.getId().contains("Event_"))) {
                        drawHighLightEnd((CustomProcessDiagramCanvas) processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                    } else {
                        drawHighLightNow((CustomProcessDiagramCanvas) processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                    }
                } else {
                    drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                }


            }

        }

        // Outgoing transitions of activity
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
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
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

                processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted, scaleFactor);


                // Draw sequenceFlow label
                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
                if (labelGraphicInfo != null) {
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), labelGraphicInfo, false);
                } else {
                    if (drawSequenceFlowNameWithNoLabelDI) {
                        GraphicInfo lineCenter = getLineCenter(graphicInfoList);
                        processDiagramCanvas.drawLabel(sequenceFlow.getName(), lineCenter, false);
                    }

                }
            }
        }

        // Nested elements
        if (flowNode instanceof FlowElementsContainer) {
            for (FlowElement nestedFlowElement : ((FlowElementsContainer) flowNode).getFlowElements()) {
                if (nestedFlowElement instanceof FlowNode && !isPartOfCollapsedSubProcess(nestedFlowElement, bpmnModel)) {
                    drawActivity(processDiagramCanvas, bpmnModel, (FlowNode) nestedFlowElement,
                            highLightedActivities, highLightedFlows, scaleFactor, drawSequenceFlowNameWithNoLabelDI);
                }
            }
        }
    }
}

