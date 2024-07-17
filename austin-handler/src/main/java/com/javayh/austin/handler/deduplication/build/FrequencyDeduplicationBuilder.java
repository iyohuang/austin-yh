package com.javayh.austin.handler.deduplication.build;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.AnchorState;
import com.javayh.austin.common.enums.DeduplicationType;
import com.javayh.austin.handler.deduplication.DeduplicationParam;

import java.util.Date;
import java.util.Objects;

/**
 * @author yh
 */
public class FrequencyDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder {
    
    public FrequencyDeduplicationBuilder(){
        deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }
    
    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {

        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if(Objects.isNull(deduplicationParam)){
            return null;
        }
        deduplicationParam.setDeduplicationTime((DateUtil.endOfDay(new Date()).getTime() - DateUtil.current()) / 1000);
        deduplicationParam.setAnchorState(AnchorState.RULE_DEDUPLICATION);
        return deduplicationParam;
    }
}
