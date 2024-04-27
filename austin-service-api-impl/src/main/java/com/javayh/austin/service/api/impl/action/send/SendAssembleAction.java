package com.javayh.austin.service.api.impl.action.send;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.ReflectUtils;
import com.alibaba.nacos.shaded.io.grpc.internal.JsonUtil;
import com.google.common.base.Throwables;
import com.javayh.austin.common.constant.CommonConstant;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.dto.model.ContentModel;
import com.javayh.austin.common.enums.ChannelType;
import com.javayh.austin.common.enums.RespStatusEnum;
import com.javayh.austin.common.pipeline.BusinessProcess;
import com.javayh.austin.common.pipeline.ProcessContext;
import com.javayh.austin.common.vo.BasicResultVO;
import com.javayh.austin.service.api.domain.MessageParam;
import com.javayh.austin.service.api.impl.domain.SendTaskModel;
import com.javayh.austin.support.dao.MessageTemplateDao;
import com.javayh.austin.support.domain.MessageTemplate;
import com.javayh.austin.support.utils.ContentHolderUtil;
import com.javayh.austin.support.utils.TaskInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author yh
 *
 * @description 拼装参数
 */
@Slf4j
@Service
public class SendAssembleAction implements BusinessProcess<SendTaskModel> {
    private static final String LINK_NAME = "url";

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    /**
     * 获取 contentModel，替换模板msgContent中占位符信息
     */
    private static ContentModel getContentModelValue(MessageTemplate messageTemplate, MessageParam messageParam){
        // 得到真正的ContentModel 类型
        Integer sendChannel = messageTemplate.getSendChannel();
        Class<? extends ContentModel> contentModelClass = ChannelType.getChanelModelClassByCode(sendChannel);

        // 得到模板的 msgContent 和 入参
        Map<String, String> variables = messageParam.getVariables();
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent());

        // 通过反射 组装出 contentModel
        Field[] fields = ReflectUtil.getFields(contentModelClass);
        ContentModel contentModel = ReflectUtil.newInstance(contentModelClass);

        for (Field field : fields) {
            String originValue = jsonObject.getString(field.getName());
            if(CharSequenceUtil.isNotBlank(originValue)){
                String resultValue = ContentHolderUtil.replacePlaceHolder(originValue,variables);
                Object resultObj = JSONUtil.isJsonObj(resultValue) ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;
                ReflectUtil.setFieldValue(contentModel, field.getName(), resultObj);
            }
        }
        // 如果 url 字段存在，则在url拼接对应的埋点参数
        String url = (String)ReflectUtil.getFieldValue(contentModel, LINK_NAME);
        if(CharSequenceUtil.isNotBlank(url)){
            String resultUrl = TaskInfoUtils.generateUrl(url, messageTemplate.getId(), messageTemplate.getTemplateType());
            ReflectUtil.setFieldValue(contentModel, LINK_NAME, resultUrl);
        }
        return contentModel;
    }
    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();
        try {
            Optional<MessageTemplate> messageTemplate = messageTemplateDao.findById(messageTemplateId);
            
            if (!messageTemplate.isPresent()||messageTemplate.get().getIsDeleted().equals(CommonConstant.TRUE)){
                context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TEMPLATE_NOT_FOUND));
                return;
            }
            
            List<TaskInfo> taskInfoList = assembleTaskInfo(sendTaskModel, messageTemplate.get());
            sendTaskModel.setTaskInfo(taskInfoList);
        }catch (Exception e){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("assemble task fail! templateId:{}, e:{}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 拼装 TaskInfo
     * @param sendTaskModel
     * @param messageTemplate
     * @return
     */
    private List<TaskInfo> assembleTaskInfo(SendTaskModel sendTaskModel,MessageTemplate messageTemplate){
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        List<TaskInfo> taskInfoList = new ArrayList<>();
        for (MessageParam messageParam : messageParamList){
            
            TaskInfo taskInfo = TaskInfo.builder()
                    .messageId(TaskInfoUtils.generateMessageId())
                    .bizId(messageParam.getBizId())
                    .messageTemplateId(messageTemplate.getId())
                    .businessId(TaskInfoUtils.generateBusinessId(messageTemplate.getId(), messageTemplate.getTemplateType()))
                    .receiver(new HashSet<>(Arrays.asList(messageParam.getReceiver().split(String.valueOf(StrPool.C_COMMA)))))
                    .idType(messageTemplate.getIdType())
                    .sendChannel(messageTemplate.getSendChannel())
                    .templateType(messageTemplate.getTemplateType())
                    .msgType(messageTemplate.getMsgType())
                    .shieldType(messageTemplate.getShieldType())
                    .sendAccount(messageTemplate.getSendAccount())
                    .contentModel(getContentModelValue(messageTemplate, messageParam)).build();

            //生成id业务消息发送Id, 用于链路追踪，若不存在, 则使用 messageId
            if (CharSequenceUtil.isBlank(taskInfo.getBizId())) {
                taskInfo.setBizId(taskInfo.getMessageId());
            }
            taskInfoList.add(taskInfo);
        }
        return taskInfoList;
    }
}
