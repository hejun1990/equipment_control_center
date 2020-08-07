package com.geekbang.equipment.management.dao;

import com.geekbang.equipment.management.core.TableMapper;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.model.vo.DeviceSensirionRecordVO;

import java.util.List;

/**
 * 温湿度设备数据上报记录Mapper
 *
 * @author hejun
 */
public interface DeviceSensirionRecordMapper extends TableMapper<DeviceSensirionRecord> {

    /**
     * 动态查询表记录
     *
     * @param recordVO 参数
     * @return List
     */
    List<DeviceSensirionRecord> selectDynamic(DeviceSensirionRecordVO recordVO);
}