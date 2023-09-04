package com.kl.core.exception;


import com.kl.core.model.KlResponse;

public class WarnKlException extends KlException {
    public WarnKlException() {
    }

    public WarnKlException(String message, String logMessage) {
        super(message, logMessage);
    }

    public WarnKlException(int code, String message, String logMessage) {
        super(code, message, logMessage);
    }

    public WarnKlException(String message) {
        super(message);
    }

    public WarnKlException(int code, String message) {
        super(code, message);
    }

    public WarnKlException(Throwable cause) {
        super(cause);
    }

    public WarnKlException(int code, Throwable cause) {
        super(code, cause);
    }

    public WarnKlException(String message, Throwable cause) {
        super(message, cause);
    }

    public WarnKlException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public WarnKlException(int code, String message, Object returnData) {
        super(code, message, returnData);
    }

    public int getCode() {
        return super.getCode();
    }

    public Object getReturnData() {
        return super.getReturnData();
    }

    public KlResponse getKlResponse() {
        return super.getKlResponse();
    }
}
