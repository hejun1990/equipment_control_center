package com.geekbang.equipment.management.model.dto;

import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 温湿度设备数据上报记录DTO
 *
 * @author jun_h
 */
@Setter
@Getter
@ToString
public class DeviceSensirionRecordDTO extends DeviceSensirionRecord implements Serializable {

    private static final long serialVersionUID = 9177254331877746425L;

    /**
     * 表名
     */
    private String tableName;
}
