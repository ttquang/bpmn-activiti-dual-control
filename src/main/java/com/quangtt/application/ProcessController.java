package com.quangtt.application;

import com.quangtt.service.ProcessUseCase;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    @Autowired
    private ProcessUseCase processUseCase;

    @Autowired
    private HistoryService historyService;

    @GetMapping("/{processInstanceId}")
    public ProcessInstance read(@PathVariable String processInstanceId) {
        return processUseCase.get(processInstanceId);
    }

    @PostMapping
    public ProcessInstance create(
            @RequestBody Map<String, String> request
    ) {
        return processUseCase.startNewProcess(request);
    }

    @GetMapping("/{processInstanceId}/history")
    public List<HistoricProcessInstance> getHistory(@PathVariable String processInstanceId) {
        var historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).list();

        System.out.println(historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).list());
//        System.out.println(historyService.createHistoricActivityInstanceQuery()
//                .processInstanceId(processInstanceId).list());
        System.out.println(historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list());
        System.out.println(historyService.createHistoricDetailQuery()
                .processInstanceId(processInstanceId).list());

        return List.of();
    }

}
