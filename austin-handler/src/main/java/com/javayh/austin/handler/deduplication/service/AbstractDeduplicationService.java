package com.javayh.austin.handler.deduplication.service;

import cn.hutool.core.collection.CollUtil;
import com.javayh.austin.common.domain.AnchorInfo;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.deduplication.DeduplicationHolder;
import com.javayh.austin.handler.deduplication.DeduplicationParam;
import com.javayh.austin.handler.deduplication.limit.LimitService;
import com.javayh.austin.support.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author yh
 * 
 * 去重服务
 */

@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService{
    
    protected Integer deduplicationType;
    
    protected LimitService limitService;
    
    @Autowired
    private DeduplicationHolder deduplicationHolder;
    
    @Autowired
    private LogUtils logUtils;

    @PostConstruct
    private void init() {
        deduplicationHolder.putService(deduplicationType, this);
    }
    @Override
    public void deduplication(DeduplicationParam param) {
        TaskInfo taskInfo = param.getTaskInfo();

        Set<String> filterReceiver = limitService.limitFilter(this, taskInfo, param);

        // 剔除符合去重条件的用户
        if (CollUtil.isNotEmpty(filterReceiver)) {
            taskInfo.getReceiver().removeAll(filterReceiver);
            logUtils.print(AnchorInfo.builder().bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(filterReceiver).state(param.getAnchorState().getCode()).build());
        }
    }

    /**
     * 构建去重的Key
     *
     * @param taskInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(TaskInfo taskInfo, String receiver);
    
}
