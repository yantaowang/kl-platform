package com.kl.redis.starter.util;


import com.kl.core.thread.KlThreadLocal;

public class RedisUtil {
    public RedisUtil() {
    }

    public static String getKeyPrefix(String key) {
        return KlThreadLocal.getTenantIdNotNull() + ":" + key;
    }
}