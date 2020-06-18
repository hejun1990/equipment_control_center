package com.geekbang.equipment.management.aop;

import com.geekbang.equipment.management.model.TableEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 设备数据上报AOP
 *
 * @author hejun
 */
@Aspect
@Component
@Slf4j
public class DeviceRecordAop {

    @Pointcut("execution(public com.geekbang.equipment.management.core.Result com.geekbang.equipment.management.service.impl.*.update(com.geekbang.equipment.management.model.TableEntity, String)) && args(record, lang)")
    public void updatePointcut(TableEntity record, String lang) {
    }

    @Before("updatePointcut(record, lang)")
    public void beforeUpdate(TableEntity record, String lang) {
        log.info("---------- update开始前 ----------");
        record.setPrefixName("device_sensirion_record");
    }
}
