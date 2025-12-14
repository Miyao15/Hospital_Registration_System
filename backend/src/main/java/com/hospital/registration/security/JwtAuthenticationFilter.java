package com.hospital.registration.security;

import com.hospital.registration.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 检查令牌是否在黑名单中
            String blacklisted = redisTemplate.opsForValue().get("blacklist:" + token);
            if (blacklisted != null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.isTokenValid(token)) {
                String userId = jwtService.extractUserId(token);
                UserRole role = jwtService.extractRole(token);

                // 检查用户会话是否已失效
                String invalidatedAt = redisTemplate.opsForValue().get("invalidate:" + userId);
                if (invalidatedAt != null) {
                    long invalidTime = Long.parseLong(invalidatedAt);
                    long tokenIssuedAt = jwtService.extractClaims(token).getIssuedAt().getTime();
                    if (tokenIssuedAt < invalidTime) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token无效，继续过滤链但不设置认证
        }

        filterChain.doFilter(request, response);
    }
}
