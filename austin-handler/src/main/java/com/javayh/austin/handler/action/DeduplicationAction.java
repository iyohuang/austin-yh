package com.javayh.austin.handler.action;

import cn.hutool.core.collection.CollUtil;
import com.javayh.austin.common.constant.CommonConstant;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.DeduplicationType;
import com.javayh.austin.common.enums.EnumUtil;
import com.javayh.austin.common.pipeline.BusinessProcess;
import com.javayh.austin.common.pipeline.ProcessContext;
import com.javayh.austin.handler.deduplication.DeduplicationHolder;
import com.javayh.austin.handler.deduplication.DeduplicationParam;
import com.javayh.austin.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 去重服务
 * 1. 根据相同内容N分钟去重（SlideWindowLimitService）
 * 2. 相同的渠道一天内频次去重（SimpleLimitService）
 * @author yh
 */
@Service
public class DeduplicationAction implements BusinessProcess<TaskInfo> {

    public static final String DEDUPLICATION_RULE_KEY = "deduplicationRule";

    @Autowired
    private ConfigService config;

    @Autowired
    private DeduplicationHolder deduplicationHolder;
    
    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        // 配置样例{"deduplication_10":{"num":1,"time":300},"deduplication_20":{"num":5}}
        String deduplicationConfig = config.getProperty(DEDUPLICATION_RULE_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        List<Integer> deduplicationTypeList = EnumUtil.getCodeList(DeduplicationType.class);
        for (Integer deduplicationType : deduplicationTypeList){
            DeduplicationParam deduplicationParam = deduplicationHolder.selectBuilder(deduplicationType).build(deduplicationConfig, taskInfo);
            if (Objects.nonNull(deduplicationParam)){
                deduplicationHolder.selectService(deduplicationType).deduplication(deduplicationParam);
            }
        }
        if (CollUtil.isEmpty(taskInfo.getReceiver())){
            context.setNeedBreak(true);
        }
    }
}
