package com.javayh.austin.handler.deduplication.service;

import com.javayh.austin.handler.deduplication.DeduplicationParam;

/**
 * @author yh
 */
public interface DeduplicationService {

    /**
     * 去重
     *
     * @param param
     */
    void deduplication(DeduplicationParam param);
    
}
