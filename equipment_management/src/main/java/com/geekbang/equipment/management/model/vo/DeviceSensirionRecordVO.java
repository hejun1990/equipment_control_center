package com.geekbang.equipment.management.model.vo;

import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 温湿度设备数据上报记录表
 *
 * @author hejun
 */
@Getter
@Setter
@ToString
public class DeviceSensirionRecordVO extends DeviceSensirionRecord implements Serializable {

    private static final long serialVersionUID = -5848681023462172141L;

    /**
     * 查询开始时间
     */
    private String queryStartTime;

    /**
     * 查询结束时间
     */
    private String queryEndTime;
}
