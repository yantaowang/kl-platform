package com.kl.starter.kafka.config;


import com.kl.starter.kafka.aop.KafkaConsumerAop;
import com.kl.starter.kafka.producer.KafkaManagerBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

@Configuration
@ConditionalOnProperty(
        name = {"spring.kafka.bootstrap-servers"}
)
public class KafkaManagerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KafkaManagerAutoConfiguration.class);
    @Autowired
    private KafkaProperties kafkaProperties;

    public KafkaManagerAutoConfiguration() {
    }

    public Map<String, Object> consumerProperties() {
        return this.kafkaProperties.buildConsumerProperties();
    }

    @Bean
    public KafkaConsumerAop buildKafkaConsumerAop() {
        return new KafkaConsumerAop();
    }

    @Bean
    public KafkaManagerBiz buildKafkaManagerBiz(KafkaTemplate kafkaTemplate) {
        return new KafkaManagerBiz(kafkaTemplate);
    }

    @Bean({"consumerFactory"})
    @Primary
    public DefaultKafkaConsumerFactory consumerFactory() {
        return new DefaultKafkaConsumerFactory(this.consumerProperties());
    }

    @Bean({"listenerContainerFactory"})
    public ConcurrentKafkaListenerContainerFactory listenerContainerFactory(DefaultKafkaConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        return factory;
    }

    private Map<String, Object> producerProperties() {
        Map<String, Object> producerProperties = this.kafkaProperties.buildProducerProperties();
        return producerProperties;
    }

    @Bean({"produceFactory"})
    public DefaultKafkaProducerFactory produceFactory() {
        return new DefaultKafkaProducerFactory(this.producerProperties());
    }

    @Bean
    public KafkaTemplate kafkaTemplate(DefaultKafkaProducerFactory produceFactory) {
        return new KafkaTemplate(produceFactory);
    }
}
