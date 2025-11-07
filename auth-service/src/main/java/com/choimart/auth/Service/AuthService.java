package com.choimart.auth.Service;

import com.choimart.auth.BaseResponse.BaseResponse;
import com.choimart.auth.Model.UserLoginReqDto;
import com.choimart.auth.Model.UserLoginResDto;
import com.choimart.auth.Model.UserRegistReqDto;
import com.choimart.auth.Model.UserRegistResDto;

public interface AuthService {
    BaseResponse<UserRegistResDto> register(UserRegistReqDto req);
    BaseResponse<UserLoginResDto> login(UserLoginReqDto req);
    BaseResponse<Object> logout(String header);
}
