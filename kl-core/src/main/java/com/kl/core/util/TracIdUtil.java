package com.kl.core.util;


import org.slf4j.MDC;

/**
 * @ClassName TracIdUtil
 * @Description TracIdUtil
 * @Author yzy
 * @Date 2021/8/16 7:27 下午
 * @Version 1.0
 */
public class TracIdUtil {

    public static final String LOGGER_ID_PARAM_NAME = "trace_id";

    public static final int CONTENTSIZE = 1024 * 5;


    /**
     * 创建10位长度uuid
     *
     * @return 随机字符串
     */
    public static String creaTracId() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    /**
     * 手动初始化traceId
     *
     * @param traceId
     */
    public static void initTraceId(String traceId) {
        MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, traceId);
    }

    /**
     * 自动初始化traceId
     */
    public static void initTraceId() {
        MDC.put(TracIdUtil.LOGGER_ID_PARAM_NAME, creaTracId());
    }

    /**
     * 移除traceId
     */
    public static void removeTraceId() {
        MDC.remove(TracIdUtil.LOGGER_ID_PARAM_NAME);
    }
}
