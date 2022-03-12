package com.kl.mq.starter.config;

import com.kl.core.constants.CommonConstants;
import com.kl.core.thread.KlThreadLocal;
import com.kl.core.util.TracIdUtil;
import com.kl.mq.starter.consts.MessageConsts;
import com.kl.mq.starter.producer.KlMqTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.cloud.stream", name = "enable", havingValue = "true")
public class KlMqAutoConfiguration {

    @ConditionalOnMissingBean(KlMqTemplate.class)
    @Bean
    public KlMqTemplate KlMqTemplate(StreamBridge streamBridge, BindingServiceProperties bindingServiceProperties) {
        KlMqTemplate KlMqTemplate = new KlMqTemplate();
        KlMqTemplate.setStreamBridge(streamBridge);
        bindingServiceProperties.setDynamicDestinationCacheSize(30);
        bindingServiceProperties.setBindingRetryInterval(3600 * 24);
        KlMqTemplate.setBindingServiceProperties(bindingServiceProperties);
        log.debug("KlMqTemplate init completed.");
        return KlMqTemplate;
    }

    @Bean
    @GlobalChannelInterceptor(patterns = "${spring.cloud.stream.channel.interceptor.patterns:*}")
    public ChannelInterceptor channelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                String traceId = String.valueOf(message.getHeaders().get(TracIdUtil.LOGGER_ID_PARAM_NAME));
                String partnerCode = (null == message.getHeaders().get(CommonConstants.TENANT_ID)) ? null
                        : String.valueOf(message.getHeaders().get(CommonConstants.TENANT_ID));
                if (StringUtils.isNotEmpty(partnerCode)) {
                    KlThreadLocal.setTenantId(Integer.parseInt(partnerCode));
                    MDC.put(CommonConstants.TENANT_ID, partnerCode);
                }
                MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
                log.info(
                        "[KlMqAutoConfiguration] [mqType:{}] [receive] [partnerCode:{}] [traceId:{}] [msgId:{}] [mqMsgId:{}]消息拦截器",
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MQTYPE_HEADER), partnerCode, traceId,
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_MSGID_HEADER),
                        message.getHeaders().get(MessageConsts.KL_MESSAGE_ROCKETMQ_MESSAGE_ID_HEADER));
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent,
                                            @Nullable Exception ex) {
                KlThreadLocal.remove();
                MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
                MDC.remove(CommonConstants.TENANT_ID);
            }

            @Override
            public boolean preReceive(MessageChannel channel) {
                return true;
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                return message;
            }

            @Override
            public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel,
                                               @Nullable Exception ex) {
            }
        };
    }
}
