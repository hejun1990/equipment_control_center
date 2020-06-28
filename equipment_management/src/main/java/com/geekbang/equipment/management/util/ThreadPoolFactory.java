package com.geekbang.equipment.management.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 线程池工厂类
 *
 * @author hejun
 * @date 2019/7/18
 */
public enum ThreadPoolFactory {
    /**
     * 通用线程池
     */
    COMMON("common-pool-%d");

    /**
     * 通用线程池
     */
    private ExecutorService pool;

    ThreadPoolFactory(String name) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(name).build();
        this.pool = new ThreadPoolExecutor(5, 200, 1000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public ExecutorService getPool() {
        return pool;
    }
}
