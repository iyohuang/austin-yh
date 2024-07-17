package com.javayh.austin.handler.config;

import com.javayh.austin.common.pipeline.ProcessController;
import com.javayh.austin.common.pipeline.ProcessTemplate;
import com.javayh.austin.handler.action.DeduplicationAction;
import com.javayh.austin.handler.action.DiscardAction;
import com.javayh.austin.handler.action.SendMessageAction;
import com.javayh.austin.handler.action.ShieldAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * handler层的pipeline配置类
 *
 * @author yh
 */
@Configuration
public class TaskPipelineConfig {
    
    public static final String PIPELINE_HANDLER_CODE = "handler";

    @Autowired
    private DiscardAction discardAction;
    @Autowired
    private ShieldAction shieldAction;
    @Autowired
    private DeduplicationAction deduplicationAction;
    @Autowired
    private SendMessageAction sendMessageAction;


    /**
     * 消息从MQ消费的流程
     * 0.丢弃消息
     * 1.屏蔽消息
     * 2.通用去重功能
     * 3.发送消息
     *
     * @return
     */
    @Bean("taskTemplate")
    public ProcessTemplate taskTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(discardAction, shieldAction, deduplicationAction, sendMessageAction));
        return processTemplate;
    }
    
    @Bean("handlerProcessController")
    public ProcessController processController(){
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate> templateConfig = new HashMap<>(4);
        templateConfig.put(PIPELINE_HANDLER_CODE, taskTemplate());
        processController.setTemplateConfig(templateConfig);
        return processController;
    }
    
    
}
