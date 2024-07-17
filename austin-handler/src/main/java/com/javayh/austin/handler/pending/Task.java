package com.javayh.austin.handler.pending;

import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.pipeline.ProcessContext;
import com.javayh.austin.common.pipeline.ProcessController;
import com.javayh.austin.common.pipeline.ProcessModel;
import com.javayh.austin.common.vo.BasicResultVO;
import com.javayh.austin.handler.config.TaskPipelineConfig;
import groovy.util.logging.Slf4j;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Accessors(chain = true)
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Task implements Runnable{
    private TaskInfo taskInfo;
    @Autowired
    @Qualifier("handlerProcessController")
    private ProcessController processController;
    @Override
    public void run() {
        ProcessContext<ProcessModel> context = ProcessContext.builder()
                .processModel(taskInfo).code(TaskPipelineConfig.PIPELINE_HANDLER_CODE)
                .needBreak(false).response(BasicResultVO.success())
                .build();
        processController.process(context);
    }
}
