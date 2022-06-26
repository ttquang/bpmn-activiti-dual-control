package com.quangtt.application;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process-definitions")
public class ProcessDefinitionController {

    @Autowired
    private ProcessRuntime processRuntime;

    @GetMapping()
    public List<ProcessDefinition> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return processRuntime.processDefinitions(Pageable.of(offset, pageSize)).getContent();
    }

}
