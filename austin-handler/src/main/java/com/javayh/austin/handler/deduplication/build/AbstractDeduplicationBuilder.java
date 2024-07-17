package com.javayh.austin.handler.deduplication.build;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.deduplication.DeduplicationHolder;
import com.javayh.austin.handler.deduplication.DeduplicationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * 抽象类
 * 
 * @author yh
 */
@Component
public abstract class AbstractDeduplicationBuilder implements Builder {
    
    protected Integer deduplicationType;

    @Autowired
    private DeduplicationHolder deduplicationHolder;
    
    @PostConstruct
    public void init(){
        deduplicationHolder.putBuilder(deduplicationType, this);
    }
    
    public DeduplicationParam getParamsFromConfig(Integer key, String duplicationConfig, TaskInfo taskInfo){
        JSONObject object = JSON.parseObject(duplicationConfig);
        if (Objects.isNull(object)){
            return null;
        }
        DeduplicationParam deduplicationParam = JSON.parseObject(object.getString(DEDUPLICATION_CONFIG_PRE + key), DeduplicationParam.class);
        if (Objects.isNull(deduplicationParam)){
            return null;
        }
        deduplicationParam.setTaskInfo(taskInfo);
        return deduplicationParam;
    }
}
