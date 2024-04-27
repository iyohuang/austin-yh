package com.javayh.austin.handler.mq;

/**
 * @author yh
 * 发送数据至消息队列
 */
public interface SendMqService {
    /**
     * 
     * @param topic
     * @param jsonValue
     * @param tag
     */
    void send(String topic,String jsonValue,String tag);

    /**
     * 
     * @param topic
     * @param jsonValue
     */
    void send(String topic,String jsonValue);
    
}
