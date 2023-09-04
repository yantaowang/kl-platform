package com.kl.common.exception;


import com.kl.common.dto.KlResponse;
import com.kl.common.enums.ResponseCode;

/**
 * @mudule : 错误异常 需要人为介入排查
 */
public class KlException extends RuntimeException {

    /**
     * 版本标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 响应编码
     */
    private int code = ResponseCode.COMMON_CODE.getCode();

    /**
     * 自定义异常msg
     */
    private String selfMessage;

    private Object returnData;

    /**
     * 构造函数
     */
    public KlException() {
        super();
    }


    public String getSelfMessage() {
        return selfMessage;
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public KlException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message    异常消息
     * @param logMessage log日志
     */
    public KlException(String message, String logMessage) {
        super(message);
        this.selfMessage = logMessage;
    }

    /**
     * 构造函数
     *
     * @param code       异常编码
     * @param message    异常消息
     * @param logMessage log日志
     */
    public KlException(int code, String message, String logMessage) {
        super(message);
        this.code = code;
        this.selfMessage = logMessage;
    }

    /**
     * 构造函数
     *
     * @param code    异常编码
     * @param message 异常消息
     */
    public KlException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     *
     * @param cause 异常原因
     */
    public KlException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数
     *
     * @param code  异常编码
     * @param cause 异常原因
     */
    public KlException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public KlException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param code    异常编码
     * @param message 异常消息
     * @param cause   异常原因
     */
    public KlException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public KlException(int code, String message, Object returnData) {
        super(message);
        this.code = code;
        this.returnData = returnData;
    }

    /**
     * 获取异常编码
     *
     * @return 异常编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 异常附带数据值
     * 
     * @return 数据值
     */
    public Object getReturnData() {
        return returnData;
    }

    public KlResponse getEwpResponse(){
        KlResponse response = new KlResponse();
        response.setCode(code);
        response.setMessage(this.getMessage());
        response.setException(this.getMessage());
        response.setData(this.getReturnData());
        return response;
    }


}
