package com.quangtt.service;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProcessUseCase {

    @Autowired
    private ProcessRuntime processRuntime;

    public ProcessInstance startNewProcess(Map<String, String> request) {
        var processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(request.get("process_key"))
                .withVariable("transitionName", request.get("transition_name"))
                .withVariable("type", request.get("type"))
                .build());
        return processInstance;
    }

    public ProcessInstance get(String processInstanceId) {
        return processRuntime.processInstance(processInstanceId);
    }

}
