package com.javayh.austin.handler.handler;

import com.javayh.austin.common.domain.AnchorInfo;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.AnchorState;
import com.javayh.austin.handler.flowcontrol.FlowControlFactory;
import com.javayh.austin.handler.flowcontrol.FlowControlParam;
import com.javayh.austin.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yh
 */

public abstract class BaseHandler implements Handler{
    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer channelCode;
    /**
     * 限流相关的参数
     * 子类初始化的时候指定
     */
    protected FlowControlParam flowControlParam;
    @Autowired
    private HandlerHolder handlerHolder;
    @Autowired
    private LogUtils logUtils;
    @Autowired
    private FlowControlFactory flowControlFactory;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 初始化渠道与Handler的映射关系
     */
    @PostConstruct
    private void init() {
        handlerHolder.putHandler(channelCode, this);
    }

    @Override
    public void doHandler(TaskInfo taskInfo) {
        if (Objects.nonNull(flowControlParam)){
            flowControlFactory.flowControl(taskInfo, flowControlParam);
        }
        if (handler(taskInfo)){
            logUtils.print(AnchorInfo.builder()
                    .state(AnchorState.SEND_SUCCESS.getCode())
                    .bizId(taskInfo.getBizId())
                    .messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
            return;
        }
        logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_FAIL.getCode()).bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
    }

    /**
     * 统一处理的handler接口
     *
     * @param taskInfo
     * @return
     */
    public abstract boolean handler(TaskInfo taskInfo);

    /**
     * 将撤回的消息存储到redis
     *
     * @param prefix            redis前缀
     * @param messageTemplateId 消息模板id
     * @param taskId            消息下发taskId
     * @param expireTime        存储到redis的有效时间（跟对应渠道可撤回多久的消息有关系)
     */
    protected void saveRecallInfo(String prefix, Long messageTemplateId, String taskId, Long expireTime){
        redisTemplate.opsForList().leftPush(prefix + messageTemplateId, taskId);
        redisTemplate.opsForValue().set(prefix + taskId, taskId);
        redisTemplate.expire(prefix + messageTemplateId, expireTime, TimeUnit.SECONDS);
        redisTemplate.expire(prefix + taskId, expireTime, TimeUnit.SECONDS);
    }
}
