package com.kl.db.starter;


import cn.hutool.core.net.Ipv4Util;
import cn.hutool.extra.spring.SpringUtil;
import com.kl.core.util.IpAddressHelper;
import com.kl.db.starter.snowflake.DSnowflakeWorkerRunner;
import com.kl.db.starter.snowflake.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : yzy
 */
@Slf4j
public class IdGeneratorUtil {

    private static SnowflakeIdGenerator snowflakeIdGenerator = null;

    /**
     * 备用workId
     */
    private static final SnowflakeIdGenerator BACK_UP_SNOWFLAKE_ID_GENERATOR = new SnowflakeIdGenerator(getWorkerId());

    public static void setSnowflakeIdGenerator(SnowflakeIdGenerator snowflakeIdGenerator) {
        IdGeneratorUtil.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    public static SnowflakeIdGenerator getSnowflakeIdGenerator() {
        return snowflakeIdGenerator;
    }

    /**
     * 生成主键
     *
     * @return 雪花id
     */
    public static long nextId() {
        if (snowflakeIdGenerator == null) {
            DSnowflakeWorkerRunner snowflakeWorkerRunner = SpringUtil.getBean(DSnowflakeWorkerRunner.class);
            if (snowflakeWorkerRunner != null) {
                snowflakeWorkerRunner.initWorkId();
            }
            log.warn("[IdGeneratorUtilError]雪花工具类暂未初始化");
            return BACK_UP_SNOWFLAKE_ID_GENERATOR.nextId();
        }
        return snowflakeIdGenerator.nextId();
    }

    /**
     * 使用备份雪花工具类覆盖默认雪花工具类
     */
    public static void overrideSnowflakeIdGenerator(){
        snowflakeIdGenerator = BACK_UP_SNOWFLAKE_ID_GENERATOR;
    }

    /**
     * 获取workId 备用方案
     *
     * @return workId
     */
    private static long getWorkerId() {
        try {
            long halfWorkId = SnowflakeIdGenerator.MAX_WORKERID / 2;
            long workId = Ipv4Util.ipv4ToLong(IpAddressHelper.getLocalIpAddress()) % halfWorkId + halfWorkId;
            log.info("[backUpSnowflakeIdGenerator] 备用workId:{}", workId);
            return workId;
        } catch (Exception e) {
            log.error("[backUpSnowflakeIdGenerator] 生成备用workId失败", e);
            return SnowflakeIdGenerator.MAX_WORKERID;
        }
    }
}
