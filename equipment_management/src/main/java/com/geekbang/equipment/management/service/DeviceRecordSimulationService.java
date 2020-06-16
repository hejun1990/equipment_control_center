package com.geekbang.equipment.management.service;

import com.geekbang.equipment.management.core.Result;

/**
 * 设备数据上报模拟器
 *
 * @author hejun
 * @date 2020/06/06
 **/
public interface DeviceRecordSimulationService {

    /**
     * 模拟温湿度数据上报
     *
     * @param lang 国际化语言
     * @return Result
     */
    Result<?> simulationSensirion(String lang);
}
