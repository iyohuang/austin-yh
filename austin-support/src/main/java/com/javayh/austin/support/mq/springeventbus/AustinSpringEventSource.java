package com.javayh.austin.support.mq.springeventbus;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yh
 */
@Data
@Builder
public class AustinSpringEventSource implements Serializable {
    private String topic;
    private String jsonValue;
    private String tagId;
}
