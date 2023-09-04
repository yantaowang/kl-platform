package com.kl.common.enums;

/**
 */
public enum ResponseCode {

    /**
     * 字段相关
     */
    SUCCESS(0, "成功"),
    /**
     * 时间戳错误
     * 如果期望前端
     */

    TIMESTAMP_ERROR(1, "时间戳错误"),
    /**
     * 签名错误
     */
    SIGNATURE_ERROR(2, "签名错误"),
    /**
     * 令牌无效
     */
    TOKEN_INVALID(3, "登录无效"),
    RIGHT_NOT_ENOUGH(4, "权限不足"),
    /**
     * 参数错误
     */
    PARAMETER_ERROR(5, "参数错误"),
    /**
     * 未知错误
     */
    COMMON_CODE(9999, "未知错误"),
    API_INVALID_ERROR(1000001, "无效API"),

    SERVICE_TIMEOUT(1000002, "网关超时"),
    SERVICE_ERROR(1000003, "服务暂时不可用，请稍后再试"),
    SERVICE_NOT_EXIST(1000004, "服务不存在"),
    SERVICE_GATEWAY_ERROR(1000005, "网关错误"),
    /**
     * sentinel 流控
     */
    SENTINEL_BLOCK(1000006, "服务器繁忙，请稍候再试"),
    PARTNER_CODE_NOT_EXISTS(1000007, "合作方信息不存在"),
    ;

    /**
     * 响应编码值
     */
    private int code = 0;
    /**
     * 异常编码描述
     */
    private String description;

    /**
     * 构造函数
     *
     * @param code        异常编码值
     * @param description 异常编码描述
     */
    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取异常编码描述
     *
     * @return 异常编码描述
     */
    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}