package com.geekbang.equipment.management.web.controller;

import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.service.DeviceRecordSimulationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 设备数据上报模拟器
 *
 * @author hejun
 * @date 2020/06/16
 */
@RestController
@RequestMapping("/device/record/simulation/{lang}")
public class DeviceRecordSimulationController {

    @Resource
    private DeviceRecordSimulationService deviceRecordSimulationService;

    /**
     * 模拟温湿度数据上报
     *
     * @param lang 国际化语言
     * @return Result
     */
    @GetMapping
    public Result<?> simulationSensirion(@PathVariable(value = "lang") String lang) {
        return deviceRecordSimulationService.simulationSensirion(lang);
    }
}
