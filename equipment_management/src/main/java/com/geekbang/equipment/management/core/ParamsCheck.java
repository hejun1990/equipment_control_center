package com.geekbang.equipment.management.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 参数校验的公共方法
 *
 * @author xieyiwen
 */
public class ParamsCheck {
    /**
     * 参数校验
     **/
    private static volatile ParamsCheck paramsCheck;

    private static final Object LOCK = new Object();

    private ParamsCheck() {
    }

    public static ParamsCheck init() {
        if (paramsCheck == null) {
            synchronized (LOCK) {
                if (paramsCheck == null) {
                    paramsCheck = new ParamsCheck();
                }
            }
        }
        return paramsCheck;
    }

    public ParamsCheck notEmpty(String string, int code) {
        try {
            Preconditions.checkState(!Strings.isNullOrEmpty(string));
        } catch (Exception e) {
            throw new ParamsException(code);
        }
        return this;
    }

    public ParamsCheck notEmpty(String string, String message) {
        try {
            Preconditions.checkState(!Strings.isNullOrEmpty(string));
        } catch (Exception e) {
            throw new ParamsException(message);
        }
        return this;
    }

    public ParamsCheck notEmpty(List<Object> list, int code) {
        try {
            Preconditions.checkState(!CollectionUtils.isEmpty(list));
        } catch (Exception e) {
            throw new ParamsException(code);
        }
        return this;
    }

    public ParamsCheck notEmpty(List<Object> list, String message) {
        try {
            Preconditions.checkState(!CollectionUtils.isEmpty(list));
        } catch (Exception e) {
            throw new ParamsException(message);
        }
        return this;
    }

    public ParamsCheck notNull(Object t, int code) {
        try {
            Preconditions.checkNotNull(t);
        } catch (Exception e) {
            throw new ParamsException(code);
        }
        return this;
    }

    public ParamsCheck notNull(Object t, String message) {
        try {
            Preconditions.checkNotNull(t);
        } catch (Exception e) {
            throw new ParamsException(message);
        }
        return this;
    }
}
