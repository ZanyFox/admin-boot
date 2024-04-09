package com.fz.admin.framework.web.handler;


import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

import static com.fz.admin.framework.common.enums.ServRespCode.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /*
     * 处理 SpringMVC 请求参数缺失  如@RequestParam 参数未传递
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ServRespEntity<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return ServRespEntity.fail(REQUEST_PARAMETER_ERROR.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }


    /*
     * 处理参数校验异常 针对@RequestBody请求参数
     * */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ServRespEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.warn("[methodArgumentNotValidExceptionHandler]", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError == null)
            return ServRespEntity.fail(REQUEST_PARAMETER_ERROR);

        return ServRespEntity.fail(REQUEST_PARAMETER_ERROR.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }


    /*
     * 处理 SpringMVC 请求参数类型错误
     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ServRespEntity<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return ServRespEntity.fail(REQUEST_PARAMETER_ERROR.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /*
     * 处理请求参数异常  针对SpringMVC 参数的校验
     * */
    @ExceptionHandler(ConstraintViolationException.class)
    public ServRespEntity<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.warn("[constraintViolationExceptionHandler]: ", e);
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().iterator().next();
        return ServRespEntity.fail(REQUEST_PARAMETER_ERROR, String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }


    /*
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验  比如controller参数是对象，但是没有使用@RequestBody注解
     */
    @ExceptionHandler(BindException.class)
    public ServRespEntity<?> bindExceptionHandler(BindException ex) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        assert fieldError != null;
        return ServRespEntity.fail(REQUEST_PARAMETER_ERROR.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }


    /*
     * 处理 SpringMVC 请求地址不存在
     *
     * 注意，它需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
     * 2. spring.mvc.static-path-pattern 为 /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ServRespEntity<?> noHandlerFoundExceptionHandler(HttpServletRequest req, NoHandlerFoundException ex) {
        log.warn("[noHandlerFoundExceptionHandler]", ex);
        return ServRespEntity.fail(REQUEST_NOT_FOUND_ERROR.getCode(), String.format("请求地址不存在:%s", ex.getRequestURL()));
    }

    /*
     * 处理 SpringMVC 请求方法不正确
     *
     * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ServRespEntity<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return ServRespEntity.fail(REQUEST_METHOD_ERROR.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
    }


    /*
     * 处理 Spring Security 权限不足的异常
     *
     * 来源是，使用 @PreAuthorize 注解，AOP 进行权限拦截
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ServRespEntity<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][用户 无法访问 url({})]", req.getRequestURL(), ex);
        return ServRespEntity.fail(ACCESS_FORBIDDEN);
    }


    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<?> noResourceFoundExceptionHandler(NoResourceFoundException exception) {
        log.warn("[noResourceFoundExceptionHandler], {}", exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /*
     * 业务异常处理
     */
    @ExceptionHandler({ServiceException.class})
    public ServRespEntity<?> serverExceptionHandler(ServiceException e) {
        log.error("[serverExceptionHandler]", e);
        if (e.getServCode() != null)
            return ServRespEntity.fail(e.getServCode());
        if (e.getMessage() != null)
            return ServRespEntity.fail(ServRespCode.SERVER_INTERNAL_ERROR.getCode(), e.getMessage());

        return ServRespEntity.fail(ServRespCode.SERVER_INTERNAL_ERROR);
    }


    /**
     * 处理系统异常
     */

    @ResponseStatus
    @ExceptionHandler(value = Exception.class)
    public ServRespEntity<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {

        log.error("[defaultExceptionHandler] ", ex);

        return ServRespEntity.fail(SERVER_INTERNAL_ERROR);
    }


}
