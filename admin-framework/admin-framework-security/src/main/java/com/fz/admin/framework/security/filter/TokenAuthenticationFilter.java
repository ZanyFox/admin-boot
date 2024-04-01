package com.fz.admin.framework.security.filter;


import com.fz.admin.framework.common.pojo.LoginUser;
import com.fz.admin.framework.common.util.Json;
import com.fz.admin.framework.redis.constant.RedisCacheConstant;
import com.fz.admin.framework.security.config.WebSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import static com.fz.admin.framework.security.config.WebSecurityConfig.AUTHORIZATION_BEARER;
import static com.fz.admin.framework.security.config.WebSecurityConfig.REQUEST_ATTRIBUTE_LOGIN_USER_ID;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final WebSecurityProperties webSecurityProperties;

    private final StringRedisTemplate redisTemplate;

    @Value("${admin.token.expired-time}")
    private Duration tokenExpiredTime = Duration.ofDays(7);

    @Value("${admin.token.renewal-time}")
    private Duration renewalTime = Duration.ofDays(3);

    public TokenAuthenticationFilter(WebSecurityProperties webSecurityProperties,
                                     StringRedisTemplate redisTemplate) {
        this.webSecurityProperties = webSecurityProperties;
        this.redisTemplate = redisTemplate;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        AntPathMatcher pathMatcher = new AntPathMatcher();

        if (pathMatcher.match("/exception", request.getRequestURI()))
            throw new RuntimeException("过滤器中抛出异常");

        String tokenHeader = request.getHeader(webSecurityProperties.getTokenHeader());
        if (!StringUtils.hasText(tokenHeader) || !tokenHeader.startsWith(AUTHORIZATION_BEARER)) {
            chain.doFilter(request, response);
            return;
        }

        String token = tokenHeader.substring(AUTHORIZATION_BEARER.length());
        String tokenKey = RedisCacheConstant.USER_TOKEN_PREFIX + token;
        String tokenUserCacheJson = redisTemplate.opsForValue().get(tokenKey);


        if (!StringUtils.hasText(tokenUserCacheJson)) {
            chain.doFilter(request, response);
            return;
        }

        LoginUser loginUser = Json.parseObject(tokenUserCacheJson, LoginUser.class);

        if (loginUser == null) {
            chain.doFilter(request, response);
            return;
        }

        // 判断登录时间刷新有效期
        if (loginUser.getExpiredAt() - System.currentTimeMillis() <= renewalTime.toMillis()) {
            loginUser.setExpiredAt(System.currentTimeMillis() + tokenExpiredTime.toMillis());
            redisTemplate.opsForValue().set(tokenKey, Json.toJsonString(loginUser), tokenExpiredTime);
        }

        // 保存登录用户
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        // 设置details
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 创建 Authentication 并设置到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 当前用户id设置到请求域中
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, loginUser.getId());

        chain.doFilter(request, response);
    }

}
