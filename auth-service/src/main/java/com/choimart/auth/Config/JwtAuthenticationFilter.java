package com.choimart.auth.Config;

import com.choimart.auth.Entity.User;
import com.choimart.auth.Repository.UserRepository;
import com.choimart.auth.Service.JwtBlacklistService;
import com.choimart.auth.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    // Redis optional: if present can be uses to blacklist token
    private final @Nullable RedisTemplate<String, Object> redisTemplate;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String path = request.getRequestURI();
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.debug("Incoming request: {} {}", request.getMethod(), path);

        if (path.startsWith("/api/auth/register") || path.startsWith("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header == null || !header.startsWith("Bearer ")){
            log.warn("Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7).trim();


        try {
            //cek if redis present and token blaclisted then reject
                if (jwtBlacklistService != null) {
                    Boolean blacklisted = jwtBlacklistService.isTokenBlacklisted(token);
                    if (Boolean.TRUE.equals(blacklisted)) {
                        log.warn("Token is blacklisted, rejecting request");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Token has been invalidated. Please log in again.");
                        return;
                    }
                } else {
                    log.debug("Redis blacklist service not configured - skipping check");
                }

            if (!jwtUtil.validateToken(token)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }

            String email = jwtUtil.getUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null ){
                    User user = userRepo.findByEmail(email).orElse(null);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user != null ? user : email, null, Collections.emptyList());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        } catch (Exception ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
