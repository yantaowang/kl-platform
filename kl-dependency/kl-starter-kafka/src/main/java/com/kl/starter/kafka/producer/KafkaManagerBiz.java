package com.kl.starter.kafka.producer;

import com.kl.core.util.JsonUtils;
import com.kl.starter.kafka.enums.KafkaCMDTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class KafkaManagerBiz {
    private static final Logger log = LoggerFactory.getLogger(KafkaManagerBiz.class);
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public KafkaManagerBiz(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public <T> boolean sendMessage(String topic, long id, int tenantId, Object data, KafkaCMDTypeEnum cmd) {
        KafkaCommonData<Object> kafaData = new KafkaCommonData(id, data, cmd.type());
        return this.sendMessage(topic, tenantId, kafaData);
    }

    public <T> boolean sendMessage(final String topic, final int tenantId, KafkaCommonData<T> kafaData) {
        kafaData.setTenantId(tenantId);
        boolean succeed = true;
        final String message = JsonUtils.objectToJson(kafaData, JsonUtils.DateFormatStr.NORMAL_MILLISECOND);
        final String traceId = MDC.get("trace_id");

        try {
            Message<?> msg = MessageBuilder.withPayload(message).setHeader("kafka_topic", topic).setHeader("trace_id", traceId).setHeader("partner_code", tenantId).build();
            this.kafkaTemplate.send(msg).addCallback(new ListenableFutureCallback() {
                public void onSuccess(Object o) {
                    try {
                        MDC.put("trace_id", traceId);
                        KafkaManagerBiz.log.info("[sendKafkaMessage] [sendSuccess] [tenantId: {}],[traceId: {}],[topic: {}],message: {}", new Object[]{tenantId, traceId, topic, message});
                    } finally {
                        MDC.remove("trace_id");
                    }

                }

                public void onFailure(Throwable throwable) {
                    try {
                        MDC.put("trace_id", traceId);
                        KafkaManagerBiz.log.error("[sendKafkaMessage] [sendError] [tenantId: {}],[traceId:{}],[topic: {}],message: {}", new Object[]{tenantId, traceId, topic, message, throwable});
                    } finally {
                        MDC.remove("trace_id");
                    }

                }
            });
        } catch (Exception var8) {
            log.error("[sendKafkaMessage] [sendError] [tenantId: {}] [traceId: {}],[topic: {}],{}", new Object[]{tenantId, traceId, topic, message, var8});
            succeed = false;
        }

        return succeed;
    }
}

