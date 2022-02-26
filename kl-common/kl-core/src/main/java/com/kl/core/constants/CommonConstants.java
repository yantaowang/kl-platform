package com.kl.core.constants;


public class CommonConstants {
    public static String BasePackageName = "com.kl";
    public static String APPLICATION_NAME = "null";
    // 日志长度太长
    public static String THE_LOG_DATA_IS_TOO_LONG = "[THE_LOG_DATA_IS_TOO_LONG]";

    // 最大日志长度 50000 个字符串
    public static final int MAX_LOG_BODY_LENGTH = 1024 * 50;

    // 超级管理员默认合作方id
    public static Integer ADMIN_PARTNER_CODE = -1;
    // 花费一秒以上两秒以下
    public static final String TAKE_MORETHAN_ONE_SECOND = "[SLOW_API][TAKE_MORETHAN_ONE_SECOND]:";

    // 花费两秒以上
    public static final String TAKE_MORETHAN_TWO_SECOND = "[SLOW_API][TAKE_MORETHAN_TWO_SECOND]:";

    // 雪花工作表常量
    public static String SNOWFLAKE_WORKER_TABLE = "t_snowflake_worker_auto";
    /**
     * 测试环境免签名配置
     */
    public static final String GREEN_PASS = "greenPass";

    // 数据库表里面合作方id列名称
    public static final String TENANT_ID = "tenant_id";

    // dubbo请求来源
    public static final String DUBBO_FROM = "dubbo_from";
    // 签名过期时间
    public static final long SIGN_INTERVAL = 1800L;


    /**
     * jwt 相关header里面的 key
     */
    public static class JWTModelKeyHeader {
        /** 常量相关 */
        /**
         * 时间戳名称
         */
        public static final String TIMESTAMP = "timestamp";

        /**
         * 常量相关
         */
        //token在header 属性名
        public static final String JWT_HEADER_PARAM = "token";
        // 客户端唯一标识
        public static final String CLIENT_UNION_ID = "clientUnionId";
        //登陆者ID
        public static final String CUSTOMER_ID = "customerId";
        // 合作商户标识
        public static final String TENANT_ID = "tenantId";

        /**
         * 签名名称
         */
        public static final String SIGNATURE = "signature";

        public static final String TRACE_ID = "traceId";
    }
}
