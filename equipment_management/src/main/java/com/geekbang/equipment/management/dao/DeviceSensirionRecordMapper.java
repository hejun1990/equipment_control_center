package com.geekbang.equipment.management.dao;

import com.geekbang.equipment.management.core.Mapper;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.model.dto.DeviceSensirionRecordDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 温湿度设备数据上报记录Mapper
 *
 * @author jun_h
 */
public interface DeviceSensirionRecordMapper extends Mapper<DeviceSensirionRecord> {

    /**
     * 动态创建表
     *
     * @param tableName    表名
     * @param tableComment 注释
     * @return int
     */
    int createTable(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 动态插入数据记录
     *
     * @param record 参数
     * @return int
     */
    int addRecord(DeviceSensirionRecordDTO record);
}