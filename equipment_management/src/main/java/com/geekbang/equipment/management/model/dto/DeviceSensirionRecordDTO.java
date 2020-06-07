package com.geekbang.equipment.management.model.dto;

import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 温湿度设备数据上报记录DTO
 *
 * @author jun_h
 */
@Setter
@Getter
@ToString
public class DeviceSensirionRecordDTO extends DeviceSensirionRecord {

    /**
     * 前缀名
     */
    private String prefixName;

    /**
     * 表名
     */
    private String tableName;
}
