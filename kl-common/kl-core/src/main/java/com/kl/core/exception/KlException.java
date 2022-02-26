package com.kl.core.exception;


import com.kl.core.enums.ResponseCode;
import com.kl.core.model.KlResponse;

public class KlException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int code;
    private String selfMessage;
    private Object returnData;

    public KlException() {
        this.code = ResponseCode.COMMON_CODE.getCode();
    }

    public String getSelfMessage() {
        return this.selfMessage;
    }

    public KlException(String message) {
        super(message);
        this.code = ResponseCode.COMMON_CODE.getCode();
    }

    public KlException(String message, String logMessage) {
        super(message);
        this.code = ResponseCode.COMMON_CODE.getCode();
        this.selfMessage = logMessage;
    }

    public KlException(int code, String message, String logMessage) {
        super(message);
        this.code = ResponseCode.COMMON_CODE.getCode();
        this.code = code;
        this.selfMessage = logMessage;
    }

    public KlException(int code, String message) {
        super(message);
        this.code = ResponseCode.COMMON_CODE.getCode();
        this.code = code;
    }

    public KlException(Throwable cause) {
        super(cause);
        this.code = ResponseCode.COMMON_CODE.getCode();
    }

    public KlException(int code, Throwable cause) {
        super(cause);
        this.code = ResponseCode.COMMON_CODE.getCode();
        this.code = code;
    }

    public KlException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.COMMON_CODE.getCode();
    }

    public KlException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.COMMON_CODE.getCode();
        this.code = code;
    }

    public KlException(int code, String message, Object returnData) {
        super(message);
        this.code = ResponseCode.COMMON_CODE.getCode();
        this.code = code;
        this.returnData = returnData;
    }

    public int getCode() {
        return this.code;
    }

    public Object getReturnData() {
        return this.returnData;
    }

    public KlResponse getKlResponse() {
        KlResponse response = new KlResponse();
        response.setCode(this.code);
        response.setMessage(this.getMessage());
        response.setException(this.getMessage());
        response.setData(this.getReturnData());
        return response;
    }
}
