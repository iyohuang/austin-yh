package com.javayh.austin.handler.receiver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.javayh.austin.common.domain.AnchorInfo;
import com.javayh.austin.common.domain.LogParam;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.AnchorState;
import com.javayh.austin.handler.handler.HandlerHolder;
import com.javayh.austin.handler.pending.Task;
import com.javayh.austin.handler.pending.TaskPendingHolder;
import com.javayh.austin.handler.receiver.service.ConsumeService;
import com.javayh.austin.handler.utils.GroupIdMappingUtils;
import com.javayh.austin.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yh
 */
@Service
public class ConsumeServiceImpl implements ConsumeService {

    private static final String LOG_BIZ_TYPE = "Receiver#consumer";

    private ApplicationContext context;
    
    @Autowired
    private TaskPendingHolder taskPendingHolder;
    
    @Autowired
    private LogUtils logUtils;
    
    @Autowired
    private HandlerHolder handlerHolder;
    @Override
    public void consume2Send(List<TaskInfo> taskInfoLists) {
        String topicGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
        for (TaskInfo taskInfo : taskInfoLists) {
            logUtils.print(LogParam.builder()
                    .bizType(LOG_BIZ_TYPE)
                    .object(taskInfo).build(),
                    AnchorInfo.builder()
                            .bizId(taskInfo.getBizId())
                            .messageId(taskInfo.getMessageId())
                            .ids(taskInfo.getReceiver())
                            .businessId(taskInfo.getBusinessId())
                            .state(AnchorState.RECEIVE.getCode()).build());
            Task task = context.getBean(Task.class).setTaskInfo(taskInfo);
            taskPendingHolder.route(topicGroupId).execute(task);            
        }
    }
}
