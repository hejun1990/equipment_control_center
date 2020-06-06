package com.geekbang.equipment.management.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 设备基本信息表
 *
 * @author hejun
 */
@Table(name = "device_info")
@Data
public class DeviceInfo {
    /**
     * 主键ID
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
     * 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建者ID
     */
    @Column(name = "create_by")
    private Integer createBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 更新者ID
     */
    @Column(name = "update_by")
    private Integer updateBy;

    /**
     * 是否删除(0-否,1-是)
     */
    @Column(name = "is_deleted")
    private Byte isDeleted;

    /**
     * 版本号
     */
    @Column(name = "version_no")
    private Integer versionNo;
}