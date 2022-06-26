package com.quangtt.application;

import com.quangtt.service.ProcessModelUseCase;
import com.quangtt.service.TaskUseCase;
import com.quangtt.service.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/processes/{processInstanceId}/tasks")
public class TaskController {

    @Autowired
    private TaskUseCase taskUseCase;

    @Autowired
    private ProcessModelUseCase processModelUseCase;

    @GetMapping
    public List<TaskDto> getTasks(@PathVariable String processInstanceId) {
        return taskUseCase.getTasks(processInstanceId);
    }

    @GetMapping("/{taskDefinitionKey}")
    public TaskDto getTask(
            @PathVariable String processInstanceId,
            @PathVariable String taskDefinitionKey
    ) {
        return taskUseCase.getTask(processInstanceId, taskDefinitionKey);
    }

    @GetMapping("/{taskDefinitionKey}/available-actions")
    public List<String> availableActions(
            @PathVariable String processInstanceId,
            @PathVariable String taskDefinitionKey
    ) {
        return processModelUseCase.availableActions(processInstanceId, taskDefinitionKey);
    }

    @PutMapping("/{taskDefinitionKey}/complete/{transitionName}")
    public String completeTask(
            @PathVariable String processInstanceId,
            @PathVariable String taskDefinitionKey,
            @PathVariable String transitionName
    ) {
        taskUseCase.completeTask(processInstanceId, taskDefinitionKey, transitionName);
        return "COMPLETE";
    }
}
