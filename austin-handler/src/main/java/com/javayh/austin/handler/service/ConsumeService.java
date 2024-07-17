package com.javayh.austin.handler.service;

import com.javayh.austin.common.domain.TaskInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消费消息服务
 *
 * @author yh
 */

@Service
public interface ConsumeService {

    /**
     * 从MQ拉到消息进行消费，发送消息
     *
     * @param taskInfoLists
     */
    void consume2Send(List<TaskInfo> taskInfoLists);
    
}
