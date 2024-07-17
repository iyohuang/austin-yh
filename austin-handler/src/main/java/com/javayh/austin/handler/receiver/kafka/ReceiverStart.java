package com.javayh.austin.handler.receiver.kafka;

import cn.hutool.core.text.StrPool;
import com.javayh.austin.handler.utils.GroupIdMappingUtils;
import com.javayh.austin.support.constans.MessageQueuePipeline;
import groovy.util.logging.Slf4j;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
/**
 * 启动消费者
 * 
 * @author Yh
 */

@Service
@ConditionalOnProperty(name = "austin.mq.pipeline",havingValue = MessageQueuePipeline.KAFKA)
@Slf4j
public class ReceiverStart {

    /**
     * receiver的消费方法常量
     */
    private static final String RECEIVER_METHOD_NAME = "Receiver.consumer";
    /**
     * 获取得到所有的groupId
     */
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroupIds();

    /**
     * 下标 用于迭代gropIds位置
     */
    private static Integer index = 0;
    
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ConsumerFactory consumerFactory;

    /**
     * ReceiverStart的init方法会初始化Receiver消费者（有多少个groupId就会初始化多少个消费者）。
     * 而groupIdEnhancer实际上就是每一个Receiver初始化的时候做了动态的切面，
     * 拿到对应的@KafkaListener注解，修改其groupId。
     */
    
    /**
     * 为每个渠道不同的消息类型 创建一个Receiver对象
     */
    @PostConstruct
    public void init(){
        for (int i = 0; i < groupIds.size(); i++){
            context.getBean(Receiver.class);
        }
    }

    /**
     * 给每个Receiver对象的consumer方法 @KafkaListener赋值相应的groupId
     * 翻阅官方文档的方法
     */
    @Bean
    public static KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer groupIdEnhancer() {
        return (attrs, element) -> {
            if (element instanceof Method) {
                String name = ((Method) element).getDeclaringClass().getSimpleName() + StrPool.DOT + ((Method) element).getName();
                if (RECEIVER_METHOD_NAME.equals(name)) {
                    attrs.put("groupId", groupIds.get(index++));
                }
            }
            return attrs;
        };
    }
    
    /**
     * 针对tag消息过滤
     * producer 将tag写进header里
     *
     * @return true 消息将会被丢弃
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory filterContainerFactory(@Value("${austin.business.tagId.key}") String tagIdKey,
                                                                          @Value("${austin.business.tagId.value}") String tagIdValue) {
        
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        factory.setAckDiscarded(true);
        
        factory.setRecordFilterStrategy(consumerRecord -> {
            if (Optional.ofNullable(consumerRecord.value()).isPresent()){
                for (Header header : consumerRecord.headers()){
                    if(header.key().equals(tagIdKey) && new String(header.value()).equals(new String(tagIdValue.getBytes(StandardCharsets.UTF_8)))){
                        return false;
                    }
                }
            }
            return true;
        });
        return factory;
    }
}
