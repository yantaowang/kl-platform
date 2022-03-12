package com.kl.mq.starter.producer;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kl.core.constants.CommonConstants;
import com.kl.core.thread.KlThreadLocal;
import com.kl.core.util.JsonUtils;
import com.kl.core.util.TracIdUtil;
import com.kl.mq.starter.consts.MessageConsts;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.slf4j.MDC;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import cn.hutool.core.lang.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class KlMqTemplate {

    private StreamBridge streamBridge;
    private BindingServiceProperties bindingServiceProperties;
    private Map<String, Boolean> channelCache = new ConcurrentHashMap<String, Boolean>();

    /**
     * 发送普通消息
     *
     * @param destination
     * @param payload
     * @return
     */
    public boolean send(String destination, Object payload) {
        return sendDelay(destination, payload, 0);
    }

    /**
     * 发送普通消息（支持tags）
     *
     * @param destination
     * @param payload
     * @param tags
     * @return
     */
    public boolean send(String destination, Object payload, String tags) {
        return sendDelay(destination, payload, 0, tags);
    }

    /**
     * 发送延迟消息(级别)
     *
     * @param destination
     * @param payload
     * @param delayLevel
     * @return
     */
    public boolean sendDelay(String destination, Object payload, Integer delayLevel) {
        Message<String> message = null;
        boolean sendResult = false;
        try {
            message = preSend(destination, payload, delayLevel, null, null);
            sendResult = sendEnhance(destination, message);
            if (sendResult) {
                log.info(
                        "[KLMqTemplate] [mqType:{}] [send] [partnerCode:{}] [traceId:{}] [msgId:{}] [destination:{}]消息发送成功",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                        message.getHeaders().get(CommonConstants.TENANT_ID),
                        message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination);
            } else {
                log.error(
                        "[KLMqTemplate] [mqType:{}] [send] [partnerCode:{}] [traceId:{}] [msgId:{}] [destination:{}] [payload:{}]消息发送失败",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                        message.getHeaders().get(CommonConstants.TENANT_ID),
                        message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination,
                        message.getPayload());
            }
            return sendResult;
        } catch (Throwable e) {
            log.error(String.format(
                    "[KLMqTemplate] [mqType:%s] [send] [partnerCode:%s] [traceId:%s] [msgId:%s] [destination:%s] [payload:%s]消息发送失败",
                    message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                    message.getHeaders().get(CommonConstants.TENANT_ID),
                    message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                    message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination,
                    message.getPayload()), e);
            return false;
        }
    }

    /**
     * 发送延迟消息(级别、tags)
     *
     * @param destination
     * @param payload
     * @param delayLevel
     * @return
     */
    public boolean sendDelay(String destination, Object payload, Integer delayLevel, String tags) {
        Message<String> message = null;
        Boolean sendResult = false;
        try {
            message = preSend(destination, payload, delayLevel, null, tags);
            sendResult = sendEnhance(destination, message);
            if (sendResult) {
                log.info(
                        "[KLMqTemplate] [mqType:{}] [send] [partnerCode:{}] [traceId:{}] [msgId:{}] [destination:{}]消息发送成功",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                        message.getHeaders().get(CommonConstants.TENANT_ID),
                        message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination);
            } else {
                log.error(
                        "[KLMqTemplate] [mqType:{}] [send] [partnerCode:{}] [traceId:{}] [msgId:{}] [destination:{}] [payload:{}]消息发送失败",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                        message.getHeaders().get(CommonConstants.TENANT_ID),
                        message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination,
                        message.getPayload());
            }
            return sendResult;
        } catch (Throwable e) {
            log.error(String.format(
                    "[KLMqTemplate] [mqType:%s] [send] [partnerCode:%s] [traceId:%s] [msgId:%s] [destination:%s] [payload:%s]消息发送失败",
                    message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                    message.getHeaders().get(CommonConstants.TENANT_ID),
                    message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                    message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination,
                    message.getPayload()), e);
            return false;
        }
    }

    /**
     * 发送延迟消息(自定义延时)
     *
     * @param destination
     * @param payload
     * @param delayTime   单位ms
     * @param diff        true时，delayTime是相对时间 false时，delayTime是绝对时间 ,null时默认是相对时间
     * @return
     */
    public boolean sendDelay(String destination, Object payload, Long delayTime, Boolean diff) {
        Message<String> message = null;
        Boolean sendResult = false;
        try {
            if (null == diff) {
                diff = true;
            }
            if (!diff) {
                Long diffTime = delayTime - System.currentTimeMillis();
                delayTime = diffTime > 0 ? diffTime : 0;
            }
            message = preSend(destination, payload, 0, delayTime, null);
            sendResult = sendEnhance(destination, message);
            if (sendResult) {
                log.info(
                        "[KLMqTemplate] [mqType:{}] [send] [partnerCode:{}] [traceId:{}] [msgId:{}] [destination:{}]消息发送成功",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                        message.getHeaders().get(CommonConstants.TENANT_ID),
                        message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination);
            } else {
                log.error(
                        "[KLMqTemplate] [mqType:{}] [send] [partnerCode:{}] [traceId:{}] [msgId:{}] [destination:{}] [payload:{}]消息发送失败",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                        message.getHeaders().get(CommonConstants.TENANT_ID),
                        message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination,
                        message.getPayload());
            }
            return sendResult;
        } catch (Throwable e) {
            log.error(String.format(
                    "[KLMqTemplate] [mqType:%s] [send] [partnerCode:%s] [traceId:%s] [msgId:%s] [destination:%s] [payload:%s]消息发送失败",
                    message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER),
                    message.getHeaders().get(CommonConstants.TENANT_ID),
                    message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME),
                    message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER), destination,
                    message.getPayload()), e);
            return false;
        }
    }

    private Message<String> preSend(String destination, Object payload, Integer delayLevel, Long delayTime,
                                    String tags) {
        String traceId = MDC.get(TracIdUtil.LOGGER_ID_PARAM_NAME);
        Integer partnerCode = KlThreadLocal.getTenantIdNotNull();
        if (StringUtils.isBlank(traceId)) {
            traceId = TracIdUtil.creaTracId();
        }
        String messageBody = null;
        if (payload instanceof String) {
            messageBody = String.valueOf(payload);
        } else {
            messageBody = JsonUtils.objectToJson(payload, JsonUtils.DateFormatStr.NORMAL_MILLISECOND);
        }

        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(messageBody)
                .setHeader(MessageConsts.KL_MESSAGE_MSGID_HEADER, UUID.fastUUID().toString())
                .setHeader(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId)
                .setHeader(MessageConsts.KL_MESSAGE_DELAY_HEADER, delayLevel)
                .setHeader(MessageConsts.KL_MESSAGE_MQTYPE_HEADER, getBinderName(destination));
        if (null != partnerCode) {
            messageBuilder.setHeader(CommonConstants.TENANT_ID, partnerCode);
        }
        if (StringUtils.isNotBlank(tags)) {
            messageBuilder.setHeader(RocketMQHeaders.TAGS, tags);
        }
        if (null != delayTime) {
            messageBuilder.setHeader(MessageConsts.KL_MESSAGE_DELAY_TIME_HEADER, delayTime);
        }
        return messageBuilder.build();
    }

    private Boolean sendEnhance(String destination, Object message) {
        Boolean sendResult = true;
        if (!channelCache.containsKey(destination)) {
            synchronized (this) {
                sendResult = streamBridge.send(destination, message, MimeTypeUtils.TEXT_PLAIN);
                channelCache.put(destination, true);
            }
        } else {
            sendResult = streamBridge.send(destination, message, MimeTypeUtils.TEXT_PLAIN);
        }
        return sendResult;
    }

    private String getBinderName(String destination) {
        BindingProperties bindings = bindingServiceProperties.getBindingProperties(destination);
        if (null != bindings) {
            return StringUtils.isNotBlank(bindings.getBinder()) ? bindings.getBinder()
                    : bindingServiceProperties.getDefaultBinder();
        }
        return bindingServiceProperties.getDefaultBinder();
    }

}
