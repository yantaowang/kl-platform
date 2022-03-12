package com.kl.redis.starter.lock;


import com.kl.core.exception.KlException;
import com.kl.redis.starter.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author : yuezhenyu
 * @className : RedisLockServiceImpl
 * @since : 2021/09/16 下午2:42
 */
@Slf4j
public class RedisLockServiceImpl implements LockService {


    private StringRedisTemplate redisTemplate;

    /**
     * /**
     * 线程局部变量
     */
    private ThreadLocal<String> lockFlag = new ThreadLocal<String>();

    public static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    public RedisLockServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
    }

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key    锁名
     * @param expire 毫秒，过期时间
     * @return
     */
    @Override
    public boolean lock(String key, long expire) {
        return lock(key, expire, 0, 0);
    }

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key    锁名
     * @param value  ，锁内容
     * @param expire 毫秒，过期时间
     * @return
     */
    @Override
    public boolean lock(String key, String value, long expire) {
        return lock(key, value, expire, 0, 0);
    }

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key         锁名
     * @param expire      毫秒，过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 毫秒，睡眠时间
     * @return
     */
    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        return lock(key, null, expire, retryTimes, sleepMillis);
    }

    /**
     * 分布式锁
     * 自动解锁
     *
     * @param key      锁名
     * @param expire   毫秒，过期时间
     * @param supplier
     * @return
     */
    @Override
    public <T> T lockAutoRelease(String key, long expire, Supplier<T> supplier) {
        return lockAutoRelease(key, expire, 0, 0, supplier);
    }

    /**
     * 分布式锁
     * 自动解锁
     *
     * @param key         锁名
     * @param expire      毫秒，过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 毫秒，睡眠时间
     * @param supplier
     * @return
     */
    @Override
    public <T> T lockAutoRelease(String key, long expire, int retryTimes, long sleepMillis, Supplier<T> supplier) {
        if (!lock(key, null, expire, retryTimes, sleepMillis)) {
            log.warn("lockKey:{}, 已被锁定", key);
            throw new KlException("请稍后再试");
        }
        try {
            return supplier.get();
        } finally {
            releaseLock(key, null);
        }
    }

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key         锁名
     * @param value       ，锁内容
     * @param expire      毫秒，过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 毫秒，睡眠时间
     * @return
     */
    @Override
    public boolean lock(String key, String value, long expire, int retryTimes, long sleepMillis) {
        boolean result = setRedis(key, value, expire);
        // 如果获取锁失败，按照传入的重试次数进行重试
        while ((!result) && retryTimes-- > 0) {
            try {
                log.debug("lock failed, retrying..." + retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                return false;
            }
            result = setRedis(key, null, expire);
        }
        return result;
    }

    /**
     * 释放锁
     *
     * @param lockKey lockKey
     * @param value   锁内容
     * @return
     */
    @Override
    public boolean releaseLock(String lockKey, String value) {
        try {
            String finalKey = RedisUtil.getKeyPrefix(lockKey);
            String uuid = value;
            if (StringUtils.isEmpty(uuid)) {
                uuid = lockFlag.get();
            }
            if (StringUtils.isEmpty(uuid)) {
                uuid = "";
            }
            String finalUuid = uuid;
            RedisCallback<Boolean> callback = (connection) -> {
                return connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, finalKey.getBytes(Charset.forName("UTF-8")), finalUuid.getBytes(Charset.forName("UTF-8")));
            };
            boolean result = (Boolean) redisTemplate.execute(callback);
            if (!result) {
                String redisValue = getLockValue(lockKey);
                log.error("[redisLockReleaseLock]release lock error key:[{}], value:{},finalUuid:{},redisValue:{}", finalKey, value, finalUuid, redisValue);
            }
            return result;
        } catch (Exception e) {
            log.error("[redisLockReleaseLock]release lock occured an exception", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            lockFlag.remove();
        }
        return false;
    }

    /**
     * setnx
     *
     * @param key
     * @param expire ms
     * @return
     */
    private boolean setRedis(String key, String value, long expire) {
        try {
            if (StringUtils.isEmpty(value)) {
                value = UUID.randomUUID().toString();
            }
            String finalKey = RedisUtil.getKeyPrefix(key);
            String finalValue = value;
            RedisCallback<Boolean> callback = (connection) -> {
                return connection.set(finalKey.getBytes(Charset.forName("UTF-8")), finalValue.getBytes(Charset.forName("UTF-8")), Expiration.milliseconds(expire), RedisStringCommands.SetOption.SET_IF_ABSENT);
            };
            boolean result = (Boolean) redisTemplate.execute(callback);
            if (result) {
                log.info("[redisLockSetRedis] 加锁成功 key:[{}],finalValue:{}", finalKey, finalValue);
                lockFlag.set(finalValue);
            }
            return result;
        } catch (Exception e) {
            log.error("[redisLockSetRedis]set redis occured an exception", e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     * @return
     */
    @Override
    public boolean releaseLock(String lockKey) {
        return releaseLock(lockKey, null);
    }

    /**
     * 获取Redis锁的value值
     *
     * @param lockKey
     * @return
     */
    @Override
    public String getLockValue(String lockKey) {
        try {
            String finalKey = RedisUtil.getKeyPrefix(lockKey);
            RedisCallback<String> callback = (connection) -> {
                return new String(connection.get(finalKey.getBytes()), Charset.forName("UTF-8"));
            };
            return (String) redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("[redisLockGetLockValue]get redis occurred an exception", e);
        }
        return null;
    }
}