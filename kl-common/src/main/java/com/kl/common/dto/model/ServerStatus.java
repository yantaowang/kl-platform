package com.kl.common.dto.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
public class ServerStatus extends HashMap<String, String> implements Serializable {
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String TIMEOUT = "TIMEOUT";
    /*private String mysqlFirst;
    private String mysqlSecond;
    private String tidbFirst;
    private String tidbSecond;
    private String redis;
    private String nacos;
    private String kafka;
    private String sentinel;
    // 业务回调api
    private String callbackApi;*/
}
