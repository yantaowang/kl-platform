package com.kl.core.model;


import com.kl.core.enums.ResponseCode;

import java.io.Serializable;

public class KlResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code = 0;
    private String message;
    private String exception;
    private T data;

    public KlResponse() {
    }

    public static <T> KlResponse<T> success() {
        KlResponse<T> response = new KlResponse();
        response.setCode(ResponseCode.SUCCESS.getCode());
        return response;
    }

    public static <T> KlResponse<T> success(T data) {
        KlResponse<T> response = new KlResponse();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> KlResponse<T> success(T data, String message) {
        KlResponse<T> response = new KlResponse();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public static <T> KlResponse<T> failure() {
        return failure(ResponseCode.COMMON_CODE);
    }

    public static <T> KlResponse<T> failure(Integer code, String message, String exception) {
        KlResponse<T> response = new KlResponse();
        response.setCode(code);
        response.setMessage(message);
        response.setException(exception);
        return response;
    }

    public static <T> KlResponse<T> failure(ResponseCode code, String exception) {
        KlResponse<T> response = new KlResponse();
        response.setCode(code.getCode());
        response.setMessage(code.getDescription());
        response.setException(exception);
        return response;
    }

    public static <T> KlResponse<T> failure(ResponseCode code) {
        KlResponse<T> response = new KlResponse();
        response.setCode(code.getCode());
        response.setMessage(code.getDescription());
        return response;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getException() {
        return this.exception;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return "KlResponse(code=" + this.getCode() + ", message=" + this.getMessage() + ", exception=" + this.getException() + ", data=" + this.getData() + ")";
    }
}