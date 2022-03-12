package com.kl.mq.starter.config;

import com.kl.mq.starter.jackson.SpringMimeTypeDeserializer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.activation.MimeType;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class KlJacksonAutoConfiguration {
    @Bean
    public Module springMimeTypeModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MimeType.class, new SpringMimeTypeDeserializer());
        return module;
    }
}
