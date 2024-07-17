package com.javayh.austin.handler.utils;

import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.common.enums.ChannelType;
import com.javayh.austin.common.enums.EnumUtil;
import com.javayh.austin.common.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * groupId 标识着每一个消费者组
 *
 * @author yh
 */
public class GroupIdMappingUtils {

    private GroupIdMappingUtils() {
    }
    
    /**
     * 获取所有的groupIds
     * (不同的渠道不同的消息类型拥有自己的groupId)
     */
    public static List<String> getAllGroupIds(){
        List<String> groupIds = new ArrayList<>();
        for (ChannelType channelType : ChannelType.values()){
            for(MessageType messageType : MessageType.values()){
                groupIds.add(channelType.getCodeEn()+"."+messageType.getCodeEn());
            }
        }
        return groupIds;
    }

    /**
     * 根据taskInfo获取groupId
     * @param taskInfo
     * @return
     */
    public static String getGroupIdByTaskInfo(TaskInfo taskInfo){
        String channelCode = EnumUtil.getEnumByCode(taskInfo.getSendChannel(),ChannelType.class).getCodeEn();
        String msgCodeEn = EnumUtil.getEnumByCode(taskInfo.getMsgType(),MessageType.class).getCodeEn();
        return channelCode+"."+msgCodeEn;
    }
    
}
