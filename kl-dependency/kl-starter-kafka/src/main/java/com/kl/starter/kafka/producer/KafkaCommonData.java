package com.kl.starter.kafka.producer;


public class KafkaCommonData<T> {
    private T data;
    private Long id;
    private int tenantId;
    private Long eventTime;
    private String cmd;

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public KafkaCommonData() {
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public KafkaCommonData(Long id, T data, String cmd) {
        this.id = id;
        this.data = data;
        this.cmd = cmd;
        this.eventTime = System.currentTimeMillis();
    }
}
