package com.geekbang.equipment.management.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
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
    SENSIRION("device_sensirion_record", "温湿度设备数据上报记录表"),

    /**
     * 电监测设备数据上报记录表
     */
    ELECTRICITY("device_electricity_record", "温湿度设备数据上报记录表");

    /**
     * 行数阈值
     */
    public static final int ROW_THRESHOLD = 200;

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
     * 表字段
     */
    private List<String> columns;

    /**
     * 重入锁
     */
    public final Lock lock = new ReentrantLock(true);

    /**
     * 读写锁（乐观）
     */
    private final StampedLock stampedLock = new StampedLock();

    DeviceRecordTableConstant(String prefixName, String tableComment) {
        this.prefixName = prefixName;
        this.tableComment = tableComment;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public String getTableComment() {
        return tableComment;
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

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
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
