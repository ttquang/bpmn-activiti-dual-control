package com.quangtt.service;

import com.quangtt.service.dto.TaskDto;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskUseCase {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private ProcessModelUseCase processModelUseCase;

    public List<TaskDto> getTasks(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId).list();
        return tasks.stream().map(task -> {
            TaskDto taskDto = new TaskDto();
            taskDto.setName(task.getName());
            taskDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            taskDto.setAvailableActions(processModelUseCase.availableActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey()));
            return taskDto;
        }).collect(Collectors.toList());
    }

    public TaskDto getTask(String processInstanceId, String taskDefinitionKey) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();
        TaskDto taskDto = new TaskDto();
        taskDto.setName(task.getName());
        taskDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
        taskDto.setAvailableActions(processModelUseCase.availableActions(task.getProcessDefinitionId(), task.getTaskDefinitionKey()));
        return taskDto;
    }

    public void completeTask(
            String processInstanceId,
            String taskDefinitionKey,
            String transitionName
    ) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        if (processModelUseCase.isNotValidAction(task.getProcessDefinitionId(), task.getTaskDefinitionKey(), transitionName)) {
            throw new RuntimeException("Action is invalid");
        }

//        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).withVariable("transitionName", transitionName).build());
    }
}
