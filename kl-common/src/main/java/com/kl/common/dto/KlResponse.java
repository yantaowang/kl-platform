package com.kl.common.dto;

import com.kl.common.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 接口响应对象
 *
 * @param <T>
 */
@Getter
@Setter
@ToString
public class KlResponse<T> implements Serializable {

    /**
     * 版本标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 应答编码
     */
    private Integer code = 0;

    /**
     * 应答消息
     */
    private String message;
    /**
     * 应答异常
     */
    private String exception;
    /**
     * 应答数据
     */
    private T data;

    /**
     * 成功应答
     *
     * @return 成功应答
     */
    public static <T> KlResponse<T> success() {
        KlResponse<T> response = new KlResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        return response;
    }

    /**
     * 成功应答
     *
     * @param data 应答数据
     * @return 成功应答
     */
    public static <T> KlResponse<T> success(T data) {
        KlResponse<T> response = new KlResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    /**
     * 成功应答
     *
     * @param data    应答数据
     * @param message 应答消息
     * @return 成功应答
     */
    public static <T> KlResponse<T> success(T data, String message) {
        KlResponse<T> response = new KlResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        response.setMessage(message);
        return response;
    }
    /**
     * 失败应答
     *
     * @return 失败应答
     */
    public static <T> KlResponse<T> failure() {
        return failure(ResponseCode.COMMON_CODE);
    }

    /**
     * 失败应答
     *
     * @param code      应答编码
     * @param message   应答消息
     * @param exception 应答异常
     * @return 失败应答
     */
    public static <T> KlResponse<T> failure(Integer code, String message, String exception) {
        KlResponse<T> response = new KlResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setException(exception);
        return response;
    }

    /**
     * 失败应答
     *
     * @param code      应答编码
     * @param exception 应答异常
     * @return 失败应答
     */
    public static <T> KlResponse<T> failure(ResponseCode code, String exception) {
        KlResponse<T> response = new KlResponse<>();
        response.setCode(code.getCode());
        response.setMessage(code.getDescription());
        response.setException(exception);
        return response;
    }

    /**
     * 失败应答
     *
     * @param code      应答编码
     * @return 失败应答
     */
    public static <T> KlResponse<T> failure(ResponseCode code) {
        KlResponse<T> response = new KlResponse<>();
        response.setCode(code.getCode());
        response.setMessage(code.getDescription());
        return response;
    }

}
