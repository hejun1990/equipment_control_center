package com.geekbang.equipment.management.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 电监测设备数据上报记录表
 *
 * @author hejun
 */
@Table(name = "device_electricity_record", schema = "equipment_control_center")
@Data
public class DeviceElectricityRecord extends TableEntity implements Serializable {
    private static final long serialVersionUID = 7159660141229463457L;
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备编码
     */
    @Column(name = "device_code")
    private String deviceCode;

    /**
     * 止码
     */
    private BigDecimal degree;

    /**
     * 功率
     */
    @Column(name = "power_rate")
    private BigDecimal powerRate;

    /**
     * 电流
     */
    private BigDecimal current;

    /**
     * 电压
     */
    private BigDecimal voltage;

    /**
     * 上报时间
     */
    @Column(name = "record_time")
    private Date recordTime;
}