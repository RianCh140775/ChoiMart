package com.choimart.auth.Util;

import com.choimart.auth.ErrorCode.ErrorCode;
import com.choimart.auth.Exception.AppException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        System.out.println("JWTUtil initialized (key length: " + secretKey.length() + ")");
    }

    //Generate token baru
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //validate token
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new AppException(e.getMessage(), ErrorCode.EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new AppException(e.getMessage(), ErrorCode.INVALID_TOKEN, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   public Claims extractAllClaims(String token ){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
   }

   public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
   }

   public String getUsername(String token){
        return extractClaim(token, Claims::getSubject);
   }

   public Date getExpirationFromToken(String token){
        return extractClaim(token, Claims::getExpiration);
   }
}
