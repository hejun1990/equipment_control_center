package com.geekbang.equipment.management.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 设备上报数据记录表表信息常量
 *
 * @author jun_h
 */
public enum DeviceRecordTableConstant {

    /**
     * 温湿度设备数据上报记录表
     */
    SENSIRION("device_sensirion_record");

    /**
     * 前缀名
     */
    private final String prefixName;

    /**
     * 表名(当前插入数据时应使用的表名)
     */
    private volatile String tableName;

    /**
     * 锁
     */
    public final Lock lock = new ReentrantLock(true);

    DeviceRecordTableConstant(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

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
