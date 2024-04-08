package com.javayh.austin.support.mq.springeventbus;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author yh
 *
 */
@Getter
public class AustinSpringEventBusEvent extends ApplicationEvent {

    private AustinSpringEventSource austinSpringEventSource;

    public AustinSpringEventBusEvent(Object source, AustinSpringEventSource austinSpringEventSource) {
        super(source);
        this.austinSpringEventSource = austinSpringEventSource;
    }

}
