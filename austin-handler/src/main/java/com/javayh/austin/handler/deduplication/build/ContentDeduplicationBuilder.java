package com.javayh.austin.handler.deduplication.build;

import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.AnchorState;
import com.javayh.austin.common.enums.DeduplicationType;
import com.javayh.austin.handler.deduplication.DeduplicationParam;

import java.util.Objects;

/**
 * @author yh
 */
public class ContentDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder{
    
    public ContentDeduplicationBuilder(){
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }
    
    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if(Objects.isNull(deduplicationParam)){
            return null;
        }
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION);
        return deduplicationParam;
    }
}
