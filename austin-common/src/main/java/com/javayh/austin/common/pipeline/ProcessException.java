package com.javayh.austin.common.pipeline;

import com.javayh.austin.common.enums.RespStatusEnum;

import java.util.Objects;

/**
 * @author yh
 *
 */
public class ProcessException extends RuntimeException {

    /**
     * 流程处理上下文
     */
    private final ProcessContext processContext;

    public ProcessException(ProcessContext processContext) {
        super();
        this.processContext = processContext;
    }

    public ProcessException(ProcessContext processContext, Throwable cause) {
        super(cause);
        this.processContext = processContext;
    }

    @Override
    public String getMessage() {
        if (Objects.nonNull(this.processContext)) {
            return this.processContext.getResponse().getMsg();
        }
        return RespStatusEnum.CONTEXT_IS_NULL.getMsg();

    }

    public ProcessContext getProcessContext() {
        return processContext;
    }
}
