package com.kl.redis.starter.serializer;


import com.kl.redis.starter.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class PartnerStringRedisSerializer extends StringRedisSerializer {
    private static final Logger log = LoggerFactory.getLogger(PartnerStringRedisSerializer.class);
    private String mode;

    public PartnerStringRedisSerializer(String mode) {
        this.mode = mode;
    }

    public String deserialize(byte[] bytes) {
        return RedisUtil.getKeyPrefix(super.deserialize(bytes));
    }

    public byte[] serialize(String string) {
        return super.serialize(RedisUtil.getKeyPrefix(string));
    }
}
