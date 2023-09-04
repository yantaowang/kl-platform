package com.kl.common.exception;


import com.kl.common.dto.KlResponse;

/**
 * @mudule : 警告异常 打印 warn日志
 * @since ：2021/7/13 15:22
 */
public class WarnKlException extends KlException {

    /**
     * 构造函数
     */
    public WarnKlException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param message    异常消息
     * @param logMessage log日志
     */
    public WarnKlException(String message, String logMessage) {
        super(message, logMessage);
    }

    /**
     * 构造函数
     *
     * @param code       异常编码
     * @param message    异常消息
     * @param logMessage log日志
     */
    public WarnKlException(int code, String message, String logMessage) {
        super(code, message, logMessage);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public WarnKlException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param code    异常编码
     * @param message 异常消息
     */
    public WarnKlException(int code, String message) {
        super(code, message);
    }

    /**
     * 构造函数
     *
     * @param cause 异常原因
     */
    public WarnKlException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数
     *
     * @param code  异常编码
     * @param cause 异常原因
     */
    public WarnKlException(int code, Throwable cause) {
        super(code, cause);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public WarnKlException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param code    异常编码
     * @param message 异常消息
     * @param cause   异常原因
     */
    public WarnKlException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public WarnKlException(int code, String message, Object returnData) {
        super(code, message, returnData);
    }

    /**
     * 获取异常编码
     *
     * @return 异常编码
     */
    @Override
    public int getCode() {
        return super.getCode();
    }

    /**
     * 异常附带数据值
     *
     * @return 数据值
     */
    @Override
    public Object getReturnData() {
        return super.getReturnData();
    }

    @Override
    public KlResponse getEwpResponse() {
        return super.getEwpResponse();
    }
}
