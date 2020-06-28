package com.geekbang.equipment.management.constant;

/**
 * 设备类型枚举类
 *
 * @author hejun
 */

public enum DeviceTypeConstant {
    /**
     * 温湿度
     */
    SENSIRION("2101", "温湿度"),

    /**
     * 电表
     */
    ELECTRICITY("1901", "电表");

    DeviceTypeConstant(String deviceTypeNo, String deviceTypeName) {
        this.deviceTypeNo = deviceTypeNo;
        this.deviceTypeName = deviceTypeName;
    }

    /**
     * 设备类型编码
     */
    private String deviceTypeNo;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    public String getDeviceTypeNo() {
        return deviceTypeNo;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }
}
