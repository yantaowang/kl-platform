package com.kl.core.enums;


public enum ResponseCode {
    SUCCESS(0, "成功"),
    TIMESTAMP_ERROR(1, "时间戳错误"),
    SIGNATURE_ERROR(2, "签名错误"),
    TOKEN_INVALID(3, "登录无效"),
    RIGHT_NOT_ENOUGH(4, "权限不足"),
    PARAMETER_ERROR(5, "参数错误"),
    COMMON_CODE(9999, "未知错误"),
    API_INVALID_ERROR(1000001, "无效API"),
    SERVICE_TIMEOUT(1000002, "网关超时"),
    SERVICE_ERROR(1000003, "服务暂时不可用，请稍后再试"),
    SERVICE_NOT_EXIST(1000004, "服务不存在"),
    SERVICE_GATEWAY_ERROR(1000005, "网关错误"),
    SENTINEL_BLOCK(1000006, "服务器繁忙，请稍候再试"),
    PARTNER_CODE_NOT_EXISTS(1000007, "合作方信息不存在");

    private int code = 0;
    private String description;

    private ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCode() {
        return this.code;
    }
}
