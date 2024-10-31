package com.javayh.austin.cron.handler;

import com.dtp.core.thread.DtpExecutor;
import com.javayh.austin.cron.config.CronAsyncThreadPoolConfig;
import com.javayh.austin.cron.service.TaskHandler;
import com.javayh.austin.support.utils.ThreadPoolUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 后台提交的定时任务处理类
 * @author yh
 */
@Service
@Slf4j
public class CronTaskHandler {
    
    @Autowired
    private TaskHandler taskHandler;
    
    @Autowired
    private ThreadPoolUtils threadPoolUtils;
    
    private DtpExecutor dtpExecutor = CronAsyncThreadPoolConfig.getXxlCronExecutor();
    
    @XxlJob("austinJob")
    public void execute(){
        log.info("CronTaskHandler#execute messageTemplateId:{} cron exec!", XxlJobHelper.getJobParam());
        threadPoolUtils.register(dtpExecutor);
        Long messageTemplateId = Long.valueOf(XxlJobHelper.getJobParam());
        dtpExecutor.execute(() -> taskHandler.handle(messageTemplateId));
    }
    
    
}
