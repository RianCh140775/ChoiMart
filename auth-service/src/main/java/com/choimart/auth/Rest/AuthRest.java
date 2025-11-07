package com.choimart.auth.Rest;

import com.choimart.auth.BaseResponse.BaseResponse;
import com.choimart.auth.Model.UserLoginReqDto;
import com.choimart.auth.Model.UserLoginResDto;
import com.choimart.auth.Model.UserRegistReqDto;
import com.choimart.auth.Model.UserRegistResDto;
import com.choimart.auth.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRest {
    private final AuthService authService;

    @PostMapping("/register")
    public BaseResponse<UserRegistResDto> register(@RequestBody UserRegistReqDto req){
        return authService.register(req);
    }

    @PostMapping("/login")
    public BaseResponse<UserLoginResDto> login(@RequestBody UserLoginReqDto req){
        return authService.login(req);
    }

    @GetMapping("/secure")
    public BaseResponse<String> secureEndpoint(){
        return BaseResponse.success(200, "You are authorized!", "Secure data content");
    }

    @PostMapping("/logout")
    public BaseResponse<Object> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String header){
        return authService.logout(header);
    }
}
