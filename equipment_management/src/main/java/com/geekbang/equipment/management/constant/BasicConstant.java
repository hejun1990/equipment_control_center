package com.geekbang.equipment.management.constant;

/**
 * 基础常量类
 *
 * @author hejun
 */
public class BasicConstant {

    /**
     * 已删除
     */
    public static final int IS_DELETE = 1;
    /**
     * 未删除
     */
    public static final int NO_DELETE = 0;

    /**
     * 默认失败消息
     */
    public static final String DEFAULT_ERROR_MESSAGE = "FAIL";

    /**
     * 表字段-建表语句
     */
    public static final String TABLE_FIELD_CREATE_TABLE = "Create Table";

    /**
     * 线程池类型-普通
     */
    public static final String THREAD_POOL_COMMON = "common";

    /**
     * 线程池类型-无阻塞队列
     */
    public static final String THREAD_POOL_CACHED = "cached";

    /**
     * 线程池类型-固定线程数
     */
    public static final String THREAD_POOL_FIXED = "fixed";
}
