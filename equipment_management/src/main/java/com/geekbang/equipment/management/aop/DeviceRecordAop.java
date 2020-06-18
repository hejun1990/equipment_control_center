package com.geekbang.equipment.management.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.persistence.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 设备数据上报AOP
 *
 * @author hejun
 */
@Aspect
@Component
@Slf4j
public class DeviceRecordAop {

    @Pointcut("execution(public com.geekbang.equipment.management.core.Result com.geekbang.equipment.management.service.impl.*.update(..)) && @args(javax.persistence.Table,..)")
    public void updatePointcut() {
    }

    @Before("updatePointcut()")
    public void beforeUpdate(JoinPoint joinPoint) {
        log.info("---------- update开始前 ----------");
        Object model = joinPoint.getArgs()[0];
        Class<?> modelClass = model.getClass();
        Table table = modelClass.getAnnotation(Table.class);
        String prefixName = table.name();
        try {
            Method setPrefixName = modelClass.getMethod("setPrefixName", String.class);
            setPrefixName.invoke(model, prefixName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("{}", e.getMessage());
        }
    }
}
