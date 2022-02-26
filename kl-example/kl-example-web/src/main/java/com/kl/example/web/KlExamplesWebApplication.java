package com.kl.example.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
public class KlExamplesWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(KlExamplesWebApplication.class, args);
        log.info("KlExamplesWebApplication启动完成");
    }

}
