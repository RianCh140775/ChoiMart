package com.choimart.auth.Model;

import lombok.Data;

@Data
public class UserLoginReqDto {
    private String email;
    private String password;
}
