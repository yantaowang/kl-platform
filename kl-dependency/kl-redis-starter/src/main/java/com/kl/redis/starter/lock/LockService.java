package com.kl.redis.starter.lock;


import java.util.function.Supplier;

/**
 * @className : LockService
 */
public interface LockService {

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key    锁名
     * @param expire 毫秒，过期时间
     * @return
     */
    boolean lock(String key, long expire);

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key       锁名
     * @param value，锁内容
     * @param expire    毫秒，过期时间
     * @return
     */
    boolean lock(String key, String value, long expire);

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
    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    /**
     * 分布式锁
     * 自动解锁
     *
     * @param key         锁名
     * @param expire      毫秒，过期时间
     * @return
     */
    <T> T lockAutoRelease(String key, long expire, Supplier<T> supplier);

    /**
     * 分布式锁
     * 自动解锁
     *
     * @param key         锁名
     * @param expire      毫秒，过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 毫秒，睡眠时间
     * @return
     */
    <T> T lockAutoRelease(String key, long expire, int retryTimes, long sleepMillis, Supplier<T> supplier);

    /**
     * 分布式锁
     * 解锁用releaseLock(String key)
     *
     * @param key         锁名
     * @param value，锁内容
     * @param expire      毫秒，过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 毫秒，睡眠时间
     * @return
     */
    boolean lock(String key, String value, long expire, int retryTimes, long sleepMillis);

    /**
     * 释放锁
     *
     * @param lockKey lockKey
     * @param value   锁内容
     * @return
     */
    boolean releaseLock(String lockKey, String value);

    /**
     * 释放锁
     *
     * @param lockKey lockKey
     * @return
     */
    boolean releaseLock(String lockKey);

    /**
     * 获取Redis锁的value值
     *
     * @param lockKey lockKey
     * @return
     */
    String getLockValue(String lockKey);
}