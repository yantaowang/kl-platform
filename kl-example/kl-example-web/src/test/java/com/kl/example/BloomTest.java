package com.kl.example;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import com.kl.example.service.redis.RedissonBloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KlExamplesWebApplication.class)
@Slf4j
public class BloomTest {

    @Resource
    private RedissonBloomFilterService redissonBloomFilterService;

    public static void main(String[] args) {
        BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(10);
        bitMapBloomFilter.add("abc");
        bitMapBloomFilter.add("123");
        bitMapBloomFilter.add("cde");
        System.out.println(bitMapBloomFilter.contains("p"));
    }

    @Test
    public void testRedissonBloom() throws InterruptedException {
        //创建过滤器
        redissonBloomFilterService.initNewFilter("bloomkey1", 60L, TimeUnit.SECONDS, 50, 0.1);
        redissonBloomFilterService.initNewFilter("bloomkey2", 60L, TimeUnit.SECONDS, 50, 0.1);

        for (int i = 0; i < 50; i++) {
            redissonBloomFilterService.add("bloomkey1", "" + i);
            if (i > 30) {
                redissonBloomFilterService.add("bloomkey2", "" + i);
            }
        }
        boolean exists = redissonBloomFilterService.containsValue("bloomkey1", "" + 1);

        CountDownLatch beginCount = new CountDownLatch(1);
        beginCount.await();
    }
}
