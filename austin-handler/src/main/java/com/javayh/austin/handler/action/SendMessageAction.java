package com.javayh.austin.handler.action;

import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.pipeline.BusinessProcess;
import com.javayh.austin.common.pipeline.ProcessContext;
import org.springframework.stereotype.Service;

@Service
public class SendMessageAction implements BusinessProcess<TaskInfo> {
    @Override
    public void process(ProcessContext<TaskInfo> context) {
        
    }
}
