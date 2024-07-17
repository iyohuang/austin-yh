package com.javayh.austin.handler.handler;

import com.javayh.austin.common.domain.RecallTaskInfo;
import com.javayh.austin.common.domain.TaskInfo;

public interface Handler {

    /**
     * 处理器
     *
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     *
     * @param recallTaskInfo
     * @return
     */
    void recall(RecallTaskInfo recallTaskInfo);
    
}
