package com.javayh.austin.cron.xxl.enums;

/**
 * 调度类型
 * @author yh
 */
public enum ScheduleTypeEnum {

    /**
     * NONE
     */
    NONE,
    /**
     * schedule by cron
     */
    CRON,

    /**
     * 按固定速率调度(以秒为单位)
     */
    FIX_RATE;

    ScheduleTypeEnum() {
    }
}
