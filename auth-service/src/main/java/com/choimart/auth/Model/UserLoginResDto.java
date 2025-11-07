package com.choimart.auth.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResDto {
    private String token;
    private UserInfoDto user;

    @Data
    @Builder
    public static class UserInfoDto {
        private Long id;
        private String username;
        private String email;
    }
}
