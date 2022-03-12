package com.kl.starter.rocketmq.config;

import com.alibaba.fastjson.JSON;
import com.kl.core.constants.CommonConstants;
import com.kl.core.util.TracIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
public class RocketmqManagerBiz {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public RocketmqManagerBiz(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 发送异步消息
     *
     * @param topic       主题
     * @param partnerCode 合作商户标识
     * @param payload     消息内容
     * @return true: 成功 false：失败git
     */
    public boolean sendAsyncMessage(String topic, int partnerCode, Object payload) {
        String msg = JSON.toJSONString(payload);
        String traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
        try {
            Message<?> message = MessageBuilder.withPayload(msg).setHeader(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId).setHeader(CommonConstants.TENANT_ID, partnerCode).build();
            rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    handleSucess(topic, partnerCode, msg, traceId, sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    handleError(topic, partnerCode, msg, traceId, e);
                }
            });
        } catch (Exception e) {
            log.error("[rocketmqProducer] [sendMsgRocketmqError] [partnerCode: {}] [traceId]:{} Topic:{},payload:{}", partnerCode, traceId, topic, msg, e);
            return false;
        }
        return true;
    }

    /**
     * 发送同步消息
     *
     * @param topic       主题
     * @param partnerCode 合作商户标识
     * @param payload     消息内容
     * @return true: 成功 false：失败
     */
    public boolean sendSyncMessage(String topic, int partnerCode, Object payload) {
        String msg = JSON.toJSONString(payload);
        String traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
        try {
            Message<?> message = MessageBuilder.withPayload(msg).setHeader(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId).setHeader(CommonConstants.TENANT_ID, partnerCode).build();
            SendResult sendResult = rocketMQTemplate.syncSend(topic, message);
            return handleSucess(topic, partnerCode, msg, traceId, sendResult);
        } catch (Exception e) {
            log.error("[rocketmqProducer] [sendMsgRocketmqError] [partnerCode: {}] [traceId]:{} Topic:{},payload:{}", partnerCode, traceId, topic, msg, e);
        }
        return false;
    }

    /**
     * 成功日志打印
     *
     * @param topic      主题
     * @param msg        消息内容
     * @param traceId    traceId
     * @param sendResult 发送结果
     */
    private boolean handleSucess(String topic, int partnerCode, String msg, String traceId, SendResult sendResult) {
        try {
            MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                log.info("[rocketmqProducer] [sendMsgRocketmqSuccess] [partnerCode:{}] [traceId:{}] [msgId:{}] Topic:{},payload:{}", partnerCode, traceId, sendResult.getMsgId(), topic, msg);
                return true;
            }
            log.error("[rocketmqProducer] [sendMsgRocketmqError] [partnerCode:{}] [traceId:{}],[SendStatus:{}] Topic:{},payload:{}", partnerCode, traceId, sendResult.getSendStatus(), topic, msg);
        } finally {
            MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
        }
        return false;
    }

    /**
     * 失败日志打印
     *
     * @param topic   主题
     * @param msg     消息内容
     * @param traceId traceId
     * @param e       异常
     */
    private void handleError(String topic, int partnerCode, String msg, String traceId, Throwable e) {
        try {
            MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
            log.error("[rocketmqProducer] [sendMsgRocketmqError] [partnerCode: {}] [traceId]:{} Topic:{},payload:{}", partnerCode, traceId, topic, msg, e);
        } finally {
            MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
        }
    }
}