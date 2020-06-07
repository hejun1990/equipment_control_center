package com.geekbang.equipment.management.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

/**
 * 设备上报数据记录表表信息表
 *
 * @author jun_h
 */
@Table(name = "device_record_table_info")
@Data
public class DeviceRecordTableInfo {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 前缀名
     */
    @Column(name = "prefix_name")
    private String prefixName;

    /**
     * 表名
     */
    @Column(name = "table_name")
    private String tableName;

    /**
     * 总行数
     */
    @Column(name = "row_number")
    private Integer rowNumber;

    /**
     * 首行记录时间
     */
    @Column(name = "start_record_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.ssssss")
    private Date startRecordTime;

    /**
     * 末行记录时间
     */
    @Column(name = "end_record_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.ssssss")
    private Date endRecordTime;

    /**
     * 版本号
     */
    @Column(name = "version_no")
    private Integer versionNo;
}