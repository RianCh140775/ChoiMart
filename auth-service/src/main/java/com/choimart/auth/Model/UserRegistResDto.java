package com.choimart.auth.Model;

import com.choimart.auth.BaseResponse.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistResDto {
    private String username;
    private String email;
}
