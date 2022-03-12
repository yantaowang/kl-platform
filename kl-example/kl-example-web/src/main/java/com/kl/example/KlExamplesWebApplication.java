package com.kl.example;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableDiscoveryClient
@SpringBootApplication
@MapperScan({"com.kl.example.service.data.*"})
@Slf4j
public class KlExamplesWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(KlExamplesWebApplication.class, args);
        log.info("KlExamplesWebApplication启动完成");
    }

}
