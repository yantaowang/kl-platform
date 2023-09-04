package com.kl.monitor.starter.utils;

public enum CommonMonitorEnum implements MonitorEnum {
    BASE_REQUEST_COUNT_BUCKET("base_request_count_bucket","接口响应时间分桶"),
    APP_REQUEST_EXCEPTION("base_app_request_exception","异常数统计"),
    CONSUMER_REQUEST_EXCEPTION("consumer_app_request_exception","异常数统计"),
    APP_REQUEST_COUNT("base_app_request_total","请求总数"),
    HTTP_SERVEICE_REQUEST_COUNT("http_server_requests", "请求总数"),
    CONSUMER_REQUEST_COUNT("consumer_app_request_total","异常数统计"),
    CONSUMER_REQUEST_TIME("consumer_app_request_time","调用fegin响应时间统计"),
    THREAD_REQUEST_COUNT("base_thread_request_total","请求总数"),
    THREAD_REQUEST_EXCEPTION("base_thread_request_exception","请求总数"),


    BASE_SQL_EXECUTE_TOTAL("base_sql_execute_total","sql执行数"),
    BASE_SQL_EXECUTE_EXCEPTION_TOTAL("base_sql_execute_exception","sql异常数量"),
    BASE_SQL_EXECUTE_SLOW_SQL_TOTAL("base_sql_execute_slow_sql_total","慢sql数量"),

    BASE_ROCKETMQ_PRODUCER_EXCEPTION("base_rocketmq_producer_exception","rocketmq生产消息异常数"),
    BASE_ROCKETMQ_PRODUCER_TOTAL("base_rocketmq_producer_total","rocketmq生产消息总数"),
    BASE_ROCKETMQ_CONSUMER_EXCEPTION("base_rocketmq_consumer_exception","rocketmq消费消息异常数"),
    BASE_ROCKETMQ_CONSUMER_TOTAL("base_rocketmq_consumer_total","rocketmq消费消息总数"),
    BASE_ROCKETMQ_CONSUMER_TIMER("base_rocketmq_consumer_timer", "rocketmq消费消息时长"),

    BASE_KAFKA_CONSUMER_TIMER("base_kafka_consumer_timer", "kafka消费消息时长"),
    BASE_KAFKA_PRODUCER_EXCEPTION("base_kafka_producer_exception","kafka生产消息异常数"),
    BASE_KAFKA_PRODUCER_TOTAL("base_kafka_producer_total","kafka生产消息总数"),
    BASE_KAFKA_CONSUMER_EXCEPTION("base_kafka_consumer_exception","kafka消费消息异常数"),
    BASE_KAFKA_CONSUMER_TOTAL("base_kafka_consumer_total","kafka消费消息总数"),


    BASE_REDIS_REQUEST_EXCEPTION("base_redis_request_exception","redis异常"),
    BASE_REDIS_REQUEST_TIMER("base_redis_request_timer","redis执行时间"),

    BASE_DUBBO_REQUEST_TIMER("base_dubbo_request_timer","dubbo执行时间"),
    CONSUMER_DUBBO_REQUEST_TIMER("consumer_dubbo_request_timer","dubbo调用耗时"),
    CONSUMER_DUBBO_REQUEST_COUNT("consumer_dubbo_request_exception_total","dubbo消费者异常数统计"),

    DUBBO_PROVIDER_THREAD_CORE_SIZE("provider_dubbo_thread_pool_core_size","生产者线程池核心数"),
    DUBBO_CONSUMER_THREAD_CORE_SIZE("consumer_dubbo_thread_pool_core_size","消费者线程池核心数"),
    DUBBO_PROVIDER_THREAD_MAX_SIZE("provider_dubbo_thread_pool_max_size","生产者线程池最大大小"),
    DUBBO_CONSUMER_THREAD_MAX_SIZE("consumer_dubbo_thread_pool_max_size","消费者线程池最大大小"),
    DUBBO_PROVIDER_THREAD_ACTIVE_SIZE("provider_dubbo_thread_pool_active_size","生产者线程池活跃线程数"),
    DUBBO_CONSUMER_THREAD_ACTIVE_SIZE("consumer_dubbo_thread_pool_active_size","消费者线程活跃线程数"),
    DUBBO_PROVIDER_THREAD_POOL_SIZE("provider_dubbo_thread_pool_pool_size","生产者线程池当前线程数"),
    DUBBO_CONSUMER_THREAD_POOL_SIZE("consumer_dubbo_thread_pool_pool_size","消费者线程当前线程数"),
    DUBBO_PROVIDER_THREAD_QUEUE_SIZE("provider_dubbo_thread_pool_queue_size","生产者线程池排队数"),
    DUBBO_CONSUMER_THREAD_QUEUE_SIZE("consumer_dubbo_thread_pool_queue_size","消费者线程排队数"),
    DUBBO_PROVIDER_THREAD_TASK_SIZE("provider_dubbo_thread_pool_task_size","生产者线程池任务总数"),
    DUBBO_CONSUMER_THREAD_TASK_SIZE("consumer_dubbo_thread_pool_task_size","消费者线程任务总数"),
    DUBBO_PROVIDER_THREAD_COMPLETED_TASK_SIZE("provider_dubbo_thread_pool_task_completed_size","生产者线程池完成任务数"),
    DUBBO_CONSUMER_THREAD_COMPLETED_TASK_SIZE("consumer_dubbo_thread_pool_task_completed_size","消费者线程完成任务数"),


    THREAD_CORE_POOL_SIZE("thread_core_pool_size", "核心线程数"),
    THREAD_CORE_MAX_SIZE("thread_core_max_size", "最大线程数"),
    THREAD_QUEUE_CAPACITY("thread_queue_capacity", "队列大小"),
    THREAD_KEEP_ALIVE_SECONDS("thread_keep_alive_seconds", "线程池维护线程所允许的空闲时间"),
    THREAD_ACTIVE_COUNT("thread_active_count", "正在执行任务的线程数量"),
    THREAD_TASK_COUNT("thread_task_count", "已经执行的和未执行的任务总数"),
    THREAD_COMPLETED_TASK_COUNT("thread_completed_task_count", "已完成的任务数量"),


    ;

    private String name;
    private String desc;

    CommonMonitorEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
