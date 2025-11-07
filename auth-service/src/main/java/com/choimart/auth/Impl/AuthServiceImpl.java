package com.choimart.auth.Impl;

import com.choimart.auth.BaseResponse.BaseResponse;
import com.choimart.auth.Model.UserLoginReqDto;
import com.choimart.auth.Model.UserLoginResDto;
import com.choimart.auth.Service.AuthService;
import com.choimart.auth.Service.JwtBlacklistService;
import com.choimart.auth.Util.EncoderUtil;
import com.choimart.auth.Entity.User;
import com.choimart.auth.ErrorCode.ErrorCode;
import com.choimart.auth.Exception.AppException;
import com.choimart.auth.Model.UserRegistReqDto;
import com.choimart.auth.Model.UserRegistResDto;
import com.choimart.auth.Repository.UserRepository;
import com.choimart.auth.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final EncoderUtil encUtil;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    @Transactional
    public BaseResponse<UserRegistResDto> register(UserRegistReqDto req){
        if (req == null){
            throw new AppException("Invalid param", ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (userRepo.existsByEmail(req.getEmail())){
            throw new AppException("Email already registered" , ErrorCode.USER_EXIST, HttpStatus.BAD_REQUEST);
        }

        try {

            User user = User.builder()
                    .username(req.getUsername())
                    .email(req.getEmail())
                    .password(encUtil.getEncoder().encode(req.getPassword()))
                    .build();

            userRepo.save(user);

            UserRegistResDto res = UserRegistResDto.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
            return BaseResponse.success(200,"User registered successfully", res);

        } catch (Exception ex){
            throw new AppException("Failed process Service", ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public BaseResponse<UserLoginResDto> login(UserLoginReqDto req){
        Optional<User> userCekEmail = userRepo.findByEmail(req.getEmail());
        if (userCekEmail.isEmpty()){
            throw new AppException("Invalid email or password", ErrorCode.VALIDATION_ERROR, HttpStatus.UNAUTHORIZED);
        }

        User user = userCekEmail.get();
        log.info("User registered: {}", user.getEmail());
        if (!encUtil.getEncoder().matches(req.getPassword(), user.getPassword())){
            throw new AppException("Invalid email or password", ErrorCode.VALIDATION_ERROR, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserLoginResDto.UserInfoDto userInfo = UserLoginResDto.UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        UserLoginResDto res = UserLoginResDto.builder()
                .token(token)
                .user(userInfo)
                .build();

        return BaseResponse.success(200, "Login successful", res);

    }

    @Override
    public BaseResponse<Object> logout(String header){
        if (header == null || !header.startsWith("Bearer ")){
            throw new AppException("Missing token", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        String token = header.substring(7).trim();
        try {
            Date expiredToken = jwtUtil.getExpirationFromToken(token);
            long ttlMillis = expiredToken.getTime() - System.currentTimeMillis();
            if (ttlMillis > 0){
                jwtBlacklistService.blacklistToken(token, ttlMillis);
                log.info("Token blacklisted for {} ms", ttlMillis);
            } else {
                log.info("Token already expired - no need to blacklist");
            }
            return BaseResponse.success(200, "Logout successful", null);
        } catch (Exception e) {
            throw new AppException("Failed to process logout" + e.getMessage(), ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }


}
