package tech.waterfall.register.exception;

import org.slf4j.helpers.MessageFormatter;

import tech.waterfall.register.support.SystemExceptionCode;

public class BaseException extends RuntimeException {
    private int code;

    public BaseException(String message) {
        super(message);
        this.code = SystemExceptionCode.SYSTEM_ERROR.getCode();
    }

    public BaseException(String format, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage());
        this.code = SystemExceptionCode.SYSTEM_ERROR.getCode();
    }

    public BaseException(String message, Exception e) {
        super(message, e);
        this.code = SystemExceptionCode.SYSTEM_ERROR.getCode();
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = SystemExceptionCode.SYSTEM_ERROR.getCode();
        this.code = code;
    }

    public BaseException(int code, String format, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage());
        this.code = SystemExceptionCode.SYSTEM_ERROR.getCode();
        this.code = code;
    }

    public BaseException(int code, String message, Exception e) {
        super(message, e);
        this.code = SystemExceptionCode.SYSTEM_ERROR.getCode();
        this.code = code;
    }
}
