package com.kl.redis.starter.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * @mudule: 聊天室限流器
 */
@Slf4j
public class RedisLimiter {

    /**
     * Redis模板
     */
    private StringRedisTemplate stringRedisTemplate = null;

    /**
     * Redis脚本
     */
    private static final DefaultRedisScript<Long> limiterScript = new DefaultRedisScript<>();

    static {
        String script = "local bucket_capacity = tonumber(ARGV[1])\n" +
                "local add_token = tonumber(ARGV[2])\n" +
                "local add_interval = tonumber(ARGV[3])\n" +
                "local now = tonumber(ARGV[4])\n" +
                "\n" +
                "local LAST_TIME_KEY = KEYS[1]..\"_time\";\n" +
                "local token_cnt = redis.call(\"get\", KEYS[1])\n" +
                "local reset_time = math.ceil(bucket_capacity / add_token) * add_interval;\n" +
                "\n" +
                "if token_cnt then\n" +
                "    local last_time = redis.call('get', LAST_TIME_KEY)\n" +
                "    local multiple = math.floor((now - last_time) / add_interval)\n" +
                "    local recovery_cnt = multiple * add_token\n" +
                "    local token_cnt = math.min(bucket_capacity, token_cnt + recovery_cnt) - 1\n" +
                "    if token_cnt < 0 then\n" +
                "        return -1;\n" +
                "    end\n" +
                "    redis.call('set', KEYS[1], token_cnt, 'EX', reset_time)\n" +
                "    redis.call('set', LAST_TIME_KEY, last_time + multiple * add_interval, 'EX', reset_time)\n" +
                "    return token_cnt\n" +
                "\n" +
                "else\n" +
                "    token_cnt = bucket_capacity - 1\n" +
                "    redis.call('set', KEYS[1], token_cnt, 'EX', reset_time);\n" +
                "    redis.call('set', LAST_TIME_KEY, now, 'EX', reset_time + 1);\n" +
                "    return token_cnt\n" +
                "end";
        limiterScript.setScriptText(script);
        limiterScript.setResultType(Long.class);
    }

    public RedisLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 基于redis的分布式限流器
     * 调用完毕后返回剩余次数
     * 若不允许则返回-1
     *
     * @return
     */
    public Long pass(String keyPattern, BucketInitModel parameter) {
        // 初始化
        List<String> keyList = new ArrayList<>();
        // 添加主键
        keyList.add(String.format(keyPattern, parameter.getKey()));
        // 执行脚本
        Long res = stringRedisTemplate.execute(limiterScript
                , keyList, parameter.getBucketCapacity(), parameter.getAddToken(), parameter.getAddInterval(), String.valueOf(System.currentTimeMillis()));
        return res;
    }

    @Data
    public static class BucketInitModel {
        /**
         * 令牌桶最大容量
         */
        private String bucketCapacity = "40";
        /**
         * 每次添加令牌的个数
         */
        private String addToken = "4";

        /**
         * 多少毫秒添加一次令牌
         */
        private String addInterval = "100";
        /**
         * 令牌桶的key
         */
        private String key;
    }
}
