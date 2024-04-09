package com.fz.admin.framework.security.config;


import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.framework.common.util.Json;
import com.fz.admin.framework.security.filter.TokenAuthenticationFilter;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@EnableConfigurationProperties(WebSecurityProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class WebSecurityConfig {

    public static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";

    /**
     * HEADER 认证头 value 的前缀
     */

    public static final String AUTHORIZATION_BEARER = "Bearer ";

    private final WebSecurityProperties properties;

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;


    public WebSecurityConfig(WebSecurityProperties properties,
                             TokenAuthenticationFilter tokenAuthenticationFilter,
                             @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.properties = properties;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.requestMappingHandlerMapping = handlerMapping;
    }

    /**
     * 处理 未认证 和 无权限 异常
     */
    private final Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlingCustomizer = httpSecurityExceptionHandlingConfigurer ->
            httpSecurityExceptionHandlingConfigurer
                    // 访问未认证接口时的处理
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write(Json.toJsonString(ServRespEntity.fail(ServRespCode.UNAUTHORIZED)));
                    })
                    // 访问无权限资源时的处理
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write(Json.toJsonString(ServRespEntity.fail(ServRespCode.ACCESS_FORBIDDEN)));

                    });

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder(properties.getPasswordEncoderLength());
    }

    /**
     * 添加AuthenticationManager到容器中，添加后不会生成默认用户  UserDetailsServiceAutoConfiguration不生效
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers(
                                "/api/admin/system/auth/login",
                                "/webjars/**",
                                "/*/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/actuator",
                                "/actuator/**",
                                "/druid/**"

                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/*.html", "/*.css", "/*.js").permitAll()
                        .anyRequest().authenticated()
        );

        http.cors(withDefaults());
        // 禁用csrf
        http.csrf(AbstractHttpConfigurer::disable);
        // 禁用logout过滤器
        http.logout(AbstractHttpConfigurer::disable);
        // 不会创建和使用session
        http.sessionManagement(sc -> sc.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(exceptionHandlingCustomizer);

        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private Map<HttpMethod, Set<String>> getPermitAllUrlsFromAnnotations() {

        Map<HttpMethod, Set<String>> result = new HashMap<>();
        // 获得接口对应的 HandlerMethod 列表
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        // 获得有 @PermitAll 注解的接口
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
                continue;
            }
            if (entry.getKey().getPatternsCondition() == null) {
                continue;
            }
            Set<String> urls = entry.getKey().getPatternsCondition().getPatterns();
            // 特殊：使用 @RequestMapping 注解，并且未写 method 属性，这些请求路径没有指定请求方法，此时认为都需要免登录
            Set<RequestMethod> methods = entry.getKey().getMethodsCondition().getMethods();
            if (ObjectUtils.isEmpty(methods)) {
                result.put(HttpMethod.GET, urls);
                result.put(HttpMethod.POST, urls);
                result.put(HttpMethod.PUT, urls);
                result.put(HttpMethod.DELETE, urls);
                result.put(HttpMethod.HEAD, urls);
                result.put(HttpMethod.PATCH, urls);
                continue;
            }
            // 获取所有制定了请求方法的控制器方法的请求路径
            entry.getKey().getMethodsCondition().getMethods().forEach(requestMethod -> {
                switch (requestMethod) {
                    case GET:
                        result.get(HttpMethod.GET).addAll(urls);
                        break;
                    case POST:
                        result.get(HttpMethod.POST).addAll(urls);
                        break;
                    case PUT:
                        result.get(HttpMethod.PUT).addAll(urls);
                        break;
                    case DELETE:
                        result.get(HttpMethod.DELETE).addAll(urls);
                        break;
                    case HEAD:
                        result.get(HttpMethod.HEAD).addAll(urls);
                        break;
                    case PATCH:
                        result.get(HttpMethod.PATCH).addAll(urls);
                        break;
                }
            });
        }
        return result;
    }


}
