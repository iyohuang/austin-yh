package com.javayh.austin.handler.deduplication.service;

import cn.hutool.core.text.StrPool;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.DeduplicationType;
import com.javayh.austin.handler.deduplication.limit.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FrequencyDeduplicationService extends AbstractDeduplicationService{
    
    private static final String PREFIX = "FRE";
    
    @Autowired
    public FrequencyDeduplicationService(@Qualifier("SimpleLimitService")LimitService limitService){
        this.deduplicationType = DeduplicationType.FREQUENCY.getCode();
        this.limitService = limitService;
    }
    
    
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return PREFIX + StrPool.C_UNDERLINE
                + receiver + StrPool.C_UNDERLINE
                + taskInfo.getMessageTemplateId() + StrPool.C_UNDERLINE
                + taskInfo.getSendChannel();
    }
}
