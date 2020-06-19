package com.geekbang.equipment.management.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

/**
 * 设备上报数据记录表表信息常量
 *
 * @author jun_h
 */
public enum DeviceRecordTableConstant {

    /**
     * 温湿度设备数据上报记录表
     */
    SENSIRION("device_sensirion_record", "温湿度设备数据上报记录表", 800);

    /**
     * 前缀名
     */
    private final String prefixName;

    /**
     * 表注释
     */
    private final String tableComment;

    /**
     * 表名(当前插入数据时应使用的表名)
     */
    private volatile String tableName;

    /**
     * 行数阈值
     */
    private final int rowThreshold;

    /**
     * 重入锁
     */
    public final Lock lock = new ReentrantLock(true);

    /**
     * 读写锁
     */
    private final StampedLock stampedLock = new StampedLock();

    DeviceRecordTableConstant(String prefixName, String tableComment, int rowThreshold) {
        this.prefixName = prefixName;
        this.tableComment = tableComment;
        this.rowThreshold = rowThreshold;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public int getRowThreshold() {
        return rowThreshold;
    }

    public void setTableName(String tableName) {
        long stamp = stampedLock.writeLock();
        try {
            this.tableName = tableName;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    public String getTableName() {
        // 乐观读
        long stamp = stampedLock.tryOptimisticRead();
        String tableName = this.tableName;
        // 校验stamp。如果执行读操作期间存在写操作，乐观读锁升级为悲观读锁
        if (!stampedLock.validate(stamp)) {
            // 升级为悲观读锁
            stamp = stampedLock.readLock();
            try {
                // 读入方法局部变量
                tableName = this.tableName;
            } finally {
                // 释放悲观读锁
                stampedLock.unlockRead(stamp);
            }
        }
        return tableName;
    }

    /**
     * 通过表前缀名获取对应的枚举类
     *
     * @param prefixName 表前缀名
     * @return DeviceRecordTableConstant
     */
    public static DeviceRecordTableConstant getTableConstant(String prefixName) {
        if (StringUtils.isBlank(prefixName)) {
            return null;
        }
        DeviceRecordTableConstant[] deviceRecordTableConstants = values();
        for (DeviceRecordTableConstant deviceRecordTableConstant : deviceRecordTableConstants) {
            if (prefixName.equals(deviceRecordTableConstant.getPrefixName())) {
                return deviceRecordTableConstant;
            }
        }
        return null;
    }
}
