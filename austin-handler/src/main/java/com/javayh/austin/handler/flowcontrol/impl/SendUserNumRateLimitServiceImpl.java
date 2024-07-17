package com.javayh.austin.handler.flowcontrol.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.enums.RateLimitStrategy;
import com.javayh.austin.handler.flowcontrol.FlowControlParam;
import com.javayh.austin.handler.flowcontrol.FlowControlService;
import com.javayh.austin.handler.flowcontrol.annotations.LocalRateLimit;

/**
 * @author yh
 */
@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.SEND_USER_NUM_RATE_LIMIT)
public class SendUserNumRateLimitServiceImpl implements FlowControlService {

    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo
     * @param flowControlParam
     */
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(taskInfo.getReceiver().size());
    }
}
