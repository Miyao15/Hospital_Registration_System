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
@lombok.extern.slf4j.Slf4j
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
            log.debug("Processing JWT token for request: {}", request.getRequestURI());
            
            // 尝试检查令牌是否在黑名单中（Redis 不可用时跳过）
            try {
                String blacklisted = redisTemplate.opsForValue().get("blacklist:" + token);
                if (blacklisted != null) {
                    log.debug("Token is blacklisted");
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception redisEx) {
                log.debug("Redis not available, skipping blacklist check");
            }

            if (jwtService.isTokenValid(token)) {
                String userId = jwtService.extractUserId(token);
                UserRole role = jwtService.extractRole(token);
                log.info("JWT valid - userId: {}, role: {}", userId, role);

                // 尝试检查用户会话是否已失效（Redis 不可用时跳过）
                try {
                    String invalidatedAt = redisTemplate.opsForValue().get("invalidate:" + userId);
                    if (invalidatedAt != null) {
                        long invalidTime = Long.parseLong(invalidatedAt);
                        long tokenIssuedAt = jwtService.extractClaims(token).getIssuedAt().getTime();
                        if (tokenIssuedAt < invalidTime) {
                            log.debug("Token invalidated for user: {}", userId);
                            filterChain.doFilter(request, response);
                            return;
                        }
                    }
                } catch (Exception redisEx) {
                    log.debug("Redis not available, skipping session check");
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for user: {} with role: ROLE_{}", userId, role.name());
            } else {
                log.warn("JWT token is invalid");
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
