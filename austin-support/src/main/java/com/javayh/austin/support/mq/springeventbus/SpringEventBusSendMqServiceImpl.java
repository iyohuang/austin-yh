package com.javayh.austin.support.mq.springeventbus;

import com.javayh.austin.support.constans.MessageQueuePipeline;
import com.javayh.austin.support.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author yh
 *
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.SPRING_EVENT_BUS)
public class SpringEventBusSendMqServiceImpl implements SendMqService {

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void send(String topic, String jsonValue, String tagId) {
        AustinSpringEventSource source = AustinSpringEventSource.builder().topic(topic).jsonValue(jsonValue).tagId(tagId).build();
        AustinSpringEventBusEvent austinSpringEventBusEvent = new AustinSpringEventBusEvent(this, source);
        applicationContext.publishEvent(austinSpringEventBusEvent);
    }

    @Override
    public void send(String topic, String jsonValue) {
        send(topic, jsonValue, null);
    }
}
