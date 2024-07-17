package com.javayh.austin.handler.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.javayh.austin.common.constant.CommonConstant;
import com.javayh.austin.common.domain.AnchorInfo;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.AnchorState;
import com.javayh.austin.common.pipeline.BusinessProcess;
import com.javayh.austin.common.pipeline.ProcessContext;
import com.javayh.austin.support.service.ConfigService;
import com.javayh.austin.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 丢弃消息
 * 一般将需要丢弃的模板id写在分布式配置中心
 *
 * @author yh
 */
@Service
public class DiscardAction implements BusinessProcess<TaskInfo> {

    private static final String DISCARD_MESSAGE_KEY = "discardMsgIds";
    
    @Autowired
    private ConfigService configService;
    @Autowired
    private LogUtils logUtils;
    
    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        // 配置示例:	["1","2"]
        JSONArray array = JSON.parseArray(configService.getProperty(DISCARD_MESSAGE_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY));
        if(array.contains(String.valueOf(taskInfo.getMessageTemplateId()))){
            logUtils.print(AnchorInfo.builder().bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).state(AnchorState.DISCARD.getCode()).build());
            context.setNeedBreak(true);
        }
    }
}
