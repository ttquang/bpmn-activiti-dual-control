package org.activiti.examples;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @PostMapping
    public ProcessInstance start(
            @RequestBody Map<String, String> request
    ) {

        var processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(request.get("process_key"))
                .withVariable("transitionName", request.get("transition_name"))
                .withVariable("type", request.get("type"))
                .build());
        String message = ">>> Created Process Instance: " + processInstance;
        System.out.println(message);
        return processInstance;
    }

    @PutMapping("/{processInstanceId}/{taskDefinitionKey}/complete")
    public String completeTask(
            @PathVariable String processInstanceId,
            @PathVariable String taskDefinitionKey,
            @RequestBody String transitionName
    ) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).withVariable("transitionName", transitionName).build());

        return "COMPLETE";
    }

    @GetMapping("/{processInstanceId}")
    public String getFile(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        System.out.println(runtimeService.getVariable(processInstanceId, "type"));

        System.out.println(availableActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey()));

//        var processInstance = processRuntime.suspend()

        return task.getTaskDefinitionKey() + "--" + task.getName();
    }

    @GetMapping("/{processInstanceId}/available-actions")
    public List<String> availableActions(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        System.out.println(availableActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey()));
        return availableActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
    }

    @GetMapping("/{processInstanceId}/history")
    public List<HistoricProcessInstance> getHistory(@PathVariable String processInstanceId) {
        var historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).list();

        System.out.println(historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).list());
        System.out.println(historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list());
        System.out.println(historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list());
        System.out.println(historyService.createHistoricDetailQuery()
                .processInstanceId(processInstanceId).list());

        return historicProcessInstances;
    }

    private List<String> availableActions(String processDefinitionId, String taskDefinitionKey) {
        var bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        var taskDefinition = bpmnModel.getMainProcess().getFlowElement(taskDefinitionKey);
        return availableActions(taskDefinition);
    }

    private List<String> availableActions(FlowElement element) {
        List<String> actions = new ArrayList<>();

        if (element instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) element;

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                if (Objects.nonNull(sequenceFlow.getDocumentation())) {
                    actions.add(sequenceFlow.getDocumentation());
                } else {
                    actions.addAll(availableActions(sequenceFlow.getTargetFlowElement()));
                }
            }
        }

        return actions;
    }

}
