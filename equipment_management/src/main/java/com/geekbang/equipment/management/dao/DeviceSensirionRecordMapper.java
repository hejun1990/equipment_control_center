package com.geekbang.equipment.management.dao;

import com.geekbang.equipment.management.core.TableMapper;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;

/**
 * 温湿度设备数据上报记录Mapper
 *
 * @author jun_h
 */
public interface DeviceSensirionRecordMapper extends TableMapper<DeviceSensirionRecord> {

    /**
     * 动态插入数据记录
     *
     * @param record 参数
     * @return int
     */
    int addRecord(DeviceSensirionRecord record);
}