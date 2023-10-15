package com.kl.promethues.service;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PrometheusMeter {

    @Autowired
    private CollectorRegistry collectorRegistry;

    // 定义name为prometheus_counter的counter
    public Counter prometheusCounter(){
        return Counter.build().name("prometheus_counter").help("prometheus counter test")
                .register(collectorRegistry);
    }

    @PostConstruct
    public void init(){
        Counter counter = prometheusCounter();
        new Thread(()-> {
            while (true){
                counter.inc();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
