package com.geekbang.equipment.management.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * 从Spring IOC容器中获取Bean对象
 *
 * @author hejun
 * @date 2019/8/9
 */
@Component
@Slf4j
public class BeanHeader implements ApplicationContextAware {

    /**
     * 上下文对象
     */
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，注入上下文对象
     *
     * @param applicationContext ApplicationContext
     * @throws BeansException BeansException
     */
    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        BeanHeader.applicationContext = applicationContext;
    }

    /**
     * 获取上下文对象
     *
     * @return applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 判断上下文对象是否为空
     *
     * @return boolean
     */
    public static boolean checkApplicationContext() {
        boolean flag = getApplicationContext() != null;
        if (!flag) {
            log.error("ApplicationContext未注入,实现ApplicationContextAware的类必须被spring管理");
        }
        return flag;
    }

    /**
     * 根据name获取bean
     *
     * @param name bean的name
     * @param <T>  bean的Class泛型声明
     * @return 实体bean
     */
    public static <T> T getBean(String name) {
        if (checkApplicationContext()) {
            return (T) getApplicationContext().getBean(name);
        } else {
            return null;
        }
    }

    /**
     * 根据class 获取bean
     *
     * @param clazz bean的Class
     * @param <T>   bean的Class泛型声明
     * @return 实体bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (checkApplicationContext()) {
            return getApplicationContext().getBean(clazz);
        } else {
            return null;
        }
    }

    /**
     * 根据name,以及Clazz返回指定的Bean
     *
     * @param name  bean的name
     * @param clazz bean的Class
     * @param <T>   bean的Class泛型声明
     * @return 实体bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (checkApplicationContext()) {
            return getApplicationContext().getBean(name, clazz);
        } else {
            return null;
        }
    }
}
