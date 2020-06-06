package com.geekbang.equipment.management.web.controller;

import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.model.DeviceInfo;
import com.geekbang.equipment.management.service.DeviceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *
 * @author hejun
 * @date 2020/06/06
 **/
@Slf4j
@RestController
@RequestMapping("/device/info/{lang}")
public class DeviceInfoController {

    @Resource
    private DeviceInfoService deviceInfoService;

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @PostMapping
    public Result<?> add(@RequestBody DeviceInfo record, @PathVariable(value = "lang") String lang) {
        return deviceInfoService.add(record, lang);
    }

    /**
     * 删除
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id, @PathVariable(value = "lang") String lang) {
        return deviceInfoService.delete(id, lang);
    }

    /**
     * 修改
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody DeviceInfo record, @PathVariable(value = "lang") String lang) {
        return deviceInfoService.update(record, lang);
    }

    /**
     * 单个查询
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id, @PathVariable(value = "lang") String lang) {
        return deviceInfoService.detail(id, lang);
    }

    /**
     * 分页查询
     *
     * @param page   页码
     * @param size   每页行数
     * @param record 查询条件
     * @param lang   国际化语言
     * @return Result
     */
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size,
                          DeviceInfo record, @PathVariable(value = "lang") String lang) {
        return deviceInfoService.list(page, size, record, lang);
    }
}
