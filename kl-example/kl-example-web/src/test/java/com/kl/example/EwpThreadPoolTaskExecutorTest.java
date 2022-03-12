package com.kl.example;

import com.kl.core.thread.KlThreadLocal;
import com.kl.core.thread.KlThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

/**
 * @author : yuezhenyu
 * @className : EwpThreadPoolTaskExecutorTest
 * @since : 2021/09/28 下午6:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootApplication.class)
@Slf4j
public class EwpThreadPoolTaskExecutorTest {

    @Test
    public void testEwpThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor ewpThreadPoolTaskExecutor = KlThreadPoolTaskExecutor.newInstance(1, 20, "test-thread-");
        KlThreadLocal.setTenantId(111);
        IntStream.range(1, 1200).forEach(value -> {
            ewpThreadPoolTaskExecutor.execute(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                log.info(KlThreadLocal.getTenantId() + "---" + value);
            });
        });

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}