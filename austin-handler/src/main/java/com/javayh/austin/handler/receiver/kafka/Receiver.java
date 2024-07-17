package com.javayh.austin.handler.receiver.kafka;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSON;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.service.ConsumeService;
import com.javayh.austin.handler.utils.GroupIdMappingUtils;
import com.javayh.austin.support.constans.MessageQueuePipeline;
import groovy.util.logging.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author yh
 * 消费MQ信息
 */

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class Receiver {

    @Autowired
    private ConsumeService consumeService;

    /**
     * 发送消息
     *
     * @param consumerRecord
     * @param topicGroupId
     */
    @KafkaListener(topics = "#{'${austin.business.topic.name}'}", containerFactory = "filterContainerFactory")
    public void consumer(ConsumerRecord<?,String> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId){
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        
        if(kafkaMessage.isPresent()){
            List<TaskInfo> taskInfoLists = JSON.parseArray(kafkaMessage.get(), TaskInfo.class);
            String messageGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
            
            if(messageGroupId.equals(topicGroupId)){
                consumeService.consume2Send(taskInfoLists);
            }
        }
    }
    
}
