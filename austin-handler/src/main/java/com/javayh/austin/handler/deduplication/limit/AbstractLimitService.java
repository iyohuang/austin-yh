package com.javayh.austin.handler.deduplication.limit;

import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.deduplication.DeduplicationParam;
import com.javayh.austin.handler.deduplication.service.AbstractDeduplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yh
 * 去重服务
 */
public abstract class AbstractLimitService implements LimitService{
    
    protected List<String> deduplicationAllKey(AbstractDeduplicationService service, TaskInfo taskInfo){
        List<String> result = new ArrayList<>(taskInfo.getReceiver().size());
        for (String receiver : taskInfo.getReceiver()){
            String key = deduplicationSingleKey(service, taskInfo, receiver);
            result.add(key);
        }
        return result;
    }

    protected String deduplicationSingleKey(AbstractDeduplicationService service, TaskInfo taskInfo, String receiver) {
        return service.deduplicationSingleKey(taskInfo, receiver);
    }
}
