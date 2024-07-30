package com.javayh.austin.cron.xxl.enums;

/**
 * 调度过期策略
 * @author yh
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW;

    MisfireStrategyEnum() {
    }
}
