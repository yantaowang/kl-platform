package com.kl.db.starter.snowflake;


import org.springframework.util.Assert;

/**
 * 雪花标识生成器类
 *
 */
public class SnowflakeIdGenerator implements IdGenerator {

    /**
     * 纪元时间戳(毫秒, 从"2018-01-01 00:00:00 000"开始)
     */
    public static final long EPOCH_TIMESTAMP = 1514736000000L;
    /**
     * 时间戳位数
     */
    private static final long TIMESTAMP_BITS = 41L;
    /**
     * 工作者标识位数
     */
    private static final long WORKERID_BITS = 12L;

    // 位数分配: 64=1+41+10+12, 时间戳41位支持68年, 工作者标识10位支持1024模块, 序列号12位支持4096个
    /**
     * 序列号位数
     */
    private static final long SEQUENCE_BITS = 10L;
    /**
     * 工作者标识位移
     */
    private static final long WORKERID_SHIFT = SEQUENCE_BITS;
    /**
     * 时间戳位移
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKERID_BITS;
    /**
     * 序列号掩码
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    /**
     * 序列号掩码(四分之一值)
     */
    private static final long SEQUENCE_MASK_QUARTER = SEQUENCE_MASK >> 2;
    /**
     * 最大工作者标识
     */
    public static final long MAX_WORKERID = ~(-1L << WORKERID_BITS);
    /**
     * 最大时间戳
     */
    private static final long MAX_TIMESTAMP = ~(-1L << TIMESTAMP_BITS);
    /**
     * 最大时间间隔(毫秒)
     */
    private static final long MAX_INTERVAL = 180_000L;
    /**
     * 工作者标识
     */
    private volatile long workerId = 0L;
    /**
     * 当前学历号
     */
    private volatile long sequence = 0L;
    /**
     * 上次时间戳
     */
    private volatile long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        // 获取工作者标识
        Assert.isTrue(workerId >= 0L, "工作者标识不能为负");
        Assert.isTrue(workerId <= MAX_WORKERID, "工作者标识不能大于" + MAX_WORKERID);
        this.workerId = workerId;

        // 设置上次时间戳
        lastTimestamp = System.currentTimeMillis();
    }

    /**
     * 下一个标识
     *
     * @return 下一个标识
     */
    @Override
    public synchronized long nextId() {
        // 获取当前时间戳
        long timestamp = getCurrentTimestamp();

        // 判断时间戳大小
        // 判断时间戳大小: 相等则递增, 归零则等待下一毫秒
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = getNextTimestamp();
            }
        }
        // 判断时间戳大小: 大于则设置顺序值
        else {
            // sequence = 0L;
            // 给同一毫秒时留出递增空间
            if (sequence < (SEQUENCE_MASK_QUARTER)) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
            } else {
                sequence = 0L;
            }
        }

        // 设置上次时间戳
        lastTimestamp = timestamp;

        // 返回雪花标识
        return ((timestamp - EPOCH_TIMESTAMP) << TIMESTAMP_SHIFT) | (workerId << WORKERID_SHIFT) | sequence;
    }

    /**
     * 获取时间戳
     *
     * @param id 雪花标识
     * @return 时间戳
     */
    @Override
    public long getTime(long id) {
        return EPOCH_TIMESTAMP + (id >>> TIMESTAMP_SHIFT);
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    private long getCurrentTimestamp() {
        // 获取并判断当前时间戳
        long timestamp = System.currentTimeMillis();
        Assert.isTrue(timestamp - EPOCH_TIMESTAMP <= MAX_TIMESTAMP, "时间戳已用尽,无法分配标识");
        Assert.isTrue(lastTimestamp - timestamp <= MAX_INTERVAL, "服务器时间回退,无法分配标识");

        // 直到等于上次时间戳
        while (timestamp < lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }

        // 返回当前时间戳
        return timestamp;
    }

    /**
     * 获取下一时间戳
     *
     * @return 下一时间戳
     */
    private long getNextTimestamp() {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}

