package com.geekbang.equipment.management.core;

/**
 * @author hejun
 */
public class ParamsException extends RuntimeException {

    public ParamsException(String message) {
        super(message);
    }

    public ParamsException(int code) {
        super(String.valueOf(code));
    }
}
