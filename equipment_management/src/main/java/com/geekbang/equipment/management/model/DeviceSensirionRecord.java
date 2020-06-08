package com.geekbang.equipment.management.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

/**
 * 温湿度设备数据上报记录表
 *
 * @author jun_h
 */
@Table(name = "device_sensirion_record")
@Data
public class DeviceSensirionRecord implements Serializable {

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
    private Short battery;

    /**
     * 上报时间
     */
    @Column(name = "record_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.ssssss")
    private Date recordTime;
}