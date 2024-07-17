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

@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.REQUEST_RATE_LIMIT)
public class RequestRateLimitServiceImpl implements FlowControlService {
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(1);
    }
}
