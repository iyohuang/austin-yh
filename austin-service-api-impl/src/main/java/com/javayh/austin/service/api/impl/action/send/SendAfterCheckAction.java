package com.javayh.austin.service.api.impl.action.send;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.IdType;
import com.javayh.austin.common.enums.RespStatusEnum;
import com.javayh.austin.common.pipeline.BusinessProcess;
import com.javayh.austin.common.pipeline.ProcessContext;
import com.javayh.austin.common.vo.BasicResultVO;
import com.javayh.austin.service.api.impl.domain.SendTaskModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yh
 * 
 * 后置参数检查
 */
@Slf4j
@Service
public class SendAfterCheckAction implements BusinessProcess<SendTaskModel> {
    /**
     * 邮件和手机号正则
     */
    public static final String PHONE_REGEX_EXP = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$";
    public static final String EMAIL_REGEX_EXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    protected static final Map<Integer, String> CHANNEL_REGEX_EXP = new HashMap<>();
    
    static {
        CHANNEL_REGEX_EXP.put(IdType.PHONE.getCode(), PHONE_REGEX_EXP);
        CHANNEL_REGEX_EXP.put(IdType.EMAIL.getCode(), EMAIL_REGEX_EXP);
    }

    /**
     * 过滤不合法的
     * @param context
     */
    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel processModel = context.getProcessModel();
        List<TaskInfo> taskInfos = processModel.getTaskInfo();
        
        filterIllegalReceiver(taskInfos);
        if(CollUtil.isEmpty(taskInfos)){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS, "手机号或邮箱不合法, 无有效的发送任务"));
        }
    }

    /**
     * 根据是邮箱还是手机进行过滤
     * @param taskInfos
     */
    private void filterIllegalReceiver(List<TaskInfo> taskInfos){
        Integer idType = CollUtil.getFirst(taskInfos.iterator()).getIdType();
        filter(taskInfos,CHANNEL_REGEX_EXP.get(idType));
    }

    /**
     * 过滤具体逻辑
     * @param taskInfos
     * @param regexExp
     */
    private void filter(List<TaskInfo> taskInfos, String regexExp){
        Iterator<TaskInfo> iterator = taskInfos.iterator();
        while(iterator.hasNext()){
            TaskInfo taskInfo = iterator.next();
            Set<String> illegalPhone = taskInfo.getReceiver().stream()
                    .filter(x -> !ReUtil.isMatch(regexExp, x))
                    .collect(Collectors.toSet());
            if(CollUtil.isNotEmpty(illegalPhone)){
                taskInfo.getReceiver().removeAll(illegalPhone);
                log.error("messageTemplateId:{} find illegal receiver!{}", taskInfo.getMessageTemplateId(), JSON.toJSONString(illegalPhone));
            }
            if(CollUtil.isEmpty(taskInfo.getReceiver())){
                iterator.remove();
            }
        }
    }
}
