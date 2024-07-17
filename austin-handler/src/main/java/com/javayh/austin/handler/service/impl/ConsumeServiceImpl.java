package com.javayh.austin.handler.service.impl;

import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.handler.Handler;
import com.javayh.austin.handler.handler.HandlerHolder;
import com.javayh.austin.handler.pending.TaskPendingHolder;
import com.javayh.austin.handler.service.ConsumeService;
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
        
    }
}
