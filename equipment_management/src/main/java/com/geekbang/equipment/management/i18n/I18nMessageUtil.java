package com.geekbang.equipment.management.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;

/**
 * 国际化工具
 *
 * @author zwsky
 */
@Slf4j
public class I18nMessageUtil {
    /**
     * 国际化资源路径
     */
    private static final String PATH_PARENT = "classpath:i18n" + File.separator + "messages_";
    /**
     * 国际化资源文件后缀名
     */
    private static final String SUFFIX = ".properties";

    private I18nMessageUtil() {
    }

    /**
     * 初始化资源文件的存储器
     * 加载指定语言配置文件
     *
     * @param language 语言类型(文件名即为语言类型,eg: en_us 表明使用 美式英文 语言配置)
     * @return MessageSourceAccessor
     * @throws IOException IOException
     */
    private static MessageSourceAccessor initMessageSourceAccessor(String language) throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        // 获取配置文件名
        Resource resource = resourcePatternResolver.getResource(PATH_PARENT + language + SUFFIX);
        String fileName = resource.getURL().toString();
        int lastIndex = fileName.lastIndexOf(".");
        String baseName = fileName.substring(0, lastIndex);
        // 读取配置文件
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename(baseName);
        reloadableResourceBundleMessageSource.setCacheSeconds(5);
        return new MessageSourceAccessor(reloadableResourceBundleMessageSource);
    }

    /**
     * 获取一条语言配置信息
     *
     * @param language       语言类型,zh_cn: 简体中文, en_us: 英文
     * @param messageCode    配置信息属性名,eg: api.response.code.user.signUp
     * @param defaultMessage 默认信息,当无法从配置文件中读取到对应的配置信息时返回改信息
     * @return String
     * @throws IOException IOException
     */
    public static String getMessage(String language, String messageCode, String defaultMessage) {
        String message = null;
        try {
            MessageSourceAccessor accessor = initMessageSourceAccessor(language);
            message = accessor.getMessage(messageCode, defaultMessage, LocaleContextHolder.getLocale());
        } catch (IOException e) {
            log.error("{}", e.getMessage() != null ? e.getMessage() : e);
        }
        return message;
    }
}
