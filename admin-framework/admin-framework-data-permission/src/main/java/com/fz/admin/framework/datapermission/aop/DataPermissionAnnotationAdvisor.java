package com.fz.admin.framework.datapermission.aop;


import com.fz.admin.framework.datapermission.annotation.DataPermission;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.NonNull;

/**
 * {@link DataPermission} 注解的 Advisor 实现类
 *
 *
 */

public class DataPermissionAnnotationAdvisor extends AbstractPointcutAdvisor {

    private final Advice advice;

    private final Pointcut pointcut;

    @NonNull
    public Advice getAdvice() {
        return advice;
    }

    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    public DataPermissionAnnotationAdvisor() {
        this.advice = new DataPermissionAnnotationInterceptor();
        this.pointcut = this.buildPointcut();
    }

    protected Pointcut buildPointcut() {
        // 在类上检查注解
        Pointcut classPointcut = new AnnotationMatchingPointcut(DataPermission.class, true);
        // 在方法上检查注解
        Pointcut methodPointcut = new AnnotationMatchingPointcut(null, DataPermission.class, true);
        return new ComposablePointcut(classPointcut).union(methodPointcut);
    }

}
