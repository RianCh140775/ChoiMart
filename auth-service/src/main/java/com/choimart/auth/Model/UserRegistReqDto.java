package com.choimart.auth.Model;

import lombok.Data;

@Data
public class UserRegistReqDto {
    private String username;
    private String email;
    private String password;
}
