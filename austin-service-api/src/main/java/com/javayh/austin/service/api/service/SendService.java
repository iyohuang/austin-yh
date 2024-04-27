package com.javayh.austin.service.api.service;

import com.javayh.austin.service.api.domain.BatchSendRequest;
import com.javayh.austin.service.api.domain.SendRequest;
import com.javayh.austin.service.api.domain.SendResponse;

public interface SendService {
    /**
     * 单文案发送接口
     *
     * @param sendRequest eg:    {"code":"send","messageParam":{"bizId":null,"extra":null,"receiver":"717146638@qq.com","variables":null},"messageTemplateId":17,"recallMessageId":null}
     * @return SendResponse eg:    {"code":"0","data":[{"bizId":"ecZim2-FzdejNSY-sqgCM","businessId":2000001720230815,"messageId":"ecZim2-FzdejNSY-sqgCM"}],"msg":"操作成功"}
     */
    SendResponse send(SendRequest sendRequest);


    /**
     * 多文案发送接口
     *
     * @param batchSendRequest
     * @return
     */
    SendResponse batchSend(BatchSendRequest batchSendRequest);
}
