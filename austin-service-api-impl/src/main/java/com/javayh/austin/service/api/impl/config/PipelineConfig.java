package com.javayh.austin.service.api.impl.config;


import com.javayh.austin.common.pipeline.ProcessController;
import com.javayh.austin.common.pipeline.ProcessTemplate;
import com.javayh.austin.service.api.enums.BusinessCode;
import com.javayh.austin.service.api.impl.action.send.SendAfterCheckAction;
import com.javayh.austin.service.api.impl.action.send.SendAssembleAction;
import com.javayh.austin.service.api.impl.action.send.SendMqAction;
import com.javayh.austin.service.api.impl.action.send.SendPreCheckAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * api层的pipeline配置类
 *
 * @author yh
 */
@Configuration
public class PipelineConfig {

    @Autowired
    private SendPreCheckAction sendPreCheckAction;
    @Autowired
    private SendAssembleAction sendAssembleAction;
    @Autowired
    private SendAfterCheckAction sendAfterCheckAction;
    @Autowired
    private SendMqAction sendMqAction;

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     *
     * @return
     */
    public ProcessTemplate commonSendTemplate(){
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(sendPreCheckAction, sendAssembleAction, sendAfterCheckAction, sendMqAction));
        return processTemplate;
    }
    
    public ProcessController apiProcessController(){
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate> templateConfig = new HashMap<>(4);
        templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
        processController.setTemplateConfig(templateConfig);
        return processController;
    }
    
}
