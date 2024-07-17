package com.javayh.austin.handler.flowcontrol;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.javayh.austin.common.constant.CommonConstant;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.ChannelType;
import com.javayh.austin.common.enums.EnumUtil;
import com.javayh.austin.handler.enums.RateLimitStrategy;
import com.javayh.austin.handler.flowcontrol.FlowControlParam;
import com.javayh.austin.handler.flowcontrol.FlowControlService;
import com.javayh.austin.handler.flowcontrol.annotations.LocalRateLimit;
import com.javayh.austin.support.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yh
 */
@Service
@Slf4j
public class FlowControlFactory implements ApplicationContextAware {
    private static final String FLOW_CONTROL_KEY = "flowControl";
    private static final String FLOW_CONTROL_PREFIX = "flow_control_";
    
    private Map<RateLimitStrategy, FlowControlService> flowControlServiceMap = new ConcurrentHashMap<>();
    
    @Autowired
    private ConfigService config;
    
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @PostConstruct
    private void init(){
        Map<String, Object> serviceMap = this.applicationContext.getBeansWithAnnotation(LocalRateLimit.class);
        serviceMap.forEach((name, service) -> {
            LocalRateLimit localRateLimit = AopUtils.getTargetClass(service).getAnnotation(LocalRateLimit.class);
            RateLimitStrategy rateLimitStrategy = localRateLimit.rateLimitStrategy();
            flowControlServiceMap.put(rateLimitStrategy, (FlowControlService) service);
        });
    }
    
    public void flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam){
        RateLimiter rateLimiter;
        Double rateInitValue = flowControlParam.getRateInitValue();
        Double rateLimitConfig = getRateLimitConfig(taskInfo.getSendChannel());
        if (Objects.nonNull(rateLimitConfig) && !rateInitValue.equals(rateLimitConfig)) {
            rateLimiter = RateLimiter.create(rateLimitConfig);
            flowControlParam.setRateInitValue(rateLimitConfig);
            flowControlParam.setRateLimiter(rateLimiter);
        }

        FlowControlService flowControlService = flowControlServiceMap.get(flowControlParam.getRateLimitStrategy());
        if (Objects.nonNull(flowControlService)){
            log.error("可惜~没有找到对应的单机限流策略");
            return;
        }
        Double costTime = flowControlService.flowControl(taskInfo, flowControlParam);
        if (costTime > 0){
            log.info("consumer {} flow control time {}", EnumUtil.getEnumByCode(taskInfo.getSendChannel(), ChannelType.class).getDescription(), costTime);
        }
    }

    /**
     * 得到限流值的配置
     * <p>
     * apollo配置样例     key：flowControl value：{"flow_control_40":1}
     * <p>
     * 渠道枚举可看：com.javayh.austin.common.enums.ChannelType
     * @param channelCode
     * @return 发送类型对应的限流量
     */
    private Double getRateLimitConfig(Integer channelCode){
        String flowControlConfig = config.getProperty(FLOW_CONTROL_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        JSONObject jsonObject = JSON.parseObject(flowControlConfig);
        if (ObjectUtil.isNull(jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode))){
            return null;
        }
        return jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode);
    }
    
}
