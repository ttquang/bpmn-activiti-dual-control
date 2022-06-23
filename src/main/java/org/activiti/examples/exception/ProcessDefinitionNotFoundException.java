package org.activiti.examples.exception;

public class ProcessDefinitionNotFoundException extends RuntimeException{

    public ProcessDefinitionNotFoundException(String processDefinitionKey) {
        super(String.format("{} not found", processDefinitionKey));
    }

}
