package com.kl.starter.rocketmq.producer;

import com.kl.starter.rocketmq.aop.RocketMqConsumerAop;
import com.kl.starter.rocketmq.config.RocketmqManagerBiz;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "rocketmq.name-server")
public class RocketmqManagerAutoConfiguration {

    @Bean
    public RocketMqConsumerAop buildRocketMqConsumerAop() {
        return new RocketMqConsumerAop();
    }

    @Bean
    public RocketmqManagerBiz buildRocketmqManagerBiz(RocketMQTemplate rocketMQTemplate) {
        return new RocketmqManagerBiz(rocketMQTemplate);
    }

}