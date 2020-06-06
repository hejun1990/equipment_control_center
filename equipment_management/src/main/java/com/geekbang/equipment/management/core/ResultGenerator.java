package com.geekbang.equipment.management.core;

import com.geekbang.equipment.management.i18n.I18nMessageUtil;
import com.geekbang.equipment.management.i18n.LanguageEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 响应结果生成工具
 *
 * @author hejun
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE);
    }


    public static <T> Result<T> genSuccessResult(T data) {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static Result genFailResult(String message) {
        return new Result()
                .setCode(ResultCode.FAIL)
                .setMessage(message);
    }

    public static Result genFailResult(String messageStr, String lang) {
        String message;
        String language = LanguageEnum.LANGUAGE_ZH_CN.getLanguage();
        if (StringUtils.isNotBlank(lang)) {
            language = LanguageEnum.getLanguageType(lang);
        }
        if (StringUtils.isBlank(language)) {
            message = "Response Msg Error! This language not support!";
        } else {
            try {
                message = I18nMessageUtil.getMessage(language, messageStr, "FAIL");
            } catch (IOException e) {
                message = "language Msg get Fail!";
            }
        }
        return new Result()
                .setCode(ResultCode.FAIL)
                .setMessage(message);
    }

    public static <T> Result<T> genBusinessFailResult(String message, BusinessResultCode businessResult, T data) {
        Result result = new Result();
        result.setCode(businessResult.getCode());
        if (StringUtils.isNotBlank(message)) {
            result.setMessage(message);
        } else {
            result.setMessage(businessResult.getMessage());
        }
        if (data != null) {
            result.setData(data);
        }

        return result;
    }

    public static Result genSuccessResult(String messageStr, String lang) {
        String message;
        String language = LanguageEnum.LANGUAGE_ZH_CN.getLanguage();
        if (StringUtils.isNotBlank(lang)) {
            language = LanguageEnum.getLanguageType(lang);
        }
        if (StringUtils.isBlank(language)) {
            message = "Response Msg Error! This language not support!";
        } else {
            try {
                message = I18nMessageUtil.getMessage(language, messageStr, "FAIL");
            } catch (IOException e) {
                message = "language Msg get Fail!";
            }
        }
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(message);
    }
}
