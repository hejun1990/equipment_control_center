package com.geekbang.equipment.management.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author hejun
 */
@Getter
@Setter
@ToString
public class DeviceRecordQueryDTO implements Serializable {

    private static final long serialVersionUID = 2144264212051849692L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 起始偏移量
     */
    private Integer startOffset;

    /**
     * 结束偏移量
     */
    private Integer endOffset;
}
