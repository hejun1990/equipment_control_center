package com.geekbang.equipment.management.util;

import com.geekbang.equipment.management.constant.BasicConstant;
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
     * 普通线程池
     * <br/>
     * 核心线程数5，最大线程数200，阻塞队列长度1024
     */
    COMMON("common-pool-%d", "common"),

    /**
     * 无阻塞队列线程池
     * <br/>
     * 核心线程数5，最大线程数200
     */
    CACHED("cached-pool-%d", "cached"),

    /**
     * 固定线程数线程池
     * <br/>
     * 核心线程数5，阻塞队列长度1024
     */
    FIXED("fixed-pool-%d", "fixed");

    /**
     * 通用线程池
     */
    private ExecutorService pool;

    ThreadPoolFactory(String name, String type) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(name).build();
        if (BasicConstant.THREAD_POOL_COMMON.equals(type)) {
            this.pool = new ThreadPoolExecutor(5, 200, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        } else if (BasicConstant.THREAD_POOL_CACHED.equals(type)) {
            this.pool = new ThreadPoolExecutor(5, 200, 1, TimeUnit.SECONDS,
                    new SynchronousQueue<>(), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        } else if (BasicConstant.THREAD_POOL_FIXED.equals(type)) {
            this.pool = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        }
    }

    public ExecutorService getPool() {
        return pool;
    }
}
