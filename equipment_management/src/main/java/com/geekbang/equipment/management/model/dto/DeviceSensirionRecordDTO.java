package com.geekbang.equipment.management.model.dto;

import com.geekbang.equipment.management.constant.DeviceRecordTableConstant;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;

import java.io.Serializable;

/**
 * 温湿度设备数据上报记录DTO
 *
 * @author jun_h
 */
public class DeviceSensirionRecordDTO extends DeviceSensirionRecord implements Serializable {

    private static final long serialVersionUID = 9177254331877746425L;

    /**
     * 前缀名
     */
    private final String prefixName = DeviceRecordTableConstant.SENSIRION.getPrefixName();

    /**
     * 表名
     */
    private String tableName;

    public String getPrefixName() {
        return prefixName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "DeviceSensirionRecordDTO{" +
                "prefixName='" + prefixName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
