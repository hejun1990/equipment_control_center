package com.geekbang.equipment.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 温湿度设备数据上报记录表
 *
 * @author hejun
 */
@Table(name = "device_sensirion_record", schema = "equipment_control_center")
@Data
@EqualsAndHashCode(callSuper = false)
public class DeviceSensirionRecord extends TableEntity implements Serializable {

    private static final long serialVersionUID = -1785245250765406347L;

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
     * 温度
     */
    private BigDecimal temperature;

    /**
     * 湿度
     */
    private BigDecimal humidity;

    /**
     * 电池电量(0-100)
     */
    private Integer battery;

    /**
     * 上报时间
     */
    @Column(name = "record_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date recordTime;
}