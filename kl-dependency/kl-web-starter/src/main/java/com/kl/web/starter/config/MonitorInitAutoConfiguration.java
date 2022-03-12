//package com.kl.web.starter.config;
//
//
//import com.alibaba.cloud.dubbo.metadata.repository.DubboServiceMetadataRepository;
//import com.alibaba.fastjson.JSON;
//import com.kl.core.util.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.info.GitProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//
///**
// * 服务监控地址打印初始化
// */
//@Configuration
//@Slf4j
//@Order(10)
//public class MonitorInitAutoConfiguration implements CommandLineRunner {
//
//    @Autowired
//    private GitProperties git;
//
//    @Value("${server.port:8080}")
//    private String port;
//
//    @Value("${server.servlet.context-path:}")
//    public String contextPath;
//
//    @Value("${spring.application.name:}")
//    public String applicationName;
//
//
//    @Autowired(required = false)
//    private DubboServiceMetadataRepository dubboServiceMetadataRepository;
//
//    // 服务git版本信息
//    private static String format = "http://localhost:%s%s/actuator/info";
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("[startUpSuccess]{}服务启动完成,actuator监控地址:{} , git版本信息 : {}", applicationName, String.format(format, port, contextPath), git == null ? "" : JsonUtils.objectToJson(git));
//        try {
//            if (dubboServiceMetadataRepository != null) {
//                log.info("[startUpDubboDone]:{}", JSON.toJSONString(dubboServiceMetadataRepository.getSubscribedServices()));
//            }
//        } catch (Exception e) {
//            log.error("[startUpDubboDone]", e);
//        }
//    }
//
//}
