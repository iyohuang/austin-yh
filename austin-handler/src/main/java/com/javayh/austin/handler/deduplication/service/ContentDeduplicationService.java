package com.javayh.austin.handler.deduplication.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.DeduplicationType;
import com.javayh.austin.handler.deduplication.DeduplicationParam;
import com.javayh.austin.handler.deduplication.limit.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author yh
 * 内容去重服务（默认5分钟相同的文案发给相同的用户去重）
 */
@Service
public class ContentDeduplicationService extends AbstractDeduplicationService{

    @Autowired
    public ContentDeduplicationService (@Qualifier("SlideWindowLimitService") LimitService limitService){
        this.limitService = limitService;
        this.deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    /**
     * 内容去重 构建key
     * key: md5(templateId + receiver + content)
     * 相同的内容相同的模板短时间内发给同一个人
     *
     * @param taskInfo
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return DigestUtil.md5Hex(taskInfo.getMessageTemplateId() + receiver
                + JSON.toJSONString(taskInfo.getContentModel()));
    }
}
