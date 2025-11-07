package com.choimart.auth.Util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Data
@RequiredArgsConstructor
@Component
public class EncoderUtil {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public BCryptPasswordEncoder getEncoder(){
        return encoder;
    }
}
