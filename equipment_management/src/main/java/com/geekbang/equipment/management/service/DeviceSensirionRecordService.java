package com.geekbang.equipment.management.service;

import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.Service;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.model.TableEntity;


/**
 * @author hejun
 * @date 2020/06/07
 */
public interface DeviceSensirionRecordService extends Service<DeviceSensirionRecord> {

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    Result<?> add(DeviceSensirionRecord record, String lang);

    /**
     * 删除
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    Result<?> delete(Integer id, String lang);

    /**
     * 修改
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    Result<?> update(DeviceSensirionRecord record, String lang);

    Result<?> update(TableEntity record, String lang);

    /**
     * 单个查询
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    Result<?> detail(Integer id, String lang);

    /**
     * 分页查询
     *
     * @param page   页码
     * @param size   每页行数
     * @param record 查询条件
     * @param lang   国际化语言
     * @return Result
     */
    Result<?> list(Integer page, Integer size, DeviceSensirionRecord record, String lang);
}
