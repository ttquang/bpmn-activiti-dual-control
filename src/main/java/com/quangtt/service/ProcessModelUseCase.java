package com.quangtt.service;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessModelUseCase {

    @Autowired
    private RepositoryService repositoryService;

    public List<String> availableActions(String processDefinitionId, String taskDefinitionKey) {
        var bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        var taskDefinition = bpmnModel.getMainProcess().getFlowElement(taskDefinitionKey);
        return availableActions(taskDefinition);
    }

    private List<String> availableActions(FlowElement element) {
        List<String> actions = new ArrayList<>();

        if (element instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) element;

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
//                if (Objects.nonNull(sequenceFlow.getDocumentation())) {
                if (sequenceFlow.getTargetFlowElement() instanceof UserTask) {
                    actions.add(sequenceFlow.getDocumentation());
                } else {
                    actions.addAll(availableActions(sequenceFlow.getTargetFlowElement()));
                }
            }
        }

        return actions;
    }

    public boolean isValidAction(String processDefinitionId, String taskDefinitionKey, String transition) {
        List<String> availableActions = availableActions(processDefinitionId, taskDefinitionKey);
        return availableActions.contains(transition);
    }

    public boolean isNotValidAction(String processDefinitionId, String taskDefinitionKey, String transition) {
        return !isValidAction(processDefinitionId, taskDefinitionKey, transition);
    }
}
