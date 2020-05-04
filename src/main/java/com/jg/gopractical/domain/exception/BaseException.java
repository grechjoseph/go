package com.jg.gopractical.domain.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = "errorCode", callSuper = false)
public class BaseException extends RuntimeException {

    @Getter
    private ErrorCode errorCode;

    public BaseException(final ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }
}
