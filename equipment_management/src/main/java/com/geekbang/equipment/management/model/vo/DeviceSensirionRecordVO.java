package com.geekbang.equipment.management.model.vo;

import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 温湿度设备数据VO
 *
 * @author hejun
 */
@Getter
@Setter
@ToString
public class DeviceSensirionRecordVO extends DeviceSensirionRecord implements Serializable {

    private static final long serialVersionUID = 6288560166325218332L;

    /**
     * 查询开始时间
     */
    private String queryStartTime;

    /**
     * 查询结束时间
     */
    private String queryEndTime;

    /**
     * 分页偏移量
     */
    private Integer offset;

    /**
     * 分页行数
     */
    private Integer rows;
}
