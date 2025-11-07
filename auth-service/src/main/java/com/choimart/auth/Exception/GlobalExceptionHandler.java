package com.choimart.auth.Exception;

import com.choimart.auth.BaseResponse.BaseResponse;
import com.choimart.auth.ErrorCode.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Object>> handlerAppException(AppException ex){
        BaseResponse<Object> response = BaseResponse.failure(
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getErrorCode().name()
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handlerGeneralException(Exception ex){
        BaseResponse<Object> response = BaseResponse.failure(
                500,
                "Internal server error",
                ErrorCode.INTERNAL_ERROR.name()
        );
        return ResponseEntity.internalServerError().body(response);
    }
}
