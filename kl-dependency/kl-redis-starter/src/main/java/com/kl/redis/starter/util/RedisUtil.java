package com.kl.redis.starter.util;

import com.kl.common.thread.KlThreadLocal;

public class RedisUtil {
    public RedisUtil() {
    }

    public static String getKeyPrefix(String key) {
        return KlThreadLocal.getPartnerCodeNotNull() + ":" + key;
    }
}