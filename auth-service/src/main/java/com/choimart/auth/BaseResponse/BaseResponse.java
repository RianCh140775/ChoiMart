package com.choimart.auth.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private String errorCode;
    private T data;

    public static <T> BaseResponse<T> success(int statusCode, String message, T data){
        return BaseResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> failure(int statusCode, String message, String errorCode){
        return BaseResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

}
