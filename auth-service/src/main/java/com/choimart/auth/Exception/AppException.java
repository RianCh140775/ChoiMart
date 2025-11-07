package com.choimart.auth.Exception;

import com.choimart.auth.ErrorCode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException{
    private final HttpStatus status;
    private final ErrorCode errorCode;


    public AppException(String message, ErrorCode errorCode, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
